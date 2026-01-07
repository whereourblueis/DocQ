package com.teamB.hospitalreservation.service;

import com.teamB.hospitalreservation.dto.MyReservationResponseDto;
import com.teamB.hospitalreservation.dto.ReservationRequestDto;
import com.teamB.hospitalreservation.dto.ReservationResponseDto;
import com.teamB.hospitalreservation.entity.Hospital;
import com.teamB.hospitalreservation.entity.Reservation;
import com.teamB.hospitalreservation.entity.Subject;
import com.teamB.hospitalreservation.entity.User;
import com.teamB.hospitalreservation.repository.HospitalRepository;
import com.teamB.hospitalreservation.repository.ReservationRepository;
import com.teamB.hospitalreservation.repository.SubjectRepository;
import com.teamB.hospitalreservation.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final SubjectRepository subjectRepository;

    public ReservationResponseDto createReservation(ReservationRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Hospital hospital = hospitalRepository.findById(requestDto.getHospitalId())
                .orElseThrow(() -> new EntityNotFoundException("병원을 찾을 수 없습니다."));
        Subject subject = subjectRepository.findByName(requestDto.getSubjectName())
                .orElseThrow(() -> new EntityNotFoundException("진료과목을 찾을 수 없습니다."));

        if (reservationRepository.existsByHospitalIdAndReservationTime(hospital.getId(), requestDto.getReservationTime())) {
            throw new IllegalStateException("해당 시간에 이미 예약이 존재합니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setHospital(hospital);
        reservation.setSubject(subject);
        reservation.setReservationTime(requestDto.getReservationTime());

        String phoneNumber = requestDto.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.isBlank()) {
            phoneNumber = user.getPhone_number();
        }

        if (phoneNumber == null) {
            phoneNumber = "";
        }
        reservation.setPhoneNumber(phoneNumber);
        
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationResponseDto.from(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<MyReservationResponseDto> getMyReservations(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(MyReservationResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getBookedTimes(Long hospitalId, String subjectName, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Reservation> reservations = reservationRepository.findByHospital_IdAndSubject_NameAndReservationTimeBetween(hospitalId, subjectName, startOfDay, endOfDay);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return reservations.stream()
                .map(reservation -> reservation.getReservationTime().format(timeFormatter))
                .sorted()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> getAvailableTimes(Long hospitalId, LocalDate date) {
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(18, 0);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Reservation> reservations = reservationRepository.findByHospitalIdAndReservationTimeBetween(hospitalId, startOfDay, endOfDay);
        Set<LocalTime> reservedTimes = reservations.stream()
                .map(reservation -> reservation.getReservationTime().toLocalTime())
                .collect(Collectors.toSet());

        Map<String, Boolean> availableTimes = new LinkedHashMap<>();
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            availableTimes.put(currentTime.toString(), !reservedTimes.contains(currentTime));
            currentTime = currentTime.plusMinutes(30);
        }

        return availableTimes;
    }
}