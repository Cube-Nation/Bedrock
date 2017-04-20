package de.cubenation.api.bedrock.service.confirm;

import de.cubenation.api.bedrock.helper.GenericStorable;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class ConfirmStorable<T> extends GenericStorable {

    @SuppressWarnings("unchecked")
    public ConfirmStorable(T t) {
        this.set(t);
    }

}