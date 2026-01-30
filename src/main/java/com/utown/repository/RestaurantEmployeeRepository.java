package com.utown.repository;

import com.utown.model.entity.RestaurantEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantEmployeeRepository extends JpaRepository<RestaurantEmployee, Long> {

    @Query("SELECT re FROM RestaurantEmployee re " +
           "WHERE re.restaurant.id = :restaurantId " +
           "AND re.user.id = :userId " +
           "AND re.isActive = true")
    Optional<RestaurantEmployee> findByRestaurantIdAndUserId(
            @Param("restaurantId") Long restaurantId,
            @Param("userId") Long userId
    );

    boolean existsByRestaurantIdAndUserIdAndIsActiveTrue(
            Long restaurantId,
            Long userId
    );

    @Query("SELECT CASE WHEN COUNT(re) > 0 THEN true ELSE false END " +
           "FROM RestaurantEmployee re " +
           "WHERE re.restaurant.id = :restaurantId " +
           "AND re.user.id = :userId " +
           "AND re.isActive = true")
    boolean existsByRestaurantIdAndUserId(
            @Param("restaurantId") Long restaurantId,
            @Param("userId") Long userId
    );
}

