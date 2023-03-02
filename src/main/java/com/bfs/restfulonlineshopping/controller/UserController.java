package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.*;
import com.bfs.restfulonlineshopping.entity.response.*;
import com.bfs.restfulonlineshopping.exception.*;
import com.bfs.restfulonlineshopping.security.AuthUserDetail;
import com.bfs.restfulonlineshopping.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

@RestController
@RequestMapping("/OnlineShop/user")
public class UserController {
    private UserService userService;
    private ProductService productService;
    private OrderService orderService;

    private OrderProductService orderProductService;
    private WatchlistService watchlistService;

    @Autowired
    public UserController(UserService userService, ProductService productService,
                          OrderService orderService, OrderProductService orderProductService,
                          WatchlistService watchlistService) {
        this.userService = userService;
        this.productService = productService;
        this.orderService = orderService;
        this.orderProductService = orderProductService;
        this.watchlistService = watchlistService;
    }

    @GetMapping("/allProducts")
    public List<ProductForUser> getAllProductsUser() {
        System.out.println("---------In getAllProductsUser---------");

        List<Product> allProducts = productService.getAllProducts_User();
        if (allProducts.isEmpty())
            throw new ProductNotFoundException();

        /* 不能直接return List<Product>, 不然就算在Dao里用cq.multiselect()只选了给看的field
           不给看的field (wholesale_price和quantity) 还是会显示 只不过value变成0 */
        List<ProductForUser> hidedProducts = new ArrayList<>();
        for (Product product: allProducts) {
            ProductForUser pfu = new ProductForUser(product.getProductId(),
                    product.getDescription(),
                    product.getRetailPrice());
            hidedProducts.add(pfu);
        }

        return hidedProducts;
    }

    @GetMapping("/product")
    public ProductForUser getProductById(@RequestParam int productId) {
        System.out.println("---------In getProductByIdUser---------");

        Optional<Product> product = productService.getProductById(productId);
        if (!product.isPresent())  // no productId found
            throw new ProductNotFoundException();

        if (product.get().getQuantity() == 0)  // out of stock
            throw new ProductNotFoundException();

        Product p = product.get();
        // hide fields
        ProductForUser pfu = new ProductForUser(p.getProductId(),
                                p.getDescription(),
                                p.getRetailPrice());

        return pfu;
    }

    // 目前是一次只能买一个product的多个quantity
    @PatchMapping("/purchase")
    public GeneralResponse makePurchase(@RequestParam int productId, int purchasedQuantity) throws NotEnoughInventoryException {
        Optional<Product> p = productService.getProductById(productId);

        // check presence
        if (!p.isPresent())
            throw new ProductNotFoundException();

        // check quantity
        Product product = p.get();
        if (product.getQuantity() < purchasedQuantity)
            throw new NotEnoughInventoryException();

        // updating product
        productService.decreaseQuantity(productId, purchasedQuantity);
        System.out.println("---------updated product---------");

        // creating a "Processing" order:
        // 1. placementTime
        Timestamp placementTime = new Timestamp(System.currentTimeMillis());

        // 2. getting the user who's making the purchase (这一行会在console上print不少东西)
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        System.out.println("Current Username: " + currentUsername);
        User user = userService.getUserByUsername(currentUsername);

        Order order = new Order(placementTime, "Processing", user);
        order.setOrderId(UUID.randomUUID());

        // System.out.println("Order id: " + order.getOrderId());
        orderProductService.commitPurchase(order, product, purchasedQuantity, product.getRetailPrice(), product.getWholesalePrice());
        // orderService.creatOrder(placementTime, "Processing", user, Arrays.asList(product));

        return GeneralResponse.builder()
                .message("Successfully purchased " + purchasedQuantity + " of [" + product.getDescription() + "]. " +
                        "Use OrderId = [" + order.getOrderId() + "] if you want to cancel this order. ")
                .build();
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

        // check user is the owner of this order
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (!order.getUser().getUsername().equals(currentUsername))
            throw new NotAuthorizedException();

        // get the products in this order
        OrderProduct op = orderProductService.getOrderProductByOrderId(orderUUID).get();
        Product product = op.getProduct();

        // use DTO(Data Transfer Object) to display selected fields
        // 得有@ToString
        OrderDetails orderDetails = new OrderDetails(order.getPlacementTime(), order.getStatus(),
                product.getDescription(), op.getExecutionRetailPrice(), op.getPurchasedQuantity());
        return orderDetails;
    }

    @GetMapping("/viewAllOrders")
    public List<OrderDetails> viewAllOrders() {
        // get the requesting user
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        int userId = userService.getUserByUsername(currentUsername).getUserId();

        List<Order> orders = orderService.getOrdersByUserId(userId);

        List<OrderDetails> ans = new ArrayList<>();
        for (Order order: orders) {
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

        // check user is the owner of this order
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (!order.getUser().getUsername().equals(currentUsername))
            throw new NotAuthorizedException();

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

    @PatchMapping("/addToWatchlist")
    public GeneralResponse addToWatchlist(@RequestParam int productId) {

        // check existence of product
        Optional<Product> productOptional = productService.getProductById(productId);
        if (!productOptional.isPresent())
            throw new ProductNotFoundException();

        Product product = productOptional.get();

        // get current user making the request
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userService.getUserByUsername(currentUsername);

        watchlistService.addToWatchlist(product, user);

        return GeneralResponse.builder()
                .message("ProductId = " + productId + " successfully added to watchlist. ")
                .build();
    }

//    @GetMapping("/viewWatchlist")
//    public List<ProductForUser> viewWatchlist() {
//        // get current user making the request
//        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//        int userId = userService.getUserByUsername(currentUsername).getUserId();
//
//        Watchlist wl = userService.viewWatchlist(userId);
//        List<Product> products = wl.getWatchedProducts();
//        List<ProductForUser> pfu = new ArrayList<>();
//        for (Product p : products) {
//            pfu.add(new ProductForUser(p.getProductId(), p.getDescription(), p.getRetailPrice()));
//        }
//        return pfu;
//    }


    @GetMapping("/top3FrequentPurchased")
    public List<ProductForUser> top3FrequentPurchased() {
        // get current user making the request
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        int userId = userService.getUserByUsername(currentUsername).getUserId();

        List<Product> top3 = productService.top3FrequentPurchased(userId);
        List<ProductForUser> ans = new ArrayList<>();
        for (Product product: top3) {
            ProductForUser pfu = new ProductForUser(product.getProductId(),
                    product.getDescription(),
                    product.getRetailPrice());
            ans.add(pfu);
        }

        return ans;
//        return GeneralResponse.builder()
//                .message("ProductId = " + productId + " successfully added to watchlist. ")
//                .build();
    }

    // this method is made for the Asynchronous homework
    @GetMapping("/{userId}/order/{orderId}")
    public UserOrderResponse getUserOrder(@PathVariable int userId, @PathVariable String orderId) {
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

        // check userId is the owner of this order
        User user = order.getUser();
        if (user.getUserId() != userId)
            throw new NotAuthorizedException();

        // Building UserOrderResponse
        ServiceStatus serviceStatus = new ServiceStatus(true);
        UserResponse userResponse = new UserResponse(user.getUsername(), user.getEmail());
        OrderProduct op = orderProductService.getOrderProductByOrderId(orderUUID).get();
        Product product = productService.getProductById(op.getProduct().getProductId()).get();
        OrderItemResponse oir = new OrderItemResponse(product.getDescription(), op.getPurchasedQuantity());
        List<OrderItemResponse> orderItemResponseList = Arrays.asList(oir);
        OrderResponse orderResponse = new OrderResponse(order.getOrderId(), order.getPlacementTime(), op.getExecutionRetailPrice(), orderItemResponseList);

        return new UserOrderResponse(serviceStatus, orderResponse, userResponse);
    }
}

