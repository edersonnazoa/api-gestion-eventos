package com.gestion.eventos.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryDto {

    private Long id;
    private String name;
    private LocalDate date;
    private String location;

}
