package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.model.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListadoEstudiantesStrategyResolver {

    private final DtiFiltroEstudiantesStrategy dtiStrategy;
    private final CoordinacionFiltroEstudiantesStrategy coordinacionStrategy;
    private final CoordinadorFiltroEstudiantesStrategy coordinadorStrategy;
    private final DocenteAsesorFiltroEstudiantesStrategy docenteAsesorStrategy;
    private final TutorEmpresarialFiltroEstudiantesStrategy tutorEmpresarialStrategy;
    private final EstudianteFiltroEstudiantesStrategy estudianteStrategy;

    public FiltroEstudiantesStrategy resolver(Rol rol) {
        return switch (rol) {
            case ADMIN_DTI -> dtiStrategy;
            case COORDINACION_ACADEMICA -> coordinacionStrategy;
            case COORDINADOR_PRACTICAS -> coordinadorStrategy;
            case DOCENTE_ASESOR -> docenteAsesorStrategy;
            case TUTOR_EMPRESARIAL -> tutorEmpresarialStrategy;
            case ESTUDIANTE -> estudianteStrategy;
            case DIRECCION -> dtiStrategy;
        };
    }
}

