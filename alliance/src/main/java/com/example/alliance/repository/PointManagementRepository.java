package com.example.alliance.repository;

import com.example.alliance.domain.PointManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "pointManagements", path = "pointManagements")
public interface PointManagementRepository extends JpaRepository<PointManagement, Long> {
    Optional<PointManagement> findByBookId(String bookId);
    void deleteByBookId(String BookId);
}
