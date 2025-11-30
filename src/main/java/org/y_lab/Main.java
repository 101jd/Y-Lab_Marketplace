package org.y_lab;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.y_lab.adapter.in.servlets.*;
import org.y_lab.application.annotations.Audition;

public class Main {
    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8082);

        ServletRegistry registry = new ServletRegistry(tomcat);
        registry.register();

        tomcat.start();
        System.out.println(tomcat.getConnector().getPort());
        tomcat.getServer().await();


    }
}