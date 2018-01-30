package com.airhacks;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures a JAX-RS endpoint. Delete this class, if you are not exposing
 * JAX-RS resources in your application.
 *
 * @author airhacks.com
 */
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(org.lpro.boundary.category.CategoryResource.class);
        classes.add(org.lpro.boundary.category.CategoryNotFound.class);
        classes.add(org.lpro.boundary.category.CategoryNotFoundMapper.class);
        classes.add(org.lpro.boundary.sandwich.SandwichResource.class);
        classes.add(org.lpro.boundary.sandwich.SandwichNotFound.class);
        classes.add(org.lpro.boundary.sandwich.SandwichNotFoundMapper.class);
        classes.add(org.lpro.boundary.sandwich.SandwichManager.class);
        classes.add(com.github.phillipkruger.apiee.ApieeService.class);
        return classes;
    }

}
