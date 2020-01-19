package me.jujin.demoinflearnrestapi.events;

import org.modelmapper.ModelMapper;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value="/api/events",produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController (EventRepository eventRepository,ModelMapper modelMapper,EventValidator eventValidator){
        this.eventRepository= eventRepository;
        this.modelMapper=modelMapper;
        this.eventValidator=eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto,errors);

        if(errors.hasErrors()){
            return ResponseEntity.badRequest().body(errors);
        }

        Event event = modelMapper.map(eventDto,Event.class);
        event.update();
        Event newEvent=this.eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder=linkTo(EventController.class).slash(newEvent.getId());
        EventResource eventResource=new EventResource(newEvent);
        eventResource.add(selfLinkBuilder.withRel("query-events"));
//        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        return ResponseEntity.created(selfLinkBuilder.toUri()).body(eventResource);
    }

}
