package com.example.symply_care.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    private Long id;

    private String role;

    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<Users> users;

    public Role(String role) {
        this.role = role;
    }

}