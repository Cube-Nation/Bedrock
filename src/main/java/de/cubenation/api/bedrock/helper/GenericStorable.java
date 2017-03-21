package de.cubenation.api.bedrock.helper;

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
