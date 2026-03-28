package com.emp.management.repository;

import com.emp.management.model.Employee;
import com.emp.management.model.Employee.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(e.lastName)  LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(e.email)     LIKE LOWER(CONCAT('%',:kw,'%')) OR " +
           "LOWER(e.jobTitle)  LIKE LOWER(CONCAT('%',:kw,'%'))")
    List<Employee> searchEmployees(@Param("kw") String keyword);

    List<Employee> findByDepartmentId(Long departmentId);
    List<Employee> findByStatus(EmployeeStatus status);
    long countByStatus(EmployeeStatus status);
    long countByDepartmentId(Long departmentId);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("SELECT e.department.name, COUNT(e) FROM Employee e WHERE e.department IS NOT NULL GROUP BY e.department.name")
    List<Object[]> countByDepartment();
}
