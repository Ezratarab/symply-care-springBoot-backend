package com.example.symply_care.service;


import com.example.symply_care.entity.*;
import com.example.symply_care.repository.DoctorRepository;
import com.example.symply_care.repository.PatientRepository;
import com.example.symply_care.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This CustomUserDetailsService class implements the Spring Security UserDetailsService interface.
 * It overrides the loadUserByUsername for fetching user details from the database using the username.
 * The Spring Security Authentication Manager calls this method for getting the user details from the database, in order to perform authentication and authorization.
 * The loadUserByUsername() method returns a UserDetails object that Spring Security uses for performing various authentication and role based validations.
 * This method used by JwtAuthenticationFilter for validating the JWT token, and loads the user details associated with that token.
 */


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from the database. If the user is not found, throw an exception.
        Optional<Users> user = usersRepository.findByEmail(email);

        if (user.isPresent()) {
            // Create a UserDetails object from the data fetched from the database.
            return new org.springframework.security.core.userdetails.User(
                    user.get().getEmail(),
                    user.get().getPassword(),
                    // The mapRolesToAuthorities() method converts the user's roles to a list of GrantedAuthority objects,
                    // which can be used for role-based authentication and authorization.
                    mapRolesToAuthorities(user.get().getRoles())
            );
        } else {
            // Handle the case when the user is not found.
            // throw new UsernameNotFoundException("Invalid username or password.");
            System.out.println("Invalid username or password, or logged out.");
            return null;
        }
    }


    // This method converts the user's roles to a list of GrantedAuthority objects, which can be used for role based authentication and authorization.
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
}
