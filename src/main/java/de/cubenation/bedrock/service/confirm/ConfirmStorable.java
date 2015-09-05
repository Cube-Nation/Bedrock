package de.cubenation.bedrock.service.confirm;

import de.cubenation.bedrock.helper.GenericStorable;

public class ConfirmStorable<T> extends GenericStorable {

    @SuppressWarnings("unchecked")
    public ConfirmStorable(T t) {
        this.set(t);
    }

}