package utec.reciscore.reporteZona.model;


import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import utec.reciscore.exceptions.DuplicateReportException;
import utec.reciscore.puntoMapa.infrastructure.PuntoMapaRepository;
import utec.reciscore.puntoMapa.model.PuntoMapa;
import utec.reciscore.puntoMapa.model.TipoPunto;
import utec.reciscore.reporteZona.dto.ReporteZonaRequestDTO;
import utec.reciscore.reporteZona.dto.ReporteZonaResponseDTO;
import utec.reciscore.reporteZona.infrastructure.ReporteZonaRepository;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteZonaService {
    private final ReporteZonaRepository reporteZonaRepository;
    private final UserRepository userRepository;
    private final PuntoMapaRepository puntoMapaRepository;
    private final ModelMapper modelMapper;


    public ReporteZonaResponseDTO create(ReporteZonaRequestDTO dto) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        List<ReporteZona> userReports =
                reporteZonaRepository.findByUser(user);

        for (ReporteZona report : userReports) {
            boolean sameZone =
                    Math.abs(report.getLatitude() - dto.getLatitude()) < 0.001
                            &&
                            Math.abs(report.getLongitude() - dto.getLongitude()) < 0.001;
            if (sameZone) {
                throw new DuplicateReportException(
                        "Ya reportaste esta zona"
                );
            }
        }
        ReporteZona reporteZona =
                modelMapper.map(dto, ReporteZona.class);

        reporteZona.setUser(user);

        ReporteZona saved =
                reporteZonaRepository.save(reporteZona);

        List<ReporteZona> allReports = reporteZonaRepository.findAll();
        int count = 0;

        for (ReporteZona report : allReports) {
            boolean sameZone = Math.abs(report.getLatitude() - dto.getLatitude()) < 0.001 &&
                            Math.abs(report.getLongitude() - dto.getLongitude()) < 0.001;

            if (sameZone && !report.isProcesado()) {
                count++;
            }
        }

        if (count >= 20) {
            PuntoMapa puntoMapa = new PuntoMapa();
            puntoMapa.setLatitude(dto.getLatitude());
            puntoMapa.setLongitude(dto.getLongitude());
            puntoMapa.setNombre("Zona Crítica");
            puntoMapa.setTipo(TipoPunto.ZONA_SUCIA);
            puntoMapaRepository.save(puntoMapa);
            for (ReporteZona report : allReports) {
                boolean sameZone = Math.abs(report.getLatitude() - dto.getLatitude()) < 0.001 &&
                                Math.abs(report.getLongitude() - dto.getLongitude()) < 0.001;
                if (sameZone) {
                    report.setProcesado(true);
                }
            }
        }


        reporteZonaRepository.saveAll(allReports);
        ReporteZonaResponseDTO response = modelMapper.map(saved, ReporteZonaResponseDTO.class);
        response.setUsername(user.getUsername());
        return response;
    }


    //obtener todos los reportes
    public List<ReporteZonaResponseDTO> getAll() {
        List<ReporteZona> reportes =
                reporteZonaRepository.findAll();

        return reportes.stream()
                .map(reporte -> {
                    ReporteZonaResponseDTO dto =
                            modelMapper.map(
                                    reporte,
                                    ReporteZonaResponseDTO.class
                            );
                    dto.setUsername(
                            reporte.getUser().getUsername()
                    );

                    return dto;
                })
                .toList();
    }



}
