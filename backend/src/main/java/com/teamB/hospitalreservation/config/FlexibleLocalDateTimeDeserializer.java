package com.teamB.hospitalreservation.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;




public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText();

        try {
            // ISO 형식: 2025-07-17T11:00:00
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignore) { }

        try {
            // 공백 형식: 2025-07-17 11:00:00
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException ignore) { }

        try {
            // 붙은 형식: 2025-07-1711:00:00
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss"));
        } catch (DateTimeParseException ignore) { }

        throw new RuntimeException("지원하지 않는 날짜 형식입니다: " + value);
    }
}