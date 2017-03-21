package de.cubenation.api.bedrock.service.confirm;

import de.cubenation.api.bedrock.helper.GenericStorable;

public class ConfirmStorable<T> extends GenericStorable {

    @SuppressWarnings("unchecked")
    public ConfirmStorable(T t) {
        this.set(t);
    }

}