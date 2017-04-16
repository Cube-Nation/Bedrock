package de.cubenation.api.bedrock.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigurationFiles {

    ConfigurationFile[] value();

}
