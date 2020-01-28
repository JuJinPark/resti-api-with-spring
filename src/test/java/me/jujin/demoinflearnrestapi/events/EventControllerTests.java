package me.jujin.demoinflearnrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.jujin.demoinflearnrestapi.common.RestDocsConfiguration;
import me.jujin.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.hibernate.validator.constraints.br.TituloEleitoral;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;


    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남에 D2 스타텁 팩토리")
                .build();



        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE+";charset=UTF-8"))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andDo(document("create-event",
                            links(
                                    linkWithRel("self").description("link to self"),
                                    linkWithRel("query-events").description("link to query events"),
                                    linkWithRel("update-event").description("link to update an existing event"),
                                    linkWithRel("profile").description("link to profile")
                            ),
                            requestHeaders(
                                    headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                            ),
                            requestFields(
                                    fieldWithPath("name").description("Name of new Event"),
                                    fieldWithPath("description").description("description of new Event"),
                                    fieldWithPath("beginEnrollmentDateTime").description("date time of begin Enrollment"),
                                    fieldWithPath("closeEnrollmentDateTime").description("date time of close Enrollment"),
                                    fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                    fieldWithPath("endEventDateTime").description("date time of end of event"),
                                    fieldWithPath("location").description("location of event"),
                                    fieldWithPath("basePrice").description("base price of event"),
                                    fieldWithPath("maxPrice").description("max price of event"),
                                    fieldWithPath("limitOfEnrollment").description("limit of enrollment")


                            ),
                            responseHeaders(
                                    headerWithName(HttpHeaders.LOCATION).description("location header"),
                                    headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")


                            ),
                            responseFields(
                                    fieldWithPath("id").description("id of new Event"),
                                    fieldWithPath("name").description("Name of new Event"),
                                    fieldWithPath("description").description("description of new Event"),
                                    fieldWithPath("beginEnrollmentDateTime").description("date time of begin Enrollment"),
                                    fieldWithPath("closeEnrollmentDateTime").description("date time of close Enrollment"),
                                    fieldWithPath("beginEventDateTime").description("date time of begin of event"),
                                    fieldWithPath("endEventDateTime").description("date time of end of event"),
                                    fieldWithPath("location").description("location of event"),
                                    fieldWithPath("basePrice").description("base price of event"),
                                    fieldWithPath("maxPrice").description("max price of event"),
                                    fieldWithPath("limitOfEnrollment").description("limit of enrollment"),
                                    fieldWithPath("offline").description("offline"),
                                    fieldWithPath("free").description("free"),
                                    fieldWithPath("eventStatus").description("status of event"),
                                    fieldWithPath("_links.self.href").description("link to self"),
                                    fieldWithPath("_links.query-events.href").description("link to query event list"),
                                    fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                    fieldWithPath("_links.profile.href").description("link to profile")

                            )

                        ))

        ;
    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남에 D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto))
            )
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .basePrice(200)
                .maxPrice(100)
                .limitOfEnrollment(100)
                .location("강남에 D2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())


        ;

    }


    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번쨰 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0,30).forEach(this::generateEvent);

        //when
        this.mockMvc.perform(get("/api/events")
                        .param("page","1")
                        .param("size","10")
                        .param("sort","name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document(("query-events")))

                //문서화 추가 링크 설명
        ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        Event event = this.generateEvent(100);

        //when
        this.mockMvc.perform(get("/api/events/{id}",event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))
                .andDo(print())
                ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을떄 404 응답받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("없는 이벤트를 업데이트 할시 404 응답받기")
    public void updateEvent404() throws Exception {

        Event event = this.generateEvent(200);

        EventDto dto=modelMapper.map(event,EventDto.class);


        mockMvc.perform(put("/api/events/1877")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @TestDescription("입렵값이 없이 업데이트 하는경우")
    public void updateEvent_Bad_Request() throws Exception{
        Event eventToUpdate = this.generateEvent(100);

        EventDto dto = new EventDto();
        mockMvc.perform(put("/api/events/{id}",eventToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    @TestDescription("업데이트 한 값이 도메인 로직검증에 실패하는 경우")
    public void updateEvent_Wrong_input() throws Exception{
        Event event = this.generateEvent(200);

        EventDto dto=modelMapper.map(event,EventDto.class);

        dto.setBasePrice(20000);
        dto.setMaxPrice(100);

        mockMvc.perform(put("/api/events/{id}",event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("content[0].objectName").exists())
//                .andExpect(jsonPath("content[0].defaultMessage").exists())
//                .andExpect(jsonPath("content[0].code").exists())
//                .andExpect(jsonPath("_links.index").exists())
                ;

    }

    @Test
    @TestDescription("권한이 충분치않아서 업데이트를 하지 못하는 경우")
    public void updateEvent_Not_Authorized() throws Exception {

    }

    @Test
    @TestDescription("정상적으로 업데이틑 할 수 있는 경우")
    public void updateEvent() throws Exception {
        Event eventToUpdate = this.generateEvent(200);
        String eventName="Update Event";
        EventDto dto=modelMapper.map(eventToUpdate,EventDto.class);
        dto.setName(eventName);

        mockMvc.perform(put("/api/events/{id}",eventToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        ;
    }




    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event"+index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,14,21))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,14,21))
                .endEventDateTime(LocalDateTime.of(2018,11,26,14,21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남에 D2 스타텁 팩토리")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

       return this.eventRepository.save(event);
    }
}
