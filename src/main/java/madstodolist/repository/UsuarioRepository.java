package madstodolist.repository;

import madstodolist.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String s);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.esAdministrador = TRUE ")
    int countUsuariosAdministradores();

    @Modifying
    @Query("UPDATE Usuario u SET u.estaBloqueado = ?1 WHERE u.id = ?2 ")
    void updateUsuarioBloqueo(boolean bloqueado, Long idUsuario);
}
