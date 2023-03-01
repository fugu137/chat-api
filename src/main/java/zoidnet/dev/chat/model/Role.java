package zoidnet.dev.chat.model;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 55)
    private String name;


    public static Role ADMIN = new Role("ADMIN");

    public static Role USER = new Role("USER");


    protected Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> asSingletonSet() {
        return new HashSet<>(List.of(this));
    }

    public String toAuthority() {
        return "ROLE_" + name;
    }

    public String toString() {
        return "{ id: " + id + ", name: " + name + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id.equals(role.id) && name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
