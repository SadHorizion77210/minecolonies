package com.minecolonies.client.gui;

import com.minecolonies.MineColonies;
import com.minecolonies.tileentities.TileEntityTownHall;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class GuiTypable extends GuiScreen
{
    private TileEntityTownHall tileEntityTownHall;
    private GuiTextField guiTextField = null;
    private final String message = "Rename Your City";
    private String       newCityName  = message;
    private EntityPlayer player;
    private World        world;
    private int          x;
    private int          y;
    private int          z;

    public GuiTypable(TileEntityTownHall tileEntityTownHall, EntityPlayer player, World world, int x, int y, int z)
    {
        this.tileEntityTownHall = tileEntityTownHall;
        this.tileEntityTownHall = tileEntityTownHall;
        this.player = player;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void initGui()
    {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        //Do Not Move down, hides crosshair
        guiTextField = new GuiTextField(this.fontRendererObj, this.width / 2 - 75, this.height / 2 - 10, 150, 18);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 110, "Done"));

        this.guiTextField.setMaxStringLength(1024);
        this.guiTextField.setText(newCityName);
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        this.guiTextField.mouseClicked(par1, par2, par3);
        super.mouseClicked(par1, par2, par3);
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        this.guiTextField.textboxKeyTyped(par1, par2);
        this.newCityName = this.guiTextField.getText();
        super.keyTyped(par1, par2);
    }

    @Override
    public void updateScreen()
    {
        this.guiTextField.updateCursorCounter();
        super.updateScreen();
        newCityName = guiTextField.getText();
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        if(guiButton.enabled)
        {
            if(guiButton.id == 0)
            {
                if(!newCityName.equals(message))
                {
                    tileEntityTownHall.setCityName(newCityName);
                    tileEntityTownHall.markDirty();
                }
                this.mc.displayGuiScreen((GuiScreen) null);
                player.openGui(MineColonies.instance, 0, world, x, y, z);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        fontRendererObj.drawString(message, this.width / 2 - fontRendererObj.getStringWidth(message) / 2, this.height / 2 - 20, 0xffffff);
        this.guiTextField.drawTextBox();
    }
}
