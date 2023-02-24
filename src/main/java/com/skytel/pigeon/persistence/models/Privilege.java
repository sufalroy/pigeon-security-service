package com.skytel.pigeon.persistence.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Collection;

@Data
@Entity
@Table(name = "privileges")
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "privileges")
    private Collection<Role> roles;

    public Privilege() {
        super();
    }

    public Privilege(final String name) {
        super();
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Privilege other = (Privilege) obj;
        if (getName() == null) {
            return other.getName() == null;

        } else return getName().equals(other.getName());
    }

    @Override
    public String toString() {
        return "Privilege [name=" + name + "]" +
                "[id=" + id + "]";
    }
}
