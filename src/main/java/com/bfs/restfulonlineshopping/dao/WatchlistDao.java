package com.bfs.restfulonlineshopping.dao;

import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.User;
import com.bfs.restfulonlineshopping.entity.Watchlist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class WatchlistDao {

    SessionFactory sessionFactory;

    @Autowired
    public WatchlistDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void addToWatchlist(Product product, User user) {
        Transaction transaction = null;
        try (Session session = sessionFactory.getCurrentSession()) {
            transaction = session.beginTransaction();

            // set user.watchlist
            Watchlist watchlist = user.getWatchlist();
            if (watchlist == null) {  // user没有watchlist
                watchlist = new Watchlist();
                watchlist.setUser(user);
                user.setWatchlist(watchlist);
                // session.save(user);  // Duplicate entry 'user1@gmail.com'
            }

            // set watchlist.products
            List<Product> watchedProducts = watchlist.getWatchedProducts();  // 这个user的watchlist没有任何东西
            if (watchedProducts == null) {
                watchedProducts = new ArrayList<>();
                // watchlist.setWatchedProducts(watchedProducts);
            }
            watchedProducts.add(product);
            session.save(watchlist);

            // set product.watchlist
            List<Watchlist> pwl = product.getWatchlists();
            if (pwl == null) {
                pwl = new ArrayList<>();
                product.setWatchlists(pwl);
            }
            pwl.add(watchlist);
            session.save(product);

            transaction.commit();
        } catch (NullPointerException e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
