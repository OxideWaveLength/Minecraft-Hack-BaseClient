package me.wavelength.baseclient.gui.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import me.wavelength.baseclient.BaseClient;
import me.wavelength.baseclient.module.Category;
import me.wavelength.baseclient.utils.RenderUtils;

public class Panel {

    public Category category;
    public float dragX,dragY;

    public int x, y, width, height;

    public boolean expanded,drag;

    public ArrayList<ModuleButton> mods;

    public Panel(Category category, int width, int height, int x, int y) {
        this.category = category;

        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;

        mods = new ArrayList<>();
        int yList = y + 22;
        for(me.wavelength.baseclient.module.Module m : BaseClient.instance.getModuleManager().getModules(category)) {
            mods.add(new ModuleButton(m, 100, 20, x ,yList));

            yList += 22;
        }
    }

    public void setX(int newX) {
        this.x = newX;
    }

    public void setY(int newY) {
        this.y = newY;
    }

    public void drawPanel(int mouseX, int mouseY) {
    	Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(92, 230, 90,89).getRGB());
		RenderUtils.drawString(category.name(), x + 5, y + 5, -1);

        int yList = y + 20;
        for(ModuleButton m : mods) {
            m.x = x;
            m.y = yList;

            if(m.expanded) {
                yList += m.expandedHeight;
            }else {
                yList += 20;
            }

            if(!m.expanded) {
                m.width = 100;
                m.height = 20;
            }
        }

        if(expanded) {
            for(ModuleButton moduleButton : mods) {
                moduleButton.drawScreen(mouseX, mouseY);
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
		if(expanded) {
			for(ModuleButton m : mods) {
                m.keyTyped(typedChar, keyCode);
            }
    	}
	}
    

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	
    }
    

    public boolean isHolding(int x, int y) {
        if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
            return true;
        }
        return false;
    }

    public boolean isDrag() {
        return this.drag;
    }

    public void setDrag(boolean b) {
        this.drag = b;
    }
}
