package de.cubenation.bedrock.service.pageablelist;

public class PageableListStorable<T> {

	private T object;
	
	public PageableListStorable() { }
	
	public PageableListStorable(T t) {
		this.set(t);
	}
	
	public T get() {
		return this.object;
	}
	
	public void set(T t) {
		this.object = t;
	}
	
}
