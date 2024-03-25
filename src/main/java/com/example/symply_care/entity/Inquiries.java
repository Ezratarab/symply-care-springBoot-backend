package com.example.symply_care.entity;

import com.example.symply_care.dto.DoctorDTO;
import com.example.symply_care.dto.PatientDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;
import java.util.List;
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "inquiries")
public class Inquiries {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(
            name = "inquiries_doctor",
            joinColumns = @JoinColumn(name = "inquiries_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id")
    )
    private List<Doctor> doctor;

    @ManyToMany
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinTable(
            name = "inquiries_doctor2",
            joinColumns = @JoinColumn(name = "inquiries_id"),
            inverseJoinColumns = @JoinColumn(name = "doctor2_id")
    )
    private List<Doctor> doctor2;

    @ManyToOne
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column()
    @ElementCollection
    @JsonIgnore
    private List<Prediction> predictions;

    @Column(nullable = false)
    private String symptoms;

    @Column
    private String answer = "";

    @Column()
    private Boolean hasAnswered = false;
}
