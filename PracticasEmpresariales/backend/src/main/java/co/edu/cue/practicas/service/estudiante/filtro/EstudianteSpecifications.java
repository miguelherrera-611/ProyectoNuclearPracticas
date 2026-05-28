package co.edu.cue.practicas.service.estudiante.filtro;

import co.edu.cue.practicas.dto.request.FiltroEstudiantesRequest;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.JoinType;

public final class EstudianteSpecifications {

    private EstudianteSpecifications() {}

    public static Specification<Estudiante> construir(FiltroEstudiantesRequest filtro) {
        Specification<Estudiante> spec = Specification.where(null);

        if (filtro.getProgramaId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("usuario").get("programa").get("id"), filtro.getProgramaId()));
        }
        if (filtro.getFacultadId() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("usuario").get("programa").get("facultad").get("id"), filtro.getFacultadId()));
        }
        if (filtro.getSemestre() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("semestre"), filtro.getSemestre()));
        }
        if (filtro.getEstadoEstudiante() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("usuario").get("estadoEstudiante"), filtro.getEstadoEstudiante()));
        }
        if (filtro.getNumeroPractica() != null || filtro.getEstadoPractica() != null || filtro.getDocenteAsesorId() != null) {
            spec = spec.and((root, query, cb) -> {
                query.distinct(true);
                var expediente = root.join("expediente", JoinType.LEFT);
                var practicas = expediente.join("practicas", JoinType.LEFT);

                var predicate = cb.conjunction();
                if (filtro.getNumeroPractica() != null) {
                    predicate = cb.and(predicate, cb.equal(practicas.get("numeroPractica"), filtro.getNumeroPractica()));
                }
                if (filtro.getEstadoPractica() != null) {
                    predicate = cb.and(predicate, cb.equal(practicas.get("estado"), filtro.getEstadoPractica()));
                }
                if (filtro.getDocenteAsesorId() != null) {
                    predicate = cb.and(predicate, cb.equal(practicas.get("docenteAsesor").get("id"), filtro.getDocenteAsesorId()));
                }
                return predicate;
            });
        }
        if (filtro.getTexto() != null && !filtro.getTexto().isBlank()) {
            String like = "%" + filtro.getTexto().trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("usuario").get("nombre")), like),
                    cb.like(cb.lower(root.get("identificacion")), like)
            ));
        }
        return spec;
    }

    public static Specification<Estudiante> porPrograma(Long programaId) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("programa").get("id"), programaId);
    }

    public static Specification<Estudiante> porEstado(EstadoEstudiante estado) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("estadoEstudiante"), estado);
    }

    public static Specification<Estudiante> porFacultad(Long facultadId) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("programa").get("facultad").get("id"), facultadId);
    }

    public static Specification<Estudiante> enviadoProceso() {
        return (root, query, cb) -> cb.isTrue(root.get("enviadoProceso"));
    }

    public static Specification<Estudiante> porUsuarioId(Long usuarioId) {
        return (root, query, cb) -> cb.equal(root.get("usuario").get("id"), usuarioId);
    }

    public static Specification<Estudiante> porDocenteAsesor(Long docenteId) {
        return (root, query, cb) -> {
            query.distinct(true);
            var expediente = root.join("expediente", JoinType.LEFT);
            var practicas = expediente.join("practicas", JoinType.LEFT);
            return cb.equal(practicas.get("docenteAsesor").get("id"), docenteId);
        };
    }

    public static Specification<Estudiante> porTutorEmpresarial(Long tutorId) {
        return (root, query, cb) -> {
            query.distinct(true);
            var expediente = root.join("expediente", JoinType.LEFT);
            var practicas = expediente.join("practicas", JoinType.LEFT);
            return cb.equal(practicas.get("tutorEmpresarial").get("id"), tutorId);
        };
    }
}

