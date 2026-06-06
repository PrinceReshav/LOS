package com.los.administration.grpc;

import com.los.grpc.employee.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class EmployeeGrpcClient {

    @GrpcClient("employee-service")
    private EmployeeServiceGrpc.EmployeeServiceBlockingStub stub;

    public boolean createEmployee(

            String employeeId,
            String userId,

            String fullName,
            String email,
            String mobile,

            String roleId,
            String roleName,

            String profileId,
            String profileName
    ) {

        try {

            CreateEmployeeRequest request =

                    CreateEmployeeRequest.newBuilder()

                            .setEmployeeId(employeeId)
                            .setUserId(userId)

                            .setFullName(fullName)
                            .setEmail(email)
                            .setMobile(mobile)

                            .setRoleId(roleId)
                            .setRoleName(roleName)

                            .setProfileId(profileId)
                            .setProfileName(profileName)

                            .build();

            CreateEmployeeResponse response =
                    stub.createEmployee(request);

            return response.getSuccess();

        } catch (StatusRuntimeException ex) {

            return false;
        }
    }

    public EmployeeResponse getEmployee(
            String employeeId
    ) {

        EmployeeRequest request =
                EmployeeRequest.newBuilder()
                        .setEmployeeId(employeeId)
                        .build();

        return stub.getEmployeeById(request);
    }

    public UpdateEmployeeStatusResponse updateEmployeeStatus(
            String employeeId,
            boolean active
    ) {

        UpdateEmployeeStatusRequest request =

                UpdateEmployeeStatusRequest.newBuilder()
                        .setEmployeeId(employeeId)
                        .setActive(active)
                        .build();

        return stub.updateEmployeeStatus(request);
    }
}