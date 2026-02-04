package com.gestion.eventos.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SpeakerRequestDto {

    @NotBlank(message = "El nombre del orador no puede estar vacio")
    @Size(max = 100, message = "El nombre del orador no puede exceder los 100 caracteres")
    private String name;

    @NotBlank(message = "El email del orador no puede estar vacio")
    @Email(message = "El formato del email no es valido")
    @Size(max = 100, message = "El email del orador no puede exceder los 100 caracteres")
    private String email;

    @Size(max = 500, message = "La biografia no puede exceder los 500 caracteres")
    private String bio;

}
