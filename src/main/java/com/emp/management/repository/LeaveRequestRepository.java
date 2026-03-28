package com.emp.management.repository;

import com.emp.management.model.LeaveRequest;
import com.emp.management.model.LeaveRequest.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByAppliedOnDesc(Long employeeId);
    List<LeaveRequest> findByStatusOrderByAppliedOnDesc(LeaveStatus status);
    List<LeaveRequest> findAllByOrderByAppliedOnDesc();
    long countByStatus(LeaveStatus status);
}
