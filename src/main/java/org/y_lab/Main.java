package org.y_lab;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.y_lab.config.WebAppConfig;

public class Main {
    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8082);

//        ServletRegistry registry = new ServletRegistry(tomcat);
//        registry.register();

        Context context = tomcat.addContext("", null);
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();

        webApplicationContext.register(WebAppConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);

        tomcat.addServlet(context, "dispatcher", dispatcherServlet);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        System.out.println(tomcat.getConnector().getPort());
        tomcat.getServer().await();

    }
}