package me.wavelength.baseclient.gui.clickgui.comp;

import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.io.IOException;

import me.wavelength.baseclient.gui.clickgui.Comp;
import me.wavelength.baseclient.module.Value;
import me.wavelength.baseclient.utils.RenderUtils;
import me.wavelength.baseclient.module.Module;

public class ToggleComp extends Comp {

    public int x, y, width, height;

    public Value value;

    public Module m;

    public ToggleComp(Value value, Module m , int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        this.value = value;
        this.m = m;
        this.x = x;
        this.y = y;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
    	int j = 14737632;

    	boolean b = (boolean) value.getObject();
    	
		if (!b) {
			j = 10526880;
		} else if (onHover(mouseX,mouseY)) {
			if (b)
				j = new Color(80, 255, 0).getRGB();
			else
				j = 16777120;
		} else if (b) {
			j = new Color(0, 240, 0).getRGB();
		}
		
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0,0,0,89).getRGB());
		RenderUtils.drawString(value.name, x + 5, y + 5, j);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	if(mouseButton == 0) {
    		onClick(mouseX, mouseY);
    	}
    }
    
    public void onClick(int x, int y) {
        if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
        	boolean b = (boolean) value.getObject();
            value.setObject(!b);
        }
    }
    
    public boolean onHover(int x, int y) {
        if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
        	return true;
        }
        
        return false;
    }
}
