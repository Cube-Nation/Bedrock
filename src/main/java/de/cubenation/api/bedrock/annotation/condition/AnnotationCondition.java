package de.cubenation.api.bedrock.annotation.condition;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public abstract class AnnotationCondition {

    @SuppressWarnings("WeakerAccess")
    public AnnotationCondition() {
    }

    public abstract boolean isValid();

}
