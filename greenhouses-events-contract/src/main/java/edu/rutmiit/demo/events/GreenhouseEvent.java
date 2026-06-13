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

    /**
     * Теплица создана. Содержит основные атрибуты новой теплицы.
     */
    record Created(
            Long greenhouseId,
            String namePlant,
            String varietyPlant,
            Integer quantityPlant,
            WateredStatus status,
            LocalDate lastWatered
    ) implements GreenhouseEvent {}

    /**
     * Теплица обновлена. Содержит актуальное состояние после обновления.
     * В промышленных системах здесь могут быть и старые значения (before/after),
     * но для демонстрации достаточно нового состояния.
     */
    record Updated(
            Long greenhouseId,
            String namePlant,
            String varietyPlant,
            Integer quantityPlant,
            WateredStatus status,
            LocalDate lastWatered
    ) implements GreenhouseEvent {}

    /**
     * Теплица полита (изменение статуса на WET и обновление даты полива).
     */
    record Watered(
            Long greenhouseId,
            String namePlant,
            WateredStatus newStatus,
            LocalDate wateredAt
    ) implements GreenhouseEvent {}

    /**
     * Изменён статус полива (например, с WET на DRY или наоборот).
     * Вызывается из метода changeWateredStatus() в GreenhouseService.
     */
    record StatusChanged(
            Long greenhouseId,
            String namePlant,
            WateredStatus oldStatus,
            WateredStatus newStatus,
            LocalDate changedAt
    ) implements GreenhouseEvent {}

    /**
     * Теплица удалена.
     */
    record Deleted(
            Long greenhouseId,
            String namePlant
    ) implements GreenhouseEvent {}
}
