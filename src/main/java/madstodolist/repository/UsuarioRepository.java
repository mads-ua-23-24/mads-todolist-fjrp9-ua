package madstodolist.repository;

import madstodolist.model.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String s);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.esAdministrador = TRUE ")
    int countUsuariosAdministradores();
}
