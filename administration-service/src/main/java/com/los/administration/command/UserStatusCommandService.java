package com.los.administration.command;

import com.los.administration.grpc.EmployeeGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatusCommandService {

    private final EmployeeGrpcClient employeeGrpcClient;

    public void deactivateEmployee(String userId) {
        employeeGrpcClient.updateEmployeeStatus(userId,false);
    }

    public void activateEmployee(String userId) {
        employeeGrpcClient.updateEmployeeStatus(userId,true);
    }
}