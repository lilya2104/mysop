package edu.rutmiit.demo.demorest.graphql.types;

import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;

import java.util.List;

/**
 * Тип-обёртка для постраничного ответа со списком авторов.
 * Соответствует типу GreenhouseConnection в GraphQL-схеме.
 */
public record GreenhouseConnectionGql(
        List<GreenhouseResponse> content,
        PageInfoGql pageInfo,
        int totalElements
) {}
