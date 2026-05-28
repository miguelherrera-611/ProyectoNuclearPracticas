package co.edu.cue.practicas.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CrearEstudianteRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombre;

    @NotBlank(message = "La identificacion es obligatoria")
    private String identificacion;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "Formato de correo invalido")
    private String correo;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    @NotBlank(message = "El contacto de emergencia es obligatorio")
    private String contactoEmergencia;

    @NotNull(message = "El programa es obligatorio")
    private Long programaId;

    @NotNull(message = "La facultad es obligatoria")
    private Long facultadId;

    @Min(value = 1, message = "El semestre debe ser mayor o igual a 1")
    private int semestre;

    @Min(value = 0, message = "Los creditos aprobados no pueden ser negativos")
    private int creditosAprobados;

    @Min(value = 0, message = "El promedio academico no puede ser negativo")
    private double promedioAcademico;

    private boolean documentosBaseCompletos;

    private boolean hojaVidaValida;
}

