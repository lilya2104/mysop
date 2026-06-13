package edu.rutmiit.demo.demorest.assemblers;

import edu.rutmiit.demo.demorest.controllers.GreenhouseController;
import edu.rutmiit.demo.plantsapicontract.dto.GreenhouseResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GreenhouseModelAssembler implements RepresentationModelAssembler<GreenhouseResponse, EntityModel<GreenhouseResponse>> {
    @Override
    public EntityModel<GreenhouseResponse> toModel(GreenhouseResponse greenhouse) {
        return EntityModel.of(greenhouse,
                linkTo(methodOn(GreenhouseController.class).getGreenhouseById(greenhouse.getId())).withSelfRel(),
                linkTo(methodOn(GreenhouseController.class).getAllGreenhouses(null,0, 20)).withRel("collection")
        ).add(linkTo(GreenhouseController.class).slash(greenhouse.getId()).withRel("delete")
        );
    }
}
