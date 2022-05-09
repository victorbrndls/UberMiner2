package com.victorbrndls.uberminer2.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.victorbrndls.uberminer2.UberMiner;
import com.victorbrndls.uberminer2.gui.menu.UberMinerContainer;
import com.victorbrndls.uberminer2.util.GuiUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UberMinerScreen extends AbstractContainerScreen<UberMinerContainer> {

    private static final ResourceLocation GUI_LOCATION = new ResourceLocation(UberMiner.MOD_ID,
                                                                              "textures/gui/uber_miner.png");

    public UberMinerScreen(UberMinerContainer menu, Inventory playerInventory, Component tile) {
        super(menu, playerInventory, tile);
    }

    @Override
    public void render(PoseStack poseStack, int pMouseX, int pMouseY, float pPartialTick) {
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

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        blit(poseStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        drawEnergy(poseStack, i, j);
        drawOperationProgress(poseStack, i, j);
    }

    private void drawEnergy(PoseStack poseStack, int i, int j) {
        fill(
                poseStack,
                i + 6,
                j + 66,
                i + 9,
                j + 66 - GuiUtil.lerp(0, 49, menu.getStoredEnergyPercentage()),
                0xFFFFFFFF
        );
    }

    private void drawOperationProgress(PoseStack poseStack, int i, int j) {
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
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        final var actualX = mouseX - i;
        final var actualY = mouseY - j;

        // Energy
        if (actualX >= 5 && actualX <= 10 && actualY >= 16 && actualY <= 67) {
            Component text = Component.nullToEmpty(menu.getStoredEnergy() + " / " + menu.getMaxStoredEnergy());
            renderTooltip(poseStack, text, mouseX, mouseY);
        }

        // Progress
        if (actualX >= 43 && actualX <= 133 && actualY >= 62 && actualY <= 66) {
            Component text = Component.nullToEmpty(menu.getOperationTime() + " / " + menu.getTotalOperationTime());
            renderTooltip(poseStack, text, mouseX, mouseY);
        }
    }

}
