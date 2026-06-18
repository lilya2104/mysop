package edu.rutmiit.demo.demorest.service;

import edu.rutmiit.demo.demorest.event.GreenhouseEventPublisher;
import edu.rutmiit.demo.demorest.storage.InMemoryStorage;
import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseRequest;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import edu.rutmiit.demo.plantsapicontract.dto.PagedResponse;
import edu.rutmiit.demo.plantsapicontract.dto.PatchGreenhouseRequest;
import edu.rutmiit.demo.plantsapicontract.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GreenhouseService {
    private final InMemoryStorage storage;

    private final GreenhouseEventPublisher eventPublisher;

    public GreenhouseService(InMemoryStorage storage, GreenhouseEventPublisher eventPublisher) {
        this.storage = storage;
        this.eventPublisher = eventPublisher;
    }

    public PagedResponse<GreenhouseResponse> findAll(String searchPlantName, int page, int size) {

        List<GreenhouseResponse> all = storage.greenhouses.values().stream()
                .filter(g -> searchPlantName == null || searchPlantName.isBlank() ||
                        (g.getNamePlant() != null && g.getNamePlant().toLowerCase().contains(searchPlantName.toLowerCase())))
                .sorted(Comparator.comparingLong(GreenhouseResponse::getId))
                .toList();
        int totalElements = all.size();
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int from = page * size;
        int to = Math.min(from + size, totalElements);
        List<GreenhouseResponse> content = (from >= totalElements) ? List.of() : all.subList(from, to);
        return new PagedResponse<>(content, page, size, totalElements, totalPages, page >= totalPages - 1);
    }

    public GreenhouseResponse findById(Long id) {
        return Optional.ofNullable(storage.greenhouses.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Greenhouse", id));
    }

    public GreenhouseResponse create(GreenhouseRequest request) {
        long id = storage.greenhouseSequence.incrementAndGet();
        storage.wateringCounters.put(id, 0);
        GreenhouseResponse ghouse = GreenhouseResponse.builder()
                .id(id)
                .namePlant(request.namePlant())
                .varietyPlant(request.varietyPlant())
                .quantityPlant(request.quantityPlant())
                .status(request.status())
                .lastWateredAt(request.lastWateredAt())
                .build();
        storage.greenhouses.put(id, ghouse);
        eventPublisher.publishCreated(ghouse, 0);
        return ghouse;
    }

    public GreenhouseResponse updateGreenhouse(Long id, GreenhouseRequest request) {
        GreenhouseResponse updated = GreenhouseResponse.builder()
                .id(id)
                .namePlant(request.namePlant())
                .varietyPlant(request.varietyPlant())
                .quantityPlant(request.quantityPlant())
                .status(request.status())
                .lastWateredAt(request.lastWateredAt())
                .build();
        storage.greenhouses.put(id, updated);
        eventPublisher.publishUpdated(updated);
        return updated;
    }

    public GreenhouseResponse patchGreenhouse(Long id, PatchGreenhouseRequest request) {
        GreenhouseResponse existing = findById(id);
        GreenhouseResponse updated = GreenhouseResponse.builder()
                .id(id)
                .namePlant(request.namePlant() != null ? request.namePlant() : existing.getNamePlant())
                .varietyPlant(request.varietyPlant() != null ? request.varietyPlant() : existing.getVarietyPlant())
                .quantityPlant(request.quantityPlant() != null ? request.quantityPlant() : existing.getQuantityPlant())
                .status(request.status() != null ? request.status() : existing.getStatus())
                .lastWateredAt(request.lastWateredAt() != null ? request.lastWateredAt() : existing.getLastWateredAt())
                .build();
        storage.greenhouses.put(id, updated);
        eventPublisher.publishUpdated(updated);
        return updated;
    }

    public GreenhouseResponse watering(Long id) {
        GreenhouseResponse existing = findById(id);
        int newCount = storage.wateringCounters.merge(id, 1, Integer::sum);
        GreenhouseResponse updated = GreenhouseResponse.builder()
                .id(id)
                .namePlant(existing.getNamePlant())
                .varietyPlant(existing.getVarietyPlant())
                .quantityPlant(existing.getQuantityPlant())
                .status(WateredStatus.WET)
                .lastWateredAt(LocalDate.now())
                .build();
        storage.greenhouses.put(id, updated);
        eventPublisher.publishWatered(updated, newCount);
        return updated;
    }

    public GreenhouseResponse changeWateredStatus(Long id, WateredStatus newStatus) {
        GreenhouseResponse existing = findById(id);
        WateredStatus oldStatus = existing.getStatus();
        GreenhouseResponse updated = GreenhouseResponse.builder()
                .id(id)
                .namePlant(existing.getNamePlant())
                .varietyPlant(existing.getVarietyPlant())
                .quantityPlant(existing.getQuantityPlant())
                .status(newStatus != null ? newStatus : existing.getStatus())
                .lastWateredAt(existing.getLastWateredAt())
                .build();
        storage.greenhouses.put(id, updated);
        eventPublisher.publishStatusChanged(updated, oldStatus);
        return updated;
    }

    public void delete(Long id) {
        GreenhouseResponse existing = findById(id); // Проверяем, что автор существует
        storage.greenhouses.remove(id);
        storage.wateringCounters.remove(id);
        eventPublisher.publishDeleted(id, existing.getNamePlant());
    }
}
