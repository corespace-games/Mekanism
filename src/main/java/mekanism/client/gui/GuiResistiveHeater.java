package mekanism.client.gui;

import java.util.ArrayList;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.api.MekanismConfig.general;
import mekanism.api.util.ListUtils;
import mekanism.api.util.UnitDisplayUtils;
import mekanism.api.util.UnitDisplayUtils.TemperatureUnit;
import mekanism.client.gui.element.GuiElement.IInfoHandler;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiHeatInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSecurityTab;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerResistiveHeater;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityResistiveHeater;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiResistiveHeater extends GuiMekanism
{
	public TileEntityResistiveHeater tileEntity;
	
	private GuiTextField energyUsageField;
	
	public GuiResistiveHeater(InventoryPlayer inventory, TileEntityResistiveHeater tentity)
	{
		super(tentity, new ContainerResistiveHeater(inventory, tentity));
		tileEntity = tentity;
		
		guiElements.add(new GuiPowerBar(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png"), 164, 15));
		guiElements.add(new GuiSlot(SlotType.POWER, this, MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png"), 14, 34).with(SlotOverlay.POWER));
		guiElements.add(new GuiSecurityTab(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png")));
		guiElements.add(new GuiRedstoneControl(this, tileEntity, MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png")));
		guiElements.add(new GuiEnergyInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyUsage);
				return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png")));
		guiElements.add(new GuiHeatInfo(new IInfoHandler() {
			@Override
			public List<String> getInfo()
			{
				System.out.println(tileEntity.lastEnvironmentLoss);
				TemperatureUnit unit = TemperatureUnit.values()[general.tempUnit.ordinal()];
				String environment = UnitDisplayUtils.getDisplayShort(tileEntity.lastEnvironmentLoss*unit.intervalSize, false, unit);
				return ListUtils.asList(LangUtils.localize("gui.dissipated") + ": " + environment + "/t");
			}
		}, this, MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png")));
	}
	
	@Override
	public void initGui()
	{
		super.initGui();

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		
		String prevEnergyUsage = energyUsageField != null ? energyUsageField.getText() : "";
		
		energyUsageField = new GuiTextField(fontRendererObj, guiWidth + 49, guiHeight + 52, 66, 11);
		energyUsageField.setMaxStringLength(7);
		energyUsageField.setEnableBackgroundDrawing(false);
		energyUsageField.setText(prevEnergyUsage);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRendererObj.drawString(tileEntity.getInventoryName(), (xSize / 2) - (fontRendererObj.getStringWidth(tileEntity.getInventoryName()) / 2), 6, 0x404040);
		fontRendererObj.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 94) + 2, 0x404040);
		
		renderScaledText(LangUtils.localize("gui.temp") + ": " + MekanismUtils.getTemperatureDisplay(tileEntity.temperature, TemperatureUnit.AMBIENT), 50, 25, 0x00CD00, 76);
		renderScaledText(LangUtils.localize("gui.usage") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.energyUsage) + "/t", 50, 41, 0x00CD00, 76);

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI, "GuiResistiveHeater.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);
		
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);
		
		if(xAxis >= 116 && xAxis <= 126 && yAxis >= 51 && yAxis <= 61)
		{
			drawTexturedModalRect(guiWidth + 116, guiHeight + 51, xSize, 0, 11, 11);
		}
		else {
			drawTexturedModalRect(guiWidth + 116, guiHeight + 51, xSize, 11, 11, 11);
		}

		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
		
		energyUsageField.drawTextBox();
	}
	
	private void setEnergyUsage()
	{
		if(!energyUsageField.getText().isEmpty())
		{
			int toUse = Integer.parseInt(energyUsageField.getText());

			ArrayList data = new ArrayList();
			data.add(toUse);

			Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));

			energyUsageField.setText("");
		}
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		energyUsageField.updateCursorCounter();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);

		energyUsageField.mouseClicked(mouseX, mouseY, button);
		
		if(button == 0)
		{
			int xAxis = (mouseX - (width - xSize) / 2);
			int yAxis = (mouseY - (height - ySize) / 2);
			
			if(xAxis >= 116 && xAxis <= 126 && yAxis >= 51 && yAxis <= 61)
			{
				setEnergyUsage();
	            SoundHandler.playSound("gui.button.press");
			}
		}
	}
	
	@Override
	public void keyTyped(char c, int i)
	{
		if(!energyUsageField.isFocused() || i == Keyboard.KEY_ESCAPE)
		{
			super.keyTyped(c, i);
		}

		if(energyUsageField.isFocused() && i == Keyboard.KEY_RETURN)
		{
			setEnergyUsage();
			return;
		}

		if(Character.isDigit(c) || i == Keyboard.KEY_BACK || i == Keyboard.KEY_DELETE || i == Keyboard.KEY_LEFT || i == Keyboard.KEY_RIGHT)
		{
			energyUsageField.textboxKeyTyped(c, i);
		}
	}
}
