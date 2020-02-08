package me.wavelength.baseclient.thealtening;

import java.lang.reflect.Field;

public class ReflectionUtility {
	private Class<?> clazz;

	public ReflectionUtility(String className) {
		try {
			this.clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setStaticField(String fieldName, Object newValue) throws NoSuchFieldException, IllegalAccessException {
		Field field = this.clazz.getDeclaredField(fieldName);
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & 0xFFFFFFEF);
		field.set(null, newValue);
	}
}
