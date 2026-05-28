package co.edu.cue.practicas.dto.request;

import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.EstadoPractica;
import lombok.Data;

@Data
public class FiltroEstudiantesRequest {

    private Long programaId;
    private Long facultadId;
    private Integer semestre;
    private EstadoEstudiante estadoEstudiante;
    private Integer numeroPractica;
    private EstadoPractica estadoPractica;
    private Long docenteAsesorId;
    private String texto;
}

