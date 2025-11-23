package org.y_lab.adapter.in.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.util.ArrayList;
import java.util.List;

public class ServletRegistry {
    private Tomcat tomcat;
    private final List<HttpServlet> servlets;
    Context context;

    public ServletRegistry(Tomcat tomcat) {
        this.tomcat = tomcat;
        this.servlets = new ArrayList<>();
        this.context = this.tomcat.addContext("", null);
        init();
    }

    private void init(){
        servlets.add(new AddItemToPlatformServlet());
        servlets.add(new AddToCartServlet());
        servlets.add(new EditItemServlet());
        servlets.add(new FilterByKeywordServlet());
        servlets.add(new FilterByPriceServlet());
        servlets.add(new GetAllServlet());
        servlets.add(new RegisterServlet());
        servlets.add(new SignInServlet());
    }

    public void register(){
        for (HttpServlet servlet : servlets){
            WebServlet annotation = servlet.getClass().getAnnotation(WebServlet.class);
            String name = annotation.name();
            String value = annotation.value()[0];
            tomcat.addServlet(context, name, servlet);
            context.addServletMappingDecoded(value, name);
        }
    }

    public Tomcat getTomcat() {
        return tomcat;
    }
}
