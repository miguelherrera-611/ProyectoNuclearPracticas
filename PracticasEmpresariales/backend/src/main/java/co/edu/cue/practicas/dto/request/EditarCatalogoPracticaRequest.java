package co.edu.cue.practicas.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditarCatalogoPracticaRequest {

    @Min(value = 1, message = "El numero de practica debe ser mayor o igual a 1")
    private int numeroPractica;

    @NotBlank(message = "El nombre de la practica es obligatorio")
    private String nombre;

    @NotBlank(message = "El nombre de la materia nucleo es obligatorio")
    private String materiaNucleoNombre;

    @NotBlank(message = "El codigo de la materia nucleo es obligatorio")
    private String materiaNucleoCodigo;

    @Min(value = 1, message = "El numero de cortes debe ser mayor o igual a 1")
    private int numeroCortesSeguimiento;

    @Min(value = 1, message = "La duracion en semanas debe ser mayor o igual a 1")
    private int duracionSemanas;

    @NotBlank(message = "Los documentos requeridos son obligatorios")
    private String documentosRequeridos;
}

