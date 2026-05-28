package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.response.ExpedienteResponse;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.dto.response.PracticaResumenResponse;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;

import java.util.ArrayList;
import java.util.List;

public class ExpedienteHistoricoBuilder {

    private Long estudianteId;
    private String nombre;
    private String identificacion;
    private String programaNombre;
    private String facultadNombre;
    private int semestre;
    private EstadoEstudiante estadoEstudiante;
    private boolean hojaVidaValida;
    private final List<HojaVidaResponse> hojasVida = new ArrayList<>();
    private final List<PracticaResumenResponse> practicas = new ArrayList<>();

    public static ExpedienteHistoricoBuilder nuevo() {
        return new ExpedienteHistoricoBuilder();
    }

    public ExpedienteHistoricoBuilder conDatosBasicos(Long estudianteId, String nombre, String identificacion, String programaNombre,
                                                     String facultadNombre, int semestre, EstadoEstudiante estadoEstudiante, boolean hojaVidaValida) {
        this.estudianteId = estudianteId;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.programaNombre = programaNombre;
        this.facultadNombre = facultadNombre;
        this.semestre = semestre;
        this.estadoEstudiante = estadoEstudiante;
        this.hojaVidaValida = hojaVidaValida;
        return this;
    }

    public ExpedienteHistoricoBuilder agregarHojaVida(HojaVidaResponse hojaVida) {
        this.hojasVida.add(hojaVida);
        return this;
    }

    public ExpedienteHistoricoBuilder agregarPractica(PracticaResumenResponse practica) {
        this.practicas.add(practica);
        return this;
    }

    public ExpedienteResponse construir() {
        return ExpedienteResponse.builder()
                .estudianteId(estudianteId)
                .nombre(nombre)
                .identificacion(identificacion)
                .programaNombre(programaNombre)
                .facultadNombre(facultadNombre)
                .semestre(semestre)
                .estadoEstudiante(estadoEstudiante)
                .hojaVidaValida(hojaVidaValida)
                .hojasVida(hojasVida)
                .practicas(practicas)
                .build();
    }
}

