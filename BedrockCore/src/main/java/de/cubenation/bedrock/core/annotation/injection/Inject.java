package de.cubenation.bedrock.core.annotation.injection;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {

    String from() default "";

}
