package optfine;

import java.lang.reflect.Method;

public class ReflectorMethod {
	private ReflectorClass reflectorClass;
	private String targetMethodName;
	private Class[] targetMethodParameterTypes;
	private boolean checked;
	private Method targetMethod;

	public ReflectorMethod(ReflectorClass p_i59_1_, String p_i59_2_) {
		this(p_i59_1_, p_i59_2_, (Class[]) null);
	}

	public ReflectorMethod(ReflectorClass p_i60_1_, String p_i60_2_, Class[] p_i60_3_) {
		this.reflectorClass = null;
		this.targetMethodName = null;
		this.targetMethodParameterTypes = null;
		this.checked = false;
		this.targetMethod = null;
		this.reflectorClass = p_i60_1_;
		this.targetMethodName = p_i60_2_;
		this.targetMethodParameterTypes = p_i60_3_;
		Method method = this.getTargetMethod();
	}

	public Method getTargetMethod() {
		if (this.checked) {
			return this.targetMethod;
		} else {
			this.checked = true;
			Class oclass = this.reflectorClass.getTargetClass();

			if (oclass == null) {
				return null;
			} else {
				Method[] amethod = oclass.getDeclaredMethods();
				int i = 0;
				Method method;

				while (true) {
					if (i >= amethod.length) {
						Config.log("(Reflector) Method not present: " + oclass.getName() + "." + this.targetMethodName);
						return null;
					}

					method = amethod[i];

					if (method.getName().equals(this.targetMethodName)) {
						if (this.targetMethodParameterTypes == null) {
							break;
						}

						Class[] aclass = method.getParameterTypes();

						if (Reflector.matchesTypes(this.targetMethodParameterTypes, aclass)) {
							break;
						}
					}

					++i;
				}

				this.targetMethod = method;

				if (!this.targetMethod.isAccessible()) {
					this.targetMethod.setAccessible(true);
				}

				return this.targetMethod;
			}
		}
	}

	public boolean exists() {
		return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
	}

	public Class getReturnType() {
		Method method = this.getTargetMethod();
		return method == null ? null : method.getReturnType();
	}

	public void deactivate() {
		this.checked = true;
		this.targetMethod = null;
	}
}
