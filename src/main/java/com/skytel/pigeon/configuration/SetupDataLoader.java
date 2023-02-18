package com.skytel.pigeon.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.skytel.pigeon.persistence.models.Privilege;
import com.skytel.pigeon.persistence.models.Role;
import com.skytel.pigeon.persistence.models.User;
import com.skytel.pigeon.persistence.repository.PrivilegeRepository;
import com.skytel.pigeon.persistence.repository.RoleRepository;
import com.skytel.pigeon.persistence.repository.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {

        if (alreadySetup) {
            return;
        }

        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        final List<Privilege> adminPrivileges = new ArrayList<>(
                Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        
        createRoleIfNotFound("ROLE_USER", userPrivileges);

        createUserIfNotFound("test@test.com",
                "Test",
                "Test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                "test",
                new ArrayList<>(Arrays.asList(adminRole)));

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    User createUserIfNotFound(final String email,
            final String firstName,
            final String lastName,
            final String company,
            final String phone,
            final String password,
            final String reference,
            final String postalCode,
            final String streetAddress,
            final String state,
            final String city,
            final String country,
            final Collection<Role> roles) {

        User user = userRepository.findByEmail(email);

        if (user == null) {

            user = new User();
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setCompany(company);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(encoder.encode(password));
            user.setReference(reference);
            user.setPostal(postalCode);
            user.setStreet(streetAddress);
            user.setState(state);
            user.setCity(city);
            user.setCountry(country);
            user.setEnabled(true);
        }

        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }
}
