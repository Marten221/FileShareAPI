package com.example.FileShareAPI.Back_End.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
    @Id
    @Column(unique = true, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;
    private Long totalAvailableBytes;

    @OneToMany(mappedBy = "role")
    @JsonManagedReference
    private List<User> users;
}
