package edu.rutmiit.demo.plantsapicontract.dto;

import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Запрос для частичного обновления теплицы (PATCH, семантика JSON Merge Patch).
 *
 * Все поля необязательны. Передайте только то, что нужно изменить.
 * Поля, которые не переданы (null), сервис оставляет без изменений.
 */
@Schema(description = "Частичное обновление теплицы (PATCH). Передайте только те поля, которые нужно изменить.")
public record PatchGreenhouseRequest(

        @Schema(description = "Название растения", example = "Томаты")
        @Size(max = 100, message = "Название растения не может превышать 100 символов")
        String namePlant,

        @Schema(description = "Сорт растения", example = "Черри")
        @Size(max = 100, message = "Сорт растения не может превышать 100 символов")
        String varietyPlant,

        @Schema(description = "Количество растений", example = "10")
        @Min(value = 1, message = "Количество растений не может быть меньше 1")
        @Max(value = 200, message = "Количество растений должно быть не больше 200")
        Integer quantityPlant,

        @Schema(description = "Статус состояния почвы", example = "WET", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Статус состояния почвы не может быть пустым")
        WateredStatus status,

        @Schema(description = "Момент последнего полива теплицы", example = "2000-01-01T13:01:00", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "Время последнего полива теплицы не может быть пустым")
        LocalDate lastWateredAt

) {}
