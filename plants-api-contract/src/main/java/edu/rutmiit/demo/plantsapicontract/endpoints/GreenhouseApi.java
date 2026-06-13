package edu.rutmiit.demo.plantsapicontract.endpoints;

import edu.rutmiit.demo.plantsapicontract.config.GreenhousesApiContractConfig;
import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import edu.rutmiit.demo.plantsapicontract.dto.ErrorResponse;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseRequest;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import edu.rutmiit.demo.plantsapicontract.dto.PatchGreenhouseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контракт API для управления авторами.
 * Реализующий контроллер в сервисе должен имплементировать этот интерфейс.
 */
@Tag(name = "Greenhouse", description = "Управление теплицами в саду")
@RequestMapping(
        value = "/api/greenhouse",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public interface GreenhouseApi {
    @Operation(
            summary = "Список теплиц",
            description = "Возвращает постраничный список теплиц с HATEOAS-ссылками. "
                    + "Ссылки prev/next позволяют клиенту навигировать по страницам без знания офсетов.",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Список теплиц")
    @GetMapping
    PagedModel<EntityModel<GreenhouseResponse>> getAllGreenhouses(
            @Parameter(description = "Фильтр по названию растения") @RequestParam (required = false) String searchPlantName,
            @Parameter(description = "Номер страницы (0..N)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20") @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "Получить теплицу по ID",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Теплица найдена")
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    EntityModel<GreenhouseResponse> getGreenhouseById(
            @Parameter(description = "ID теплицы", required = true, example = "1") @PathVariable Long id
    );

    @Operation(
            summary = "Получить теплицу по названию растения",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Теплица найден")
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/search")
    PagedModel<EntityModel<GreenhouseResponse>> searchByPlantName(
            @Parameter(description = "Название растений для поиска", required = true, example = "Томат черри")
            @RequestParam String name,
            @Parameter(description = "Номер страницы (0..N)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы", example = "20")
            @RequestParam(defaultValue = "20") int size
    );

    @Operation(
            summary = "Создать теплицу",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "201", description = "Теплица создана. Location header содержит URI нового ресурса.")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity<EntityModel<GreenhouseResponse>> createGreenhouse(@Valid @RequestBody GreenhouseRequest request);

    @Operation(
            summary = "Полное обновление теплицы (PUT)",
            description = "Заменяет все поля теплицы. Для обновления отдельных полей используйте PATCH.",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Теплица обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<GreenhouseResponse> updateGreenhouse(
            @Parameter(description = "ID теплицы", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody GreenhouseRequest request
    );

    @Operation(
            summary = "Частичное обновление теплицы (PATCH)",
            description = """
                    Обновляет только переданные поля (семантика JSON Merge Patch, RFC 7396).
                    Непереданные поля остаются без изменений.
                    """,
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Теплица обновлена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<GreenhouseResponse> patchGreenhouse(
            @Parameter(description = "ID теплицы", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody PatchGreenhouseRequest request
    );

    @Operation(
            summary = "Полив теплицы",
            description = "Заменяет поле status на WET, время последнего полива на настоящее.",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Теплица полита")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{id}/water", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<GreenhouseResponse> watering(
            @Parameter(description = "ID теплицы", required = true, example = "1") @PathVariable Long id
    );

    @Operation(
            summary = "Изменение статуса состояния почвы теплицы",
            description = "Изменяет статус состояния почвы теплицы на переданное.",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "200", description = "Статус изменен")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    EntityModel<GreenhouseResponse> changeWateredStatus(
            @Parameter(description = "ID теплицы", required = true, example = "1") @PathVariable Long id,
            @Valid@RequestBody WateredStatus newStatus
    );

    @Operation(
            summary = "Удалить теплицу",
            description = "Удаляет теплицу.",
            security = @SecurityRequirement(name = GreenhousesApiContractConfig.SECURITY_SCHEME_BEARER)
    )
    @ApiResponse(responseCode = "204", description = "Теплица удалёна")
    @ApiResponse(responseCode = "404", description = "Теплица не найдена",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteGreenhouse(
            @Parameter(description = "ID теплицы", required = true, example = "1") @PathVariable Long id
    );
}
