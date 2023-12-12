package com.example.symply_care.dto;

import com.example.symply_care.entity.Appointments;
import com.example.symply_care.entity.Doctor;
import com.example.symply_care.entity.Inquiries;
import lombok.Data;

import java.util.List;

@Data
public class PatientDTO extends DTO {

    private List<Inquiries> inquiriesList;
    private List<Doctor> doctors;
    private List<Appointments> appointments;

}

