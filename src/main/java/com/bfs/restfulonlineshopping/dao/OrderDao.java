package com.bfs.restfulonlineshopping.dao;

import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.OrderProduct;
import com.bfs.restfulonlineshopping.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public class OrderDao {
    SessionFactory sessionFactory;

    @Autowired
    public OrderDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

//    public void createOrder(Timestamp placementTime, String status, User user, List<Product> boughtProducts) {
//        System.out.println("--------In OrderDao: createOrder-------");
//        Session session = null;
//        Transaction transaction = null;
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//            System.out.println("---------before hql---------");
//
//            Time sqlTime = new Time(System.currentTimeMillis());
//            Order order = new Order(sqlTime, status, user, boughtProducts);
//            // Order order = new Order(placementTime, status, user, boughtProducts);
//            session.save(order);
//            System.out.println("---------after hql---------");
//
//            transaction.commit();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            if (transaction != null) {
//                transaction.rollback();
//            }
//        }
//        finally {
//            session.close();
//        }

    public Optional<Order> getOrderByID(UUID orderId) {
        System.out.println("--------In OrderDao: getOrderByID-------");
        Session session = null;
        Transaction transaction = null;
        Optional<Order> order = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);
            cq.select(root).where(cb.equal(root.get("orderId"), orderId));
            order = session.createQuery(cq).uniqueResultOptional();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return order;
    }

    public void changeStatus(UUID orderId, String status) {
        System.out.println("--------In OrderDao: changeStatus-------");
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            String hql = "UPDATE Order o SET o.status = :status WHERE o.orderId = :orderId";
            Query query = session.createQuery(hql);
            query.setParameter("status", status);
            query.setParameter("orderId", orderId);
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

    public List<Order> getOrdersByUserId(int userId) {
        System.out.println("--------In OrderDao: getOrdersByUserId-------");
        Session session = null;
        Transaction transaction = null;
        List<Order> order = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);
            Join<OrderProduct, Order> userJoin = root.join("user");  // Order.java存的是User obj 得先join一下
            cq.select(root).where(cb.equal(userJoin.get("userId"), userId));
            order = session.createQuery(cq).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return order;
    }

    public List<Order> getAllOrders() {
        System.out.println("--------In OrderDao: getAllOrders-------");
        Session session = null;
        Transaction transaction = null;
        List<Order> orders = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Order> cq = cb.createQuery(Order.class);
            Root<Order> root = cq.from(Order.class);
            cq.select(root);
            orders = session.createQuery(cq).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return orders;
    }

    public List<User> mostSpentUser() {
        System.out.println("--------In OrderDao: mostSpentUser-------");
        Session session = null;
        Transaction transaction = null;
        List<User> users = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);

            Root<OrderProduct> orderProduct = query.from(OrderProduct.class);
            Join<OrderProduct, Order> order = orderProduct.join("order");
            Join<Order, User> user = order.join("user");

            Expression<Double> totalSpent = cb.diff(orderProduct.get("executionRetailPrice"), orderProduct.get("executionWholesalePrice"));

            Predicate statusPredicate = cb.not(cb.or(
                    cb.equal(order.get("status"), "Canceled"),
                    cb.equal(order.get("status"), "Processing")
            ));

            Expression<Double> maxTotalSpent = cb.max(totalSpent);
            query.select(user)
                    .where(statusPredicate)
                    .groupBy(user)
                    .having(cb.equal(cb.max(totalSpent), maxTotalSpent))
                    .orderBy(cb.asc(user.get("username")));

            users = session.createQuery(query).getResultList();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }

        return users;
    }
}
