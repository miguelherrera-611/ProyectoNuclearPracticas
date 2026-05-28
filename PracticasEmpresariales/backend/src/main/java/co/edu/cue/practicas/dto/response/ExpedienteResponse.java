package co.edu.cue.practicas.dto.response;

import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExpedienteResponse {

    private Long estudianteId;
    private String nombre;
    private String identificacion;
    private String programaNombre;
    private String facultadNombre;
    private int semestre;
    private EstadoEstudiante estadoEstudiante;
    private boolean hojaVidaValida;
    private List<HojaVidaResponse> hojasVida;
    private List<PracticaResumenResponse> practicas;
}

