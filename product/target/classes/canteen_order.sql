CREATE TABLE `product` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NULL,
`price` decimal(20,0) NULL,
`additinal` varchar(20) NULL,
`window_id` bigint(20) NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `seller` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NULL,
`layer` int(11) NULL,
`positon` varchar(255) NULL,
`canteen_id` int(11) NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `school` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NULL,
`region_name` varchar(255) NULL,
`country_name` varchar(255) NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `canteen` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`name` varchar(255) NULL,
`school_id` int(11) NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `order` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`status` varchar(255) NULL,
`price` decimal(10,2) NULL,
`user_id` bigint(20) NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `student` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`student_number` varchar(255) NULL,
`password` varchar(255) NULL,
`school_id` bigint(20) NULL,
`phone_number` int NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `order_item` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`order_id` bigint(20) NULL,
`goods_id` bigint(20) NULL,
`count` int(20) NULL,
PRIMARY KEY (`id`) 
);
CREATE TABLE `staff` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`teacher_number` varchar(255) NULL,
`password` varchar(255) NULL,
`school_id` bigint(20) NULL,
`phone_number` int NULL,
PRIMARY KEY (`id`) 
);
