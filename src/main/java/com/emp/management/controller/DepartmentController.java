package com.emp.management.controller;

import com.emp.management.model.Department;
import com.emp.management.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "department/list";
    }

    @GetMapping("/add")
    public String showAdd(Model model) {
        model.addAttribute("department", new Department());
        model.addAttribute("pageTitle",  "Add Department");
        return "department/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("department") Department department,
                       BindingResult result, Model model, RedirectAttributes ra) {
        if (department.getId() == null && departmentService.nameExists(department.getName())) {
            result.rejectValue("name", "error.name", "Department name already exists!");
        } else if (department.getId() != null && departmentService.nameExistsForOther(department.getName(), department.getId())) {
            result.rejectValue("name", "error.name", "Department name already exists!");
        }
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", department.getId() == null ? "Add Department" : "Edit Department");
            return "department/form";
        }
        departmentService.saveDepartment(department);
        ra.addFlashAttribute("successMessage",
                (department.getId() == null ? "Department added" : "Department updated") + " successfully!");
        return "redirect:/departments";
    }

    @GetMapping("/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Department> dept = departmentService.getDepartmentById(id);
        if (dept.isPresent()) {
            model.addAttribute("department", dept.get());
            model.addAttribute("pageTitle",  "Edit Department");
            return "department/form";
        }
        ra.addFlashAttribute("errorMessage", "Department not found!");
        return "redirect:/departments";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<Department> dept = departmentService.getDepartmentById(id);
        if (dept.isPresent()) {
            model.addAttribute("department", dept.get());
            return "department/view";
        }
        ra.addFlashAttribute("errorMessage", "Department not found!");
        return "redirect:/departments";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            departmentService.deleteDepartment(id);
            ra.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Cannot delete — employees exist in this department!");
        }
        return "redirect:/departments";
    }
}
