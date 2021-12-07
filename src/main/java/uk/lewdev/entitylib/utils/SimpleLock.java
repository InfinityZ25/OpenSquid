package uk.lewdev.entitylib.utils;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Syntactic Sugar for Try, Finally Blocks When Used in Safe Locking
 *
 * @author Lewys Davies (Lew_)
 */
public class SimpleLock {

    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Obtains the read lock and executes the functional executor [Use lambda :)]<br>
     * This will block the current thread until the lock is obtained<br>
     * For safety, the lock will always be released, even after an exception
     *
     * @param executor
     */
    public final void lockOrWait(FunctionalExecutor executor) {
        this.lock.lock();
        try {
            executor.execute();
        } finally {
            this.lock.unlock();
        }
    }

    public interface FunctionalExecutor {
        void execute();
    }
}
