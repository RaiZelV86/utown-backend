package com.utown.repository;

import com.utown.model.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantIdAndIsAvailable(Long restaurantId, Boolean isAvailable);

    List<MenuItem> findByRestaurantIdOrderBySortOrderAscCreatedAtDesc(Long restaurantId);

    @Query("SELECT m FROM MenuItem m " +
            "WHERE m.restaurant.id = :restaurantId " +
            "AND (:categoryName IS NULL OR m.categoryName = :categoryName) " +
            "AND (:isAvailable IS NULL OR m.isAvailable = :isAvailable) " +
            "ORDER BY m.sortOrder ASC, m.createdAt DESC")
    List<MenuItem> findByRestaurantWithFilters(
            @Param("restaurantId") Long restaurantId,
            @Param("categoryName") String categoryName,
            @Param("isAvailable") Boolean isAvailable
    );
}
