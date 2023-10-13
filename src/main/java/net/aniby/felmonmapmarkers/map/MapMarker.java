package net.aniby.felmonmapmarkers.map;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import net.aniby.felmonmapmarkers.FelmonMapMarkers;

import javax.annotation.Nullable;

public class MapMarker {
    private final int x;

    public int getX() {
        return x;
    }

    private final int z;

    public int getZ() {
        return z;
    }

    private final DyeColor dyeColor;

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    private MapMarker(int x, int z, DyeColor dyeColor) {
        this.x = x;
        this.z = z;
        this.dyeColor = dyeColor;
    }

    public static @Nullable MapMarker request(@NotNull Block block) {
        if (block.getState() instanceof Banner banner)
            return new MapMarker(block.getX(), block.getZ(), banner.getBaseColor());
        return null;
    }

    public static @Nullable MapMarker request(@NotNull ReadableNBT root) {
        ReadableNBT nbt = root.getCompound("banner_pos");
        if (nbt != null) {
            Integer x = nbt.getInteger("x");
            Integer z = nbt.getInteger("z");
            String color = nbt.getString("color");
            if (x != null && z != null && color != null) {
                try {
                    DyeColor dyeColor = DyeColor.valueOf(color);
                    return new MapMarker(x, z, dyeColor);
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return null;
    }

    public void apply(ItemStack map) {
        NBT.modify(map, nbt -> {
            ReadWriteNBT pos = nbt.getOrCreateCompound("banner_pos");
            pos.setString("color", this.dyeColor.name());
            pos.setInteger("x", this.x);
            pos.setInteger("z", this.z);
        });
    }

    public void decorate(ItemStack map, final byte value, boolean replace) {
        NBT.modify(map, nbt -> {
            ReadWriteNBTCompoundList decor = nbt.getCompoundList("Decorations");
            if (replace) {
                decor.clear();
                for (int i = 0; i < decor.size(); i++)
                    decor.remove(i);
            }
            ReadWriteNBT mark = decor.addCompound();
            mark.setDouble("x", (double) this.x);
            mark.setDouble("z", (double) this.z);
            mark.setByte("type", value);
            mark.setDouble("rot", 180.0);
            mark.setString("id", String.valueOf(FelmonMapMarkers.getRandom().nextInt(1, 10000)));
        });
    }
}
