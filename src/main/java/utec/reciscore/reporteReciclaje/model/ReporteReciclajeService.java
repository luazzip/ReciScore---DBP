package utec.reciscore.reporteReciclaje.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    public ReporteReciclajeResponseDTO crear(ReporteReciclajeRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        Material material = materialRepository.findById(dto.getMaterialId())
                .orElseThrow(() -> new NoSuchElementException("Material no encontrado"));

        boolean gpsValido = puntoMapaService.estaEnZonaValida(dto.getLatitud(), dto.getLongitud());

        boolean iaValida = dto.getMaterialDetectadoIa();

        ReporteReciclaje reporte = new ReporteReciclaje();
        reporte.setUsuario(user);
        reporte.setMaterial(material);
        reporte.setFotoUrl(dto.getFotoUrl());
        reporte.setTamanoObjeto(dto.getTamanoObjeto());
        reporte.setNumeroArticulos(dto.getNumeroArticulos());
        reporte.setMaterialDetectadoIa(iaValida);
        reporte.setConfianzaIa(dto.getConfianzaIa());
        reporte.setValidadoIa(iaValida);
        reporte.setGpsValidado(gpsValido);

        if (gpsValido && iaValida) {
            double pesoTotal = material.getPointsPerKg() * dto.getNumeroArticulos();
            int puntosGanados = (int) Math.round(pesoTotal);
            user.setPoints(user.getPoints() + puntosGanados);
            userRepository.save(user);
        }

        return toDto(reporteRepository.save(reporte));
    }

    public List<ReporteReciclajeResponseDTO> obtenerTodos() {
        return reporteRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<ReporteReciclajeResponseDTO> obtenerPorUsuario(Long userId) {
        return reporteRepository.findByUsuarioId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<ReporteReciclajeResponseDTO> buscarPorId(Long id) {
        return reporteRepository.findById(id).map(this::toDto);
    }

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
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
        dto.setFecha(reporte.getFecha());
        return dto;
    }
}