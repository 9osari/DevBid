package org.devbid.auction.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class AuctionLoggingAspect {

    @Pointcut("execution(* org.devbid.auction.application.AuctionFacade.placeBid(..))")
    public void bidMethod() {}

    @Before("bidMethod()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("입찰 메서드 호출: {} - 파라미터 {}", methodName, args);
    }

    @AfterReturning(pointcut = "bidMethod()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        log.info("입찰 완료! 반환값: {}", result.toString());
    }

}
