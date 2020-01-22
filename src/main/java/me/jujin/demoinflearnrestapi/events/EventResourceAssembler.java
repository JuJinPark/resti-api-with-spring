package me.jujin.demoinflearnrestapi.events;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

class EventRepresentationModelAssembler implements RepresentationModelAssembler {
    @Override
    public RepresentationModel toModel(Object event) {
        return new EventResource((Event) event);
    }
}
