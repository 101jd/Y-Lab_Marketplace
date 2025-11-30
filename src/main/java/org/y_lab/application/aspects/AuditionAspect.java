package org.y_lab.application.aspects;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.y_lab.application.annotations.Audition;
import org.y_lab.application.model.AuditionEntity;
import org.y_lab.application.service.AuditionService;
import org.y_lab.application.service.interfaces.AuditionHandler;

@Aspect
public class AuditionAspect {
    @Pointcut("within(org/y_lab/application/annotations/Audition *) && execution(* * (..))")
    public void annotatedByAudition(){}

    @Around("annotatedByAudition()")
    public void createAudition(ProceedingJoinPoint joinPoint) throws Throwable{
        String result = joinPoint.proceed().toString();
        HttpServletRequest request = (HttpServletRequest)joinPoint.getArgs()[0];
        long id = Long.valueOf(request.getCookies()[0].getAttribute("userId"));
        String message = joinPoint.getClass().getAnnotation(Audition.class).message();

        AuditionHandler handler = AuditionService.getInstance();
        handler.save(new AuditionEntity(id, message + " " + result));

        System.out.println(id + " " + message + " " + result);
    }

}
