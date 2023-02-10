package de.cubenation.bedrock.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EbeanEntity {

    Class<?> value();

    /**
     * Identifier of data source to be used.
     * @return Data source identifier
     */
    String identifier();

}
