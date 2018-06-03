package mrmango.bsuir.schedule.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * created by Ilya Aleksandrovich
 * on 03-Jun-2018
 */
public class ExceptionHandler implements AsyncUncaughtExceptionHandler {
    private static final Logger log = LogManager.getLogger(ExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.warn("Caught exception: " + ex.getMessage() + " from method [" + method.toGenericString() + "]");
    }
}
