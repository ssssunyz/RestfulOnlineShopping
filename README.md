# Restful Online Shopping
## Tech Stack & Tools
- Spring Boot
- Hibernate (HQL, Criteria)
- MySQL
- Spring Security + JWT
- Spring AOP
- Spring Email
- RabbitMQ
- JUnit
- Mockito
- JaCoCo

## Description
RestfulOnlineShopping is an application which supports concurrent accesses where different users can shop for different products, add products to their watchlist, etc.
(see "Postman Requests" directory for all the available requests).

User Section:
- Registration:
  - prevents registration using the same username or email

- Login:
  - if correct credentials:
    - the user may proceed to the page based on their authorities
  - otherwise, a custom exception "InvalidCredentials" will be thrown

- Homepage:
  - view all the products (excluding out of stock products)
  - product details (excluding the wholesale price, only showing the retail price)
  - can place orders and see the order's details

- Purchasing:
  - can purchase listing items with a specified quantity (not exceeding the stock quantity; otherwise, "NotEnoughInventoryException" will be thrown)
  - if the order is later cancelled (either by the user herself/himself or by admin), the stock will be restored
  - later changes to the pricing will not affect exising orders' details
  - can view their top 3 most frequently purchased products

- Watchlist:
  - add/remove products to/from their watchlist

Admin Section:
- Dashboard: 
  - all orders along with their detail page (placement time, username, status (Processing, Canceled, Completed))
  - current products listed to sell, along with their detail page
- CRUD operations to the product description, wholesale_price, retail_price, and stock quantity
- cancel/complete an order, which automatically restores the stock quantity of the related products
- view the top 3 most sold products
- view the top 3 most spent users
- view the top 3 most profitable products

Security:
- Authentication: guests cannot access any endpoint other than login or registration
- Authorization: 
  - users cannot access endpoints designated to the admin
  - one user cannot access orders placed by other users, or other unrelated information

Spring Email & RabbitMQ: 
- a scheduled task will send to each user the details of their most recent order
- the task published to an exchange of RabbitMQ, binding with a queue to which an email app (see "EmailApp") listens
- the emailApp then sends the email to the corresponding user through Spring Email

Concurrency:
- some methods are implemented with CompletableFuture to maximize the performance under multi-threading environment 

AOP:
- log the time when a user places/updates an order
- @ControllerAdvice as a central exception handler

Unit Testing:
- Uses JUnit and Mockito test and JaCoCo to achieve 60% code coverage for Controller, Service and DAO layers