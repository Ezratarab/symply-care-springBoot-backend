package com.example.symply_care.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "patients")
public class Patient extends User {

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "patients_doctors",
            joinColumns = @JoinColumn(name = "patient_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "id"))
    private List<Doctor> doctors;

    @Column
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<Inquiries> inquiries;

    @OneToMany
    @JsonIgnore
    private List<Appointments> appointments;
}
