package com.emp.management.controller;

import com.emp.management.model.Employee;
import com.emp.management.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired private EmployeeService employeeService;
    @Autowired private DepartmentService departmentService;
    @Autowired private ExportService exportService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) Long deptId) {
        List<Employee> employees;
        if (keyword != null && !keyword.isEmpty()) {
            employees = employeeService.searchEmployees(keyword);
        } else if (status != null && !status.isEmpty()) {
            employees = employeeService.getEmployeesByStatus(Employee.EmployeeStatus.valueOf(status));
        } else if (deptId != null) {
            employees = employeeService.getEmployeesByDepartment(deptId);
        } else {
            employees = employeeService.getAllEmployees();
        }
        model.addAttribute("employees",    employees);
        model.addAttribute("totalCount",   employees.size());
        model.addAttribute("keyword",      keyword);
        model.addAttribute("departments",  departmentService.getAllDepartments());
        model.addAttribute("selectedDept", deptId);
        model.addAttribute("selectedStatus", status);
        return "employee/list";
    }

    @GetMapping("/add")
    public String showAdd(Model model) {
        model.addAttribute("employee",    new Employee());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("pageTitle",   "Add Employee");
        return "employee/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("employee") Employee employee,
                       BindingResult result,
                       @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
                       Model model, RedirectAttributes ra) {
        // Email duplicate check
        if (employee.getId() == null && employeeService.emailExists(employee.getEmail())) {
            result.rejectValue("email", "error.email", "Email already exists!");
        } else if (employee.getId() != null && employeeService.emailExistsForOther(employee.getEmail(), employee.getId())) {
            result.rejectValue("email", "error.email", "Email already exists!");
        }

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("pageTitle", employee.getId() == null ? "Add Employee" : "Edit Employee");
            return "employee/form";
        }

        // Handle profile photo upload
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
            	String uploadDir = System.getProperty("user.dir") + "/uploads/profile-photos/";
            	Files.createDirectories(Paths.get(uploadDir));
            	String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();
            	Path filePath = Paths.get(uploadDir + fileName);
            	Files.write(filePath, photoFile.getBytes());
            	employee.setProfileImage(fileName);
            } catch (IOException e) {
                // skip photo upload silently
            }
        }

        employeeService.saveEmployee(employee);
        ra.addFlashAttribute("successMessage",
                (employee.getId() == null ? "Employee added" : "Employee updated") + " successfully!");
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Employee> emp = employeeService.getEmployeeById(id);
        if (emp.isPresent()) {
            model.addAttribute("employee",    emp.get());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("pageTitle",   "Edit Employee");
            return "employee/form";
        }
        ra.addFlashAttribute("errorMessage", "Employee not found!");
        return "redirect:/employees";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Employee> emp = employeeService.getEmployeeById(id);
        if (emp.isPresent()) {
            model.addAttribute("employee", emp.get());
            return "employee/view";
        }
        ra.addFlashAttribute("errorMessage", "Employee not found!");
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            employeeService.deleteEmployee(id);
            ra.addFlashAttribute("successMessage", "Employee deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Cannot delete — employee has related records!");
        }
        return "redirect:/employees";
    }

    // ── Export Excel ──
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=employees.xlsx");
        ByteArrayOutputStream out = exportService.exportToExcel(employeeService.getAllEmployees());
        response.getOutputStream().write(out.toByteArray());
    }

    // ── Export PDF ──
    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=employees.pdf");
        ByteArrayOutputStream out = exportService.exportToPdf(employeeService.getAllEmployees());
        response.getOutputStream().write(out.toByteArray());
    }
}
