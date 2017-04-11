package de.cubenation.api.bedrock.annotation.condition;

public abstract class AnnotationCondition {

    @SuppressWarnings("WeakerAccess")
    public AnnotationCondition() {
    }

    public abstract boolean isValid();

}
