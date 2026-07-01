package utec.reciscore.desafio.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public List<ListDesafioResponse> findAll() {
        List<Desafio> desafios = desafioRepository.findAll();

        return desafios.stream()
                .map(desafio -> modelMapper.map(desafio, ListDesafioResponse.class))
                .toList();
    }

    public List<ListDesafioResponse> findAllWithUserStatus(Long userId) {
        List<Desafio> desafios = desafioRepository.findAll();
        List<UsuarioDesafio> inscripciones = desafioRepository.findInscripcionesByUsuarioId(userId);

        return desafios.stream()
                .map(desafio -> {
                    ListDesafioResponse response = modelMapper.map(desafio, ListDesafioResponse.class);
                    inscripciones.stream()
                            .filter(i -> i.getDesafio().getId().equals(desafio.getId()))
                            .findFirst()
                            .ifPresent(i -> {
                                response.setInscrito(true);
                                response.setProgresoActual(i.getProgresoActual());
                                response.setCompletado(i.getCompletado());
                            });
                    return response;
                })
                .toList();
    }

    public DetailDesafioResponse findById(Long id) {
        Desafio desafio=desafioRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontró el desafío con id: "+ id));

        return modelMapper.map(desafio,DetailDesafioResponse.class);
    }

    public DetailDesafioResponse findByIdWithUserStatus(Long id, Long userId) {
        Desafio desafio = desafioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el desafío con id: " + id));

        DetailDesafioResponse response = modelMapper.map(desafio, DetailDesafioResponse.class);

        desafioRepository.findInscripcionByUsuarioAndDesafio(userId, id)
                .ifPresent(i -> {
                    response.setInscrito(true);
                    response.setProgresoActual(i.getProgresoActual());
                    response.setCompletado(i.getCompletado());
                });

        return response;
    }

    @Transactional
    public DetailDesafioResponse unirse(Long desafioId, Long userId) {
        Desafio desafio = desafioRepository.findById(desafioId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el desafío con id: " + desafioId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el usuario con id: " + userId));

        desafioRepository.findInscripcionByUsuarioAndDesafio(userId, desafioId)
                .ifPresent(i -> {
                    throw new IllegalArgumentException("El usuario ya está inscrito en este desafío");
                });

        UsuarioDesafio inscripcion = UsuarioDesafio.builder()
                .usuario(user)
                .desafio(desafio)
                .progresoActual(0)
                .completado(false)
                .activo(true)
                .build();

        desafio.getInscripciones().add(inscripcion);
        desafioRepository.save(desafio);

        return modelMapper.map(desafio, DetailDesafioResponse.class);
    }

    @Transactional
    public DetailDesafioResponse desistir(Long desafioId, Long userId) {
        Desafio desafio = desafioRepository.findById(desafioId)
                .orElseThrow(() -> new NoSuchElementException("No se encontró el desafío con id: " + desafioId));

        UsuarioDesafio inscripcion = desafio.getInscripciones().stream()
                .filter(i -> i.getUsuario().getId().equals(userId) && i.getActivo())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El usuario no está inscrito en este desafío"));

        desafio.getInscripciones().remove(inscripcion);
        desafioRepository.save(desafio);

        return modelMapper.map(desafio, DetailDesafioResponse.class);
    }

    @Transactional
    public void actualizarProgreso(Long userId, int articulosReciclados, String materialCategoria) {
        List<UsuarioDesafio> inscripciones = desafioRepository.findInscripcionesByUsuarioId(userId);

        for (UsuarioDesafio inscripcion : inscripciones) {
            if (inscripcion.getCompletado() || !inscripcion.getActivo()) continue;

            Desafio desafio = inscripcion.getDesafio();
            if (!desafio.getActivo()) continue;

            String catDesafio = desafio.getCategoria();
            if ("RECICLAJE".equals(catDesafio)) {
                inscripcion.setProgresoActual(inscripcion.getProgresoActual() + articulosReciclados);
            }

            if (inscripcion.getProgresoActual() >= desafio.getMeta_valor()) {
                inscripcion.setCompletado(true);
                User user = inscripcion.getUsuario();
                user.setPoints(user.getPoints() + desafio.getPuntos());
                user.setNivel(User.calcularNivel(user.getPoints()));
                userRepository.save(user);
            }

            desafioRepository.save(desafio);
        }
    }

    public DetailDesafioResponse updateDesafio(Long id, UpdateDesafioRequest request) {
        Desafio desafio=desafioRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("No se encontró el desafío con id: "+ id));

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
