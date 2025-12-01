package org.devbid.auction.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class PerformanceAspect {
    @Around("execution(* org.devbid.auction.application.AuctionApplicationService.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        long elapsedTime = endTime - startTime;
        if(elapsedTime > 1000) {
            log.warn("느린 메서드 감지: {} ms", elapsedTime);
        }
        log.info("실행 시간: {} ms", elapsedTime);

        return result;
    }
}
