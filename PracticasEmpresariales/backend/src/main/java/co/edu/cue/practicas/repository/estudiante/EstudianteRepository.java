package co.edu.cue.practicas.repository.estudiante;

import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long>, JpaSpecificationExecutor<Estudiante> {

    boolean existsByIdentificacion(String identificacion);

    Optional<Estudiante> findByUsuario_Id(Long usuarioId);

    @Query("SELECT e FROM Estudiante e WHERE e.usuario.rol = :rol AND e.usuario.estadoEstudiante = :estado AND e.usuario.programa.facultad.id = :facultadId")
    Page<Estudiante> findPorEstadoYFacultad(
            @Param("rol") Rol rol,
            @Param("estado") EstadoEstudiante estado,
            @Param("facultadId") Long facultadId,
            Pageable pageable);

    Page<Estudiante> findByUsuario_RolAndUsuario_EstadoEstudianteAndUsuario_Programa_Id(
            Rol rol, EstadoEstudiante estado, Long programaId, Pageable pageable);
}

