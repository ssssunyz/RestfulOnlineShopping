package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.*;
import com.bfs.restfulonlineshopping.entity.request.AddProductRequest;
import com.bfs.restfulonlineshopping.entity.response.GeneralResponse;
import com.bfs.restfulonlineshopping.exception.CancelingCompletedOrderException;
import com.bfs.restfulonlineshopping.exception.NotAuthorizedException;
import com.bfs.restfulonlineshopping.exception.OrderNotFoundException;
import com.bfs.restfulonlineshopping.exception.ProductNotFoundException;
import com.bfs.restfulonlineshopping.service.OrderProductService;
import com.bfs.restfulonlineshopping.service.OrderService;
import com.bfs.restfulonlineshopping.service.ProductService;
import com.bfs.restfulonlineshopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/OnlineShop/admin")
public class AdminController {

    private UserService userService;
    private ProductService productService;

    private OrderService orderService;

    private OrderProductService orderProductService;

    @Autowired
    public AdminController(UserService userService, ProductService productService,
                           OrderService orderService, OrderProductService orderProductService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
    }

    @PostMapping("/addProduct")
    public GeneralResponse addProduct(@RequestBody AddProductRequest addProductRequest) {
        System.out.println("---------In addProduct---------");

        String description = addProductRequest.getDescription();
        double wholesalePrice = addProductRequest.getWholesalePrice(),
                retailPrice = addProductRequest.getRetailPrice();
        int quantity = addProductRequest.getQuantity();
        try {
            productService.addProduct(description, wholesalePrice, retailPrice, quantity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return GeneralResponse.builder()
                .message("Product: [" + description
                        + "] with retail price = " + retailPrice + " successfully added. ")
                .build();
    }

    @GetMapping("/allProducts")
    public List<Product> getAllProductsAdmin() {
        System.out.println("---------In getAllProductsAdmin---------");

        List<Product> allProducts = productService.getAllProducts_Admin();
        if (allProducts.isEmpty())
            throw new ProductNotFoundException();

        return allProducts;
    }

    @GetMapping("/product")
    public Product getProductById(@RequestParam int productId) {
        System.out.println("---------In getProductByIdAdmin---------");

        Optional<Product> product = productService.getProductById(productId);
        if (!product.isPresent())  // no productId found
            throw new ProductNotFoundException();

        return product.get();
    }

    @PatchMapping("/completeOrder")
    public GeneralResponse completeOrder(@RequestParam String orderId) {
        // check valid orderId
        UUID orderUUID;
        try {
            orderUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new OrderNotFoundException(orderId);
        }

        Optional<Order> orderOptional = orderService.getOrderByID(orderUUID);
        if (!orderOptional.isPresent())
            throw new OrderNotFoundException(orderId);

        Order order = orderOptional.get();

        // Completed/Canceled order cannot be changed to Completed
        if (order.getStatus().equals("Completed") || order.getStatus().equals("Canceled"))
            throw new CancelingCompletedOrderException(orderId);

        // change order status
        orderService.changeStatus(orderUUID, "Completed");

        return GeneralResponse.builder()
                .message("OrderID = " + orderId + " successfully completed. ")
                .build();
    }

    @PatchMapping("/cancelOrder")
    public GeneralResponse cancelOrder(@RequestParam String orderId) {
        // check valid orderId
        UUID orderUUID;
        try {
            orderUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new OrderNotFoundException(orderId);
        }

        Optional<Order> orderOptional = orderService.getOrderByID(orderUUID);
        if (!orderOptional.isPresent())
            throw new OrderNotFoundException(orderId);

        Order order = orderOptional.get();

        // Completed/Canceled order cannot be changed to Cancel
        if (order.getStatus().equals("Completed") || order.getStatus().equals("Canceled"))
            throw new CancelingCompletedOrderException(orderId);

        // change order status
        orderService.changeStatus(orderUUID, "Canceled");

        // increase product quantity
        OrderProduct op = orderProductService.getOrderProductByOrderId(orderUUID).get();
        int productId = op.getProduct().getProductId();
        productService.decreaseQuantity(productId, -op.getPurchasedQuantity());

        return GeneralResponse.builder()
                .message("OrderID = " + orderId + " successfully canceled. ")
                .build();
    }

    @GetMapping("/viewAllOrders")
    public List<OrderDetails> viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();

        List<OrderDetails> ans = new ArrayList<>();
        for (Order order : orders) {
            // get the products in this order
            OrderProduct op = orderProductService.getOrderProductByOrderId(order.getOrderId()).get();
            Product product = op.getProduct();

            // use DTO(Data Transfer Object) to display selected fields
            // 得有@ToString
            OrderDetails orderDetails = new OrderDetails(order.getPlacementTime(), order.getStatus(),
                    product.getDescription(), op.getExecutionRetailPrice(), op.getPurchasedQuantity());

            ans.add(orderDetails);
        }
        return ans;
    }

    @GetMapping("/viewOrder")
    public OrderDetails viewOrder(@RequestParam String orderId) {
        // check valid orderId
        UUID orderUUID;
        try {
            orderUUID = UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            throw new OrderNotFoundException(orderId);
        }

        Optional<Order> orderOptional = orderService.getOrderByID(orderUUID);
        if (!orderOptional.isPresent())
            throw new OrderNotFoundException(orderId);

        Order order = orderOptional.get();

        // get the products in this order
        OrderProduct op = orderProductService.getOrderProductByOrderId(orderUUID).get();
        Product product = op.getProduct();

        // use DTO(Data Transfer Object) to display selected fields
        // 得有@ToString
        OrderDetails orderDetails = new OrderDetails(order.getPlacementTime(), order.getStatus(),
                product.getDescription(), op.getExecutionRetailPrice(), op.getPurchasedQuantity());
        return orderDetails;
    }

    @PatchMapping("/modifyProduct")
    public GeneralResponse modifyProduct(@RequestParam int productId,
                                         @RequestParam double wholesalePrice,
                                         @RequestParam double retailPrice,
                                         @RequestParam String description,
                                         @RequestParam int quantity) {

        Optional<Product> optionalProduct = productService.getProductById(productId);
        if (!optionalProduct.isPresent())
            throw new ProductNotFoundException();

        productService.modifyProduct(productId, wholesalePrice, retailPrice, description, quantity);

        return GeneralResponse.builder()
                .message("ProductId = " + productId + " successfully updated. ")
                .build();
    }

    @GetMapping("/top3MostSold")
    public List<Product> top3MostSold() {
        return productService.top3MostSold();
    }

//    @GetMapping("/amountSoldByProduct")
//    public Map<String, Integer> amountSoldByProduct() {
//        return orderProductService.amountSoldByProduct();
//    }

    @GetMapping("/mostProfitableProduct")
    public Product mostProfitable() {
        return productService.mostProfitable();
    }

    @GetMapping("/mostSpentUser")
    public List<UserDto> mostSpentUser() {
        List<User> users = orderService.mostSpentUser();
        List<UserDto> ans = new ArrayList<>();
        for (User user: users) {
            ans.add(new UserDto(user.getUserId(), user.getUsername(), user.getEmail()));
        }
        return ans;
    }
}