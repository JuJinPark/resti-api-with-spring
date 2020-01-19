package me.jujin.demoinflearnrestapi.events;


import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class EventResource extends EntityModel {
//EntityModel    @JsonUnwrapped
//    private Event event;

    public EventResource(Event event, Link... links){
       super(event,links);
       add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
//event
//    public Event getEvent(){
//        return this.event;
//    }
}
