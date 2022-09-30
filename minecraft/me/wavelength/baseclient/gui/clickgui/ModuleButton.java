package me.wavelength.baseclient.gui.clickgui;


import me.wavelength.baseclient.gui.clickgui.comp.DoubleComp;
import me.wavelength.baseclient.gui.clickgui.comp.FloatComp;
import me.wavelength.baseclient.gui.clickgui.comp.IntComp;
import me.wavelength.baseclient.gui.clickgui.comp.SettingComp;
import me.wavelength.baseclient.gui.clickgui.comp.ToggleComp;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.module.Value;
import me.wavelength.baseclient.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ModuleButton {

    public Module module;
    public int x, y, width, height, expandedHeight = 20;

    public boolean expanded;

    public ArrayList<Comp> comps;

    public ModuleButton(Module module, int width, int height, int x, int y) {
        this.module = module;

        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;

        comps = new ArrayList<>();
        
        expandedHeight += 20;
        comps.add(new SettingComp(module, 100, 20, x, 40));
        
        for (Value v : module.getValues()) {
            if (v.getObject() instanceof Boolean) {
                comps.add(new ToggleComp(v, module , x, y + height, 100, 20));
                expandedHeight += 20;
            }
            if (v.getObject() instanceof Double) {
                comps.add(new DoubleComp(v, module ,100, 20,x, y + height));
                expandedHeight += 20;
            }
            
            if (v.getObject() instanceof Integer) {
                comps.add(new IntComp(v, module ,100, 20,x, y + height));
                expandedHeight += 20;
            }
            
            if (v.getObject() instanceof Float) {
                comps.add(new FloatComp(v, module ,100, 20,x, y + height));
                expandedHeight += 20;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY) {
    	int j = 14737632;

		if (!module.isToggled()) {
			j = 10526880;
		} else if (onHover(mouseX,mouseY)) {
			if (module.isToggled())
				j = new Color(80, 255, 0).getRGB();
			else
				j = 16777120;
		} else if (module.isToggled()) {
			j = new Color(0, 240, 0).getRGB();
		}
    	
		if(expanded) {
			//For later use
			int i = 1;
			for(Comp c : comps) {
				if(c instanceof SettingComp) {
					SettingComp sc = (SettingComp) c;
					sc.x = x;
					sc.y = y + height;
					
					c.drawScreen(mouseX, mouseY);
				}else if (c instanceof ToggleComp) {
					ToggleComp sc = (ToggleComp) c;
					sc.x = x;
					sc.y = y + height + (expandedHeight - (20 * i));
					
					c.drawScreen(mouseX, mouseY);
				}else if (c instanceof DoubleComp) {
					DoubleComp sc = (DoubleComp) c;
					sc.x = x;
					sc.y = y + height + (expandedHeight - (20 * i));
					
					c.drawScreen(mouseX, mouseY);
				}else if (c instanceof IntComp) {
					IntComp sc = (IntComp) c;
					sc.x = x;
					sc.y = y + height + (expandedHeight - (20 * i));
					
					c.drawScreen(mouseX, mouseY);
				}else if (c instanceof FloatComp) {
					FloatComp sc = (FloatComp) c;
					sc.x = x;
					sc.y = y + height + (expandedHeight - (20 * i));
					
					c.drawScreen(mouseX, mouseY);
				}
				
				i += 1;
			}
		}
		
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0,0,0,89).getRGB());
		RenderUtils.drawString(module.getName(), x + 5, y + 5, j);
		
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	if(onHover(mouseX, mouseY)) {
    		if(mouseButton == 0) {
    			this.module.toggle();
    		}
    		if(mouseButton == 1) {
    			this.expanded = !this.expanded;
    		}
    		if(mouseButton == 2) {
    			Minecraft.getMinecraft().displayGuiScreen(new GuiBind(module, new ClickGUI()));
    		}
    	}
    	
    	if(expanded) {
    		for(Comp c : comps) {
				c.mouseClicked(mouseX, mouseY, mouseButton);
			}
    	}
    }
    
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		if(expanded) {
    		for(Comp c : comps) {
				c.onTyping(typedChar, keyCode);
			}
    	}
	}
    
    
    
    public boolean onHover(int x, int y) {
        if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
            return true;
        }

        return false;
    }
}
