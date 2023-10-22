package madstodolist.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "equipos")
public class Equipo {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String nombre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "equipo_usuario",
            joinColumns = { @JoinColumn(name = "fk_equipo") },
            inverseJoinColumns = {@JoinColumn(name = "fk_usuario")})
    private Set<Usuario> usuarios = new HashSet<>();

    public Equipo() {}

    public Equipo(String nombre) {
        this.nombre = nombre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public Set<Usuario> getUsuarios() { return usuarios; }

    public void addUsuario(Usuario usuario) {
        // Si el usuario ya está en la lista, no lo añadimos
        if (this.getUsuarios().contains(usuario)) return;
        // Añadimos el usuario a la lista de usuarios del equipo
        this.getUsuarios().add(usuario);
        // Establecemos la relación inversa del equipo en el usuario
        if (!usuario.getEquipos().contains(this)) {
            usuario.getEquipos().add(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipo e = (Equipo) o;
        if (this.getId() != null && e.getId() != null)
            return Objects.equals(this.getId(), e.getId());
        return this.getNombre().equals(e.getNombre());
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
