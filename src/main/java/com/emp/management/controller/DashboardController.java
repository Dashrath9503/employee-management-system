package com.emp.management.controller;

import com.emp.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;

@Controller
public class DashboardController {

    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentService departmentService;
    @Autowired private LeaveService leaveService;

    @GetMapping("/")
    public String home() { return "redirect:/dashboard"; }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Long> stats = employeeService.getDashboardStats();
        model.addAttribute("totalEmployees",      stats.get("totalEmployees"));
        model.addAttribute("activeEmployees",     stats.get("activeEmployees"));
        model.addAttribute("inactiveEmployees",   stats.get("inactiveEmployees"));
        model.addAttribute("onLeaveEmployees",    stats.get("onLeaveEmployees"));
        model.addAttribute("terminatedEmployees", stats.get("terminatedEmployees"));
        model.addAttribute("totalDepartments",    departmentService.getTotalCount());
        model.addAttribute("pendingLeaves",       leaveService.countPending());
        model.addAttribute("recentEmployees",
                employeeService.getAllEmployees().stream().limit(6).toList());

        // Chart data
        List<Object[]> deptData = employeeService.getEmployeeCountByDepartment();
        List<String> deptNames  = new ArrayList<>();
        List<Long>   deptCounts = new ArrayList<>();
        for (Object[] row : deptData) {
            deptNames.add((String) row[0]);
            deptCounts.add((Long) row[1]);
        }
        model.addAttribute("deptNames",  deptNames);
        model.addAttribute("deptCounts", deptCounts);

        // Status chart data
        model.addAttribute("statusLabels", List.of("Active","On Leave","Inactive","Terminated"));
        model.addAttribute("statusData",   List.of(
                stats.get("activeEmployees"),
                stats.get("onLeaveEmployees"),
                stats.get("inactiveEmployees"),
                stats.get("terminatedEmployees")
        ));

        return "dashboard";
    }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/access-denied")
    public String accessDenied() { return "access-denied"; }
}
