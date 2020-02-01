package net.minecraft.client.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class GuiScreenRealmsProxy extends GuiScreen {
	private RealmsScreen field_154330_a;

	public GuiScreenRealmsProxy(RealmsScreen p_i1087_1_) {
		this.field_154330_a = p_i1087_1_;
		super.buttonList = Collections.<GuiButton>synchronizedList(Lists.<GuiButton>newArrayList());
	}

	public RealmsScreen func_154321_a() {
		return this.field_154330_a;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.field_154330_a.init();
		super.initGui();
	}

	public void func_154325_a(String p_154325_1_, int p_154325_2_, int p_154325_3_, int p_154325_4_) {
		super.drawCenteredString(this.fontRendererObj, p_154325_1_, p_154325_2_, p_154325_3_, p_154325_4_);
	}

	public void func_154322_b(String p_154322_1_, int p_154322_2_, int p_154322_3_, int p_154322_4_) {
		super.drawString(this.fontRendererObj, p_154322_1_, p_154322_2_, p_154322_3_, p_154322_4_);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width,
	 * height
	 */
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		this.field_154330_a.blit(x, y, textureX, textureY, width, height);
		super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors (ARGB
	 * format). Args : x1, y1, x2, y2, topColor, bottomColor
	 */
	public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
		super.drawGradientRect(left, top, right, bottom, startColor, endColor);
	}

	/**
	 * Draws either a gradient over the background screen (when it exists) or a flat
	 * gradient over background.png
	 */
	public void drawDefaultBackground() {
		super.drawDefaultBackground();
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return super.doesGuiPauseGame();
	}

	public void drawWorldBackground(int tint) {
		super.drawWorldBackground(tint);
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.field_154330_a.render(mouseX, mouseY, partialTicks);
	}

	public void renderToolTip(ItemStack stack, int x, int y) {
		super.renderToolTip(stack, x, y);
	}

	/**
	 * Draws the text when mouse is over creative inventory tab. Params: current
	 * creative tab to be checked, current mouse x position, current mouse y
	 * position.
	 */
	public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
		super.drawCreativeTabHoveringText(tabName, mouseX, mouseY);
	}

	/**
	 * Draws a List of strings as a tooltip. Every entry is drawn on a seperate
	 * line.
	 */
	public void drawHoveringText(List<String> textLines, int x, int y) {
		super.drawHoveringText(textLines, x, y);
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		this.field_154330_a.tick();
		super.updateScreen();
	}

	public int func_154329_h() {
		return this.fontRendererObj.FONT_HEIGHT;
	}

	public int func_154326_c(String p_154326_1_) {
		return this.fontRendererObj.getStringWidth(p_154326_1_);
	}

	public void func_154319_c(String p_154319_1_, int p_154319_2_, int p_154319_3_, int p_154319_4_) {
		this.fontRendererObj.drawStringWithShadow(p_154319_1_, (float) p_154319_2_, (float) p_154319_3_, p_154319_4_);
	}

	public List<String> func_154323_a(String p_154323_1_, int p_154323_2_) {
		return this.fontRendererObj.listFormattedStringToWidth(p_154323_1_, p_154323_2_);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	public final void actionPerformed(GuiButton button) throws IOException {
		this.field_154330_a.buttonClicked(((GuiButtonRealmsProxy) button).getRealmsButton());
	}

	public void func_154324_i() {
		super.buttonList.clear();
	}

	public void func_154327_a(RealmsButton p_154327_1_) {
		super.buttonList.add(p_154327_1_.getProxy());
	}

	public List<RealmsButton> func_154320_j() {
		List<RealmsButton> list = Lists.<RealmsButton>newArrayListWithExpectedSize(super.buttonList.size());

		for (GuiButton guibutton : super.buttonList) {
			list.add(((GuiButtonRealmsProxy) guibutton).getRealmsButton());
		}

		return list;
	}

	public void func_154328_b(GuiButton p_154328_1_) {
		super.buttonList.remove(p_154328_1_);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.field_154330_a.mouseClicked(mouseX, mouseY, mouseButton);
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		this.field_154330_a.mouseEvent();
		super.handleMouseInput();
	}

	/**
	 * Handles keyboard input.
	 */
	public void handleKeyboardInput() throws IOException {
		this.field_154330_a.keyboardEvent();
		super.handleKeyboardInput();
	}

	/**
	 * Called when a mouse button is released. Args : mouseX, mouseY, releaseButton
	 */
	public void mouseReleased(int mouseX, int mouseY, int state) {
		this.field_154330_a.mouseReleased(mouseX, mouseY, state);
	}

	/**
	 * Called when a mouse button is pressed and the mouse is moved around.
	 * Parameters are : mouseX, mouseY, lastButtonClicked & timeSinceMouseClick.
	 */
	public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		this.field_154330_a.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	public void keyTyped(char typedChar, int keyCode) throws IOException {
		this.field_154330_a.keyPressed(typedChar, keyCode);
	}

	public void confirmClicked(boolean result, int id) {
		this.field_154330_a.confirmResult(result, id);
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		this.field_154330_a.removed();
		super.onGuiClosed();
	}
}
