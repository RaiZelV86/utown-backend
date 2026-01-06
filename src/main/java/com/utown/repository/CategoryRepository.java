package com.utown.repository;

import com.utown.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByIsActiveTrueOrderByPriorityAsc();

    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.priority ASC")
    List<Category> findAllActiveOrderedByPriority();
}
