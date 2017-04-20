package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.service.AbstractService;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Services.class)
public @interface Service {

    Class<? extends AbstractService> value();

}
