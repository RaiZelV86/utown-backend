package com.utown.repository;

import com.utown.model.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.category WHERE r.id = :id")
    Optional<Restaurant> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.category")
    Page<Restaurant> findAllWithCategory(Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.category WHERE r.isActive = true")
    Page<Restaurant> findAllActiveWithCategory(Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.category WHERE r.city = :city AND r.isActive = true")
    Page<Restaurant> findByCityWithCategory(@Param("city") String city, Pageable pageable);

    @Query("SELECT r FROM Restaurant r JOIN FETCH r.category WHERE r.category.id = :categoryId AND r.isActive = true")
    Page<Restaurant> findByCategoryWithCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    List<Restaurant> findByIsOpenTrueAndIsActiveTrue();

    List<Restaurant> findByIsFeaturedTrueAndIsActiveTrue(Pageable pageable);

    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND r.rating >= :minRating ORDER BY r.rating DESC")
    List<Restaurant> findTopRatedRestaurants(@Param("minRating") BigDecimal minRating, Pageable pageable);
}
