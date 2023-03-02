package com.bfs.restfulonlineshopping.dao;

import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.OrderProduct;
import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDao {

    SessionFactory sessionFactory;

    @Autowired
    public ProductDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addProduct(String description, double wholesalePrice, double retailPrice, int quantity) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            Product product = new Product(description, wholesalePrice, retailPrice, quantity);
            session.save(product);

            transaction.commit();
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }
    }

    public List<Product> getAllProducts_User(){
        Session session = null;
        Transaction transaction = null;
        List<Product> products = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<Product> root = cq.from(Product.class);
            // excluding out of stock products; hiding wholesale_price, and quantity to users
            /* cq.multiselect method is selecting specific fields from the Product entity and creating a new object
                所以Product.java得加一个对应的constructor
                不然会有error: Unable to locate appropriate constructor on class [.....Product].
             */
//            cq.multiselect(
//                    root.get("productId"),  // 对应的是Product.java的名字 而不是db里的
//                    root.get("description"),
//                    root.get("retailPrice")
//                    ).where(cb.gt(root.get("quantity"), 0));  // cb.gt: greater than
            cq.select(root).where(cb.gt(root.get("quantity"), 0));
            products = session.createQuery(cq).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return products;
    }

    public List<Product> getAllProducts_Admin(){
        Session session = null;
        Transaction transaction = null;
        List<Product> products = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<Product> root = cq.from(Product.class);
            cq.select(root);
            products = session.createQuery(cq).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return products;
    }

    public Optional<Product> getProductById(int productId){
        Session session = null;
        Transaction transaction = null;
        Optional<Product> product = null;
        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            // excluding out of stock products; hiding wholesale_price, and quantity to users
//            String hql = "SELECT p.productId, p.description, p.retailPrice " +
//                    "FROM Product p " +
//                    "WHERE p.productId = :productId";
//                    // dealing with out-of-stock products in UserController layer
//            TypedQuery<Product> query = session.createQuery(hql, Product.class);
//            query.setParameter("productId", productId);
//            product = query.getResultList();
//
//            transaction.commit();

            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<Product> root = cq.from(Product.class);
            // excluding out of stock products; hiding wholesale_price, and quantity to users
            /* cq.multiselect method is selecting specific fields from the Product entity and creating a new object
                所以Product.java得加一个对应的constructor
                不然会有error: Unable to locate appropriate constructor on class [.....Product].
             */
//            cq.multiselect(
//                    root.get("productId"),  // 对应的是Product.java的名字 而不是db里的
//                    root.get("description"),
//                    root.get("retailPrice")
//            )
            cq.select(root).where(cb.equal(root.get("productId"), productId));
            // dealing with out-of-stock products in UserController layer
            product = session.createQuery(cq).uniqueResultOptional();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return product;
    }


    public void decreaseQuantity(int productId, int quantity) {
        System.out.println("--------In ProductDao: makePurchase-------");
        Session session = null;
        Transaction transaction = null;
        int originalQuantity = getProductById(productId).get().getQuantity();
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            String hql = "UPDATE Product p SET p.quantity = :quantity WHERE p.productId = :productId";
            Query query = session.createQuery(hql);
            query.setParameter("quantity", originalQuantity - quantity);
            query.setParameter("productId", productId);
            query.executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
    }

    public void modifyProduct(int productId, double wholesalePrice, double retailPrice, String description, int quantity) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaUpdate<Product> update = cb.createCriteriaUpdate(Product.class);
            Root<Product> root = update.from(Product.class);
            update.set("wholesalePrice", wholesalePrice);
            update.set("retailPrice", retailPrice);
            update.set("description", description);
            update.set("quantity", quantity);
            update.where(cb.equal(root.get("productId"), productId));

            session.createQuery(update).executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
    }

    public List<Product> top3FrequentPurchased(int userId) {
        System.out.println("--------In ProductDao: top3FrequentPurchased-------");
        Transaction transaction = null;
        Session session = null;
        List top3 = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<OrderProduct> orderProductRoot = cq.from(OrderProduct.class);
            Join<OrderProduct, Product> productJoin = orderProductRoot.join("product");
            cq.select(productJoin)
                    .groupBy(productJoin)
                    .orderBy(cb.desc(cb.count(productJoin)))
                    .where(
                            cb.and(
                                    cb.equal(orderProductRoot.get("order").get("user"), userId),
                                    cb.notEqual(orderProductRoot.get("order").get("status"), "Canceled")
                            )
                    );

            TypedQuery<Product> query = session.createQuery(cq)
                    .setMaxResults(3);
            top3 = query.getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return top3;
    }

    public List<Product> top3MostSold() {
        System.out.println("--------In ProductDao: top3FrequentPurchased-------");
        Transaction transaction = null;
        Session session = null;
        List top3 = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<OrderProduct> orderProductRoot = cq.from(OrderProduct.class);
            Join<OrderProduct, Product> productJoin = orderProductRoot.join("product");
            cq.select(productJoin)
                    .groupBy(productJoin)
                    .orderBy(cb.desc(cb.count(productJoin)))
                    .where(
                            cb.and(
                                    cb.notEqual(orderProductRoot.get("order").get("status"), "Canceled"),
                                    cb.notEqual(orderProductRoot.get("order").get("status"), "Processing")
                            )
                    );

            TypedQuery<Product> query = session.createQuery(cq)
                    .setMaxResults(3);
            top3 = query.getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return top3;
    }

//    public List<Product> top3RecentlyPurchased(int userId) {
//        System.out.println("--------In ProductDao: top3RecentlyPurchased-------");
//        Transaction transaction = null;
//        Session session = null;
//        List top3 = null;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//
//            CriteriaBuilder cb = session.getCriteriaBuilder();
//            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
//            Root<OrderProduct> orderProductRoot = cq.from(OrderProduct.class);
//            Join<OrderProduct, Product> productJoin = orderProductRoot.join("product");
//            cq.select(productJoin)
//                    .groupBy(productJoin)
//                    .orderBy(cb.desc(cb.count(productJoin)))
//                    .where(
//                            cb.and(
//                                    cb.equal(orderProductRoot.get("order").get("user"), userId),
//                                    cb.notEqual(orderProductRoot.get("order").get("status"), "Canceled")
//                            )
//                    );
//
//            TypedQuery<Product> query = session.createQuery(cq)
//                    .setMaxResults(3);
//            top3 = query.getResultList();
//
//            transaction.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (transaction != null)
//                transaction.rollback();
//        }
//        return top3;
//    }

    public Product mostProfitable() {
        System.out.println("--------In ProductDao: mostProfitable-------");
        Transaction transaction = null;
        Session session = null;
        Product mostProfitableProduct = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Product> cq = cb.createQuery(Product.class);
            Root<Product> productRoot = cq.from(Product.class);
            cq.select(productRoot);
            cq.orderBy(cb.desc(cb.diff(productRoot.get("retailPrice"), productRoot.get("wholesalePrice"))));
            mostProfitableProduct = session.createQuery(cq).setMaxResults(1).getSingleResult();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        return mostProfitableProduct;
    }
}
