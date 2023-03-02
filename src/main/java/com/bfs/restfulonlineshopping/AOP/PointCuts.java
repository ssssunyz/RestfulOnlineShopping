package com.bfs.restfulonlineshopping.AOP;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PointCuts {

    // Log the time that a user places the order
    // and the time that the seller update the order (Cancel or Complete)
    @Pointcut("execution(* com.bfs.restfulonlineshopping.controller.UserController.makePurchase(..))")
    public void makePurchasePointCut(){}

    @Pointcut("execution(* com.bfs.restfulonlineshopping.controller.AdminController.completeOrder(..))")
    public void completeOrderPointCut(){}

    @Pointcut("execution(* com.bfs.restfulonlineshopping.controller.AdminController.cancelOrder(..))")
    public void cancelOrderPointCut(){}

    // below two methods are made for the Asynchronous homework
    @Pointcut("execution(* com.bfs.restfulonlineshopping.controller.UserController.getUserOrder(..))")
    public void getUserOrderPointCut(){}

    @Pointcut("execution(* com.bfs.restfulonlineshopping.controller.AsyncController.getUserOrder(..))")
    public void getUserOrderAsyncPointCut(){}
}