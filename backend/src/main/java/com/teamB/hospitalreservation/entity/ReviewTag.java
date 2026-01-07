package com.teamB.hospitalreservation.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReviewTag {
    KIND("친절해요"),
    CLEAN("청결해요"),
    COMFORTABLE("진료 분위기가 편안해요"),
    QUICK("진료가 빨라요");

    private final String label;

    ReviewTag(String label) {
        this.label = label;
    }

    @JsonCreator
    public static ReviewTag fromString(String input) {
        return Arrays.stream(ReviewTag.values())
                .filter(tag -> tag.label.equals(input))
                .findFirst()
                .orElseGet(() -> {
                    try {
                        return ReviewTag.valueOf(input);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("존재하지 않는 리뷰 태그입니다: " + input);
                    }
                });
    }

    @JsonValue
    public String getLabel() {
        return label;
    }
}