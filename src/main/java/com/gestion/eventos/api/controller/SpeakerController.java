package com.gestion.eventos.api.controller;

import com.gestion.eventos.api.domain.Speaker;
import com.gestion.eventos.api.dto.SpeakerRequestDto;
import com.gestion.eventos.api.dto.SpeakerResponseDto;
import com.gestion.eventos.api.mapper.SpeakerMapper;
import com.gestion.eventos.api.service.ISpeakerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/speakers")
public class SpeakerController {

    private final ISpeakerService speakerService;
    private final SpeakerMapper speakerMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SpeakerResponseDto> createSpeaker(
            @Valid @RequestBody SpeakerRequestDto speakerRequestDto) {

        Speaker speaker = speakerService.save(speakerRequestDto);

        return new ResponseEntity<>(speakerMapper.toDto(speaker), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<SpeakerResponseDto> getSpeakerById(@PathVariable Long id) {
        Speaker speaker = speakerService.findById(id);
        return ResponseEntity.ok(speakerMapper.toDto(speaker));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<SpeakerResponseDto>> findAll() {
        return ResponseEntity.ok(speakerMapper.toResponseDtoList(speakerService.findAll()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SpeakerResponseDto> updateSpaker(@PathVariable Long id,
                                                           @Valid @RequestBody SpeakerRequestDto speakerRequestDto) {

        Speaker updatedSpeaker = speakerService.update(id, speakerRequestDto);
        return ResponseEntity.ok(speakerMapper.toDto(updatedSpeaker));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteSpeakerById(@PathVariable Long id) {
        speakerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
