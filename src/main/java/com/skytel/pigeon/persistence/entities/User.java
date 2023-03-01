package com.skytel.pigeon.persistence.entities;

import java.util.Collection;

import org.jboss.aerogear.security.otp.api.Base32;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_account")
public class User {

    @Id
    @Column(unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String company;

    private String email;

    private String phone;

    @Column(length = 60)
    private String password;

    private String reference;

    private String postal;

    private String street;

    private String state;

    private String city;

    private String country;

    private boolean enabled;

    private boolean isUsing2FA = false;

    private String secret;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public User() {
        super();
        this.secret = Base32.random();
        this.enabled = false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((getEmail() == null) ? 0 : getEmail().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final User user = (User) obj;
        return getEmail().equals(user.getEmail()) || getCompany().equals(user.getCompany());
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        builder.append("User [id=").append(id)
                .append(", firstname=").append(firstname)
                .append(", lastname=").append(lastname)
                .append(", company=").append(company)
                .append(", email=").append(email)
                .append(", phone=").append(phone)
                .append(", reference=").append(reference)
                .append(", postal=").append(postal)
                .append(", street=").append(street)
                .append(", state=").append(state)
                .append(", city=").append(city)
                .append(", country=").append(country)
                .append(", enabled=").append(enabled)
                .append(", isUsing2FA=").append(isUsing2FA)
                .append(", secret=").append(secret)
                .append(", roles=").append(roles)
                .append("]");

        return builder.toString();
    }
}
