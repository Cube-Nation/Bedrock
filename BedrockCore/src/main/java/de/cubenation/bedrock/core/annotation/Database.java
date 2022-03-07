package de.cubenation.bedrock.core.annotation;

import de.cubenation.bedrock.core.database.CustomDatabase;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 2.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Databases.class)
public @interface Database {

    Class<? extends CustomDatabase> value();
}
