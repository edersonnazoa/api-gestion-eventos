package com.gestion.eventos.api.service;

import com.gestion.eventos.api.domain.Category;
import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.EventRequestDTO;
import com.gestion.eventos.api.dto.EventResponseDTO;
import com.gestion.eventos.api.exception.ResourceNotFoundException;
import com.gestion.eventos.api.mapper.EventMapper;
import com.gestion.eventos.api.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SpeakerService speakerService;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventRequestDTO eventRequestDTO;
    private EventResponseDTO eventResponseDTO;
    private Category category;
    private Speaker speaker1, speaker2;
    private Pageable pageable;

    @BeforeEach
    void setUp(){
        // Inicializar datos de prueba
        category = new Category(1L, "Conferencia", "Descripción de conferencia");
        speaker1 = new Speaker(10L, "John Doe", "john@example.com", "Bio de John", new HashSet<>());
        speaker2 = new Speaker(11L, "Jane Smith", "jane@example.com", "Bio de Jane", new HashSet<>());

        event = new Event();
        event.setId(1L);
        event.setName("Spring Boot Conf");
        event.setDate(LocalDate.of(2023, 10, 26));
        event.setLocation("Online");
        event.setCategory(category);
        event.getSpeakers().add(speaker1);
        event.getSpeakers().add(speaker2);

        eventRequestDTO = new EventRequestDTO();
        eventRequestDTO.setName("Spring Boot Conf");
        eventRequestDTO.setDate(LocalDate.of(2023, 10, 26));
        eventRequestDTO.setLocation("Online");
        eventRequestDTO.setCategoryId(1L);
        eventRequestDTO.setSpeakersIds(new HashSet<>(Set.of(10L, 11L)));

        eventResponseDTO = new EventResponseDTO();
        eventResponseDTO.setId(1L);
        eventResponseDTO.setName("Spring Boot Conf");
        eventResponseDTO.setDate(LocalDate.of(2023, 10, 26));
        eventResponseDTO.setLocation("Online");
        eventResponseDTO.setCategoryId(1L);
        eventResponseDTO.setCategoryName("Conferencia");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Debe retornar un Evento cuando el ID existe")
    void shouldReturnEventWhenIdExists() {

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        Event foundEvent = eventService.findById(1L);

        assertNotNull(foundEvent);
        assertEquals(event.getId(), foundEvent.getId());

        verify(eventRepository, times(1)).findById(1L);

    }

    @Test
    @DisplayName("Debe lanzar un ResourceNotFoundException cuando el ID no existe")
    void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){

        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class, () -> {
                    eventService.findById(99L);
                }
        );

        assertEquals("Evento no encontrado con el ID 99", thrown.getMessage());

        verify(eventRepository, times(1)).findById(99L);

    }

    @Test
    @DisplayName("Debe guardar un Evento exitosamente con categoria y oradores")
    void shouldSaveEventSuccessfullyWithCategoryAndSpeakers(){

        Event eventWithoutId = new Event();
        eventWithoutId.setName(eventRequestDTO.getName());
        eventWithoutId.setDate(eventRequestDTO.getDate());
        eventWithoutId.setLocation(eventRequestDTO.getLocation());

        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(eventWithoutId);

        when(categoryService.findById(eventRequestDTO.getCategoryId())).thenReturn(category);
        when(speakerService.findById(10L)).thenReturn(speaker1);
        when(speakerService.findById(11L)).thenReturn(speaker2);

        when(eventRepository.save(any(Event.class))).thenAnswer(
                invocation -> {
                    Event savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1L);
                    return savedEvent;
                });

        Event savedEvent = eventService.save(eventRequestDTO);

        assertNotNull(savedEvent);
        assertEquals(1L, savedEvent.getId());
        assertEquals(eventRequestDTO.getName(), savedEvent.getName());
        assertEquals(category, savedEvent.getCategory());
        assertEquals(2, savedEvent.getSpeakers().size());

        assertTrue(savedEvent.getSpeakers().contains(speaker1));
        assertTrue(savedEvent.getSpeakers().contains(speaker2));

        verify(eventMapper, times(1)).toEntity(eventRequestDTO);
        verify(categoryService, times(1)).findById(eventRequestDTO.getCategoryId());
        verify(speakerService, times(1)).findById(10L);
        verify(speakerService, times(1)).findById(11L);
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Debe guardar un Evento exitosamente sin oradores")
    void shouldSaveEventSuccessfullyWithoutSpeakers() {

        eventRequestDTO.setSpeakersIds(null);

        Event eventWithoutId = new Event();
        eventWithoutId.setName(eventRequestDTO.getName());
        eventWithoutId.setLocation(eventRequestDTO.getLocation());
        eventWithoutId.setDate(eventRequestDTO.getDate());

        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(eventWithoutId);

        when(categoryService.findById(eventRequestDTO.getCategoryId())).thenReturn(category);

        when(eventRepository.save(any(Event.class))).thenAnswer(
                invocation -> {
                    Event savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1L);
                    return savedEvent;
                }
        );

        Event savedEvent = eventService.save(eventRequestDTO);

        assertNotNull(savedEvent);
        assertEquals(1L, savedEvent.getId());
        assertEquals(eventRequestDTO.getName(), savedEvent.getName());
        assertEquals(category, savedEvent.getCategory());
        assertTrue(savedEvent.getSpeakers().isEmpty());

        verify(eventMapper, times(1)).toEntity(eventRequestDTO);
        verify(categoryService, times(1)).findById(eventRequestDTO.getCategoryId());
        verify(speakerService, never()).findById(anyLong());
        verify(eventRepository, times(1)).save(any(Event.class));

    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si la categoria no existe al guardar")
    void shouldThrowResourceNotFoundExceptionWhenCategoryNotFoundOnSave() {

        Event eventWithoutId = new Event();

        when(eventMapper.toEntity(any(EventRequestDTO.class))).thenReturn(eventWithoutId);

        when(categoryService.findById(anyLong())).thenThrow(
                new ResourceNotFoundException("Categoria no encontrada con el id: "
                        + eventRequestDTO.getCategoryId())
        );

        ResourceNotFoundException thron = assertThrows(
                ResourceNotFoundException.class, () -> {
                    eventService.save(eventRequestDTO);
                }
        );

        assertEquals("Categoria no encontrada con el id: "
                        + eventRequestDTO.getCategoryId(), thron.getMessage());

        verify(eventRepository, never()).save(any(Event.class));

    }

    @Test
    @DisplayName("Debe retornar una pagina de eventos sin filtros de nombre")
    void shouldReturnPageOfEventsWithoutNameFilter() {

        List<Event> events = Collections.singletonList(event);
        Page<Event> eventPage = new PageImpl<>(events, pageable, 1);

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDTO);

        Page<EventResponseDTO> result = eventService.findAll(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(eventResponseDTO, result.getContent().getFirst());

        verify(eventRepository, times(1)).findAll(pageable);
        verify(eventRepository, never()).findByNameContainingIgnoreCase(anyString(), any(Pageable.class));
        verify(eventMapper, times(1)).toResponseDto(event);

    }

    @Test
    @DisplayName("Debe retornar una pagina de eventos con filtros de nombre")
    void shouldReturnPageOfEventsWithNameFilter() {

        String filterName = "Spring";

        List<Event> events = Collections.singletonList(event);
        Page<Event> eventPage = new PageImpl<>(events, pageable, 1);

        when(eventRepository.findByNameContainingIgnoreCase(filterName, pageable)).thenReturn(eventPage);

        when(eventMapper.toResponseDto(any(Event.class))).thenReturn(eventResponseDTO);

        Page<EventResponseDTO> result = eventService.findAll(filterName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(eventResponseDTO, result.getContent().getFirst());

        verify(eventRepository, times(1)).findByNameContainingIgnoreCase(filterName, pageable);
        verify(eventRepository, never()).findAll(any(Pageable.class));
        verify(eventMapper, times(1)).toResponseDto(event);

    }


}