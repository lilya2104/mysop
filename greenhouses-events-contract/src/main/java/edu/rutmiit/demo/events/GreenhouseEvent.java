package edu.rutmiit.demo.events;

import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;

import java.time.LocalDate;

/**
 * Семейство событий, связанных с теплицами.
 *
 * Аналогично GreenhouseEvent — sealed interface гарантирует полный перечень вариантов.
 * Десериализация по eventType, а не через Jackson-аннотации.
 */
public sealed interface GreenhouseEvent {
    record Created(Long greenhouseId, String namePlant, String varietyPlant, Integer quantityPlant,
                   WateredStatus status, LocalDate lastWatered, Integer totalWaterings
    ) implements GreenhouseEvent {}
    record Updated(Long greenhouseId, String namePlant, String varietyPlant,
                   Integer quantityPlant, WateredStatus status, LocalDate lastWatered
    ) implements GreenhouseEvent {}
    record Watered(Long greenhouseId, String namePlant, WateredStatus newStatus,
                   LocalDate wateredAt, Integer totalWaterings
    ) implements GreenhouseEvent {}
    record StatusChanged(Long greenhouseId, String namePlant, WateredStatus oldStatus,
                         WateredStatus newStatus, LocalDate changedAt
    ) implements GreenhouseEvent {}
    record Analyzed(Long greenhouseId, String healthStatus, Double healthScore,
                    String wateringUrgency, Integer recommendedWateringDays, String growthStage
    ) implements GreenhouseEvent {}
    record Deleted(Long greenhouseId, String namePlant) implements GreenhouseEvent {}
}
