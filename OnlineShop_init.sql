-- 先 drop database OnlineShopping, 再create database OnlineShopping, 然后start the application --
-- run these sql after starting the application! --

use OnlineShopping;

INSERT INTO OnlineShopping.`User` (user_id,email,password,username,fk_watchlist) VALUES
	 (3,'admin@gmail.com','pswd','admin',NULL),
	 (6,'user1@gmail.com','pswd','user1',NULL),
	 (9,'user2@gmail.com','pswd','user2',NULL),
	 (12,'user3@gmail.com','pswd','user3',NULL);

INSERT INTO OnlineShopping.Permission (permission_id,`role`) VALUES
	 (1,'user'),
	 (2,'admin'),
	 (4,'user'),
	 (5,'admin'),
	 (7,'user'),
	 (8,'admin'),
	 (10,'user'),
	 (11,'admin');

INSERT INTO OnlineShopping.user_permission (fk_user,fk_permission) VALUES
	 (3,2),
	 (3,1),
	 (6,4),
	 (9,7),
	 (12,10);

INSERT INTO OnlineShopping.Product (product_id,description,quantity,retail_price,wholesale_price) VALUES
	 (13,'rabbit',100,30.0,20.0),
	 (14,'cat',100,30.0,20.0),
	 (15,'flower',80,9.99,4.99),
	 (16,'pants',120,50.99,10.99),
	 (17,'out of stock item',0,50.99,10.99);

INSERT INTO OnlineShopping.Orders (orderId,placement_time,status,fk_user) VALUES
	 ('3ddda56a-8e19-4fe8-a1af-5fa15ec8b2ab','2023-02-17 06:20:36.360000','Canceled',6),
	 ('bcc2cd28-548c-4c19-ab82-ebd665f391b8','2023-02-17 08:12:58.481000','Completed',6);

INSERT INTO OnlineShopping.order_product (execution_retail_price,execution_wholesale_price,purchased_quantity,fk_order,fk_product) VALUES
	 (50.99,10.99,2,'3ddda56a-8e19-4fe8-a1af-5fa15ec8b2ab',16),
	 (50.99,10.99,2,'bcc2cd28-548c-4c19-ab82-ebd665f391b8',16);