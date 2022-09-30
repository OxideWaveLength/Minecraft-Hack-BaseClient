package me.wavelength.baseclient.gui.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import me.wavelength.baseclient.module.Category;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGUI extends GuiScreen {

    public ArrayList<Panel> panels;

    public ClickGUI() {
        panels = new ArrayList<>();

        int y = 2;
        for(Category c : Category.values()) {
        	if(c.equals(Category.HIDDEN)|| c.equals(Category.SEMI_HIDDEN)) return;
            Panel p = new Panel(c, 100, 20, 50, y);
            panels.add(p);

            y += 30;
        }
    }

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {

        for(Panel p : panels) {
            p.drawPanel(p_73863_1_, p_73863_2_);
        }

        for(Panel p : panels) {
            if(p.isDrag()) {
                p.setX((int) (p_73863_1_ - p.dragX));
                p.setY((int) (p_73863_2_ - p.dragY));
            }
        }



        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel Panel : panels) {
            if (Panel.isHolding(mouseX, mouseY) && mouseButton == 0) {
                Panel.setDrag(true);
                Panel.dragX = mouseX - Panel.x;
                Panel.dragY = mouseY - Panel.y;
            }
            if (Panel.isHolding(mouseX, mouseY) && mouseButton == 1) {
                Panel.expanded = !Panel.expanded;
            }

            if(Panel.expanded) {
                for(ModuleButton m : Panel.mods) {
                    if(m.onHover(mouseX, mouseY)) {
                        if (mouseButton == 0) {
                            m.module.toggle();
                        }else if (mouseButton == 1) {
                        	 m.expanded = !m.expanded;
                        }
                    }
                    if(m.expanded) {
                        for(Comp c : m.comps) {
                        	c.mouseClicked(mouseX, mouseY, mouseButton);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        System.out.println(keyCode);


        if(keyCode == Keyboard.KEY_ESCAPE ) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
        for (Panel Panel : panels) {
            Panel.setDrag(false);
            if(Panel.expanded) {
            	for(ModuleButton m : Panel.mods) {
                	m.keyTyped(typedChar, keyCode);
                }
            }

        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel Panel : panels) {
            Panel.setDrag(false);

        }


    }
    
}
