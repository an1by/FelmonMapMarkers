package net.aniby.felmonmapmarkers;

import net.aniby.felmonmapmarkers.other.FBMConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class FelmonMapMarkers extends JavaPlugin {
    private static FelmonMapMarkers instance;
    private static final Random random = new Random();

    public static Random getRandom() {
        return random;
    }

    public static FelmonMapMarkers getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        FBMConfig.init();

        getServer().getPluginManager().registerEvents(new FBMEvents(), this);
    }
}
