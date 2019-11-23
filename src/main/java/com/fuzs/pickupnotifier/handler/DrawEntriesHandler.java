package com.fuzs.pickupnotifier.handler;

import com.fuzs.pickupnotifier.util.DisplayEntry;
import com.fuzs.pickupnotifier.util.PositionPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DrawEntriesHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlayText(RenderGameOverlayEvent.Text evt) {

        if (!AddEntriesHandler.PICK_UPS.isEmpty() && !this.mc.isGamePaused() && ConfigBuildHandler.GENERAL_CONFIG.displayTime.get() != 0) {
            synchronized (AddEntriesHandler.PICK_UPS) {
                AddEntriesHandler.PICK_UPS.forEach(it -> it.tick(evt.getPartialTicks()));
                AddEntriesHandler.PICK_UPS.removeIf(DisplayEntry::isDead);
            }
        }

        if (AddEntriesHandler.PICK_UPS.isEmpty()) {
            return;
        }

        float scale = ConfigBuildHandler.DISPLAY_CONFIG.scale.get() / 6.0F;
        int scaledWidth = (int) (this.mc.mainWindow.getScaledWidth() / scale);
        int scaledHeight = (int) (this.mc.mainWindow.getScaledHeight() / scale);
        PositionPreset position = ConfigBuildHandler.DISPLAY_CONFIG.position.get();
        boolean bottom = position.isBottom();
        int posX = (int) (ConfigBuildHandler.DISPLAY_CONFIG.xOffset.get() / scale);
        int posY = (int) (ConfigBuildHandler.DISPLAY_CONFIG.yOffset.get() / scale);
        int offset = position.getY(DisplayEntry.HEIGHT, scaledHeight, posY);
        boolean move = ConfigBuildHandler.GENERAL_CONFIG.move.get();
        int totalFade = move ? (int) (AddEntriesHandler.PICK_UPS.stream().mapToDouble(DisplayEntry::getRelativeLife).average().orElse(0.0) * AddEntriesHandler.PICK_UPS.size() * DisplayEntry.HEIGHT) : 0;
        int renderY = offset + (bottom ? totalFade : -totalFade);
        GlStateManager.scalef(scale, scale, 1.0F);

        synchronized (AddEntriesHandler.PICK_UPS) {
            for (DisplayEntry entry : AddEntriesHandler.PICK_UPS) {
                int renderX = position.getX(entry.getTotalWidth(this.mc), scaledWidth, posX);
                if (bottom) {
                    if (renderY < offset + DisplayEntry.HEIGHT) {
                        entry.render(this.mc, renderX, renderY, move ? MathHelper.clamp((float) (renderY - offset) / DisplayEntry.HEIGHT, 0.0F, 1.0F) : entry.getRelativeLife());
                    }
                } else if (renderY > offset - DisplayEntry.HEIGHT) {
                    entry.render(this.mc, renderX, renderY, move ? MathHelper.clamp((float) (renderY - offset) / -DisplayEntry.HEIGHT, 0.0F, 1.0F) : entry.getRelativeLife());
                }
                renderY += bottom ? -DisplayEntry.HEIGHT : DisplayEntry.HEIGHT;
            }
        }

        GlStateManager.scalef(1.0F / scale, 1.0F / scale, 1.0F);

    }

}