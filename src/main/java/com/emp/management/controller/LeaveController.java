package com.emp.management.controller;

import com.emp.management.model.*;
import com.emp.management.model.LeaveRequest.LeaveStatus;
import com.emp.management.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/leave")
public class LeaveController {

    @Autowired private LeaveService leaveService;
    @Autowired private EmployeeService employeeService;

    @GetMapping
    public String myLeaves(Model model, Authentication auth) {
        model.addAttribute("leaves",    leaveService.getAllLeaves());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "leave/list";
    }

    @GetMapping("/all")
    public String allLeaves(Model model) {
        model.addAttribute("leaves",    leaveService.getAllLeaves());
        model.addAttribute("pendingCount", leaveService.countPending());
        return "leave/all";
    }

    @GetMapping("/apply")
    public String applyForm(Model model) {
        model.addAttribute("leave",     new LeaveRequest());
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "leave/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute LeaveRequest leave,
                       @RequestParam Long employeeId,
                       RedirectAttributes ra) {
        Optional<Employee> emp = employeeService.getEmployeeById(employeeId);
        if (emp.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Employee not found!");
            return "redirect:/leave/apply";
        }
        leave.setEmployee(emp.get());
        leave.setAppliedOn(LocalDateTime.now());
        leave.setStatus(LeaveStatus.PENDING);
        leaveService.save(leave);
        ra.addFlashAttribute("successMessage", "Leave application submitted successfully!");
        return "redirect:/leave";
    }

    @GetMapping("/review/{id}")
    public String reviewForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<LeaveRequest> leave = leaveService.findById(id);
        if (leave.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Leave request not found!");
            return "redirect:/leave/all";
        }
        model.addAttribute("leave", leave.get());
        return "leave/review";
    }

    @PostMapping("/review/{id}")
    public String review(@PathVariable Long id,
                         @RequestParam String action,
                         @RequestParam(required = false) String comment,
                         Authentication auth, RedirectAttributes ra) {
        Optional<LeaveRequest> opt = leaveService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("errorMessage", "Leave request not found!");
            return "redirect:/leave/all";
        }
        LeaveRequest leave = opt.get();
        leave.setStatus(action.equals("approve") ? LeaveStatus.APPROVED : LeaveStatus.REJECTED);
        leave.setReviewedBy(auth.getName());
        leave.setReviewComment(comment);
        leaveService.save(leave);
        ra.addFlashAttribute("successMessage", "Leave " + action + "d successfully!");
        return "redirect:/leave/all";
    }
}
