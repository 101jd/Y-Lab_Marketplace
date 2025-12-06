package com._jd;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;


@Aspect
public class AuditionAspect {

    @Autowired
    AuditionHandler handler;
    @Pointcut("within(org.y_lab.application.annotations.Audition*) && execution(* *(..))")
    public void annotatedByAudition(){}

    @Around("annotatedByAudition()")
    public void createAudition(ProceedingJoinPoint joinPoint) throws Throwable{
        String result = joinPoint.proceed().toString();
        String message = joinPoint.getClass().getAnnotation(Audition.class).message();

        handler.save(new AuditionEntity(message + " " + result));

        System.out.println( message + " " + result);
    }

}
