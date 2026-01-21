-- Test data for uTown backend
-- This file is automatically executed by Spring Boot on startup when spring.jpa.hibernate.ddl-auto=update

-- Insert test categories
INSERT IGNORE INTO categories (id, name, icon_url, priority, is_active, created_at) VALUES
(1, 'Korean Food', 'https://example.com/icons/korean.png', 1, true, NOW()),
(2, 'Japanese Food', 'https://example.com/icons/japanese.png', 2, true, NOW()),
(3, 'Chinese Food', 'https://example.com/icons/chinese.png', 3, true, NOW()),
(4, 'Fast Food', 'https://example.com/icons/fastfood.png', 4, true, NOW()),
(5, 'Cafe & Dessert', 'https://example.com/icons/cafe.png', 5, true, NOW());

-- Insert test restaurant (Owner will be Admin user with ID 11)
INSERT IGNORE INTO restaurants (id, name, description, address, city, phone, latitude, longitude, category_id, owner_id, is_open, is_active, rating, min_order_amount, delivery_fee, is_featured, created_at, updated_at) VALUES
(1, 'Seoul Kitchen', 'Authentic Korean restaurant serving traditional dishes', '123 Gangnam-daero, Gangnam-gu', 'Seoul', '+821012345678', 37.5665, 126.9780, 1, 11, true, true, 4.5, 15000, 3000, true, NOW(), NOW());

-- Insert menu items for Seoul Kitchen
INSERT IGNORE INTO menu_items (id, restaurant_id, name, description, price, category_name, image_url, is_available, is_spicy, spicy_level, sort_order, created_at, updated_at) VALUES
-- Main Dishes
(1, 1, 'Kimchi Fried Rice', 'Spicy Korean fried rice with kimchi, vegetables, and egg', 8500.00, 'Main Dishes', 'https://example.com/images/kimchi-fried-rice.jpg', true, true, 2, 1, NOW(), NOW()),
(2, 1, 'Bibimbap', 'Mixed rice bowl with vegetables, beef, egg, and gochujang sauce', 10000.00, 'Main Dishes', 'https://example.com/images/bibimbap.jpg', true, false, 0, 2, NOW(), NOW()),
(3, 1, 'Korean Fried Chicken', 'Crispy fried chicken with sweet and spicy sauce', 15000.00, 'Main Dishes', 'https://example.com/images/fried-chicken.jpg', true, true, 2, 3, NOW(), NOW()),
(4, 1, 'Bulgogi', 'Marinated beef with vegetables and rice', 12000.00, 'Main Dishes', 'https://example.com/images/bulgogi.jpg', true, false, 0, 4, NOW(), NOW()),
(5, 1, 'Japchae', 'Stir-fried glass noodles with vegetables and beef', 9000.00, 'Main Dishes', 'https://example.com/images/japchae.jpg', true, false, 0, 5, NOW(), NOW()),

-- Soups & Stews
(6, 1, 'Kimchi Jjigae', 'Spicy kimchi stew with tofu, pork, and vegetables', 8000.00, 'Soups & Stews', 'https://example.com/images/kimchi-jjigae.jpg', true, true, 3, 6, NOW(), NOW()),
(7, 1, 'Sundubu Jjigae', 'Soft tofu stew with seafood and vegetables', 8500.00, 'Soups & Stews', 'https://example.com/images/sundubu.jpg', true, true, 2, 7, NOW(), NOW()),
(8, 1, 'Doenjang Jjigae', 'Fermented soybean paste stew with vegetables', 7500.00, 'Soups & Stews', 'https://example.com/images/doenjang.jpg', true, false, 0, 8, NOW(), NOW()),

-- Snacks & Appetizers
(9, 1, 'Tteokbokki', 'Spicy Korean rice cakes in gochujang sauce', 6000.00, 'Snacks', 'https://example.com/images/tteokbokki.jpg', true, true, 3, 9, NOW(), NOW()),
(10, 1, 'Mandu (Dumplings)', 'Korean dumplings filled with meat and vegetables (6pcs)', 7000.00, 'Snacks', 'https://example.com/images/mandu.jpg', true, false, 0, 10, NOW(), NOW()),
(11, 1, 'Korean Pancake (Pajeon)', 'Savory green onion pancake', 8000.00, 'Snacks', 'https://example.com/images/pajeon.jpg', true, false, 0, 11, NOW(), NOW()),

-- Side Dishes
(12, 1, 'Kimchi', 'Traditional fermented Korean vegetables', 3000.00, 'Side Dishes', null, true, true, 1, 12, NOW(), NOW()),
(13, 1, 'Pickled Radish', 'Sweet and tangy pickled radish (Danmuji)', 2000.00, 'Side Dishes', null, true, false, 0, 13, NOW(), NOW()),
(14, 1, 'Steamed Egg', 'Soft steamed egg custard', 4000.00, 'Side Dishes', null, true, false, 0, 14, NOW(), NOW()),

-- Drinks
(15, 1, 'Coca Cola', 'Coca Cola 355ml can', 2000.00, 'Drinks', null, true, false, 0, 15, NOW(), NOW()),
(16, 1, 'Sprite', 'Sprite 355ml can', 2000.00, 'Drinks', null, true, false, 0, 16, NOW(), NOW()),
(17, 1, 'Korean Barley Tea', 'Traditional Korean barley tea (hot/cold)', 1500.00, 'Drinks', null, true, false, 0, 17, NOW(), NOW()),
(18, 1, 'Soju', 'Korean distilled spirit (Chamisul Fresh)', 4500.00, 'Drinks', null, true, false, 0, 18, NOW(), NOW());

-- Insert menu item options
INSERT IGNORE INTO menu_item_options (id, menu_item_id, name, price, type, option_group, is_default, sort_order, created_at) VALUES
-- Options for Kimchi Fried Rice (id: 1)
(1, 1, 'Extra Kimchi', 1000.00, 'EXTRA', 'Add-ons', false, 1, NOW()),
(2, 1, 'Extra Egg', 1500.00, 'EXTRA', 'Add-ons', false, 2, NOW()),
(3, 1, 'Add Cheese', 2000.00, 'EXTRA', 'Add-ons', false, 3, NOW()),

-- Options for Bibimbap (id: 2)
(4, 2, 'Beef', 0.00, 'CHOICE', 'Protein', true, 1, NOW()),
(5, 2, 'Chicken', 0.00, 'CHOICE', 'Protein', false, 2, NOW()),
(6, 2, 'Tofu (Vegetarian)', 0.00, 'CHOICE', 'Protein', false, 3, NOW()),
(7, 2, 'Extra Egg', 1500.00, 'EXTRA', 'Add-ons', false, 4, NOW()),

-- Options for Korean Fried Chicken (id: 3)
(8, 3, 'Half (6pcs)', 0.00, 'SIZE', 'Size', true, 1, NOW()),
(9, 3, 'Full (12pcs)', 5000.00, 'SIZE', 'Size', false, 2, NOW()),
(10, 3, 'Spicy', 0.00, 'CHOICE', 'Sauce', true, 3, NOW()),
(11, 3, 'Soy Garlic', 0.00, 'CHOICE', 'Sauce', false, 4, NOW()),
(12, 3, 'Half & Half', 0.00, 'CHOICE', 'Sauce', false, 5, NOW()),

-- Options for Bulgogi (id: 4)
(13, 4, 'Regular', 0.00, 'SIZE', 'Size', true, 1, NOW()),
(14, 4, 'Large', 3000.00, 'SIZE', 'Size', false, 2, NOW()),
(15, 4, 'Extra Vegetables', 2000.00, 'EXTRA', 'Add-ons', false, 3, NOW()),

-- Options for Mandu (id: 10)
(16, 10, 'Steamed', 0.00, 'CHOICE', 'Cooking Style', true, 1, NOW()),
(17, 10, 'Fried', 0.00, 'CHOICE', 'Cooking Style', false, 2, NOW()),

-- Options for Korean Barley Tea (id: 17)
(18, 17, 'Hot', 0.00, 'CHOICE', 'Temperature', true, 1, NOW()),
(19, 17, 'Cold', 0.00, 'CHOICE', 'Temperature', false, 2, NOW());
