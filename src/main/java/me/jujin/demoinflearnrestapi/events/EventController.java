package me.jujin.demoinflearnrestapi.events;

import me.jujin.demoinflearnrestapi.accounts.Account;
import me.jujin.demoinflearnrestapi.accounts.AccountAdapter;
import me.jujin.demoinflearnrestapi.accounts.CurrentUser;
import me.jujin.demoinflearnrestapi.common.ErrorResource;
import org.modelmapper.ModelMapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;

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


    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable,
                                      PagedResourcesAssembler<Event> assembler,
                                      @CurrentUser Account account
        ){

        Page<Event> page = this.eventRepository.findAll(pageable);
        var pagedResources = assembler.toModel(page,
                new EventRepresentationModelAssembler());

        pagedResources.add(new Link("/docs/index.html#resources-events-list").withRel("profile"));
       if(account != null){
           pagedResources.add(linkTo(EventController.class).withRel("create-event"));
       }
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id,
                                    @CurrentUser Account currentuser){

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

        Optional<Event> optionalEvent= this.eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        Event event=optionalEvent.get();
        EventResource eventResource=new EventResource(event);
        eventResource.add(new Link("/docs/index.html#resources-events-get").withRel("profile"));
        if(event.getManager().equals(currentuser)){
            eventResource.add(linkTo(EventController.class).slash(event.getId()).withRel("update-event"));

        }
        return ResponseEntity.ok(eventResource);


    }
    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser
    ) {
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto,errors);

        if(errors.hasErrors()){
            return  badRequest(errors);
        }

        Event event = modelMapper.map(eventDto,Event.class);
        event.update();
        event.setManager(currentUser);

        Event newEvent=eventRepository.save(event);
        WebMvcLinkBuilder selfLinkBuilder=linkTo(EventController.class).slash(newEvent.getId());
        EventResource eventResource=new EventResource(newEvent);
        eventResource.add(selfLinkBuilder.withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(new Link("/docs/index.html#resources-events-create").withRel("profile"));
        return ResponseEntity.created(selfLinkBuilder.toUri()).body(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors,
                                      @CurrentUser Account currentUser) {
        if(errors.hasErrors()){
            return badRequest(errors);
        }

        eventValidator.validate(eventDto,errors);

        if(errors.hasErrors()){
            return  badRequest(errors);
        }

        Optional<Event> optionalEvent= eventRepository.findById(id);
        if(optionalEvent.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Event existingEvent =optionalEvent.get();

        if(!existingEvent.getManager().equals(currentUser)){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
        modelMapper.map(eventDto,existingEvent);
        Event updatedEvent=eventRepository.save(existingEvent);
        EventResource eventResource=new EventResource(updatedEvent);
        eventResource.add(new Link("/docs/index.html#resources-events-update").withRel("profile"));

        return ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorResource(errors));
    }

}
