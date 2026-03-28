package com.emp.management.service;

import com.emp.management.model.LeaveRequest;
import com.emp.management.model.LeaveRequest.LeaveStatus;
import com.emp.management.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private LeaveRequestRepository leaveRepo;

    public LeaveRequest save(LeaveRequest lr) {
        return leaveRepo.save(lr);
    }

    public Optional<LeaveRequest> findById(Long id) {
        return leaveRepo.findById(id);
    }

    public List<LeaveRequest> getAllLeaves() {
        return leaveRepo.findAllByOrderByAppliedOnDesc();
    }

    public List<LeaveRequest> getLeavesByEmployee(Long empId) {
        return leaveRepo.findByEmployeeIdOrderByAppliedOnDesc(empId);
    }

    public List<LeaveRequest> getPendingLeaves() {
        return leaveRepo.findByStatusOrderByAppliedOnDesc(LeaveStatus.PENDING);
    }

    public long countPending() {
        return leaveRepo.countByStatus(LeaveStatus.PENDING);
    }
}
