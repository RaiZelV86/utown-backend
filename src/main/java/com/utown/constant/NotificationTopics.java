package com.utown.constant;

public final class NotificationTopics {

    private NotificationTopics() {
        throw new IllegalStateException("Utility class");
    }

    public static final String TOPIC_PREFIX = "/topic";
    public static final String QUEUE_PREFIX = "/queue";
    public static final String APP_PREFIX = "/app";

   
    public static final String RESTAURANT_ORDERS_PATTERN = "/topic/restaurants/%d/orders";

   
    public static final String ORDER_UPDATES_PATTERN = "/topic/orders/%d";

    
    public static final String USER_NOTIFICATIONS_QUEUE = "/queue/notifications";

    public static String restaurantOrders(Long restaurantId) {
        return String.format(RESTAURANT_ORDERS_PATTERN, restaurantId);
    }

    /**
     * Формирует топик для обновлений конкретного заказа.
     * @param orderId ID заказа
     * @return /topic/orders/{orderId}
     */
    public static String orderUpdates(Long orderId) {
        return String.format(ORDER_UPDATES_PATTERN, orderId);
    }
}
