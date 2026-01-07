package com.teamB.hospitalreservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class Location {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String sidoCode;
	private String sgguCode;

	public Location(String name, String sidoCode, String sgguCode) {
		this.name = name;
		this.sidoCode = sidoCode;
		this.sgguCode = sgguCode;
	}

}

