package com.example.symply_care.dto;

import com.example.symply_care.entity.Appointments;
import com.example.symply_care.entity.Inquiries;
import com.example.symply_care.entity.Patient;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DoctorDTO {
    @NotNull(message = "user id should not be null")
    private Long id;

    @NotNull(message = "first name should not be null")
    @NotBlank(message = "first name should not be blank")
    @NotEmpty(message = "first name should not be empty")
    private String firstName;

    @NotNull(message = "last name should not be null")
    @NotBlank(message = "last name should not be blank")
    @NotEmpty(message = "last name should not be empty")
    private String lastName;

    @NotNull(message = "email should not be null")
    @NotBlank(message = "email should not be blank")
    @NotEmpty(message = "email should not be empty")
    @Email(message = "email should be valid")
    @Column(unique = true, nullable = false, length = 70)
    private String email;


    @NotNull(message = "password should not be null")
    @NotBlank(message = "password should not be blank")
    @NotEmpty(message = "password should not be empty")
    private String password;
    private String city;

    private String country;

    private String street;

    @NotNull(message = "Birth Date should not be null")
    @NotBlank(message = "Birth Date should not be blank")
    @NotEmpty(message = "Birth Date should not be empty")
    private String birthDay;

    @Lob
    private byte[] imageData;

    @NotNull(message = "specialization should not be null")
    @NotBlank(message = "specialization should not be blank")
    @NotEmpty(message = "specialization should not be empty")
    private String specialization;

    @NotNull(message = "hospital should not be null")
    @NotBlank(message = "hospital should not be blank")
    @NotEmpty(message = "hospital should not be empty")
    private String hospital;

    @NotNull(message = "HMO should not be null")
    @NotBlank(message = "HMO should not be blank")
    @NotEmpty(message = "HMO should not be empty")
    private String hmo;

    @NotNull(message = "experience should not be null")
    @NotBlank(message = "experience should not be blank")
    @NotEmpty(message = "experience should not be empty")
    private Integer experience;

    private List<Inquiries> inquiriesList;

    private List<Patient> patients;

    private List<Appointments> appointments;
}

