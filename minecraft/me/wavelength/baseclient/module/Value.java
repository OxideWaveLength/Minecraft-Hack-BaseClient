package me.wavelength.baseclient.module;

public class Value<T> {

	public String name;
	public T object;
	
	public Value(String name , T object) {
		this.name = name;
		this.object = object;
	}
	
	public void setObject(T object) {
		this.object = object;
	}
	
	public T getObject() {
		return object;
	}

	public String getValueName() {
		return name;
	}
}
