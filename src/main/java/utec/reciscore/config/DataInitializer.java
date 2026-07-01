package utec.reciscore.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import utec.reciscore.desafio.infraestructure.DesafioRepository;
import utec.reciscore.desafio.model.Desafio;
import utec.reciscore.material.infrastructure.MaterialRepository;
import utec.reciscore.material.model.Material;
import utec.reciscore.material.model.TipoMaterial;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.Role;
import utec.reciscore.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final MaterialRepository materialRepository;
    private final PuntoMapaRepository puntoMapaRepository;
    private final DesafioRepository desafioRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedMateriales();
        seedPuntosMapa();
        seedDesafios();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;
        log.info("Insertando usuarios seed...");
        User admin = new User();
        admin.setEmail("admin@reciscore.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("Admin General");
        admin.setUsername("admin");
        admin.setPoints(500);
        admin.setMultiplier(1.0);
        admin.setLocation("Miraflores");
        admin.setReciclajes(20);
        admin.setRachaDias(7);
        admin.setRole(Role.ADMIN);

        User test = new User();
        test.setEmail("test@reciscore.com");
        test.setPassword(passwordEncoder.encode("user123456"));
        test.setName("Usuario Test");
        test.setUsername("testuser");
        test.setPoints(120);
        test.setMultiplier(1.0);
        test.setLocation("San Isidro");
        test.setReciclajes(5);
        test.setRachaDias(2);
        test.setRole(Role.USER);

        userRepository.saveAll(List.of(admin, test));
        log.info("Usuarios seed insertados.");
    }

    private void seedMateriales() {
        if (materialRepository.count() > 0) return;
        log.info("Insertando materiales...");
        materialRepository.saveAll(List.of(
                mat("Botella PET",        15.0, 0.03, TipoMaterial.PLASTICO, true),
                mat("Bolsa plástica",      5.0, 0.01, TipoMaterial.PLASTICO, false),
                mat("Botella de vidrio",  12.0, 0.40, TipoMaterial.VIDRIO,   true),
                mat("Vidrio roto",         8.0, 0.50, TipoMaterial.VIDRIO,   false),
                mat("Periódico",          10.0, 0.20, TipoMaterial.PAPEL,    true),
                mat("Cartón",             12.0, 0.30, TipoMaterial.PAPEL,    true),
                mat("Lata de aluminio",   20.0, 0.02, TipoMaterial.METAL,    true),
                mat("Lata de acero",      14.0, 0.05, TipoMaterial.METAL,    true),
                mat("Envase tetra pak",    6.0, 0.04, TipoMaterial.PAPEL,    false),
                mat("Tubo de PVC",         4.0, 0.15, TipoMaterial.PLASTICO, false)
        ));
        log.info("Materiales insertados.");
    }

    private void seedPuntosMapa() {
        if (puntoMapaRepository.count() > 0) return;
        log.info("Insertando puntos de mapa...");
        puntoMapaRepository.saveAll(List.of(
                punto(-12.0943, -77.0407, "Recolecc - San Isidro (Av. Javier Prado Este 560)", TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0917, -77.0404, "Samsung Electronics Perú - Juan de Arona 745",      TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0926, -77.0360, "Samsung Electronics Perú - Elmer Faucett 319",      TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0950, -77.0440, "Pluz Perú - Calle César López 155, Maranga",        TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0378, -77.0452, "Pluz Perú - Av. Próceres de la Independencia 3045", TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0552, -77.0672, "Pluz Perú - Conde de Superunda c/ Rufino Torrico",  TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0787, -77.0868, "Pluz Perú - Av. Sáenz Peña 1018",                  TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0519, -77.1016, "Pluz Perú - Av. Argentina 3093",                    TipoPunto.ACOPIO_OFICIAL),
                punto(-12.1219, -77.0282, "Miraflores - General Córdova 556 (Parque Blume)",   TipoPunto.ACOPIO_OFICIAL),
                punto(-12.1180, -77.0335, "Miraflores - Mariscal Castilla 640 Urb. Aurora",    TipoPunto.ACOPIO_OFICIAL),
                punto(-12.1235, -77.0308, "Miraflores - Ca. Enrique Palacios 557",             TipoPunto.ACOPIO_OFICIAL),
                punto(-12.1114, -77.0296, "San Isidro - estación de reciclaje norte",          TipoPunto.ACOPIO_OFICIAL),
                punto(-12.1098, -77.0372, "San Isidro - estación de reciclaje sur",            TipoPunto.ACOPIO_OFICIAL),
                punto(-12.1506, -77.0227, "Surco - punto de acopio de reciclables",            TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0922, -77.0438, "San Borja - punto de acopio de reciclables",        TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0719, -77.0534, "Cercado de Lima - punto de acopio de reciclables",  TipoPunto.ACOPIO_OFICIAL),
                punto(-12.0835, -77.0331, "Zona Sucia Parque Kennedy",                          TipoPunto.ZONA_SUCIA),
                punto(-12.0948, -77.0237, "Zona Sucia Av. Arequipa - Lince",                   TipoPunto.ZONA_SUCIA),
                punto(-12.0762, -77.0589, "Zona Sucia Av. Colonial - Cercado",                 TipoPunto.ZONA_SUCIA)
        ));
        log.info("Puntos de mapa insertados.");
    }

    private void seedDesafios() {
        if (desafioRepository.count() > 0) return;
        log.info("Insertando desafíos...");
        desafioRepository.saveAll(List.of(
                desafio("Reciclador Inicial",  "Recicla 5 kg de materiales en un mes.",     "RECICLAJE", 5,  100, "2026-06-01", "2026-07-31", true),
                desafio("Rey del Plástico",    "Recicla 10 kg de plástico durante el mes.", "RECICLAJE", 10, 200, "2026-06-01", "2026-07-31", true),
                desafio("Guardián del Vidrio", "Lleva 8 kg de vidrio a un punto de acopio.","RECICLAJE", 8,  180, "2026-06-01", "2026-07-31", true),
                desafio("Maestro del Papel",   "Recicla 15 kg de papel o cartón.",          "RECICLAJE", 15, 150, "2026-06-01", "2026-07-31", true),
                desafio("Héroe del Metal",     "Recicla 6 kg de metal (latas, aluminio).",  "RECICLAJE", 6,  220, "2026-07-01", "2026-07-31", true),
                desafio("Racha Semanal",       "Mantén una racha de 7 días consecutivos.",  "RACHA",     7,  300, "2026-06-01", "2026-12-31", true)
        ));
        log.info("Desafíos insertados.");
    }

    // helpers
    private Material mat(String name, double ppk, double weight, TipoMaterial cat, boolean rec) {
        Material m = new Material();
        m.setName(name);
        m.setPointsPerKg(ppk);
        m.setWeight(weight);
        m.setCategory(cat);
        m.setRecyclable(rec);
        return m;
    }

    private PuntoMapa punto(double lat, double lon, String nombre, TipoPunto tipo) {
        PuntoMapa p = new PuntoMapa();
        p.setLatitude(lat);
        p.setLongitude(lon);
        p.setNombre(nombre);
        p.setTipo(tipo);
        return p;
    }

    private Desafio desafio(String titulo, String desc, String cat, int meta,
                            int puntos, String inicio, String fin, boolean activo) {
        Desafio d = new Desafio();
        d.setTitulo(titulo);
        d.setDescripcion(desc);
        d.setCategoria(cat);
        d.setMeta_valor(meta);
        d.setPuntos(puntos);
        d.setFecha_inicio(LocalDateTime.parse(inicio + "T00:00:00"));
        d.setFecha_fin(LocalDateTime.parse(fin + "T23:59:59"));
        d.setActivo(activo);
        return d;
    }
}