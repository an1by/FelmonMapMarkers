package net.aniby.felmonmapmarkers.other;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import net.aniby.felmonmapmarkers.FelmonMapMarkers;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FBMConfig {
    public static final HashMap<Material, Byte> markers = new HashMap<>();
    public static final HashMap<Material, Byte> triangleMarkers = new HashMap<>( // 5
            Map.of(
                    Material.RED_DYE, (byte) 26,
                    Material.WHITE_DYE, (byte) 4
            )
    );

    public static int offset_x = 0;
    public static int offset_z = 0;

    public static boolean rename = true;
    public static boolean color_map = true;

    public static void init() {
        FelmonMapMarkers instance = FelmonMapMarkers.getInstance();
        FileConfiguration config = instance.getConfig();
        instance.saveDefaultConfig();

        ConfigurationSection markerSection = config.getConfigurationSection("markers");
        if (markerSection != null) {
            for (String key : markerSection.getKeys(false)) {
                try { // 10 - 25
                    byte value = (byte) markerSection.getInt(key);
                    if (value >= 10 && value <= 25) {
                        instance.getLogger().warning("`" + key + "` marker can't be loaded: " + value + " IS BANNER");
                        continue;
                    }
                    if (value < 0 || value > 26) {
                        instance.getLogger().warning("`" + key + "` marker can't be loaded: " + value + " OUT OF RANGE");
                        continue;
                    }
                    Material material = Material.valueOf(key.toUpperCase(Locale.ROOT));
                    markers.put(material, value);
                } catch (Exception ignored) {
                    instance.getLogger().warning("`" + key + "` marker can't be loaded: INVALID MATERIAL");
                }
            }
        }

        rename = config.getBoolean("rename");

        File fontFile = new File(instance.getDataFolder(), "minecraft.ttf");
        if (!fontFile.exists())
            instance.saveResource("minecraft.ttf", false);

        ConfigurationSection font = config.getConfigurationSection("font");
        if (font != null) {
            ConfigurationSection offset = font.getConfigurationSection("offset");
            if (offset != null) {
                offset_x = offset.getInt("x");
                offset_z = offset.getInt("z");
            }
        }

        color_map = config.getBoolean("color_map");
    }
}
