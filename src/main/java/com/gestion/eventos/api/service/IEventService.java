package com.gestion.eventos.api.service;

import com.gestion.eventos.api.domain.Event;
import com.gestion.eventos.api.dto.EventRequestDTO;
import com.gestion.eventos.api.dto.EventResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEventService {
    Page<EventResponseDTO> findAll(String name, Pageable pageable);
    Event save(EventRequestDTO eventRequestDTO);
    Event update(Long id, EventRequestDTO eventRequestDTO);
    Event findById(Long id);
    void deleteById(Long id);
    List<Event> getAllEventsAndTheirDetailsProblematic();
    List<Event> getAllEventsAndTheirDetailsOptimizedWithJoinFetch();
    List<Event> findAllEventsWithAllDetailsOptimized();
}
