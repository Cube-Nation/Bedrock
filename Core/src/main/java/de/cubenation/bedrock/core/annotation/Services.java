package de.cubenation.bedrock.core.annotation;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Services {

    Service[] value();

}
