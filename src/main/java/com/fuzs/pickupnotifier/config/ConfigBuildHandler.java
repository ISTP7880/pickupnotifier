package com.fuzs.pickupnotifier.config;

import com.fuzs.pickupnotifier.PickUpNotifier;
import com.fuzs.pickupnotifier.client.util.PositionPreset;
import com.fuzs.pickupnotifier.client.util.TextColor;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = PickUpNotifier.MODID)
public class ConfigBuildHandler {

    @Config.Name("general")
    public static GeneralConfig generalConfig = new GeneralConfig();
    @Config.Name("display")
    public static DisplayConfig displayConfig = new DisplayConfig();

    public static class GeneralConfig {

        @Config.Name("Blacklist")
        @Config.Comment({"Disable specific items or content from whole mods from showing.", "Format for every entry is \"<namespace>:<path>\". Path may use single asterisk as wildcard parameter."})
        public String[] blacklist = new String[0];
        @Config.Name("Draw Sprites")
        @Config.Comment("Show a small sprite next to the name of each entry showing its contents.")
        public boolean showSprite = true;
        @Config.Name("Text Color")
        @Config.Comment("Color of the entry name text.")
        public TextColor color = TextColor.WHITE;
        @Config.Name("Ignore Rarity")
        @Config.Comment("Ignore rarity of items and always use color specified in \"Text Color\" instead.")
        public boolean ignoreRarity = false;
        @Config.Name("Combine Entries")
        @Config.Comment("Combine entries of the same type instead of showing each one individually.")
        public boolean combineEntries = true;
        @Config.Name("Display Time")
        @Config.Comment("Amount of ticks each entry will be shown for. Set to 0 to only remove entries when space for new ones is needed.")
        @Config.RangeInt(min = 0)
        public int displayTime = 80;
        @Config.Name("Move Out Of Screen")
        @Config.Comment("Make outdated entries slowly move out of the screen instead of disappearing in place.")
        public boolean move = true;
        @Config.Name("Move Time")
        @Config.Comment("Amount of ticks it takes for an entry to move out of the screen. Value cannot be larger than \"Display Time\".")
        @Config.RangeInt(min = 0)
        public int moveTime = 20;
        @Config.Name("Fade Away")
        @Config.Comment("Make outdated entry names slowly fade away instead of simply vanishing.")
        public boolean fadeAway = true;
        @Config.Name("Maximum Amount")
        @Config.Comment("Maximum count number displayed. Setting this to 0 will prevent the count from being displayed at all.")
        @Config.RangeInt(min = 0)
        public int maxCount = 9999;
        @Config.Name("Display Experience")
        @Config.Comment("Include experience orbs the player has collected as part of the list of entries.")
        public boolean displayExperience = true;

    }

    public static class DisplayConfig {

        @Config.Name("Screen Corner")
        @Config.Comment("Screen corner for entry list to be drawn in.")
        public PositionPreset position = PositionPreset.BOTTOM_RIGHT;
        @Config.Name("X-Offset")
        @Config.Comment("Offset on x-axis from screen border.")
        @Config.RangeInt(min = 0)
        public int xOffset = 8;
        @Config.Name("Y-Offset")
        @Config.Comment("Offset on y-axis from screen border.")
        @Config.RangeInt(min = 0)
        public int yOffset = 4;
        @Config.Name("Maximum Height")
        @Config.Comment("Percentage of relative screen height entries are allowed to fill at max.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double height = 0.5;
        @Config.Name("Custom Scale")
        @Config.Comment("Scale of entries. A lower scale will make room for more rows to show. Works in tandem with \"GUI Scale\" option in \"Video Settings\".")
        @Config.RangeInt(min = 1, max = 24)
        public int scale = 4;

    }

}