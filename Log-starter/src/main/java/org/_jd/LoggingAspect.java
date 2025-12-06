package org._jd;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Date;
import java.util.logging.Logger;

@Aspect
public class LoggingAspect {

    @Pointcut("within(org.y_lab.application.annotations.ToLog*) && execution(* *(..))")
    public void annotatedByToLog(){}

    @Around("annotatedByToLog()")
    public void toLog(ProceedingJoinPoint joinPoint){
        Logger logger = Logger.getLogger("ServletLogs");
        Date start = new Date();
        try {
            Object result = joinPoint.proceed();
            Date end = new Date();
            logger.info(joinPoint.getClass().getName() + " finished at "
                    + String.valueOf(end.getTime() - start.getTime()) + " with " + result);
        } catch (Throwable e) {
            logger.warning(e.getMessage());
        }
    }

}
