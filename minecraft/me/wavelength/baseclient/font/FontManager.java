package me.wavelength.baseclient.font;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontManager {

	private static final Map<String, List<UnicodeFontRenderer>> REGISTERED_RENDERERS = new HashMap<>();

	private String packageName;
	private String fontName;
	private int fontSize;

	public static Map<String, List<UnicodeFontRenderer>> getRegisteredRenderers() {
		return REGISTERED_RENDERERS;
	}

	public FontManager(String packageName, String fontName, int fontSize) {
		this.packageName = packageName;
		this.fontName = fontName;
		this.fontSize = fontSize;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getFontName() {
		return fontName;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public UnicodeFontRenderer getFont() {
		return getFont(packageName, fontName, fontSize);
	}

	public UnicodeFontRenderer getFont(int fontSize) {
		return getFont(packageName, fontName, fontSize);
	}

	public UnicodeFontRenderer getFont(String fontName, int fontSize) {
		return getFont(packageName, fontName, fontSize);
	}

	public UnicodeFontRenderer getFont(String packageName, String fontName, int fontSize) {
		packageName = "." + packageName;
		packageName = packageName.replace(".", "/");
		for (Map.Entry<String, List<UnicodeFontRenderer>> entry : REGISTERED_RENDERERS.entrySet()) {
			if (((String) entry.getKey()).toLowerCase().equalsIgnoreCase(fontName.toLowerCase()))
				for (UnicodeFontRenderer afr : entry.getValue()) {
					if (afr.getFont().getFont().getSize() == fontSize)
						return afr;
				}
		}

		String pkg = String.valueOf(packageName) + "/" + fontName + (fontName.endsWith(".ttf") ? "" : ".ttf");
		InputStream is = FontManager.class.getResourceAsStream(pkg);
		UnicodeFontRenderer fr = new UnicodeFontRenderer(is, fontSize);
		if (!REGISTERED_RENDERERS.containsKey(fontName))
			REGISTERED_RENDERERS.put(fontName, new ArrayList<>());
		((List<UnicodeFontRenderer>) REGISTERED_RENDERERS.get(fontName)).add(fr);
		return fr;
	}

}