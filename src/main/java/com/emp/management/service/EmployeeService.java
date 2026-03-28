package com.emp.management.service;

import com.emp.management.model.Employee;
import com.emp.management.model.Employee.EmployeeStatus;
import com.emp.management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> searchEmployees(String keyword) {
        return employeeRepository.searchEmployees(keyword);
    }

    public List<Employee> getEmployeesByDepartment(Long deptId) {
        return employeeRepository.findByDepartmentId(deptId);
    }

    public List<Employee> getEmployeesByStatus(EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }

    public boolean emailExists(String email) {
        return employeeRepository.existsByEmail(email);
    }

    public boolean emailExistsForOther(String email, Long id) {
        return employeeRepository.existsByEmailAndIdNot(email, id);
    }

    public long getTotalCount() {
        return employeeRepository.count();
    }

    public Map<String, Long> getDashboardStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalEmployees",    employeeRepository.count());
        stats.put("activeEmployees",   employeeRepository.countByStatus(EmployeeStatus.ACTIVE));
        stats.put("inactiveEmployees", employeeRepository.countByStatus(EmployeeStatus.INACTIVE));
        stats.put("onLeaveEmployees",  employeeRepository.countByStatus(EmployeeStatus.ON_LEAVE));
        stats.put("terminatedEmployees", employeeRepository.countByStatus(EmployeeStatus.TERMINATED));
        return stats;
    }

    public List<Object[]> getEmployeeCountByDepartment() {
        return employeeRepository.countByDepartment();
    }
}
