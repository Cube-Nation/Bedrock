package de.cubenation.api.bedrock.helper;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class GenericStorable<T> {

    private T object;

    public GenericStorable() { }

    public GenericStorable(T t) {
        this.set(t);
    }

    public T get() {
        return this.object;
    }

    public void set(T t) {
        this.object = t;
    }
}
