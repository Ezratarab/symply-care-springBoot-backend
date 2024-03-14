package com.example.symply_care.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="doctors")
public class Doctor extends User{

    @Column(nullable=false)
    private String specialization;

    @Column
    private String hospital;

    @Column
    private String hmo;

    @Column(nullable = false)
    private Integer experience;

    @Column
    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Inquiries> inquiries;

    @ManyToMany
    @JsonIgnore
    private List<Patient> patients;

    @OneToMany
    @JsonIgnore
    private List<Appointments> appointments;

}
