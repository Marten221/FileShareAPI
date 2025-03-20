package com.example.FileShareAPI.Back_End.specification;

import com.example.FileShareAPI.Back_End.model.File;
import com.example.FileShareAPI.Back_End.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class FileSpecifications {
    public static Specification<File> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.equals("*")) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("fileName")), "%" + keyword.toLowerCase() + "%");
        };
    }

    public static Specification<File> hasExtension(String extension) {
        return (root, query, criteriaBuilder) -> {
            if (extension == null || extension.equals("any")) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("fileExtension"), extension);
        };
    }

    public static Specification<File> isAccessible(String userId) {
        return (root, query, criteriaBuilder) -> {
            Predicate isPublic = criteriaBuilder.isTrue(root.get("isPublic"));

            Join<File, User> userJoin = root.join("user"); // Join the File with its User to access Users fields
            Predicate isOwner = criteriaBuilder.equal(userJoin.get("userId"), userId);

            return criteriaBuilder.or(isPublic, isOwner);
        };
    }

    public static Specification<File> filterByOwner(String owner, String userId) {
        return(root, query, criteriaBuilder) -> {
            if (owner.equals("all")) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("user").get("userId"), userId);
        };
    }
}
