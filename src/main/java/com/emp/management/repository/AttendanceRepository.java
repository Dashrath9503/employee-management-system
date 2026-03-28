package com.emp.management.repository;

import com.emp.management.model.Attendance;
import com.emp.management.model.Attendance.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);
    List<Attendance> findByAttendanceDateOrderByEmployeeFirstNameAsc(LocalDate date);
    Optional<Attendance> findByEmployeeIdAndAttendanceDate(Long employeeId, LocalDate date);
    long countByEmployeeIdAndStatus(Long employeeId, AttendanceStatus status);
}
