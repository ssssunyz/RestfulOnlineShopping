package com.bfs.restfulonlineshopping.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
// import org.springframework.context.annotation.Configuration;
// 'org.hibernate.cfg.Configuration' is already defined in a single-type import
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class HibernateConfig {

    private static final SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

//    @Bean
    public static Session openSession() {
        return sessionFactory.openSession();
    }

    @Bean
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    // @Bean  // 学SpringSecurityConfig给这边所有method加了@Bean...
    // 结果一开始就把sessionFactory给close了 后面一直有error: EntityManagerFactory is closed
    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}