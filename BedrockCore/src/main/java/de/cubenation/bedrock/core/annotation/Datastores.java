package de.cubenation.bedrock.core.annotation;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Datastores {

    Datastore[] value();
}
