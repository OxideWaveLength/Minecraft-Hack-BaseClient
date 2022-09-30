package me.wavelength.baseclient.gui.clickgui.comp;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import me.wavelength.baseclient.gui.clickgui.Comp;
import me.wavelength.baseclient.module.AntiCheat;
import me.wavelength.baseclient.module.Module;
import me.wavelength.baseclient.utils.RenderUtils;
import net.minecraft.client.gui.Gui;

public class SettingComp extends Comp {

	public Module module;
    public int x, y, width, height;

    public boolean expanded;

    public ArrayList<Comp> modes;

    public SettingComp(Module module, int width, int height, int x, int y) {
        this.module = module;

        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;
    }
	
    public void drawScreen(int mouseX, int mouseY) {
    	Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0,0,0,89).darker().getRGB());
		RenderUtils.drawString("AC : " + module.getAntiCheat().name(), x + 5, y + 5, -1);
    }
    
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(isHolding(mouseX, mouseY) && mouseButton == 0) {
			ArrayList<AntiCheat> allowAc = new ArrayList<>();
			for(AntiCheat ac : module.getAllowedAntiCheats()) {
				allowAc.add(ac);
			}
			
			for(int i = 0; i < allowAc.size(); i++) {
				if(allowAc.get(i) == module.getAntiCheat()) {
					int nextMod = 0;
					if(i != allowAc.size() - 1) {
						nextMod = i + 1;
					}
					module.setAntiCheat(allowAc.get(nextMod));
					break;
				}
			}
		}
	}
	
	 public boolean isHolding(int x, int y) {
	        if (x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height) {
	            return true;
	        }
	        return false;
	 }
	
	
	public void mouseReleased(int mouseX, int mouseY, int state) {
		
	}
}
