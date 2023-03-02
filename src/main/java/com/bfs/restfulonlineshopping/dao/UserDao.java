package com.bfs.restfulonlineshopping.dao;

import com.bfs.restfulonlineshopping.entity.Order;
import com.bfs.restfulonlineshopping.entity.Permission;
import com.bfs.restfulonlineshopping.entity.User;
import com.bfs.restfulonlineshopping.entity.Watchlist;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDao {

    // should be stored in the database
    // user table, permission table
//    private final List<User> users = Arrays.asList(
//            new User(1, "user1", "user1@gmail.com", "pswd")
//    );

    SessionFactory sessionFactory;

    @Autowired
    public UserDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void createUser(String username, String email, String password) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();

            // String hql = "INSERT INTO User(username, email, password) VALUES(:username, :email, :password)";
            // HQL 的insert不支持VALUES... 只能insert from another table

            User user = new User(username, email, password);
            //user.setUserId(UUID.randomUUID());
            Permission userPermission = new Permission("user");
            Permission adminPermission = new Permission("admin");
            session.save(userPermission); // persist the Permission objects first
//            session.save(adminPermission);

            user.setPermissions(Arrays.asList(userPermission));  // regular user
            // user.setPermissions(Arrays.asList(adminPermission, userPermission));  // admin
            session.save(user);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }
    }

    /*  HQL有一点不好就是只有runtime的时候才知道对不对
        所以可以改用Criteria:
            - programmatic, type-safe way to express a query
            - type-safe in terms of using interfaces and classes to represent various structural parts
            of a query such as the query itself, the select clause, or an order-by, etc */

    // <-- Criteria -- >
    public List<User> getAllUsers(){
        Session session = null;
        List<User> users = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            cq.select(root);
            users = session.createQuery(cq).getResultList();
        } catch (Exception e) {
                e.printStackTrace();
                if (transaction != null)
                    transaction.rollback();
            }
        finally {
                session.close();
            }

        return (users.isEmpty()) ? null : users;
    }

    // <-- Criteria -- >
    // UserService的loadUserByUsername()是override UserDetailsService的method
    public Optional<User> loadUserByUsername(String username) {
        // return getAllUsers().stream().filter(user -> username.equals(user.getUsername())).findAny();
        System.out.println("In UserDao: loadUserByUsername");
        Session session = null;
        Transaction transaction = null;
        Optional<User> user = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();  // CriteriaBuilder
            CriteriaQuery<User> cq = cb.createQuery(User.class);  // CriteriaQuery
            Root<User> root = cq.from(User.class);
            cq.select(root);
            cq.where(cb.equal(root.get("username"), username));  // 也可以直接cq.select(root).where(predicate);
            user = session.createQuery(cq).uniqueResultOptional();  // Query.uniqueResultOptional();
                                                                    // Query.getResultList();
            System.out.println("user" + user);
            transaction.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }

        return user;
    }

    // <-- Criteria -- >
    //@Transactional
    public Optional<User> loadUserByEmail(String email) {
        // return getAllUsers().stream().filter(user -> email.equals(user.getEmail())).findAny();
        Session session = null;
        Transaction transaction = null;
        Optional<User> user = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            Predicate predicate = cb.equal(root.get("email"), email);  // equality predicate
            cq.select(root).where(predicate);
            user = session.createQuery(cq).uniqueResultOptional();
            transaction.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }

        return user;
    }

    public Watchlist viewWatchlist(int userId) {
        Session session = null;
        Transaction transaction = null;
        Optional<User> user = null;
        try {
            session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> cq = cb.createQuery(User.class);
            Root<User> root = cq.from(User.class);
            Predicate predicate = cb.equal(root.get("userId"), userId);  // equality predicate
            cq.select(root).where(predicate);
            user  = session.createQuery(cq).uniqueResultOptional();
            transaction.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            if (transaction != null)
                transaction.rollback();
        }
        finally {
            session.close();
        }

        return user.get().getWatchlist();
    }
}