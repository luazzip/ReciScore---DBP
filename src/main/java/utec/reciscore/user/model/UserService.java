package utec.reciscore.user.model;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import utec.reciscore.exceptions.DuplicateUserException;
import utec.reciscore.material.model.TipoMaterial;
import utec.reciscore.reporteReciclaje.infrastructure.ReporteReciclajeRepository;
import utec.reciscore.reporteReciclaje.model.ReporteReciclaje;
import utec.reciscore.user.dto.UserImpactDTO;
import utec.reciscore.user.dto.UserRequestDTO;
import utec.reciscore.user.dto.UserResponseDTO;
import utec.reciscore.user.dto.UserUpdateDTO;
import utec.reciscore.user.infrastructure.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ReporteReciclajeRepository reporteReciclajeRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    public UserResponseDTO create(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateUserException("El email ya está registrado");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateUserException("El username ya existe");
        }

        User user = modelMapper.map(dto, User.class);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponseDTO.class);
    }

    public UserResponseDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return modelMapper.map(user, UserResponseDTO.class);
    }

    // NUEVO: devuelve el usuario autenticado actual (a partir del JWT/SecurityContext)
    public UserResponseDTO getCurrent() {
        User user = getAuthenticatedUser();
        return modelMapper.map(user, UserResponseDTO.class);
    }

    // NUEVO: calcula el impacto ambiental del usuario autenticado actual
    public UserImpactDTO getImpact() {
        User user = getAuthenticatedUser();
        List<ReporteReciclaje> reportes = reporteReciclajeRepository.findByUsuarioId(user.getId());

        int totalReports = reportes.size();
        int validatedReports = 0;
        int totalItems = 0;
        double estimatedKg = 0.0;
        int plasticReports = 0;
        int paperReports = 0;
        int glassReports = 0;

        for (ReporteReciclaje r : reportes) {
            if (Boolean.TRUE.equals(r.getValidadoIa())) {
                validatedReports++;
            }
            int articulos = r.getNumeroArticulos() != null ? r.getNumeroArticulos() : 0;
            totalItems += articulos;

            if (r.getMaterial() != null) {
                Double pesoUnitario = r.getMaterial().getWeight();
                if (pesoUnitario != null) {
                    estimatedKg += pesoUnitario * articulos;
                }

                TipoMaterial categoria = r.getMaterial().getCategory();
                if (categoria == TipoMaterial.PLASTICO) plasticReports++;
                else if (categoria == TipoMaterial.PAPEL) paperReports++;
                else if (categoria == TipoMaterial.VIDRIO) glassReports++;
            }
        }

        // Factores de conversión aproximados (referenciales, no certificados):
        // 1 kg reciclado ≈ 1.5 kg CO2 evitado, ≈ 20 L de agua ahorrada, ≈ 0.017 árboles equivalentes
        double co2SavedKg = estimatedKg * 1.5;
        double waterSavedLiters = estimatedKg * 20.0;
        double treesEquivalent = estimatedKg * 0.017;

        return new UserImpactDTO(
                totalReports,
                validatedReports,
                totalItems,
                round2(estimatedKg),
                round2(co2SavedKg),
                round2(waterSavedLiters),
                round2(treesEquivalent),
                plasticReports,
                paperReports,
                glassReports
        );
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();
        return userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
    }

    public UserResponseDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        checkOwnerOrAdmin(user, "No puedes editar otra cuenta");

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getProfilePicture() != null) user.setProfilePicture(dto.getProfilePicture());
        if (dto.getLocation() != null) user.setLocation(dto.getLocation());

        return modelMapper.map(userRepository.save(user), UserResponseDTO.class);
    }

    public void delete(Long id) {
        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        checkOwnerOrAdmin(userToDelete, "No puedes eliminar otra cuenta");

        userRepository.delete(userToDelete);
    }

    private void checkOwnerOrAdmin(User target, String mensajeError) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        assert authentication != null;
        String currentEmail = authentication.getName();

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        boolean isSameUser = target.getEmail().equals(currentEmail);

        if (!isAdmin && !isSameUser) {
            throw new AccessDeniedException(mensajeError);
        }
    }
}