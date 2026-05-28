package co.edu.cue.practicas.repository.practica;

import co.edu.cue.practicas.model.entity.PracticaInstancia;
import co.edu.cue.practicas.model.enums.EstadoPractica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface PracticaInstanciaRepository extends JpaRepository<PracticaInstancia, Long> {

    long countByCatalogoPractica_IdAndEstadoIn(Long catalogoId, Collection<EstadoPractica> estados);

    Optional<PracticaInstancia> findByExpediente_IdAndNumeroPractica(Long expedienteId, int numeroPractica);
}

