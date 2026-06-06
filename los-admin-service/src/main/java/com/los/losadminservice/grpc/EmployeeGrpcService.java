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
public class EmployeeGrpcService
        extends EmployeeServiceGrpc.EmployeeServiceImplBase {

    private final EmployeeRepository employeeRepository;

    @Override
    public void createEmployee(
            CreateEmployeeRequest request,
            StreamObserver<CreateEmployeeResponse> responseObserver
    ) {

        if (
                employeeRepository.existsByEmployeeId(
                        request.getEmployeeId()
                )
                        ||
                        employeeRepository.existsByUserId(
                                request.getUserId()
                        )
        ) {

            throw Status.ALREADY_EXISTS
                    .withDescription(
                            "Employee already exists"
                    )
                    .asRuntimeException();
        }

        Employee employee = Employee.builder()

                .employeeId(request.getEmployeeId())
                .userId(request.getUserId())

                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobile(request.getMobile())

                .roleId(request.getRoleId())
                .roleName(request.getRoleName())

                .profileId(request.getProfileId())
                .profileName(request.getProfileName())

                .active(true)

                .build();

        employeeRepository.save(employee);

        CreateEmployeeResponse response =
                CreateEmployeeResponse.newBuilder()
                        .setEmployeeId(employee.getEmployeeId())
                        .setSuccess(true)
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getEmployeeById(
            EmployeeRequest request,
            StreamObserver<EmployeeResponse> responseObserver
    ) {

        Employee employee = employeeRepository
                .findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("Employee not found")
                                .asRuntimeException()
                );

        EmployeeResponse response =
                EmployeeResponse.newBuilder()

                        .setEmployeeId(employee.getEmployeeId())
                        .setUserId(employee.getUserId())

                        .setFullName(
                                employee.getFullName() != null
                                        ? employee.getFullName()
                                        : ""
                        )

                        .setEmail(
                                employee.getEmail() != null
                                        ? employee.getEmail()
                                        : ""
                        )

                        .setMobile(
                                employee.getMobile() != null
                                        ? employee.getMobile()
                                        : ""
                        )

                        .setRoleId(
                                employee.getRoleId() != null
                                        ? employee.getRoleId()
                                        : ""
                        )

                        .setRoleName(
                                employee.getRoleName() != null
                                        ? employee.getRoleName()
                                        : ""
                        )

                        .setProfileId(
                                employee.getProfileId() != null
                                        ? employee.getProfileId()
                                        : ""
                        )

                        .setProfileName(
                                employee.getProfileName() != null
                                        ? employee.getProfileName()
                                        : ""
                        )

                        .setActive(
                                Boolean.TRUE.equals(employee.getActive())
                        )

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
                .findByEmployeeId(
                        request.getEmployeeId()
                )
                .orElseThrow(() ->
                        Status.NOT_FOUND
                                .withDescription("Employee not found")
                                .asRuntimeException()
                );

        if (!Boolean.valueOf(request.getActive())
                .equals(employee.getActive())) {

            employee.setActive(request.getActive());

            employeeRepository.save(employee);
        }

        UpdateEmployeeStatusResponse response =
                UpdateEmployeeStatusResponse.newBuilder()
                        .setEmployeeId(employee.getEmployeeId())
                        .setActive(
                                Boolean.TRUE.equals(employee.getActive())
                        )
                        .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}