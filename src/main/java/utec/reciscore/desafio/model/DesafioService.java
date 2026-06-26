package utec.reciscore.desafio.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import utec.reciscore.desafio.dto.CreateDesafioRequest;
import utec.reciscore.desafio.dto.DetailDesafioResponse;
import utec.reciscore.desafio.dto.ListDesafioResponse;
import utec.reciscore.desafio.dto.UpdateDesafioRequest;
import utec.reciscore.desafio.infraestructure.DesafioRepository;
import utec.reciscore.user.infrastructure.UserRepository;
import utec.reciscore.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DesafioService {
    private final DesafioRepository desafioRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public DetailDesafioResponse createDesafio(CreateDesafioRequest request){
        if (desafioRepository.existsByCategoria(request.getCategoria())) {
            throw new IllegalArgumentException("Ya existe un desafío activo para esta categoría");
        }

        if(request.getFecha_fin().isBefore(request.getFecha_inicio())){
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        Desafio desafio=modelMapper.map(request, Desafio.class);
        desafio.setActivo(true);
        Desafio saved=desafioRepository.save(desafio);
        return modelMapper.map(saved,DetailDesafioResponse.class);
    }

    // obtener desafios
    public List<ListDesafioResponse> findAll() {
        List<Desafio> desafios=desafioRepository.findAll();

        if (desafios.isEmpty()) {
            throw new NoSuchElementException("No existen desafios registrados");
        }

        return desafios.stream()
                .map(desafio -> modelMapper.map(desafio,ListDesafioResponse.class))
                .toList();
    }

    // obtener desafios con estado de inscripcion del usuario
    public List<ListDesafioResponse> findAllWithUserStatus(Long userId) {
        List<Desafio> desafios = desafioRepository.findAll();
        List<Desafio> inscritos = desafioRepository.findByUsuariosInscritosId(userId);

        if (desafios.isEmpty()) {
            throw new NoSuchElementException("No existen desafios registrados");
        }

        return desafios.stream()
                .map(desafio -> {
                    ListDesafioResponse response = modelMapper.map(desafio, ListDesafioResponse.class);
                    response.setInscrito(inscritos.contains(desafio));
                    return response;
                })
                .toList();
    }


    //obtener desafio por id
    public DetailDesafioResponse findById(Long id) {
        Desafio desafio=desafioRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontró el desafío con id: "+ id));

        return modelMapper.map(desafio,DetailDesafioResponse.class);
    }


    // usuario se une o acepta desafio
    public DetailDesafioResponse unirse(Long desafioId,Long userId) {
        //buscar desafio
        Desafio desafio=desafioRepository.findById(desafioId)
                .orElseThrow(()-> new NoSuchElementException("No se encontró el desafío con id: "+ desafioId));

        //buscar usuario
        User user=userRepository.findById(userId)
                .orElseThrow(()-> new NoSuchElementException("No se encontró el usuario con id: "+ userId));

        //validar si ya esta inscrito
        if (desafio.getUsuariosInscritos().contains(user)) {
            throw new IllegalArgumentException("El usuario ya está inscrito en este desafío");
        }

        //agregar inscripcion
        desafio.getUsuariosInscritos().add(user);
        Desafio actualizado=desafioRepository.save(desafio);

        return modelMapper.map(actualizado,DetailDesafioResponse.class);
    }


    // usuario se retira de un desafio
    public DetailDesafioResponse desistir(Long desafioId, Long userId) {
        Desafio desafio = desafioRepository.findById(desafioId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el desafío con id: " + desafioId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con id: " + userId));

        if (!desafio.getUsuariosInscritos().contains(user)) {
            throw new IllegalArgumentException("El usuario no está inscrito en este desafío");
        }

        desafio.getUsuariosInscritos().remove(user);
        Desafio actualizado = desafioRepository.save(desafio);
        return modelMapper.map(actualizado, DetailDesafioResponse.class);
    }

    //editar desafio
    public DetailDesafioResponse updateDesafio(Long id, UpdateDesafioRequest request) {
        //buscar desafio
        Desafio desafio=desafioRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontró el desafío con id: "+ id));

        //actualizar campos si vienen en el request
        if (request.getTitulo()!=null) {
            desafio.setTitulo(request.getTitulo());
        }
        if (request.getDescripcion()!=null) {
            desafio.setDescripcion(request.getDescripcion());
        }
        if (request.getCategoria()!=null) {
            desafio.setCategoria(request.getCategoria());
        }
        if (request.getActivo()!=null) {
            desafio.setActivo(request.getActivo());
        }

        Desafio actualizado=desafioRepository.save(desafio);
        return modelMapper.map(actualizado,DetailDesafioResponse.class);
    }
}
