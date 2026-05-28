package co.edu.cue.practicas.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class EnviarAProcesoRequest {

    @NotEmpty(message = "Debe indicar estudiantes para enviar")
    private List<Long> estudianteIds;
}

