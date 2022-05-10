package com.victorbrndls.uberminer2.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.gui.GuiFormatter;
import com.victorbrndls.uberminer2.gui.menu.UberMinerContainer;
import com.victorbrndls.uberminer2.util.GuiUtil;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UberMinerScreen extends AbstractContainerScreen<UberMinerContainer> {

    private static final ResourceLocation GUI_LOCATION = new ResourceLocation(UberMiner.MOD_ID,
                                                                              "textures/gui/uber_miner.png");

    private int i;
    private int j;

    public UberMinerScreen(UberMinerContainer menu, Inventory playerInventory, Component tile) {
        super(menu, playerInventory, tile);
    }

    @Override
    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
        i = (this.width - this.imageWidth) / 2;
        j = (this.height - this.imageHeight) / 2;

        this.renderBackground(poseStack);
        super.render(poseStack, pMouseX, pMouseY, pPartialTick);

        this.renderTooltip(poseStack, pMouseX, pMouseY);
        renderTooltips(poseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_LOCATION);

        blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        drawEnergy(poseStack);
        drawOperationProgress(poseStack);
        drawOresMined(poseStack);
    }

    private void drawEnergy(PoseStack poseStack) {
        fill(
                poseStack,
                i + 6,
                j + 66,
                i + 9,
                j + 66 - GuiUtil.lerp(0, 49, menu.getStoredEnergyPercentage()),
                0xFF_FF_4F_00
        );
    }

    private void drawOperationProgress(PoseStack poseStack) {
        fill(
                poseStack,
                i + 44,
                j + 63,
                i + 44 + GuiUtil.lerp(0, 89, menu.getProgressPercentage()),
                j + 65,
                0xFFFFFFFF
        );
    }

    private void renderTooltips(PoseStack poseStack, int mouseX, int mouseY) {
        final var actualX = mouseX - i;
        final var actualY = mouseY - j;

        // Energy
        if (actualX >= 5 && actualX <= 10 && actualY >= 16 && actualY <= 67) {
            Component text = Component.nullToEmpty(menu.getStoredEnergy() + " / " + menu.getMaxStoredEnergy());
            renderTooltip(poseStack, text, mouseX, mouseY);
        }

        // Ores Mined
        if (actualX >= 43 && actualX <= 133 && actualY >= 26 && actualY <= 30) {
            Component text = Component.nullToEmpty(GuiFormatter.formatProgress(menu.getMinedOresProgress()));
            renderTooltip(poseStack, text, mouseX, mouseY);
        }

        // Progress
        if (actualX >= 43 && actualX <= 133 && actualY >= 62 && actualY <= 66) {
            Component text = Component.nullToEmpty(menu.getOperationTime() + " / " + menu.getTotalOperationTime());
            renderTooltip(poseStack, text, mouseX, mouseY);
        }
    }

    private void drawOresMined(PoseStack poseStack) {
        String text = menu.getOresMined() + " / " + menu.getScannedOres();

        drawString(poseStack,
                   font,
                   text,
                   i + 132 - font.width(text),
                   j + 18,
                   0xFFFFFFFF);

        fill(
                poseStack,
                i + 44,
                j + 27,
                i + 44 + GuiUtil.lerp(0, 89, menu.getMinedOresProgress()),
                j + 29,
                0xFFFFFFFF
        );
    }
}
