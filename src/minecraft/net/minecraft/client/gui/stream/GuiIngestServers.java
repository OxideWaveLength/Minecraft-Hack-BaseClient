package net.minecraft.client.gui.stream;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.IngestServerTester;
import net.minecraft.util.EnumChatFormatting;
import tv.twitch.broadcast.IngestServer;

public class GuiIngestServers extends GuiScreen {
	private final GuiScreen field_152309_a;
	private String field_152310_f;
	private GuiIngestServers.ServerList field_152311_g;

	public GuiIngestServers(GuiScreen p_i46312_1_) {
		this.field_152309_a = p_i46312_1_;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		this.field_152310_f = I18n.format("options.stream.ingest.title", new Object[0]);
		this.field_152311_g = new GuiIngestServers.ServerList(this.mc);

		if (!this.mc.getTwitchStream().func_152908_z()) {
			this.mc.getTwitchStream().func_152909_x();
		}

		this.buttonList.add(new GuiButton(1, this.width / 2 - 155, this.height - 24 - 6, 150, 20, I18n.format("gui.done", new Object[0])));
		this.buttonList.add(new GuiButton(2, this.width / 2 + 5, this.height - 24 - 6, 150, 20, I18n.format("options.stream.ingest.reset", new Object[0])));
	}

	/**
	 * Handles mouse input.
	 */
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		this.field_152311_g.handleMouseInput();
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		if (this.mc.getTwitchStream().func_152908_z()) {
			this.mc.getTwitchStream().func_152932_y().func_153039_l();
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 1) {
				this.mc.displayGuiScreen(this.field_152309_a);
			} else {
				this.mc.gameSettings.streamPreferredServer = "";
				this.mc.gameSettings.saveOptions();
			}
		}
	}

	/**
	 * Draws the screen and all the components in it. Args : mouseX, mouseY,
	 * renderPartialTicks
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.field_152311_g.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(this.fontRendererObj, this.field_152310_f, this.width / 2, 20, 16777215);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	class ServerList extends GuiSlot {
		public ServerList(Minecraft mcIn) {
			super(mcIn, GuiIngestServers.this.width, GuiIngestServers.this.height, 32, GuiIngestServers.this.height - 35, (int) ((double) mcIn.fontRendererObj.FONT_HEIGHT * 3.5D));
			this.setShowSelectionBox(false);
		}

		protected int getSize() {
			return this.mc.getTwitchStream().func_152925_v().length;
		}

		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
			this.mc.gameSettings.streamPreferredServer = this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl;
			this.mc.gameSettings.saveOptions();
		}

		protected boolean isSelected(int slotIndex) {
			return this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl.equals(this.mc.gameSettings.streamPreferredServer);
		}

		protected void drawBackground() {
		}

		protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
			IngestServer ingestserver = this.mc.getTwitchStream().func_152925_v()[entryID];
			String s = ingestserver.serverUrl.replaceAll("\\{stream_key\\}", "");
			String s1 = (int) ingestserver.bitrateKbps + " kbps";
			String s2 = null;
			IngestServerTester ingestservertester = this.mc.getTwitchStream().func_152932_y();

			if (ingestservertester != null) {
				if (ingestserver == ingestservertester.func_153040_c()) {
					s = EnumChatFormatting.GREEN + s;
					s1 = (int) (ingestservertester.func_153030_h() * 100.0F) + "%";
				} else if (entryID < ingestservertester.func_153028_p()) {
					if (ingestserver.bitrateKbps == 0.0F) {
						s1 = EnumChatFormatting.RED + "Down!";
					}
				} else {
					s1 = EnumChatFormatting.OBFUSCATED + "1234" + EnumChatFormatting.RESET + " kbps";
				}
			} else if (ingestserver.bitrateKbps == 0.0F) {
				s1 = EnumChatFormatting.RED + "Down!";
			}

			p_180791_2_ = p_180791_2_ - 15;

			if (this.isSelected(entryID)) {
				s2 = EnumChatFormatting.BLUE + "(Preferred)";
			} else if (ingestserver.defaultServer) {
				s2 = EnumChatFormatting.GREEN + "(Default)";
			}

			GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, ingestserver.serverName, p_180791_2_ + 2, p_180791_3_ + 5, 16777215);
			GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s, p_180791_2_ + 2, p_180791_3_ + GuiIngestServers.this.fontRendererObj.FONT_HEIGHT + 5 + 3, 3158064);
			GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s1, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(s1), p_180791_3_ + 5, 8421504);

			if (s2 != null) {
				GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, s2, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(s2), p_180791_3_ + 5 + 3 + GuiIngestServers.this.fontRendererObj.FONT_HEIGHT, 8421504);
			}
		}

		protected int getScrollBarX() {
			return super.getScrollBarX() + 15;
		}
	}
}
