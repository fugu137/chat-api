package zoidnet.dev.chat.model;


import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;


@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 55)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public static Role ADMIN = new Role("ADMIN");

    public static Role USER = new Role("USER");


    public Role() {}

    public Role(String name) {
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

    public String toAuthority() {
        return "ROLE_" + name;
    }

    public String toString() {
        return "{id: " + id + ", name: " + name + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
