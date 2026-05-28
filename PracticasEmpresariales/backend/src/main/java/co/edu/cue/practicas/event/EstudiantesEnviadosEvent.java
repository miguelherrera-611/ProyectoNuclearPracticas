package co.edu.cue.practicas.event;

import co.edu.cue.practicas.model.entity.Estudiante;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class EstudiantesEnviadosEvent extends ApplicationEvent {

    private final List<Estudiante> estudiantes;

    public EstudiantesEnviadosEvent(Object source, List<Estudiante> estudiantes) {
        super(source);
        this.estudiantes = estudiantes;
    }
}

