package com.bfs.restfulonlineshopping.service;

import com.bfs.restfulonlineshopping.dao.WatchlistDao;
import com.bfs.restfulonlineshopping.entity.Product;
import com.bfs.restfulonlineshopping.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WatchlistService {

    private WatchlistDao watchlistDao;

    @Autowired
    public WatchlistService(WatchlistDao watchlistDao) {
        this.watchlistDao = watchlistDao;
    }

    public void addToWatchlist(Product product, User user) {
        watchlistDao.addToWatchlist(product, user);
    }
}
