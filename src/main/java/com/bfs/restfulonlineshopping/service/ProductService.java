package com.bfs.restfulonlineshopping.service;

import com.bfs.restfulonlineshopping.dao.ProductDao;
import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    ProductDao productDao;

    @Autowired
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Transactional
    public void addProduct(String description, double wholesalePrice, double retailPrice, int quantity) {
        productDao.addProduct(description, wholesalePrice, retailPrice, quantity);
    }

    @Transactional
    public List<Product> getAllProducts_User() {
        return productDao.getAllProducts_User();
    }

    public List<Product> getAllProducts_Admin() {
        return productDao.getAllProducts_Admin();
    }

    //@Transactional
    public Optional<Product> getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    public void decreaseQuantity(int productId, int quantity) {
        productDao.decreaseQuantity(productId, quantity);
    }

    public List<Product> top3FrequentPurchased(int userId) {
        return productDao.top3FrequentPurchased(userId);
    }

    public void modifyProduct(int productId, double wholesalePrice, double retailPrice, String description, int quantity) {
        productDao.modifyProduct(productId, wholesalePrice, retailPrice, description, quantity);
    }

    public List<Product> top3MostSold() {
        return productDao.top3MostSold();
    }


    public Product mostProfitable() {
        return productDao.mostProfitable();
    }
}