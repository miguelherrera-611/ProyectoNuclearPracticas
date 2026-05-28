package co.edu.cue.practicas.dto.response;

import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EstudianteResponse {

    private Long id;
    private Long usuarioId;
    private String nombre;
    private String correo;
    private String telefono;
    private String identificacion;
    private String contactoEmergencia;
    private Long programaId;
    private String programaNombre;
    private Long facultadId;
    private String facultadNombre;
    private int semestre;
    private int creditosAprobados;
    private double promedioAcademico;
    private boolean documentosBaseCompletos;
    private boolean hojaVidaValida;
    private EstadoEstudiante estadoEstudiante;
    private String motivoNoApto;
    private boolean enviadoProceso;
    private boolean activo;
    private LocalDateTime creadoEn;

    public static EstudianteResponse desde(Estudiante e) {
        var u = e.getUsuario();
        return EstudianteResponse.builder()
                .id(e.getId())
                .usuarioId(u.getId())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .telefono(u.getTelefono())
                .identificacion(e.getIdentificacion())
                .contactoEmergencia(e.getContactoEmergencia())
                .programaId(u.getPrograma() != null ? u.getPrograma().getId() : null)
                .programaNombre(u.getPrograma() != null ? u.getPrograma().getNombre() : null)
                .facultadId(u.getFacultad() != null ? u.getFacultad().getId() : null)
                .facultadNombre(u.getFacultad() != null ? u.getFacultad().getNombre() : null)
                .semestre(e.getSemestre())
                .creditosAprobados(e.getCreditosAprobados())
                .promedioAcademico(e.getPromedioAcademico())
                .documentosBaseCompletos(e.isDocumentosBaseCompletos())
                .hojaVidaValida(e.isHojaVidaValida())
                .estadoEstudiante(u.getEstadoEstudiante())
                .motivoNoApto(u.getMotivoNoApto())
                .enviadoProceso(e.isEnviadoProceso())
                .activo(u.isActivo())
                .creadoEn(e.getCreadoEn())
                .build();
    }
}

