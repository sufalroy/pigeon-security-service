package com.skytel.pigeon.persistence.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Collection;

@Data
@Entity
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
            if (other.getName() != null) {
                return false;
            }

        } else if (!getName().equals(other.getName())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();
        builder.append("Privilege [name=").append(name).append("]")
                .append("[id=").append(id).append("]");

        return builder.toString();
    }
}
