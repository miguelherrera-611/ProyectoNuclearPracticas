package co.edu.cue.practicas.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearHojaVidaRequest {

    @NotBlank(message = "La URL del archivo es obligatoria")
    private String urlArchivo;
}

