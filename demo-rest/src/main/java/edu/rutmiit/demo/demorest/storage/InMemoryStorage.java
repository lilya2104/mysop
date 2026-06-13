package edu.rutmiit.demo.demorest.storage;

import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {
    public final Map<Long, GreenhouseResponse> greenhouses = new ConcurrentHashMap<>();
    public final Map<Long, Integer> wateringCounters = new ConcurrentHashMap<>();
    public final AtomicLong greenhouseSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        GreenhouseResponse ghouse1 = GreenhouseResponse.builder()
                .id(greenhouseSequence.incrementAndGet())
                .namePlant("Томат")
                .varietyPlant("Черри")
                .quantityPlant(12)
                .status(WateredStatus.WET)
                .lastWateredAt(LocalDate.now())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        wateringCounters.put(ghouse1.getId(), 0);

        GreenhouseResponse ghouse2 = GreenhouseResponse.builder()
                .id(greenhouseSequence.incrementAndGet())
                .namePlant("Тыква")
                .varietyPlant("Оранжевая")
                .quantityPlant(6)
                .status(WateredStatus.WET)
                .lastWateredAt(LocalDate.now())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        wateringCounters.put(ghouse2.getId(), 0);

        GreenhouseResponse ghouse3 = GreenhouseResponse.builder()
                .id(greenhouseSequence.incrementAndGet())
                .namePlant("Кабачок")
                .varietyPlant("Хороший")
                .quantityPlant(8)
                .status(WateredStatus.WET)
                .lastWateredAt(LocalDate.now())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        wateringCounters.put(ghouse3.getId(), 0);

        greenhouses.put(ghouse1.getId(), ghouse1);
        greenhouses.put(ghouse2.getId(), ghouse2);
        greenhouses.put(ghouse3.getId(), ghouse3);
    }
}
