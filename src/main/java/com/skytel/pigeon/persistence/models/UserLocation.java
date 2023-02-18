package com.skytel.pigeon.persistence.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String country;

    private boolean enabled;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public UserLocation() {

        super();
        enabled = false;
    }

    public UserLocation(String country, User user) {

        super();
        this.country = country;
        this.user = user;
        enabled = false;
    }

    @Override
    public int hashCode() {

        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getCountry() == null) ? 0 : getCountry().hashCode());
        result = (prime * result) + (isEnabled() ? 1231 : 1237);
        result = (prime * result) + ((getId() == null) ? 0 : getId().hashCode());
        result = (prime * result) + ((getUser() == null) ? 0 : getUser().hashCode());

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
        final UserLocation other = (UserLocation) obj;

        if (getCountry() == null) {
            if (other.getCountry() != null) {
                return false;
            }
        } else if (!getCountry().equals(other.getCountry())) {
            return false;
        }
        if (isEnabled() != other.isEnabled()) {
            return false;
        }
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        if (getUser() == null) {
            if (other.getUser() != null) {
                return false;
            }
        } else if (!getUser().equals(other.getUser())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {

        return "UserLocation [id=" + id + ", country=" + country + ", enabled=" + enabled + ", user=" + user + "]";
    }
}
