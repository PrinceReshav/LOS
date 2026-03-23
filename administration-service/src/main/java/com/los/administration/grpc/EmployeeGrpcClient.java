package com.los.administration.grpc;

import com.los.grpc.employee.*;
import com.los.grpc.employee.EmployeeRequest;
import com.los.grpc.employee.EmployeeResponse;
import com.los.grpc.employee.EmployeeServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class EmployeeGrpcClient {

    @GrpcClient("employee-service")
    private EmployeeServiceGrpc.EmployeeServiceBlockingStub stub;

    public EmployeeResponse getEmployee(String employeeId){

        EmployeeRequest request =
                EmployeeRequest.newBuilder()
                        .setEmployeeId(employeeId)
                        .build();

        return stub.getEmployeeById(request);
    }

    public void updateEmployeeStatus(String userId, boolean active){

        UpdateEmployeeStatusRequest request =
                UpdateEmployeeStatusRequest.newBuilder()
                        .setUserId(userId)
                        .setActive(active)
                        .build();

        stub.updateEmployeeStatus(request);
    }
}
