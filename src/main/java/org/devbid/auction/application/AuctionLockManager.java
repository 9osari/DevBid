package org.devbid.auction.application;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AuctionLockManager {
    private final RedissonClient redissonClient;

    public <T> T executeWithLock(Long key, Callable<T> task) {
        RLock lock = getLock(key);
        try {
            acquireLockOrThrow(lock);
            return task.call();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 대기 중 인터럽트 발생" +e);

        } catch (IllegalStateException  e) {
            // 비즈니스 로직 예외는 그대로 전파
            throw e;

        } catch (Exception e) {
            throw new IllegalStateException("락 실행 중 오류", e);

        }  finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private RLock getLock(Long auctionId) {
        return redissonClient.getLock("auction:lock:" + auctionId); //락을 가져옴
    }

    private static void acquireLockOrThrow(RLock lock) throws InterruptedException {
        //락을 못 얻으면 1초 후 예외, 락을 얻은 후 3초 유지
        boolean acquired = lock.tryLock(1000, 3000, TimeUnit.MILLISECONDS);
        if(!acquired) {
            throw new IllegalStateException("잠시 후 다시 시도해주세요.");
        }
    }
}
