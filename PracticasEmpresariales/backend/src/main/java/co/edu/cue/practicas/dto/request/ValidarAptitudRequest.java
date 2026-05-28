package co.edu.cue.practicas.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidarAptitudRequest {

    @Min(value = 1, message = "El numero de practica debe ser mayor o igual a 1")
    private int numeroPractica;

    @NotNull(message = "Debe indicar si el estudiante es apto o no")
    private Boolean apto;

    private String motivoNoApto;
}

