package com.example.symply_care.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/main")
@RequiredArgsConstructor
public class Controller {

    @Autowired
    private DoctorController doctorController;
    @Autowired
    private PatientController patientController;



}
