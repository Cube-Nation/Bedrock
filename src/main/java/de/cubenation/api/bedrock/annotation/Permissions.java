package de.cubenation.api.bedrock.annotation;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Permissions {

    Permission[] value();

}
