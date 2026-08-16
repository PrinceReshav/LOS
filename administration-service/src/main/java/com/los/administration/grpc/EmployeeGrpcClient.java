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

            // FIX: this MUST be the organizational/hierarchy role id
            // (e.g. FIELD_OFFICER, RELATIONSHIP_MANAGER) resolved and
            // validated against los-admin-service's own role catalog via
            // OrgRoleClient - NOT administration-service's system-access
            // role (role_admin/role_sales). Passing the access role here
            // is what caused "Role not found" during branch/manager
            // assignment.
            String orgRoleId,
            String orgRoleName,

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

                            .setOrgRoleId(orgRoleId)
                            .setOrgRoleName(orgRoleName)

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