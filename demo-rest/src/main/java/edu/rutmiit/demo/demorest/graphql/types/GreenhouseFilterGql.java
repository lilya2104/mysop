package edu.rutmiit.demo.demorest.graphql.types;

/**
 * Входной тип для фильтрации авторов.
 * Соответствует input GreenhouseFilter в GraphQL-схеме.
 *
 * Все поля необязательны — клиент передаёт только нужные фильтры.
 */
public record GreenhouseFilterGql(
        String searchPlantName
) {}
