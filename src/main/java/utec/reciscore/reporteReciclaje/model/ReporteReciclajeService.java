package utec.reciscore.reporteReciclaje.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import utec.reciscore.desafio.model.DesafioService;
import utec.reciscore.ia.IaClient;
import utec.reciscore.ia.IaResponse;
import utec.reciscore.material.model.Material;
import utec.reciscore.puntoMapa.model.PuntoMapaService;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeRequestDTO;
import utec.reciscore.reporteReciclaje.dto.ReporteReciclajeResponseDTO;
import utec.reciscore.reporteReciclaje.infrastructure.ReporteReciclajeRepository;
import utec.reciscore.user.model.User;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.material.infrastructure.MaterialRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReporteReciclajeService {

    private final ReporteReciclajeRepository reporteRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final PuntoMapaService puntoMapaService;
    private final IaClient iaClient;
    private final DesafioService desafioService;

    public ReporteReciclajeResponseDTO crear(ReporteReciclajeRequestDTO dto) {

        User user = getUsuarioAutenticado();

        Material material = materialRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new NoSuchElementException("Material no encontrado"));

        boolean gpsValido = puntoMapaService.estaEnZonaValida(dto.getLatitud(), dto.getLongitud());

        boolean iaValida = false;
        double confianzaIa = 0.0;

        try {
            IaResponse iaResponse = iaClient.classify(dto.getFotoUrl());
            confianzaIa = iaResponse.getConfidence();

            String tipoDetectado = iaResponse.getTipoMaterial();
            String tipoReportado = material.getCategory().name();
            iaValida = tipoDetectado != null
                    && tipoDetectado.equalsIgnoreCase(tipoReportado)
                    && confianzaIa >= 0.7;

        } catch (Exception e) {
            iaValida = false;
        }

        ReporteReciclaje reporte = new ReporteReciclaje();
        reporte.setUsuario(user);
        reporte.setMaterial(material);
        reporte.setFotoUrl(dto.getFotoUrl());
        reporte.setTamanoObjeto(dto.getTamanoObjeto());
        reporte.setNumeroArticulos(dto.getNumeroArticulos());
        reporte.setMaterialDetectadoIa(iaValida);
        reporte.setConfianzaIa(confianzaIa);
        reporte.setValidadoIa(iaValida);
        reporte.setGpsValidado(gpsValido);

        if (gpsValido && iaValida) {
            double pesoTotal = material.getPointsPerKg() * dto.getNumeroArticulos();
            int puntosGanados = (int) Math.round(pesoTotal * user.getMultiplier());
            reporte.setPuntosGanados(puntosGanados);
            user.setPoints(user.getPoints() + puntosGanados);
            user.setNivel(User.calcularNivel(user.getPoints()));
            userRepository.save(user);

            desafioService.actualizarProgreso(
                    user.getId(),
                    dto.getNumeroArticulos(),
                    material.getCategory().name()
            );
        } else {
            reporte.setPuntosGanados(0);
        }

        return toDto(reporteRepository.save(reporte));
    }

    public List<ReporteReciclajeResponseDTO> obtenerTodos() {
        return reporteRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<ReporteReciclajeResponseDTO> obtenerPorUsuario(Long userId) {
        return reporteRepository.findByUsuarioId(userId).stream().map(this::toDto).toList();
    }

    public Optional<ReporteReciclajeResponseDTO> buscarPorId(Long id) {
        return reporteRepository.findById(id).map(this::toDto);
    }

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
    }

    private User getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    private ReporteReciclajeResponseDTO toDto(ReporteReciclaje reporte) {
        ReporteReciclajeResponseDTO dto = new ReporteReciclajeResponseDTO();
        dto.setNumeroReporte(reporte.getNumeroReporte());
        dto.setUserId(reporte.getUsuario().getId());
        dto.setUserName(reporte.getUsuario().getName());
        dto.setMaterialNombre(reporte.getMaterial().getName());
        dto.setMaterialCategoria(reporte.getMaterial().getCategory().name());
        dto.setFotoUrl(reporte.getFotoUrl());
        dto.setTamanoObjeto(reporte.getTamanoObjeto().name());
        dto.setNumeroArticulos(reporte.getNumeroArticulos());
        dto.setMaterialDetectadoIa(reporte.getMaterialDetectadoIa());
        dto.setConfianzaIa(reporte.getConfianzaIa());
        dto.setValidadoIa(reporte.getValidadoIa());
        dto.setGpsValidado(reporte.getGpsValidado());

        if (reporte.getPuntosGanados() != null) {
            dto.setPuntosGanados(reporte.getPuntosGanados());
        } else if (reporte.getValidadoIa() && reporte.getGpsValidado()) {
            Material material = reporte.getMaterial();
            User usuario = reporte.getUsuario();
            double pesoTotal = material.getPointsPerKg() * reporte.getNumeroArticulos();
            dto.setPuntosGanados((int) Math.round(pesoTotal * usuario.getMultiplier()));
        } else {
            dto.setPuntosGanados(0);
        }

        dto.setPesoTotal(Math.round((reporte.getMaterial().getWeight() * reporte.getNumeroArticulos()) * 1000.0) / 1000.0);
        dto.setFecha(reporte.getFecha());
        return dto;
    }
}