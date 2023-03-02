package com.bfs.restfulonlineshopping.controller;

import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.request.AddProductRequest;
import com.bfs.restfulonlineshopping.entity.response.GeneralResponse;
import com.bfs.restfulonlineshopping.service.OrderProductService;
import com.bfs.restfulonlineshopping.service.OrderService;

import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.service.ProductService;
import com.bfs.restfulonlineshopping.service.UserService;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
//@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductService productService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderProductService orderProductService;

    @Test
    public void testAddProduct() throws Exception {
        // Create an instance of AddProductRequest
        AddProductRequest addProductRequest = new AddProductRequest();
        addProductRequest.setDescription("test product");
        addProductRequest.setWholesalePrice(10.0);
        addProductRequest.setRetailPrice(20.0);
        addProductRequest.setQuantity(100);

        // Mock the addProduct method of the productService
        Mockito.doNothing().when(productService).addProduct(
                addProductRequest.getDescription(),
                addProductRequest.getWholesalePrice(),
                addProductRequest.getRetailPrice(),
                addProductRequest.getQuantity()
        );

        // Call the addProduct method of the adminController and verify the response
        GeneralResponse response = adminController.addProduct(addProductRequest);
        assertNotNull(response);
//        assertEquals();
    }

    @Test
    public void testGetAllProductsAdmin() throws Exception {
        // Mock the getAllProducts_Admin method of the productService
        List<Product> productList = new ArrayList<>();
        productList.add(new Product());
        Mockito.when(productService.getAllProducts_Admin()).thenReturn(productList);

        // Call the getAllProductsAdmin method of the adminController and verify the response
        List<Product> response = adminController.getAllProductsAdmin();
        assertNotNull(response);
        assertEquals(productList.size(), response.size());
    }

    @Test
    public void testGetProductById() throws Exception {
        // Create a sample productId
        int productId = 1;

        // Mock the getProductById method of the productService
        Optional<Product> productOptional = Optional.of(new Product());
        Mockito.when(productService.getProductById(productId)).thenReturn(productOptional);

        // Call the getProductById method of the adminController and verify the response
        Product response = adminController.getProductById(productId);
        assertNotNull(response);
        assertEquals(productOptional.get(), response);
    }

    @Test
    public void testCompleteOrder() throws Exception {
        // Create a sample orderId
        String orderId = UUID.randomUUID().toString();

        // Mock the getOrderByID method of the orderService
        Order order = new Order();
        order.setStatus("Processing");
        Optional<Order> orderOptional = Optional.of(order);
        when(orderService.getOrderByID(UUID.fromString(orderId))).thenReturn(orderOptional);

        // Mock the changeStatus method of the orderService
        Mockito.doNothing().when(orderService).changeStatus(UUID.fromString(orderId), "Completed");

        // Call the completeOrder method of the adminController and verify the response
        GeneralResponse response = adminController.completeOrder(orderId);
        assertNotNull(response);
        assertEquals("OrderID = " + orderId + " successfully completed. ", response.getMessage());
    }
}