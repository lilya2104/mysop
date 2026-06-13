package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.demorest.assemblers.GreenhouseModelAssembler;
import edu.rutmiit.demo.demorest.service.GreenhouseService;
import edu.rutmiit.demo.plantsapicontract.diff.WateredStatus;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseRequest;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import edu.rutmiit.demo.plantsapicontract.dto.PagedResponse;
import edu.rutmiit.demo.plantsapicontract.dto.PatchGreenhouseRequest;
import edu.rutmiit.demo.plantsapicontract.endpoints.GreenhouseApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreenhouseController implements GreenhouseApi {

    private final GreenhouseService greenhouseService;
    private final GreenhouseModelAssembler greenhouseModelAssembler;
    private final PagedResourcesAssembler<GreenhouseResponse> pagedGreenhousesAssembler;

    public GreenhouseController(GreenhouseService greenhouseService,
                                GreenhouseModelAssembler greenhouseModelAssembler,
                                PagedResourcesAssembler<GreenhouseResponse> pagedGreenhousesAssembler) {
        this.greenhouseService = greenhouseService;
        this.greenhouseModelAssembler = greenhouseModelAssembler;
        this.pagedGreenhousesAssembler = pagedGreenhousesAssembler;
    }

    @Override
    public PagedModel<EntityModel<GreenhouseResponse>> getAllGreenhouses(String searchPlantName, int page, int size) {
        PagedResponse<GreenhouseResponse> paged = greenhouseService.findAll(searchPlantName, page, size);
        Page<GreenhouseResponse> springPage = new PageImpl<>(
                paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                paged.totalElements()
        );
        return pagedGreenhousesAssembler.toModel(springPage, greenhouseModelAssembler);
    }

    @Override
    public EntityModel<GreenhouseResponse> getGreenhouseById(Long id) {
        return greenhouseModelAssembler.toModel(greenhouseService.findById(id));
    }

    @Override
    public PagedModel<EntityModel<GreenhouseResponse>> searchByPlantName(String name, int page, int size) {
        PagedResponse<GreenhouseResponse> paged = greenhouseService.searchByPlantName(name, page, size);
        Page<GreenhouseResponse> springPage = new PageImpl<>(
                paged.content(),
                PageRequest.of(paged.pageNumber(), paged.pageSize()),
                paged.totalElements()
        );
        return pagedGreenhousesAssembler.toModel(springPage, greenhouseModelAssembler);
    }

    @Override
    public ResponseEntity<EntityModel<GreenhouseResponse>> createGreenhouse(GreenhouseRequest request) {
        GreenhouseResponse created = greenhouseService.create(request);
        EntityModel<GreenhouseResponse> model = greenhouseModelAssembler.toModel(created);
        return ResponseEntity
                .created(model.getRequiredLink("self").toUri())
                .body(model);
    }

    @Override
    public EntityModel<GreenhouseResponse> updateGreenhouse(Long id, GreenhouseRequest request) {
        return greenhouseModelAssembler.toModel(greenhouseService.update(id, request));
    }

    @Override
    public EntityModel<GreenhouseResponse> patchGreenhouse(Long id, PatchGreenhouseRequest request) {
        return greenhouseModelAssembler.toModel(greenhouseService.patchGreenhouse(id, request));
    }

    @Override
    public EntityModel<GreenhouseResponse> watering(Long id) {
        return greenhouseModelAssembler.toModel(greenhouseService.watering(id));
    }

    @Override
    public EntityModel<GreenhouseResponse> changeWateredStatus(Long id, WateredStatus newStatus) {
        return greenhouseModelAssembler.toModel(greenhouseService.changeWateredStatus(id, newStatus));
    }
    @Override
    public void deleteGreenhouse(Long id) {
        greenhouseService.delete(id);
    }
}
