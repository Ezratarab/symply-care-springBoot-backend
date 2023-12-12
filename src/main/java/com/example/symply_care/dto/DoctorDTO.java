package com.example.symply_care.dto;

import com.example.symply_care.entity.Appointments;
import com.example.symply_care.entity.Inquiries;
import com.example.symply_care.entity.Patient;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class DoctorDTO extends DTO {

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
    private String HMO;

    @NotNull(message = "experience should not be null")
    @NotBlank(message = "experience should not be blank")
    @NotEmpty(message = "experience should not be empty")
    private Integer experience;

    private List<Inquiries> inquiriesList;

    private List<Patient> patients;

    private List<Appointments> appointments;
}

