package com.bfs.restfulonlineshopping.dao;

import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.OrderProduct;
import com.bfs.restfulonlineshopping.entity.Product;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.sql.Time;
import java.util.*;

@Repository
public class OrderProductDao {

    SessionFactory sessionFactory;

    @Autowired
    public OrderProductDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void commitPurchase(Order order, Product product, int purchasedQuantity, double retailPrice, double wholesalePrice) {
        System.out.println("--------In OrderProductDao: commitPurchase-------");
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            OrderProduct orderProduct = new OrderProduct(order, product, purchasedQuantity, retailPrice, wholesalePrice);
            session.save(order);  // 得先save order 不然会有那什么save before persisting transient obj error
            session.save(orderProduct);

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

    public Optional<OrderProduct> getOrderProductByOrderId(UUID orderId) {
        System.out.println("--------In OrderProductDao: getOrderProductByOrderId-------");
        Session session = null;
        Transaction transaction = null;
        Optional<OrderProduct> orderProduct = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<OrderProduct> cq = cb.createQuery(OrderProduct.class);
            Root<OrderProduct> root = cq.from(OrderProduct.class);
            Join<OrderProduct, Order> orderJoin = root.join("order");
            cq.select(root).where(cb.equal(orderJoin.get("orderId"), orderId));
            // cq.select(root).where(cb.equal(root.get("order"), orderId));  // order是Order, 不能直接和orderId比较
            orderProduct = session.createQuery(cq).uniqueResultOptional();

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }
        return orderProduct;
    }

//    public Map<String, Long> amountSoldByProduct() {
//        System.out.println("--------In OrderProductDao: amountSoldByProduct-------");
//        Session session = null;
//        Transaction transaction = null;
//        Optional<OrderProduct> orderProduct = null;
//        Map<String, Integer> ans = new HashMap<>();
//        try {
//            session = sessionFactory.getCurrentSession();
//            transaction = session.beginTransaction();
//            CriteriaBuilder cb = session.getCriteriaBuilder();
//            CriteriaQuery<Tuple> cq = cb.createTupleQuery();
//            Root<OrderProduct> orderProductRoot = cq.from(OrderProduct.class);
//            cq.multiselect(orderProductRoot.get("product"),
//                    cb.sum(orderProductRoot.get("purchasedQuantity")));
//
//            Join<OrderProduct, Order> order = orderProductRoot.join("order");
//            cq.where(cb.not(order.get("status").in("Canceled", "Processing")));
//            cq.groupBy(orderProductRoot.get("product"));
//            List<Tuple> results = session.createQuery(cq).getResultList();
//            for (Tuple tuple : results) {
//                Product product = tuple.get(0, Product.class);
////                Integer amountSold = tuple.get(1, Integer.class);
//                ans.put(product.getDescription(), (Integer) tuple.get(1));
//            }
//
//            transaction.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (transaction != null)
//                transaction.rollback();
//        }
//        finally {
//            session.close();
//        }
//        return ans;
//    }
}
