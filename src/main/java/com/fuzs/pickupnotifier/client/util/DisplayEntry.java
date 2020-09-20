package com.fuzs.pickupnotifier.client.util;

import com.fuzs.pickupnotifier.config.ConfigBuildHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.mutable.MutableFloat;

@SuppressWarnings("WeakerAccess")
public abstract class DisplayEntry {

    public static final int HEIGHT = 18;
    private static final int MARGIN = 4;

    private final EnumRarity rarity;
    protected int count;
    private final MutableFloat life;

    protected DisplayEntry(int count, EnumRarity rarity) {
        this.count = Math.min(count, ConfigBuildHandler.generalConfig.maxCount);
        this.rarity = rarity;
        this.life = new MutableFloat(ConfigBuildHandler.generalConfig.displayTime);
    }

    public boolean isDead() {
        return this.life.compareTo(new MutableFloat(0.0F)) < 1;
    }

    public final void tick(float f) {
        this.life.subtract(f);
    }

    public final int getCount() {
        return this.count;
    }

    public boolean canMerge(DisplayEntry entry) {
        return this.rarity == entry.rarity;
    };

    public final void addCount(int i) {
        this.count = Math.min(this.count + i, ConfigBuildHandler.generalConfig.maxCount);
    }

    protected abstract ITextComponent getName();

    private ITextComponent getNameComponent() {

        ITextComponent name = this.getName().createCopy();
        if (this.count <= 0) {
            return name;
        }
        if (ConfigBuildHandler.displayConfig.position.isMirrored()) {
            return new TextComponentString(this.count + "x ").appendSibling(name);
        } else {
            return name.appendText(" x" + this.count);
        }

    }

    public final float getRelativeLife() {
        return 1.0F - Math.min(1.0F, this.getLife() / Math.min(ConfigBuildHandler.generalConfig.moveTime,
                ConfigBuildHandler.generalConfig.displayTime));
    }

    protected final float getLife() {
        return Math.max(0.0F, this.life.floatValue());
    }

    public final void resetLife() {
        this.life.setValue(ConfigBuildHandler.generalConfig.displayTime);
    }

    public void merge(DisplayEntry entry) {

        this.addCount(entry.getCount());
        this.resetLife();

    }

    private Style getStyle() {

        if (!ConfigBuildHandler.generalConfig.ignoreRarity && this.rarity != EnumRarity.COMMON) {
            return new Style().setColor(this.rarity.rarityColor);
        } else {
            return new Style().setColor(ConfigBuildHandler.generalConfig.color.getChatColor());
        }

    }

    private String getNameString() {
        return this.getNameComponent().setStyle(this.getStyle()).getFormattedText();
    }

    private int getTextWidth(Minecraft mc) {
        String s = this.getNameComponent().getUnformattedText();
        return mc.fontRenderer.getStringWidth(TextFormatting.getTextWithoutFormattingCodes(s));
    }

    public int getTotalWidth(Minecraft mc) {
        int length = this.getTextWidth(mc);
        return ConfigBuildHandler.generalConfig.showSprite ? length + MARGIN + 16 : length;
    }

    public final void render(Minecraft mc, int posX, int posY, float alpha) {

        boolean mirrored = ConfigBuildHandler.displayConfig.position.isMirrored();
        boolean sprite = ConfigBuildHandler.generalConfig.showSprite;
        int i = mirrored || !sprite ? posX : posX + 16 + MARGIN;
        int textWidth = this.getTextWidth(mc);

        int k = ConfigBuildHandler.generalConfig.fadeAway ? 255 - (int) (255 * alpha) : 255;
        if (k >= 5) { // prevents a bug where names would appear once at the end with full alpha
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            mc.fontRenderer.drawStringWithShadow(this.getNameString(), i, posY + 3, 16777215 + (k << 24));
            GlStateManager.disableBlend();
            if (sprite) {
                this.renderSprite(mc, mirrored ? posX + textWidth + MARGIN : posX, posY);
            }
        }

    }

    protected abstract void renderSprite(Minecraft mc, int posX, int posY);

    @SuppressWarnings("unused")
    public abstract DisplayEntry copy();

}