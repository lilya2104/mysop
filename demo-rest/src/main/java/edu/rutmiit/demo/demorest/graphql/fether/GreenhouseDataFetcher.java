package edu.rutmiit.demo.demorest.graphql.fether;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.rutmiit.demo.demorest.graphql.types.*;
import edu.rutmiit.demo.demorest.service.GreenhouseService;
import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseRequest;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import edu.rutmiit.demo.plantsapicontract.dto.PagedResponse;

/**
 * DataFetcher для операций с авторами.
 *
 * Обрабатывает корневые поля Query и Mutation, связанные с теплицами.
 *
 * Принцип разделения: один DataFetcher — одна группа связанных операций.
 * Это делает код более читаемым и тестируемым.
 */
@DgsComponent
public class GreenhouseDataFetcher {
    private final GreenhouseService greenhouseService;

    public GreenhouseDataFetcher(GreenhouseService greenhouseService) {
        this.greenhouseService = greenhouseService;
    }

    /**
     * Получение теплицы по идентификатору.
     * Соответствует полю Query.greenhouse(id: ID!) в схеме.
     */
    @DgsQuery
    public GreenhouseResponse greenhouse(@InputArgument String id) {
        return greenhouseService.findById(Long.parseLong(id));
    }

    /**
     * Список авторов с пагинацией.
     * Соответствует полю Query.greenhouses(page, size) в схеме.
     */
    @DgsQuery
    public GreenhouseConnectionGql greenhouses(
            @InputArgument GreenhouseFilterGql filter,
            @InputArgument Integer page,
            @InputArgument Integer size
    ) {

        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;

        String searchPlantName = null;

        if(filter != null) {
            searchPlantName = filter.searchPlantName();
        }

        PagedResponse<GreenhouseResponse> paged = greenhouseService.findAll(searchPlantName, pageNum, pageSize);

        return new GreenhouseConnectionGql(
                paged.content(),
                new PageInfoGql(paged.pageNumber(), paged.pageSize(), paged.totalPages(), paged.last()),
                (int) paged.totalElements());
    }

    /**
     * Создание теплицы.
     * Соответствует полю Mutation.createGreenhouse(input) в схеме.
     */
    @DgsMutation
    public GreenhouseResponse createGreenhouse(@InputArgument CreateGreenhouseInputGql input) {
        GreenhouseRequest request = new GreenhouseRequest(
                input.namePlant(),
                input.varietyPlant(),
                input.quantityPlant(),
                input.status(),
                input.lastWateredAt()
        );
        return greenhouseService.create(request);
    }

    /**
     * Обновление теплицы.
     * Соответствует полю Mutation.updateGreenhouse(id, input) в схеме.
     */
    @DgsMutation
    public GreenhouseResponse updateGreenhouse(@InputArgument String id, @InputArgument UpdateGreenhouseInputGql input) {
        GreenhouseRequest request = new GreenhouseRequest(
                input.namePlant(),
                input.varietyPlant(),
                input.quantityPlant(),
                input.status(),
                input.lastWateredAt()
        );
        return greenhouseService.update(Long.parseLong(id), request);
    }

    /**
     * Полив теплицы.
     * Соответствует полю Mutation.wateringGreenhouse(id) в схеме.
     */
    @DgsMutation
    public GreenhouseResponse wateringGreenhouse(@InputArgument String id) {
        return greenhouseService.watering(Long.parseLong(id));
    }

    /**
     * Изменение статуса состояния почвы в теплице.
     * Соответствует полю Mutation.changeWateredStatus(id, newStatus) в схеме.
     */
    @DgsMutation
    public GreenhouseResponse changeWateredStatus(@InputArgument String id, @InputArgument WateredStatus newStatus) {
        return greenhouseService.changeWateredStatus(Long.parseLong(id), newStatus);
    }

    /**
     * Удаление теплицы.
     * Соответствует полю Mutation.deleteGreenhouse(id) в схеме.
     */
    @DgsMutation
    public boolean deleteGreenhouse(@InputArgument String id) {
        greenhouseService.delete(Long.parseLong(id));
        return true;
    }
}
