package edu.rutmiit.demo.plantsapicontract.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Запрос для полного обновления теплицы (PUT).
 *
 * Все обязательные поля должны быть переданы.
 * Для изменения только отдельных полей используйте PATCH (PatchreenhouseRequest).
 */
@Schema(description = "Полное обновление теплицы (PUT). Все обязательные поля должны присутствовать.")
public record UpdateGreenhouseRequest(
        @Schema(description = "Название растения", example = "Томаты",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Название растения не может быть пустой")
        @Size(max = 100, message = "Название растения не может превышать 100 символов")
        String namePlant,
        @Schema(description = "Сорт растения", example = "Черри",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Сорт растения не может быть пустой")
        @Size(max = 100, message = "Сорт растения не может превышать 100 символов")
        String varietyPlant,
        @Schema(description = "Количество растений", example = "10",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @Min(value = 1, message = "Количество растений не может быть меньше 1")
        @Max(value = 200, message = "Количество растений должно быть не больше 200")
        Integer quantityPlant
) {}
