package utec.reciscore.desafio.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import utec.reciscore.desafio.dto.CreateDesafioRequest;
import utec.reciscore.desafio.dto.DetailDesafioResponse;
import utec.reciscore.desafio.dto.ListDesafioResponse;
import utec.reciscore.desafio.infraestructure.DesafioRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DesafioService {
    private final DesafioRepository desafioRepository;
    private final ModelMapper modelMapper;

    //crear desafio
    public DetailDesafioResponse createDesafio(CreateDesafioRequest request){
        if (desafioRepository.existsByCategoria(request.getCategoria())) {
            throw new IllegalArgumentException("La categoria ya existe");
        }
        if(request.getFecha_fin().isBefore(request.getFecha_inicio())){
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        Desafio desafio=modelMapper.map(request, Desafio.class);
        desafio.setActivo(true);
        Desafio saved=desafioRepository.save(desafio);
        return modelMapper.map(saved,DetailDesafioResponse.class);
    }

    //obtener desafios
    public List<ListDesafioResponse> findAll() {
        List<Desafio> desafios=desafioRepository.findAll();

        if (desafios.isEmpty()) {
            throw new NoSuchElementException("No existen desafios registrados");
        }

        return desafios.stream()
                .map(desafio -> modelMapper.map(desafio,ListDesafioResponse.class))
                .toList();
    }
}
