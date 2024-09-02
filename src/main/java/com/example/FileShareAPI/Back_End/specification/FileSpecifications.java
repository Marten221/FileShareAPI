package com.example.FileShareAPI.Back_End.specification;

import com.example.FileShareAPI.Back_End.model.File;
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
}
