package co.edu.cue.practicas.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PazYSalvoResponse {
    private Long instanciaPracticaId;
    private String codigo;
    private String contenido;
    private LocalDateTime generadoEn;
}
