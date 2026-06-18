package edu.rutmiit.demo.plantsapicontract.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Данные автора в ответе API.
 *
 * Расширяет RepresentationModel для поддержки HATEOAS-ссылок — поэтому здесь
 * обычный класс с Lombok, а не record (record не может расширять классы).
 * Поля со значением null не попадают в JSON ответа.
 */
@Getter
@Builder
@EqualsAndHashCode(callSuper = false) // не включаем HATEOAS-ссылки в сравнение equals
@JsonInclude(JsonInclude.Include.NON_NULL)
@Relation(collectionRelation = "greenhouses", itemRelation = "greenhouse")
@Schema(description = "Информация о теплице")
public class GreenhouseResponse extends RepresentationModel<GreenhouseResponse> {

    @Schema(description = "Уникальный идентификатор теплицы", example = "1")
    private final Long id;
    @Schema(description = "Название растения", example = "Томаты")
    private final String namePlant;
    @Schema(description = "Сорт растения", example = "Черри")
    private final String varietyPlant;
    @Schema(description = "Количество растений", example = "50")
    private final Integer quantityPlant;
    @Schema(description = "Статус состояния почвы")
    private final WateredStatus status;
    @Schema(description = "Момент последнего полива теплицы")
    private final LocalDate lastWateredAt;
    @Schema(description = "Момент создания записи в каталоге")
    private final OffsetDateTime createdAt;
    @Schema(description = "Момент последнего обновления записи")
    private final OffsetDateTime updatedAt;
}