package com.gestion.eventos.api.dto;

import com.gestion.eventos.api.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String username;
    private String email;
    private List<Role> roles;
    private List<EventResponseDTO> attendedEvents;

}
