package com.emp.management.config;

import com.emp.management.model.*;
import com.emp.management.model.Employee.EmployeeStatus;
import com.emp.management.model.Employee.Gender;
import com.emp.management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepo;
    @Autowired private DepartmentRepository deptRepo;
    @Autowired private EmployeeRepository empRepo;
    @Autowired private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        // Create users
        createUser("admin", "admin123", "ROLE_ADMIN", "Administrator");
        createUser("hr",    "hr123",    "ROLE_HR",    "HR Manager");
        createUser("emp",   "emp123",   "ROLE_EMPLOYEE", "John Employee");

        // Sample data
        if (deptRepo.count() == 0) {
            Department it      = deptRepo.save(new Department("Information Technology", "IT & Software Development", "Raj Kumar"));
            Department hrDept  = deptRepo.save(new Department("Human Resources", "HR & Recruitment", "Priya Sharma"));
            Department finance = deptRepo.save(new Department("Finance", "Accounts & Finance", "Amit Gupta"));
            Department sales   = deptRepo.save(new Department("Sales & Marketing", "Sales & Business Dev", "Neha Singh"));
            Department ops     = deptRepo.save(new Department("Operations", "Operations & Logistics", "Suresh Patel"));

            makeEmp("Rahul",  "Sharma", "rahul.sharma@company.com",  "9876543210", "Senior Developer",  Gender.MALE,   LocalDate.of(1990,5,15),  LocalDate.of(2020,1,10),  75000.0, EmployeeStatus.ACTIVE,   it);
            makeEmp("Priya",  "Patel",  "priya.patel@company.com",   "9876543211", "HR Manager",        Gender.FEMALE, LocalDate.of(1988,8,22),  LocalDate.of(2019,3,1),   65000.0, EmployeeStatus.ACTIVE,   hrDept);
            makeEmp("Amit",   "Verma",  "amit.verma@company.com",    "9876543212", "Financial Analyst", Gender.MALE,   LocalDate.of(1992,2,10),  LocalDate.of(2021,6,15),  60000.0, EmployeeStatus.ACTIVE,   finance);
            makeEmp("Sneha",  "Reddy",  "sneha.reddy@company.com",   "9876543213", "Sales Executive",   Gender.FEMALE, LocalDate.of(1995,11,3),  LocalDate.of(2022,8,20),  45000.0, EmployeeStatus.ON_LEAVE, sales);
            makeEmp("Vikram", "Singh",  "vikram.singh@company.com",  "9876543214", "Operations Lead",   Gender.MALE,   LocalDate.of(1987,7,18),  LocalDate.of(2018,4,5),   80000.0, EmployeeStatus.ACTIVE,   ops);
            makeEmp("Anjali", "Desai",  "anjali.desai@company.com",  "9876543215", "Java Developer",    Gender.FEMALE, LocalDate.of(1993,3,25),  LocalDate.of(2021,9,1),   70000.0, EmployeeStatus.ACTIVE,   it);
            makeEmp("Kiran",  "Kumar",  "kiran.kumar@company.com",   "9876543216", "DevOps Engineer",   Gender.MALE,   LocalDate.of(1991,6,14),  LocalDate.of(2020,7,15),  72000.0, EmployeeStatus.ACTIVE,   it);
            makeEmp("Pooja",  "Nair",   "pooja.nair@company.com",    "9876543217", "Accountant",        Gender.FEMALE, LocalDate.of(1994,9,8),   LocalDate.of(2022,1,10),  52000.0, EmployeeStatus.INACTIVE, finance);

            System.out.println("✅ Sample data initialized!");
        }
    }

    private void createUser(String username, String password, String role, String fullName) {
        if (!userRepo.existsByUsername(username)) {
            userRepo.save(new User(username, encoder.encode(password), role, fullName));
            System.out.println("✅ User created: " + username + " / " + password + " [" + role + "]");
        }
    }

    private void makeEmp(String fn, String ln, String email, String phone, String title,
                          Gender gender, LocalDate dob, LocalDate join, Double salary,
                          EmployeeStatus status, Department dept) {
        Employee e = new Employee();
        e.setFirstName(fn); e.setLastName(ln); e.setEmail(email); e.setPhone(phone);
        e.setJobTitle(title); e.setGender(gender); e.setDateOfBirth(dob);
        e.setJoinDate(join); e.setSalary(salary); e.setStatus(status); e.setDepartment(dept);
        empRepo.save(e);
    }
}
