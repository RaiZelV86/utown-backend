package com.utown.repository;

import com.utown.model.entity.Order;
import com.utown.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    boolean existsByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
            "FROM Order o JOIN o.restaurant r " +
            "WHERE o.id = :orderId AND r.owner.id = :ownerId")
    boolean existsByIdAndRestaurantOwnerId(
            @Param("orderId") Long orderId,
            @Param("ownerId") Long ownerId
    );

    @Query("SELECT o.restaurant.id FROM Order o WHERE o.id = :orderId")
    Optional<Long> findRestaurantIdByOrderId(@Param("orderId") Long orderId);
}