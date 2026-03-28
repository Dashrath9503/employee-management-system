package com.emp.management.service;

import com.emp.management.model.Attendance;
import com.emp.management.model.Attendance.AttendanceStatus;
import com.emp.management.repository.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepo;

    public Attendance save(Attendance a) {
        return attendanceRepo.save(a);
    }

    public List<Attendance> getByEmployee(Long empId) {
        return attendanceRepo.findByEmployeeIdOrderByAttendanceDateDesc(empId);
    }

    public List<Attendance> getByDate(LocalDate date) {
        return attendanceRepo.findByAttendanceDateOrderByEmployeeFirstNameAsc(date);
    }

    public Optional<Attendance> findByEmployeeAndDate(Long empId, LocalDate date) {
        return attendanceRepo.findByEmployeeIdAndAttendanceDate(empId, date);
    }

    public long countPresent(Long empId) {
        return attendanceRepo.countByEmployeeIdAndStatus(empId, AttendanceStatus.PRESENT);
    }

    public long countAbsent(Long empId) {
        return attendanceRepo.countByEmployeeIdAndStatus(empId, AttendanceStatus.ABSENT);
    }
}
