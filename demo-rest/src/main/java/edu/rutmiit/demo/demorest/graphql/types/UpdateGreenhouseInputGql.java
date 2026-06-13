package edu.rutmiit.demo.demorest.graphql.types;

import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;

import java.time.LocalDate;

/**
 * Входной тип для обновления теплицы.
 * Соответствует input UpdateGreenhouseInput в GraphQL-схеме.
 */
public record UpdateGreenhouseInputGql(
        String namePlant,
        String varietyPlant,
        Integer quantityPlant,
        WateredStatus status,
        LocalDate lastWateredAt
) {}
