package co.edu.cue.practicas.event;

import co.edu.cue.practicas.model.entity.Estudiante;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AptitudMarcadaEvent extends ApplicationEvent {

    private final Estudiante estudiante;
    private final boolean apto;

    public AptitudMarcadaEvent(Object source, Estudiante estudiante, boolean apto) {
        super(source);
        this.estudiante = estudiante;
        this.apto = apto;
    }
}

