package me.wavelength.baseclient.utils;

import org.lwjgl.input.Keyboard;

public class KeyUtils {

	public static enum MouseButton {
		MOUSE_LEFT(0, -1), MOUSE_RIGHT(1, -2), MOUSE_WHEEL(2, -3);

		private int defaultCode;
		private int newCode;

		MouseButton(int defaultCode, int newCode) {
			this.defaultCode = defaultCode;
			this.newCode = newCode;
		}

		public int getDefaultCode() {
			return defaultCode;
		}

		public int getNewCode() {
			return newCode;
		}

		public static MouseButton getFromName(String name) {
			for (int i = 0; i < values().length; i++)
				if (values()[i].name().equalsIgnoreCase(name))
					return values()[i];

			return null;
		}

		public static MouseButton getFromAnyKeyCode(int keyCode) {
			MouseButton mouseButton = getFromDefaultCode(keyCode);

			return (mouseButton == null ? getFromNewCode(keyCode) : mouseButton);
		}

		public static MouseButton getFromDefaultCode(int defaultCode) {
			for (int i = 0; i < values().length; i++)
				if (values()[i].getDefaultCode() == defaultCode)
					return values()[i];

			return null;
		}

		public static MouseButton getFromNewCode(int newCode) {
			for (int i = 0; i < values().length; i++)
				if (values()[i].getNewCode() == newCode)
					return values()[i];

			return null;
		}

	}

	public static int getKey(String name) {
		if (!(Integers.isInteger(name))) {
			name = name.toUpperCase();
			MouseButton mouseButton = getMouseButton(name);

			return (mouseButton == null ? Keyboard.getKeyIndex(name) : mouseButton.getNewCode());
		}

		return Integers.getInteger(name);
	}

	public static String getKeyName(int key) {
		String keyName = Integer.toString(key);

		MouseButton mouseButton = MouseButton.getFromNewCode(key);

		try {
			keyName = Keyboard.getKeyName(key);
		} catch (ArrayIndexOutOfBoundsException e) {
		}

		return (mouseButton == null ? keyName : mouseButton.name());
	}

	public static MouseButton getMouseButton(String mouseButtonName) {
		MouseButton mouseButton = MouseButton.getFromName(getMouseButtonName(mouseButtonName));

		if (Integers.isInteger(mouseButtonName))
			mouseButton = MouseButton.getFromNewCode(Integers.getInteger(mouseButtonName));

		return mouseButton;
	}

	public static String getMouseButtonName(String string) {
		String[] prefixes = new String[] { "BUTTON_", "BUTTON", "MBUTTON_", "MBUTTON", "MB_", "MB", "MOUSE_BUTTON_", "MOUSE_BUTTON", "MOUSEBUTTON_", "MOUSEBUTTON", "MOUSE_", "MOUSE" };

		for (int i = 0; i < prefixes.length; i++) {
			String prefix = prefixes[i];

			if (!(string.startsWith(prefix)))
				continue;

			return "MOUSE_" + string.substring(prefix.length(), string.length());
		}

		return null;
	}

}