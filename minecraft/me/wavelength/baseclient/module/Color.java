package me.wavelength.baseclient.module;

public class Color {
	public static final java.awt.Color EXPLOIT = new java.awt.Color(255, 0, 0);
	public static final java.awt.Color COMBAT = new java.awt.Color(0, 255, 0);
	public static final java.awt.Color MOVEMENT = new java.awt.Color(0, 0, 255);
	public static final java.awt.Color RENDER = new java.awt.Color(255, 155, 155);
	public static final java.awt.Color WORLD = new java.awt.Color(155, 255, 155);
	public static final java.awt.Color PLAYER = new java.awt.Color(155, 155, 255);
	public static final java.awt.Color REGULAR = new java.awt.Color(255, 0, 0);
	public static final java.awt.Color CLIENT = new java.awt.Color(20, 255, 20);

	public static java.awt.Color getColor(Category cat) {
		switch (cat) {
		case EXPLOIT:
			return EXPLOIT;
		case COMBAT:
			return COMBAT;
		case MOVEMENT:
			return MOVEMENT;
		case RENDER:
			return RENDER;
		case WORLD:
			return WORLD;
		case PLAYER:
			return PLAYER;
		case CLIENT:
			return CLIENT;
		default:
			return REGULAR;
		}
	}

	public static java.awt.Color getColor(String cat) {
		switch (cat.toUpperCase()) {
		case "EXPLOIT":
			return EXPLOIT;
		case "COMBAT":
			return COMBAT;
		case "MOVEMENT":
			return MOVEMENT;
		case "RENDER":
			return RENDER;
		case "WORLD":
			return WORLD;
		case "PLAYER":
			return PLAYER;
		case "CLIENT":
			return CLIENT;
		default:
			return REGULAR;
		}
	}
}
