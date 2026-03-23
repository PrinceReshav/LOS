package com.los.administration.user.spec;

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

    public static Specification<User> roleIdEquals(String roleId) {
        return (root, query, cb) ->
                roleId == null ? null :
                        cb.equal(root.get("roleId"), roleId);
    }

    public static Specification<User> profileIdEquals(String profileId) {
        return (root, query, cb) ->
                profileId == null ? null :
                        cb.equal(root.get("profileId"), profileId);
    }

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