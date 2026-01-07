package com.teamB.hospitalreservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hospital", indexes = {
    @Index(name = "idx_hospital_name", columnList = "name")
})
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(unique = true)
    private String apiId;

    @Column(name = "sido_code")
    private String sidoCode;

    @Column(name = "sggu_code")
    private String sgguCode;

    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "hospital_subject",
            joinColumns = @JoinColumn(name = "hospital_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<Subject> subjects = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "sido_code", referencedColumnName = "sidoCode", insertable = false, updatable = false),
        @JoinColumn(name = "sggu_code", referencedColumnName = "sgguCode", insertable = false, updatable = false)
    })
    private Location location;

}