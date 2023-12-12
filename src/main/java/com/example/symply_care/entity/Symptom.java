package com.example.symply_care.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Symptom {

    private String symptom;

}
