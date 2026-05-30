package utec.reciscore.reporteReciclaje.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Service;
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
public class ReporteReciclajeService {

    private final ReporteReciclajeRepository reporteRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final PuntoMapaService puntoMapaService;
    private final IaClient iaClient;
    private final ModelMapper modelMapper;

    public ReporteReciclajeService(ReporteReciclajeRepository reporteRepository,
                                   UserRepository userRepository,
                                   MaterialRepository materialRepository,
                                   PuntoMapaService puntoMapaService,
                                   IaClient iaClient,
                                   ModelMapper modelMapper) {
        this.reporteRepository = reporteRepository;
        this.userRepository = userRepository;
        this.materialRepository = materialRepository;
        this.puntoMapaService = puntoMapaService;
        this.iaClient = iaClient;
        this.modelMapper = modelMapper;

        TypeMap<ReporteReciclaje, ReporteReciclajeResponseDTO> typeMap =
                modelMapper.createTypeMap(ReporteReciclaje.class, ReporteReciclajeResponseDTO.class);

        typeMap.addMappings(mapper -> {
            mapper.map(src -> src.getUsuario().getId(),   ReporteReciclajeResponseDTO::setUserId);
            mapper.map(src -> src.getUsuario().getName(), ReporteReciclajeResponseDTO::setUserName);
            mapper.map(src -> src.getMaterial().getName(), ReporteReciclajeResponseDTO::setMaterialNombre);
        });

        typeMap.setPostConverter(ctx -> {
            ReporteReciclaje src = ctx.getSource();
            ReporteReciclajeResponseDTO dst = ctx.getDestination();
            dst.setMaterialCategoria(src.getMaterial().getCategory().name());
            dst.setTamanoObjeto(src.getTamanoObjeto().name());
            return dst;
        });
    }

    public ReporteReciclajeResponseDTO crear(ReporteReciclajeRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

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
            user.setPoints(user.getPoints() + puntosGanados);
            userRepository.save(user);
        }

        return modelMapper.map(reporteRepository.save(reporte), ReporteReciclajeResponseDTO.class);
    }

    public List<ReporteReciclajeResponseDTO> obtenerTodos() {
        return reporteRepository.findAll().stream()
                .map(r -> modelMapper.map(r, ReporteReciclajeResponseDTO.class))
                .toList();
    }

    public List<ReporteReciclajeResponseDTO> obtenerPorUsuario(Long userId) {
        return reporteRepository.findByUsuarioId(userId).stream()
                .map(r -> modelMapper.map(r, ReporteReciclajeResponseDTO.class))
                .toList();
    }

    public Optional<ReporteReciclajeResponseDTO> buscarPorId(Long id) {
        return reporteRepository.findById(id)
                .map(r -> modelMapper.map(r, ReporteReciclajeResponseDTO.class));
    }

    public void eliminar(Long id) {
        reporteRepository.deleteById(id);
    }
}