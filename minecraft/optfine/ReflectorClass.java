package optfine;

public class ReflectorClass {
	private String targetClassName = null;
	private boolean checked = false;
	private Class targetClass = null;

	public ReflectorClass(String p_i55_1_) {
		this.targetClassName = p_i55_1_;
		Class oclass = this.getTargetClass();
	}

	public ReflectorClass(Class p_i56_1_) {
		this.targetClass = p_i56_1_;
		this.targetClassName = p_i56_1_.getName();
		this.checked = true;
	}

	public Class getTargetClass() {
		if (this.checked) {
			return this.targetClass;
		} else {
			this.checked = true;

			try {
				this.targetClass = Class.forName(this.targetClassName);
			} catch (ClassNotFoundException var2) {
				Config.log("(Reflector) Class not present: " + this.targetClassName);
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			}

			return this.targetClass;
		}
	}

	public boolean exists() {
		return this.getTargetClass() != null;
	}

	public String getTargetClassName() {
		return this.targetClassName;
	}
}
