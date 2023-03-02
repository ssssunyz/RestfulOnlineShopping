package com.bfs.restfulonlineshopping.AOP;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

// Advice class
@Aspect
@Component
public class ShopAspects {

    private Logger logger = LoggerFactory.getLogger(ShopAspects.class);

    // 下面的这些annotation都是Advice
    @After("com.bfs.restfulonlineshopping.AOP.PointCuts.makePurchasePointCut()")
    public void logPurchaseTime(){
        logger.info("From ShopAspects.logPurchaseTime in controller: " + new Timestamp(System.currentTimeMillis()));
    }

    @After("com.bfs.restfulonlineshopping.AOP.PointCuts.completeOrderPointCut()")
    public void logCompleteOrderTime(){
        logger.info("From ShopAspects.logCompleteOrderTime in controller: " + new Timestamp(System.currentTimeMillis()));
    }

    @After("com.bfs.restfulonlineshopping.AOP.PointCuts.cancelOrderPointCut()")
    public void logCancelOrderTime(){
        logger.info("From ShopAspects.logCancelOrderTime in controller: " + new Timestamp(System.currentTimeMillis()));
    }

    // below two methods are made for the Asynchronous homework
    @Around("com.bfs.restfulonlineshopping.AOP.PointCuts.getUserOrderPointCut()")
    public Object logGetUserOrderTime(ProceedingJoinPoint pjp) throws Throwable {
        Long startTime = System.currentTimeMillis();
        logger.info("From ShopAspects.logGetUserOrderTime in controller: start time: " + new Timestamp(startTime));

        Object result = pjp.proceed();

        Long endTime = System.currentTimeMillis();
        logger.info("From ShopAspects.logGetUserOrderTime in controller: end time: " + new Timestamp(endTime));
        logger.info("This endpoint took: " + (endTime - startTime) + " ms. ");
        return result;
    }

    @Around("com.bfs.restfulonlineshopping.AOP.PointCuts.getUserOrderAsyncPointCut()")
    public Object logGetUserOrderAsyncTime(ProceedingJoinPoint pjp) throws Throwable {
        Long startTime = System.currentTimeMillis();
        logger.info("From ShopAspects.logGetUserOrderAsyncTime in controller: start time: " + new Timestamp(startTime));

        Object result = pjp.proceed();

        Long endTime = System.currentTimeMillis();
        logger.info("From ShopAspects.logGetUserOrderAsyncTime in controller: end time: " + new Timestamp(endTime));
        logger.info("This async endpoint took: " + (endTime - startTime) + " ms. ");
        return result;
    }
}