package com.future.demo;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AroundAspect {
    public boolean invokedBefore = false;
    public boolean invokedAfterReturning = false;
    public boolean invokedAfterThrowing = false;
    public boolean invokedAfter = false;

    @Pointcut("execution(int com.future.demo..MyCalculator.*(int, int))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            this.invokedBefore = true;

            Object result = proceedingJoinPoint.proceed();

            this.invokedAfterReturning = true;

            return result;
        } catch (Throwable throwable) {
            this.invokedAfterThrowing = true;

            throw throwable;
        } finally {
            this.invokedAfter = true;
        }
    }

    public void reset() {
        this.invokedBefore = false;
        this.invokedAfterReturning = false;
        this.invokedAfterThrowing = false;
        this.invokedAfter = false;
    }
}
