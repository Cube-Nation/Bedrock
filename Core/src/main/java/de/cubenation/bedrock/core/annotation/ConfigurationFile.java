package de.cubenation.bedrock.core.annotation;


import de.cubenation.bedrock.core.service.config.CustomConfigurationFile;

import java.lang.annotation.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ConfigurationFiles.class)
public @interface ConfigurationFile {

    Class<? extends CustomConfigurationFile> value();

}
