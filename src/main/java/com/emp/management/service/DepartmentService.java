package com.emp.management.service;

import com.emp.management.model.Department;
import com.emp.management.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public boolean nameExists(String name) {
        return departmentRepository.existsByName(name);
    }

    public boolean nameExistsForOther(String name, Long id) {
        return departmentRepository.existsByNameAndIdNot(name, id);
    }

    public long getTotalCount() {
        return departmentRepository.count();
    }
}
