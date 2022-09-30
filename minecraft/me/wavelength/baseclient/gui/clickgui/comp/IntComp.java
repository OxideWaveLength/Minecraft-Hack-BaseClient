package me.wavelength.baseclient.gui.clickgui.comp;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.gui.clickgui.Comp;
import me.wavelength.baseclient.module.Value;
import me.wavelength.baseclient.utils.RenderUtils;

import java.awt.*;
import java.io.IOException;

import me.wavelength.baseclient.module.Module;
import java.util.ArrayList;

public class IntComp extends Comp {

    public boolean isTyping;

    public int x, y, width, height;

    public Value value;

    public Module m;

    public String val  = "";

    public IntComp(Value value, Module m ,int width, int height, int x, int y) {
        this.width = width;
        this.height = height;
        this.value = value;
        this.m = m;
        this.x = x;
        this.y = y;

        this.val = String.valueOf(value.getObject());
        isTyping = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	int j = 14737632;
		
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0,0,0,89).getRGB());
		RenderUtils.drawString(isTyping ? value.getValueName() + " : " + val + "|" : value.getValueName() + " : " + val, x + 5, y + 5, j);

    }

    public void onTyping(char typedChar, int keycode) {
    	if (!isTyping) return;
    	
        if(isNumber(typedChar) || keycode == 14 || keycode == Keyboard.KEY_ESCAPE) {
            if(keycode == Keyboard.KEY_ESCAPE) {
                isTyping = false;
            }
            if(keycode == 14) {
            	 if(val.length() >= 1) {
                     String strNew = val.substring(0, val.length()-1);
                     val = strNew;
                 }
            }
            if((isNumber(typedChar) || keycode == 52) && keycode != 14 && keycode != Keyboard.KEY_ESCAPE ) {
                if(val.length() > 3) {
                    val = "";
                }
                val = val + typedChar;
                if (isNumber(val)) {
                    value.setObject(Double.valueOf(val));
                }
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	if(mouseButton == 0) {
    		onClick(mouseX, mouseY);
    	}
    }
    
    public void onClick(int x, int y) {
        if(x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
            isTyping = !isTyping;
        }
    }

    public boolean isNumber(char str) {
        boolean r = false;
        try {
            Integer doub = Integer.parseInt(String.valueOf(str));
            r = true;
        }catch (NumberFormatException ex) {
            r = false;
        }
        return r;
    }

    public boolean isNumber(String str) {
        boolean r = false;
        try {
        	Integer doub = Integer.parseInt(String.valueOf(str));
            r = true;
        }catch (NumberFormatException ex) {
            r = false;
        }
        return r;
    }
}
