package utec.reciscore.ranking.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import utec.reciscore.ranking.dto.RankingResponseDTO;
import utec.reciscore.ranking.infrastructure.RankingRepository;
import utec.reciscore.user.model.User;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final ModelMapper modelMapper;

    public List<RankingResponseDTO> getRanking() {
        List<User> usuarios = rankingRepository.findAllOrderByPointsDesc();
        AtomicInteger posicion = new AtomicInteger(1);

        return usuarios.stream()
                .map(u -> {
                    RankingResponseDTO dto = modelMapper.map(u, RankingResponseDTO.class);
                    dto.setPosicion(posicion.getAndIncrement());
                    return dto;
                })
                .toList();
    }

    public List<RankingResponseDTO> getRankingByLocation(String location) {
        List<User> usuarios = rankingRepository.findByLocationOrderByPointsDesc(location);
        AtomicInteger posicion = new AtomicInteger(1);

        return usuarios.stream()
                .map(u -> {
                    RankingResponseDTO dto = modelMapper.map(u, RankingResponseDTO.class);
                    dto.setPosicion(posicion.getAndIncrement());
                    return dto;
                })
                .toList();
    }
}