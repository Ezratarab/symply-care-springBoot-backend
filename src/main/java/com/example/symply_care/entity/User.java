package com.example.symply_care.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 45)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;

    @Column(name = "street")
    private String street;

    @Lob
    private byte[] imageData;

    @DateTimeFormat(pattern="dd/MM/yyyy")
    private Date birthDay;
}
