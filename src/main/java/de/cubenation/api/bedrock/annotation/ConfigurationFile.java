package de.cubenation.api.bedrock.annotation;

import de.cubenation.api.bedrock.service.config.CustomConfigurationFile;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ConfigurationFiles.class)
public @interface ConfigurationFile {

    Class<? extends CustomConfigurationFile> value();

}
