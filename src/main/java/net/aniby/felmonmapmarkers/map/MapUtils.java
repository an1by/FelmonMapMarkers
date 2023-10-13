package net.aniby.felmonmapmarkers.map;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

public class MapUtils {
    public static @NotNull MapView copyView(@NotNull MapView view, boolean locked) {
        World world = view.getWorld();
        if (world != null) {
            MapView newView = Bukkit.createMap(world);
            newView.setScale(view.getScale());
            newView.setCenterX(view.getCenterX());
            newView.setCenterZ(view.getCenterZ());
            newView.setUnlimitedTracking(view.isUnlimitedTracking());
            newView.setTrackingPosition(view.isTrackingPosition());
            newView.setLocked(locked);
            return newView;
        }
        return view;
    }

    public static boolean hasDecorations(@NotNull ItemStack item) {
        return new NBTItem(item).getCompoundList("Decorations").size() > 0;
    }

    public static boolean hasDecoratedMarker(@NotNull ItemStack item) {
        for (ReadWriteNBT decoration : new NBTItem(item).getCompoundList("Decorations")) {
            Byte type = decoration.getByte("type");
            if (type != null && type == 5)
                return true;
        }
        return false;
    }
}
