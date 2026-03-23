package com.los.losadminservice.grpc;

import com.los.grpc.employee.*;
import com.los.losadminservice.employee.model.Employee;
import com.los.losadminservice.employee.repository.EmployeeRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class EmployeeGrpcService extends EmployeeServiceGrpc.EmployeeServiceImplBase {

    private final EmployeeRepository employeeRepository;

    @Override
    public void getEmployeeById(
            EmployeeRequest request,
            StreamObserver<EmployeeResponse> responseObserver
    ) {

        Employee employee = employeeRepository
                .findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        EmployeeResponse response = EmployeeResponse.newBuilder()
                .setEmployeeId(employee.getEmployeeId())
                .setUserId(employee.getUserId())
                .setFullName(employee.getFullName())
                .setEmail(employee.getEmail())
                .setRoleId(employee.getRoleId())
                .setProfileId(employee.getProfileId())
                .setActive(employee.getActive())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateEmployeeStatus(
            UpdateEmployeeStatusRequest request,
            StreamObserver<UpdateEmployeeStatusResponse> responseObserver
    ) {

        Employee employee = employeeRepository
                .findByUserId(request.getUserId())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("Employee not found")
                                .asRuntimeException()
                );

        // idempotent update
        if (employee.getActive() != request.getActive()) {
            employee.setActive(request.getActive());
            employeeRepository.save(employee);
        }

        UpdateEmployeeStatusResponse response =
                UpdateEmployeeStatusResponse.newBuilder()
                        .setEmployeeId(employee.getEmployeeId())
                        .setActive(employee.getActive())
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}