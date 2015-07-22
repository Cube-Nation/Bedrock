package de.cubenation.bedrock.service.pageablelist;

public class PageableListStorable<T> {

	private T object;
	
	public PageableListStorable() { }
	
	public PageableListStorable(T t) {
		object = t;
	}
	
	public T get() {
		return object;
	}
	
	public void set(T t) {
		object = t;
	}
	
}
