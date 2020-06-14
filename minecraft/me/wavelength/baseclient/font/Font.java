package me.wavelength.baseclient.font;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.wavelength.baseclient.utils.RenderUtils;

public class Font {

	private static final Map<String, List<UnicodeFontRenderer>> REGISTERED_RENDERERS = new HashMap<>();

	private String packageName;
	private String fontName;

	private int fontSizeSmall;
	private int fontSizeNormal;
	private int fontSizeLarge;
	private int fontSizeLargest;

	public static Map<String, List<UnicodeFontRenderer>> getRegisteredRenderers() {
		return REGISTERED_RENDERERS;
	}

	public Font(String packageName, String fontName, int fontSizeSmall, int fontSizeNormal, int fontSizeLarge, int fontSizeLargest) {
		this.packageName = packageName;
		this.fontName = fontName;
		this.fontSizeSmall = fontSizeSmall;
		this.fontSizeNormal = fontSizeNormal;
		this.fontSizeLarge = fontSizeLarge;
		this.fontSizeLargest = fontSizeLargest;
	}

	public Font(String packageName, String fontName, int fontSizeNormal) {
		this.packageName = packageName;
		this.fontName = fontName;
		this.fontSizeSmall = fontSizeNormal;
		this.fontSizeNormal = fontSizeNormal;
		this.fontSizeLarge = fontSizeNormal;
		this.fontSizeLargest = fontSizeNormal;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getFontName() {
		return fontName;
	}

	public int getFontSize() {
		switch (RenderUtils.getScaledResolution().getScaleFactor()) {
		case 1:
			return fontSizeSmall;
		case 2:
			return fontSizeNormal;
		case 3:
			return fontSizeLarge;
		case 4:
			return fontSizeLargest;
		default:
			return fontSizeNormal;
		}
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public void setFontSizeSmall(int fontSizeSmall) {
		this.fontSizeSmall = fontSizeSmall;
	}

	public void setFontSizeNormal(int fontSizeNormal) {
		this.fontSizeNormal = fontSizeNormal;
	}

	public void setFontSizeLarge(int fontSizeLarge) {
		this.fontSizeLarge = fontSizeLarge;
	}

	public void setFontSizeLargest(int fontSizeLargest) {
		this.fontSizeLargest = fontSizeLargest;
	}

	public UnicodeFontRenderer getFont() {
		return getFont(packageName, fontName, getFontSize());
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
		InputStream is = Font.class.getResourceAsStream(pkg);
		UnicodeFontRenderer fr = new UnicodeFontRenderer(is, fontSize);
		if (!REGISTERED_RENDERERS.containsKey(fontName))
			REGISTERED_RENDERERS.put(fontName, new ArrayList<>());
		((List<UnicodeFontRenderer>) REGISTERED_RENDERERS.get(fontName)).add(fr);
		return fr;
	}

}