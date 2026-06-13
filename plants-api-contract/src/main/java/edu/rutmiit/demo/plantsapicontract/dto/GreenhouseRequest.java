package edu.rutmiit.demo.plantsapicontract.dto;

import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

/**
 * DTO для создания или полного обновления теплицы (POST / PUT).
 * Все обязательные поля должны присутствовать.
 */
@Schema(description = "Запрос на создание или полное обновление теплицы")
public record GreenhouseRequest(

        @Schema(description = "Название растения", example = "Томаты", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Название растения не может быть пустой")
        @Size(max = 100, message = "Название растения не может превышать 100 символов")
        String namePlant,

        @Schema(description = "Сорт растения", example = "Черри", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Сорт растения не может быть пустой")
        @Size(max = 100, message = "Сорт растения не может превышать 100 символов")
        String varietyPlant,

        @Schema(description = "Количество растений", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
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
