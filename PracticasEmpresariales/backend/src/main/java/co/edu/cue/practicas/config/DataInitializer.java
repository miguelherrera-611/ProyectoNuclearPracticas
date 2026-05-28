package co.edu.cue.practicas.config;

import co.edu.cue.practicas.model.entity.Usuario;
import co.edu.cue.practicas.model.enums.EstadoCuenta;
import co.edu.cue.practicas.model.enums.EstadoEstudiante;
import co.edu.cue.practicas.model.enums.Rol;
import co.edu.cue.practicas.repository.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Siembra el primer Administrador DTI al arrancar el sistema.
 * Solo crea el usuario si no existe ninguno con ese correo,
 * por lo que es seguro ejecutarlo en cada arranque sin duplicar el usuario.
 *
 * El correo y la contraseña inicial se leen desde application.properties:
 *   app.init.admin.correo    → correo del primer DTI
 *   app.init.admin.password  → contraseña inicial (cámbiala antes de producción)
 *
 * Al arrancar, imprime en consola un aviso visual para que el desarrollador
 * sepa que el usuario fue creado y recuerde cambiar la contraseña.
 *
 * Además de DTI, siembra un usuario de prueba por cada rol del sistema
 * para facilitar el desarrollo y las pruebas sin depender del flujo de registro:
 *   ADMIN_DTI               dti@cue.edu.co           Admin2026!
 *   COORDINACION_ACADEMICA  coordinacion@cue.edu.co  Coord2026!
 *   COORDINADOR_PRACTICAS   practicas@cue.edu.co     Pract2026!
 *   DOCENTE_ASESOR          docente@cue.edu.co       Docente2026!
 *   TUTOR_EMPRESARIAL       tutor@empresa.com        Tutor2026!
 *   ESTUDIANTE              estudiante@cue.edu.co    Estud2026!
 *   DIRECCION               direccion@cue.edu.co     Direc2026!
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // Acceso a la tabla de usuarios para verificar si el DTI inicial ya existe
    private final UsuarioRepository usuarioRepository;

    // Encripta la contraseña antes de guardarla; nunca se guarda en texto plano
    private final PasswordEncoder passwordEncoder;

    // Correo del administrador DTI inicial, configurable en application.properties
    @Value("${app.init.admin.correo}")
    private String correoDti;

    // Contraseña inicial del DTI, configurable en application.properties
    // IMPORTANTE: cambiar este valor antes de desplegar en producción
    @Value("${app.init.admin.password}")
    private String passwordDti;

    /**
     * Se ejecuta automáticamente al iniciar la aplicación (CommandLineRunner).
     * Crea el usuario DTI inicial y los usuarios de prueba por cada rol,
     * solo si no existen ya en la base de datos.
     * Con MySQL los datos persisten entre reinicios, así que solo se crean la primera vez.
     */
    @Override
    public void run(String... args) {

        // Construimos la lista de todos los usuarios semilla (uno por rol)
        List<UsuarioSeed> seeds = buildSeeds();
        int creados = 0;

        // Verificamos si ya existe cada usuario antes de crearlo (evitamos duplicados)
        for (UsuarioSeed seed : seeds) {
            if (!usuarioRepository.existsByCorreo(seed.correo())) {
                usuarioRepository.save(seed.toEntity(passwordEncoder));
                creados++;
            }
        }

        // Aviso visible en consola solo cuando se crea al menos un usuario nuevo
        if (creados > 0) {
            log.info("=======================================================");
            log.info("  USUARIOS DE PRUEBA CREADOS: {}", creados);
            log.info("  ADMIN_DTI              → {}  /  {}", correoDti, passwordDti);
            log.info("  COORDINACION_ACADEMICA → coordinacion@cue.edu.co  /  Coord2026!");
            log.info("  COORDINADOR_PRACTICAS  → practicas@cue.edu.co     /  Pract2026!");
            log.info("  DOCENTE_ASESOR         → docente@cue.edu.co       /  Docente2026!");
            log.info("  TUTOR_EMPRESARIAL      → tutor@empresa.com        /  Tutor2026!");
            log.info("  ESTUDIANTE             → estudiante@cue.edu.co    /  Estud2026!");
            log.info("  DIRECCION              → direccion@cue.edu.co     /  Direc2026!");
            log.info("  IMPORTANTE: cambia las contraseñas antes de producción");
            log.info("=======================================================");
        }
    }

    /**
     * Define los usuarios semilla para cada rol del sistema.
     * Todos tienen primerIngreso=false y estadoCuenta=ACTIVO para que
     * los testers puedan entrar directamente sin flujo de cambio de contraseña.
     */
    private List<UsuarioSeed> buildSeeds() {
        return List.of(
            // ADMIN_DTI: credenciales desde application.properties para que sean configurables
            new UsuarioSeed("Administrador DTI",        correoDti,                    passwordDti,    Rol.ADMIN_DTI,             null,         null, null),
            // Coordinación Académica: ve facultades, gestiona estudiantes y catálogo
            new UsuarioSeed("Coordinacion Academica",   "coordinacion@cue.edu.co",   "Coord2026!",   Rol.COORDINACION_ACADEMICA, null,         null, null),
            // Coordinador de Prácticas: gestiona empresas, vacantes, tutores y asignaciones
            new UsuarioSeed("Coordinador de Practicas", "practicas@cue.edu.co",      "Pract2026!",   Rol.COORDINADOR_PRACTICAS,  null,         null, null),
            // Docente Asesor: acompaña al estudiante durante la práctica
            new UsuarioSeed("Docente Asesor",           "docente@cue.edu.co",        "Docente2026!", Rol.DOCENTE_ASESOR,         null,         null, null),
            // Tutor Empresarial: supervisor de la empresa donde hace práctica el estudiante
            new UsuarioSeed("Tutor Empresarial",        "tutor@empresa.com",         "Tutor2026!",   Rol.TUTOR_EMPRESARIAL,      null,         null, null),
            // Estudiante: usuario con campos extra (identificación, semestre, contacto emergencia)
            new UsuarioSeed("Estudiante Prueba",        "estudiante@cue.edu.co",     "Estud2026!",   Rol.ESTUDIANTE,             "1234567890", 8,    "Ana Garcia - 3001234567"),
            // Dirección: rol directivo con vista de reportes y aprobaciones
            new UsuarioSeed("Direccion Academica",      "direccion@cue.edu.co",      "Direc2026!",   Rol.DIRECCION,              null,         null, null)
        );
    }

    /**
     * Record interno que representa los datos de un usuario a sembrar.
     * Usa record para mantener la inmutabilidad y reducir el boilerplate.
     */
    private record UsuarioSeed(
            String nombre,
            String correo,
            String password,
            Rol rol,
            String identificacion,
            Integer semestre,
            String contactoEmergencia
    ) {
        /**
         * Convierte el seed en una entidad Usuario lista para persistir.
         * primerIngreso=false y estadoCuenta=ACTIVO para que no pida cambio de clave.
         * El ESTUDIANTE se marca APTO para poder probarlo en el flujo de asignación.
         */
        Usuario toEntity(PasswordEncoder encoder) {
            return Usuario.builder()
                    .nombre(nombre)
                    .correo(correo)
                    .passwordHash(encoder.encode(password))  // hash BCrypt, nunca texto plano
                    .rol(rol)
                    .activo(true)
                    .primerIngreso(false)                    // sin flujo de cambio de contraseña
                    .estadoCuenta(EstadoCuenta.ACTIVO)       // activo desde el primer arranque
                    .identificacion(identificacion)
                    .semestre(semestre)
                    .contactoEmergencia(contactoEmergencia)
                    // El estudiante de prueba se siembra en APTO para probar el flujo de asignación
                    .estadoEstudiante(rol == Rol.ESTUDIANTE ? EstadoEstudiante.APTO : EstadoEstudiante.NO_APTO)
                    .build();
        }
    }
}
