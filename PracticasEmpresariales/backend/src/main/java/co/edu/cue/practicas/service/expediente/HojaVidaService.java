package co.edu.cue.practicas.service.expediente;

import co.edu.cue.practicas.dto.request.CrearHojaVidaRequest;
import co.edu.cue.practicas.dto.response.HojaVidaResponse;
import co.edu.cue.practicas.exception.RecursoNoEncontradoException;
import co.edu.cue.practicas.model.entity.Estudiante;
import co.edu.cue.practicas.model.entity.HojaVidaVersion;
import co.edu.cue.practicas.model.enums.EstadoHojaVida;
import co.edu.cue.practicas.repository.estudiante.EstudianteRepository;
import co.edu.cue.practicas.repository.estudiante.HojaVidaRepository;
import co.edu.cue.practicas.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HojaVidaService implements HojaVidaWriter {

    private final EstudianteRepository estudianteRepository;
    private final HojaVidaRepository hojaVidaRepository;
    private final ExpedienteConsultaCacheProxy cacheProxy;

    @Override
    @Transactional
    public HojaVidaResponse registrarVersion(Long estudianteId, CrearHojaVidaRequest request, CustomUserDetails actor) {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado: " + estudianteId));

        int siguienteVersion = hojaVidaRepository.findTopByEstudiante_IdOrderByVersionDesc(estudianteId)
                .map(hv -> hv.getVersion() + 1)
                .orElse(1);

        HojaVidaVersion nueva = HojaVidaVersion.builder()
                .estudiante(estudiante)
                .version(siguienteVersion)
                .urlArchivo(request.getUrlArchivo())
                .estado(EstadoHojaVida.PENDIENTE)
                .build();

        estudiante.setHojaVidaValida(false);
        estudiante.getHojasVida().add(nueva);
        hojaVidaRepository.save(nueva);

        cacheProxy.invalidar(estudianteId);
        return HojaVidaResponse.desde(nueva);
    }
}

