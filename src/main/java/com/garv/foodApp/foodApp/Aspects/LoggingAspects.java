package com.garv.foodApp.foodApp.Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspects {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspects.class);

    @Before("execution(* com.garv.foodApp.foodApp.Service.*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        logger.info("Executing before method {}" , joinPoint.getSignature().getName());
    }

    @After("execution(* com.garv.foodApp.foodApp.Service.*.*(..))")
    public void logAfterMethodExecution(JoinPoint joinPoint) {
        logger.info("Executing After method {}" , joinPoint.getSignature().getName());
    }

    @AfterReturning("execution(* com.garv.foodApp.foodApp.Service.*.*(..))")
    public void logAfterReturningMethodExecution(JoinPoint joinPoint) {
        logger.info("Executing After Returning method {}" , joinPoint.getSignature().getName());
    }

    @AfterThrowing("execution(* com.garv.foodApp.foodApp.Service.*.*(..))")
    public void logAfterThrowingMethodExecution(JoinPoint joinPoint) {
        logger.info("Executing After Throwing method {}" , joinPoint.getSignature().getName());
    }

    @Around("execution(* com.garv.foodApp.foodApp.Service.*.*(..))")
    public Object logAroundMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        logger.info("Executing AROUND BEFORE method: {}", joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();  // continue method execution
        long endTime = System.currentTimeMillis();
        logger.info("time taken: {} ms", endTime - startTime);
        logger.info("Executing AROUND AFTER method: {}", joinPoint.getSignature().getName());
        return result;
    }

}
