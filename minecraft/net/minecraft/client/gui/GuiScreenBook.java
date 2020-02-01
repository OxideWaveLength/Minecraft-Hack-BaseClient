package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends GuiScreen
{
    private static final Logger logger = LogManager.getLogger();
    private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");

    /** The player editing the book */
    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;

    /** Whether the book is signed or can still be edited */
    private final boolean bookIsUnsigned;

    /**
     * Whether the book's title or contents has been modified since being opened
     */
    private boolean bookIsModified;

    /** Determines if the signing screen is open */
    private boolean bookGettingSigned;

    /** Update ticks since the gui was opened */
    private int updateCount;
    private int bookImageWidth = 192;
    private int bookImageHeight = 192;
    private int bookTotalPages = 1;
    private int currPage;
    private NBTTagList bookPages;
    private String bookTitle = "";
    private List<IChatComponent> field_175386_A;
    private int field_175387_B = -1;
    private GuiScreenBook.NextPageButton buttonNextPage;
    private GuiScreenBook.NextPageButton buttonPreviousPage;
    private GuiButton buttonDone;

    /** The GuiButton to sign this book. */
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;

    public GuiScreenBook(EntityPlayer player, ItemStack book, boolean isUnsigned)
    {
        this.editingPlayer = player;
        this.bookObj = book;
        this.bookIsUnsigned = isUnsigned;

        if (book.hasTagCompound())
        {
            NBTTagCompound nbttagcompound = book.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8);

            if (this.bookPages != null)
            {
                this.bookPages = (NBTTagList)this.bookPages.copy();
                this.bookTotalPages = this.bookPages.tagCount();

                if (this.bookTotalPages < 1)
                {
                    this.bookTotalPages = 1;
                }
            }
        }

        if (this.bookPages == null && isUnsigned)
        {
            this.bookPages = new NBTTagList();
            this.bookPages.appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
        ++this.updateCount;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);

        if (this.bookIsUnsigned)
        {
            this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton", new Object[0])));
            this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done", new Object[0])));
            this.buttonList.add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel", new Object[0])));
        }
        else
        {
            this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done", new Object[0])));
        }

        int i = (this.width - this.bookImageWidth) / 2;
        int j = 2;
        this.buttonList.add(this.buttonNextPage = new GuiScreenBook.NextPageButton(1, i + 120, j + 154, true));
        this.buttonList.add(this.buttonPreviousPage = new GuiScreenBook.NextPageButton(2, i + 38, j + 154, false));
        this.updateButtons();
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    private void updateButtons()
    {
        this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
        this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
        this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;

        if (this.bookIsUnsigned)
        {
            this.buttonSign.visible = !this.bookGettingSigned;
            this.buttonCancel.visible = this.bookGettingSigned;
            this.buttonFinalize.visible = this.bookGettingSigned;
            this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
        }
    }

    private void sendBookToServer(boolean publish) throws IOException
    {
        if (this.bookIsUnsigned && this.bookIsModified)
        {
            if (this.bookPages != null)
            {
                while (this.bookPages.tagCount() > 1)
                {
                    String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);

                    if (s.length() != 0)
                    {
                        break;
                    }

                    this.bookPages.removeTag(this.bookPages.tagCount() - 1);
                }

                if (this.bookObj.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound = this.bookObj.getTagCompound();
                    nbttagcompound.setTag("pages", this.bookPages);
                }
                else
                {
                    this.bookObj.setTagInfo("pages", this.bookPages);
                }

                String s2 = "MC|BEdit";

                if (publish)
                {
                    s2 = "MC|BSign";
                    this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
                    this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));

                    for (int i = 0; i < this.bookPages.tagCount(); ++i)
                    {
                        String s1 = this.bookPages.getStringTagAt(i);
                        IChatComponent ichatcomponent = new ChatComponentText(s1);
                        s1 = IChatComponent.Serializer.componentToJson(ichatcomponent);
                        this.bookPages.set(i, new NBTTagString(s1));
                    }

                    this.bookObj.setItem(Items.written_book);
                }

                PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
                packetbuffer.writeItemStackToBuffer(this.bookObj);
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(s2, packetbuffer));
            }
        }
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 0)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
                this.sendBookToServer(false);
            }
            else if (button.id == 3 && this.bookIsUnsigned)
            {
                this.bookGettingSigned = true;
            }
            else if (button.id == 1)
            {
                if (this.currPage < this.bookTotalPages - 1)
                {
                    ++this.currPage;
                }
                else if (this.bookIsUnsigned)
                {
                    this.addNewPage();

                    if (this.currPage < this.bookTotalPages - 1)
                    {
                        ++this.currPage;
                    }
                }
            }
            else if (button.id == 2)
            {
                if (this.currPage > 0)
                {
                    --this.currPage;
                }
            }
            else if (button.id == 5 && this.bookGettingSigned)
            {
                this.sendBookToServer(true);
                this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (button.id == 4 && this.bookGettingSigned)
            {
                this.bookGettingSigned = false;
            }

            this.updateButtons();
        }
    }

    private void addNewPage()
    {
        if (this.bookPages != null && this.bookPages.tagCount() < 50)
        {
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        if (this.bookIsUnsigned)
        {
            if (this.bookGettingSigned)
            {
                this.keyTypedInTitle(typedChar, keyCode);
            }
            else
            {
                this.keyTypedInBook(typedChar, keyCode);
            }
        }
    }

    /**
     * Processes keystrokes when editing the text of a book
     */
    private void keyTypedInBook(char typedChar, int keyCode)
    {
        if (GuiScreen.isKeyComboCtrlV(keyCode))
        {
            this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
        }
        else
        {
            switch (keyCode)
            {
                case 14:
                    String s = this.pageGetCurrent();

                    if (s.length() > 0)
                    {
                        this.pageSetCurrent(s.substring(0, s.length() - 1));
                    }

                    return;

                case 28:
                case 156:
                    this.pageInsertIntoCurrent("\n");
                    return;

                default:
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
                    {
                        this.pageInsertIntoCurrent(Character.toString(typedChar));
                    }
            }
        }
    }

    /**
     * Processes keystrokes when editing the title of a book
     */
    private void keyTypedInTitle(char p_146460_1_, int p_146460_2_) throws IOException
    {
        switch (p_146460_2_)
        {
            case 14:
                if (!this.bookTitle.isEmpty())
                {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }

                return;

            case 28:
            case 156:
                if (!this.bookTitle.isEmpty())
                {
                    this.sendBookToServer(true);
                    this.mc.displayGuiScreen((GuiScreen)null);
                }

                return;

            default:
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(p_146460_1_))
                {
                    this.bookTitle = this.bookTitle + Character.toString(p_146460_1_);
                    this.updateButtons();
                    this.bookIsModified = true;
                }
        }
    }

    /**
     * Returns the entire text of the current page as determined by currPage
     */
    private String pageGetCurrent()
    {
        return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
    }

    /**
     * Sets the text of the current page as determined by currPage
     */
    private void pageSetCurrent(String p_146457_1_)
    {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount())
        {
            this.bookPages.set(this.currPage, new NBTTagString(p_146457_1_));
            this.bookIsModified = true;
        }
    }

    /**
     * Processes any text getting inserted into the current page, enforcing the page size limit
     */
    private void pageInsertIntoCurrent(String p_146459_1_)
    {
        String s = this.pageGetCurrent();
        String s1 = s + p_146459_1_;
        int i = this.fontRendererObj.splitStringWidth(s1 + "" + EnumChatFormatting.BLACK + "_", 118);

        if (i <= 128 && s1.length() < 256)
        {
            this.pageSetCurrent(s1);
        }
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(bookGuiTextures);
        int i = (this.width - this.bookImageWidth) / 2;
        int j = 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.bookImageWidth, this.bookImageHeight);

        if (this.bookGettingSigned)
        {
            String s = this.bookTitle;

            if (this.bookIsUnsigned)
            {
                if (this.updateCount / 6 % 2 == 0)
                {
                    s = s + "" + EnumChatFormatting.BLACK + "_";
                }
                else
                {
                    s = s + "" + EnumChatFormatting.GRAY + "_";
                }
            }

            String s1 = I18n.format("book.editTitle", new Object[0]);
            int k = this.fontRendererObj.getStringWidth(s1);
            this.fontRendererObj.drawString(s1, i + 36 + (116 - k) / 2, j + 16 + 16, 0);
            int l = this.fontRendererObj.getStringWidth(s);
            this.fontRendererObj.drawString(s, i + 36 + (116 - l) / 2, j + 48, 0);
            String s2 = I18n.format("book.byAuthor", new Object[] {this.editingPlayer.getName()});
            int i1 = this.fontRendererObj.getStringWidth(s2);
            this.fontRendererObj.drawString(EnumChatFormatting.DARK_GRAY + s2, i + 36 + (116 - i1) / 2, j + 48 + 10, 0);
            String s3 = I18n.format("book.finalizeWarning", new Object[0]);
            this.fontRendererObj.drawSplitString(s3, i + 36, j + 80, 116, 0);
        }
        else
        {
            String s4 = I18n.format("book.pageIndicator", new Object[] {Integer.valueOf(this.currPage + 1), Integer.valueOf(this.bookTotalPages)});
            String s5 = "";

            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount())
            {
                s5 = this.bookPages.getStringTagAt(this.currPage);
            }

            if (this.bookIsUnsigned)
            {
                if (this.fontRendererObj.getBidiFlag())
                {
                    s5 = s5 + "_";
                }
                else if (this.updateCount / 6 % 2 == 0)
                {
                    s5 = s5 + "" + EnumChatFormatting.BLACK + "_";
                }
                else
                {
                    s5 = s5 + "" + EnumChatFormatting.GRAY + "_";
                }
            }
            else if (this.field_175387_B != this.currPage)
            {
                if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound()))
                {
                    try
                    {
                        IChatComponent ichatcomponent = IChatComponent.Serializer.jsonToComponent(s5);
                        this.field_175386_A = ichatcomponent != null ? GuiUtilRenderComponents.func_178908_a(ichatcomponent, 116, this.fontRendererObj, true, true) : null;
                    }
                    catch (JsonParseException var13)
                    {
                        this.field_175386_A = null;
                    }
                }
                else
                {
                    ChatComponentText chatcomponenttext = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
                    this.field_175386_A = Lists.newArrayList(chatcomponenttext);
                }

                this.field_175387_B = this.currPage;
            }

            int j1 = this.fontRendererObj.getStringWidth(s4);
            this.fontRendererObj.drawString(s4, i - j1 + this.bookImageWidth - 44, j + 16, 0);

            if (this.field_175386_A == null)
            {
                this.fontRendererObj.drawSplitString(s5, i + 36, j + 16 + 16, 116, 0);
            }
            else
            {
                int k1 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

                for (int l1 = 0; l1 < k1; ++l1)
                {
                    IChatComponent ichatcomponent2 = (IChatComponent)this.field_175386_A.get(l1);
                    this.fontRendererObj.drawString(ichatcomponent2.getUnformattedText(), i + 36, j + 16 + 16 + l1 * this.fontRendererObj.FONT_HEIGHT, 0);
                }

                IChatComponent ichatcomponent1 = this.func_175385_b(mouseX, mouseY);

                if (ichatcomponent1 != null)
                {
                    this.handleComponentHover(ichatcomponent1, mouseX, mouseY);
                }
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (mouseButton == 0)
        {
            IChatComponent ichatcomponent = this.func_175385_b(mouseX, mouseY);

            if (this.handleComponentClick(ichatcomponent))
            {
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Executes the click event specified by the given chat component
     */
    protected boolean handleComponentClick(IChatComponent p_175276_1_)
    {
        ClickEvent clickevent = p_175276_1_ == null ? null : p_175276_1_.getChatStyle().getChatClickEvent();

        if (clickevent == null)
        {
            return false;
        }
        else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE)
        {
            String s = clickevent.getValue();

            try
            {
                int i = Integer.parseInt(s) - 1;

                if (i >= 0 && i < this.bookTotalPages && i != this.currPage)
                {
                    this.currPage = i;
                    this.updateButtons();
                    return true;
                }
            }
            catch (Throwable var5)
            {
                ;
            }

            return false;
        }
        else
        {
            boolean flag = super.handleComponentClick(p_175276_1_);

            if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND)
            {
                this.mc.displayGuiScreen((GuiScreen)null);
            }

            return flag;
        }
    }

    public IChatComponent func_175385_b(int p_175385_1_, int p_175385_2_)
    {
        if (this.field_175386_A == null)
        {
            return null;
        }
        else
        {
            int i = p_175385_1_ - (this.width - this.bookImageWidth) / 2 - 36;
            int j = p_175385_2_ - 2 - 16 - 16;

            if (i >= 0 && j >= 0)
            {
                int k = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

                if (i <= 116 && j < this.mc.fontRendererObj.FONT_HEIGHT * k + k)
                {
                    int l = j / this.mc.fontRendererObj.FONT_HEIGHT;

                    if (l >= 0 && l < this.field_175386_A.size())
                    {
                        IChatComponent ichatcomponent = (IChatComponent)this.field_175386_A.get(l);
                        int i1 = 0;

                        for (IChatComponent ichatcomponent1 : ichatcomponent)
                        {
                            if (ichatcomponent1 instanceof ChatComponentText)
                            {
                                i1 += this.mc.fontRendererObj.getStringWidth(((ChatComponentText)ichatcomponent1).getChatComponentText_TextValue());

                                if (i1 > i)
                                {
                                    return ichatcomponent1;
                                }
                            }
                        }
                    }

                    return null;
                }
                else
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
    }

    static class NextPageButton extends GuiButton
    {
        private final boolean field_146151_o;

        public NextPageButton(int p_i46316_1_, int p_i46316_2_, int p_i46316_3_, boolean p_i46316_4_)
        {
            super(p_i46316_1_, p_i46316_2_, p_i46316_3_, 23, 13, "");
            this.field_146151_o = p_i46316_4_;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
                int i = 0;
                int j = 192;

                if (flag)
                {
                    i += 23;
                }

                if (!this.field_146151_o)
                {
                    j += 13;
                }

                this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 23, 13);
            }
        }
    }
}
