package com.gestion.eventos.api.service;

import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.SpeakerRequestDto;

import java.util.List;

public interface ISpeakerService {

    List<Speaker> findAll();
    Speaker save(SpeakerRequestDto speakerRequestDto);
    Speaker findById(Long id);
    Speaker update(Long id, SpeakerRequestDto speakerRequestDto);
    void deleteById(Long id);

}
