package com.emp.management.controller;

import com.emp.management.model.*;
import com.emp.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired private AttendanceService attendanceService;
    @Autowired private EmployeeService employeeService;

    @GetMapping
    public String index(Model model,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        final LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        List<Attendance> records = attendanceService.getByDate(selectedDate);
        model.addAttribute("records",   records);
        model.addAttribute("date",      selectedDate);
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "attendance/list";
    }

    @GetMapping("/mark")
    public String markForm(Model model,
                           @RequestParam(required = false)
                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        final LocalDate selectedDate = (date != null) ? date : LocalDate.now();
        List<Employee> employees = employeeService.getAllEmployees();
        List<Attendance> list = new ArrayList<>();
        for (Employee emp : employees) {
            Optional<Attendance> existing = attendanceService.findByEmployeeAndDate(emp.getId(), selectedDate);
            Attendance a = existing.orElseGet(() -> {
                Attendance att = new Attendance();
                att.setEmployee(emp);
                att.setAttendanceDate(selectedDate);
                att.setStatus(Attendance.AttendanceStatus.PRESENT);
                return att;
            });
            list.add(a);
        }
        model.addAttribute("attendances", list);
        model.addAttribute("date",        selectedDate);
        model.addAttribute("employees",   employees);
        return "attendance/mark";
    }

    @PostMapping("/save")
    public String save(@RequestParam Map<String, String> params,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       RedirectAttributes ra) {
        List<Employee> employees = employeeService.getAllEmployees();
        for (Employee emp : employees) {
            String statusKey  = "status_"  + emp.getId();
            String remarkKey  = "remarks_" + emp.getId();
            String checkInKey = "checkIn_" + emp.getId();
            if (!params.containsKey(statusKey)) continue;

            Optional<Attendance> existing = attendanceService.findByEmployeeAndDate(emp.getId(), date);
            Attendance a = existing.orElseGet(Attendance::new);
            a.setEmployee(emp);
            a.setAttendanceDate(date);
            a.setStatus(Attendance.AttendanceStatus.valueOf(params.get(statusKey)));
            a.setRemarks(params.getOrDefault(remarkKey, ""));
            String ci = params.getOrDefault(checkInKey, "");
            if (!ci.isEmpty()) {
                try { a.setCheckIn(java.time.LocalTime.parse(ci)); } catch (Exception ignored) {}
            }
            attendanceService.save(a);
        }
        ra.addFlashAttribute("successMessage", "Attendance marked for " + date);
        return "redirect:/attendance?date=" + date;
    }

    @GetMapping("/employee/{id}")
    public String empAttendance(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Employee> emp = employeeService.getEmployeeById(id);
        if (emp.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Employee not found!");
            return "redirect:/attendance";
        }
        model.addAttribute("employee",  emp.get());
        model.addAttribute("records",   attendanceService.getByEmployee(id));
        model.addAttribute("present",   attendanceService.countPresent(id));
        model.addAttribute("absent",    attendanceService.countAbsent(id));
        return "attendance/employee";
    }
}