SET FOREIGN_KEY_CHECKS=0;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `favorites` (
  `favorite_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `store_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_favorite_user_store` (`user_id`,`store_id`),
  KEY `FK8n3g952187w3gpbxplj7by1gc` (`store_id`),
  CONSTRAINT `FK8n3g952187w3gpbxplj7by1gc` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`),
  CONSTRAINT `FKk7du8b8ewipawnnpg76d55fus` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `markets` (
  `market_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `address` varchar(255) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `district` varchar(100) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `status` enum('ACTIVE','INACTIVE') NOT NULL,
  PRIMARY KEY (`market_id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `notification_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` varchar(500) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `is_read` bit(1) NOT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `target_type` enum('PRODUCT_DISCOUNT','RESERVATION','STORE') DEFAULT NULL,
  `title` varchar(150) NOT NULL,
  `type` enum('DISCOUNT_CREATED','PICKUP_REMINDER','RESERVATION_CONFIRMED','RESERVATION_REJECTED','RESERVATION_REQUESTED') NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`notification_id`),
  KEY `FK9y21adhxn0ayjhfocscqox7bh` (`user_id`),
  CONSTRAINT `FK9y21adhxn0ayjhfocscqox7bh` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_discounts` (
  `product_discount_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `discount_price` decimal(10,2) NOT NULL,
  `discount_type` enum('AMOUNT','FIXED_AMOUNT','PERCENT','PERCENTAGE') NOT NULL,
  `discount_value` decimal(10,2) NOT NULL,
  `end_at` datetime(6) NOT NULL,
  `remaining_quantity` int(11) NOT NULL,
  `start_at` datetime(6) NOT NULL,
  `status` enum('ACTIVE','ENDED','INACTIVE','SCHEDULED') NOT NULL,
  `title` varchar(150) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`product_discount_id`),
  KEY `FK569m0j2bdds7b29fmo0i9jmvl` (`product_id`),
  CONSTRAINT `FK569m0j2bdds7b29fmo0i9jmvl` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `description` text DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `original_price` decimal(10,2) NOT NULL,
  `status` enum('HIDDEN','INACTIVE','ON_SALE','SOLD_OUT') NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `store_id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`product_id`),
  KEY `FKgcyffheofvmy2x5l78xam63mc` (`store_id`),
  CONSTRAINT `FKgcyffheofvmy2x5l78xam63mc` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation_items` (
  `reservation_item_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `discount_price` decimal(10,2) NOT NULL,
  `quantity` int(11) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `product_discount_id` bigint(20) DEFAULT NULL,
  `reservation_id` bigint(20) NOT NULL,
  PRIMARY KEY (`reservation_item_id`),
  KEY `FK632fy1kupsnllv3gjle4hfkvy` (`product_id`),
  KEY `FKopmvjqubnnuirxh947vi8qmwe` (`product_discount_id`),
  KEY `FKahatpyi4mk3o5dcqt7d51r31k` (`reservation_id`),
  CONSTRAINT `FK632fy1kupsnllv3gjle4hfkvy` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `FKahatpyi4mk3o5dcqt7d51r31k` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`),
  CONSTRAINT `FKopmvjqubnnuirxh947vi8qmwe` FOREIGN KEY (`product_discount_id`) REFERENCES `product_discounts` (`product_discount_id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `reservation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `pickup_date` date NOT NULL,
  `pickup_time` time NOT NULL,
  `request_note` varchar(500) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `store_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `reject_reason` varchar(500) DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `reservation_quantity` int(11) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `product_discount_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  KEY `FKjtwfyd534iam9vblvekr85me3` (`store_id`),
  KEY `FKb5g9io5h54iwl2inkno50ppln` (`user_id`),
  KEY `FKdiak2qwbjwpp948u7xar3u43x` (`product_discount_id`),
  CONSTRAINT `FKb5g9io5h54iwl2inkno50ppln` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKdiak2qwbjwpp948u7xar3u43x` FOREIGN KEY (`product_discount_id`) REFERENCES `product_discounts` (`product_discount_id`),
  CONSTRAINT `FKjtwfyd534iam9vblvekr85me3` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `review_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `content` varchar(1000) NOT NULL,
  `rating` int(11) NOT NULL,
  `reservation_id` bigint(20) NOT NULL,
  `store_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `uk_review_reservation` (`reservation_id`),
  KEY `FKg5v2uypi6rxq6647cqef2d7pt` (`store_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FK4mjgyh1vc99vaf9l18j2ejc89` FOREIGN KEY (`reservation_id`) REFERENCES `reservations` (`reservation_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKg5v2uypi6rxq6647cqef2d7pt` FOREIGN KEY (`store_id`) REFERENCES `stores` (`store_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `stores` (
  `store_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `close_time` time DEFAULT NULL,
  `description` text DEFAULT NULL,
  `name` varchar(150) NOT NULL,
  `open_time` time DEFAULT NULL,
  `phone` varchar(30) DEFAULT NULL,
  `status` enum('CLOSED','INACTIVE','OPEN') NOT NULL,
  `market_id` bigint(20) NOT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`store_id`),
  KEY `FK8nsd26uc9uyl2w6ax98sg24ij` (`market_id`),
  KEY `FK62smc31fbgclsu56aa4y2hrxg` (`owner_id`),
  CONSTRAINT `FK62smc31fbgclsu56aa4y2hrxg` FOREIGN KEY (`owner_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK8nsd26uc9uyl2w6ax98sg24ij` FOREIGN KEY (`market_id`) REFERENCES `markets` (`market_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'USER',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
SET FOREIGN_KEY_CHECKS=1;
