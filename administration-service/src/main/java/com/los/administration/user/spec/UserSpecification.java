package com.los.administration.user.spec;

import com.los.administration.role.model.RoleType;
import com.los.administration.user.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> usernameLike(String username) {
        return (root, query, cb) ->
                username == null ? null :
                        cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%");
    }

    public static Specification<User> employeeIdEquals(String employeeId) {
        return (root, query, cb) ->
                employeeId == null ? null :
                        cb.equal(root.get("employeeId"), employeeId);
    }

    public static Specification<User> roleNameEquals(String roleName) {
        return (root, query, cb) ->
                roleName == null ? null :
                        cb.equal(root.get("role").get("roleName"), roleName);
    }

    public static Specification<User> profileNameEquals(String profileName) {
        return (root, query, cb) ->
                profileName == null ? null :
                        cb.equal(root.get("profile").get("profileName"), profileName);
    }


    /*
    *public static Specification<User> visibleTo(User currentUser) {

        return (root, query, cb) -> {

            // ROOT → no restriction
            if (currentUser.getRole().getRoleType() == RoleType.ROOT) {
                return cb.conjunction();
            }

            // Only same role or subordinates
            return cb.or(
                    cb.equal(root.get("role"), currentUser.getRole())
                    // later add hierarchy logic
            );
        };
    }*/

    public static Specification<User> activeEquals(Boolean active) {
        return (root, query, cb) ->
                active == null ? null :
                        cb.equal(root.get("active"), active);
    }

    public static Specification<User> usernameStartsWith(String letter) {
        return (root, query, cb) ->
                letter == null ? null :
                        cb.like(
                                cb.lower(root.get("username")),
                                letter.toLowerCase() + "%"
                        );
    }
}