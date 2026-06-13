package edu.rutmiit.demo.demorest.graphql.types;

import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;

import java.time.LocalDate;

/**
 * Входной тип для создания теплицы.
 * Соответствует input CreateGreenhouseInput в GraphQL-схеме.
 */
public record CreateGreenhouseInputGql(
        String namePlant,
        String varietyPlant,
        Integer quantityPlant,
        WateredStatus status,
        LocalDate lastWateredAt
) {}
