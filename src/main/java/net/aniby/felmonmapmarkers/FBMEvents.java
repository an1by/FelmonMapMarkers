package net.aniby.felmonmapmarkers;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTItem;
import net.aniby.felmonmapmarkers.map.MapMarker;
import net.aniby.felmonmapmarkers.map.MapUtils;
import net.aniby.felmonmapmarkers.other.FBMConfig;
import net.aniby.felmonmapmarkers.other.SerializeUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.inventory.CartographyInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;

public class FBMEvents implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        EquipmentSlot hand = event.getHand();
        Player player = event.getPlayer();
        if (hand != null && item != null && item.getType() == Material.FILLED_MAP) {
            Block block = event.getClickedBlock();
            if (block != null) {
                PlayerInventory inventory = player.getInventory();
                MapMarker marker = MapMarker.request(block);
                if (marker != null && player.hasPermission("felmon.mapmarkers.mark")) {
                    marker.apply(item);

                    switch (hand) {
                        case OFF_HAND -> inventory.setItemInOffHand(item);
                        case HAND -> inventory.setItemInMainHand(item);
                    }
                }
                else if (player.hasPermission("felmon.mapmarkers.unlock")) {
                    NBTItem nbtItem = new NBTItem(item);
                    Boolean locked = nbtItem.getBoolean("locked");
                    if (locked != null && locked) {
                        nbtItem.removeKey("locked");
                        item = nbtItem.getItem();
                        MapMeta meta = (MapMeta) item.getItemMeta();
                        MapView view = meta.getMapView();
                        if (view != null) {
                            view.setLocked(false);
                            meta.setMapView(view);
                            item.setItemMeta(meta);
                        }
                        switch (hand) {
                            case OFF_HAND -> inventory.setItemInOffHand(item);
                            case HAND -> inventory.setItemInMainHand(item);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCraft(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof CartographyInventory inventory && event.getWhoClicked() instanceof Player player) {
            if (!player.hasPermission("felmon.mapmarkers.craft"))
                return;

            ItemStack map = inventory.getItem(0);
            ItemStack cursor = event.getCursor();
            if (cursor != null && map != null && map.getType() == Material.FILLED_MAP) {
                map = map.clone();

                MapMarker marker = MapMarker.request(new NBTItem(map));
                if (event.getSlot() == 1 && marker != null) {
                    byte value = (byte) -1;
                    boolean triangle = false;

                    Material type = cursor.getType();

                    if (MapUtils.hasDecoratedMarker(map) && FBMConfig.triangleMarkers.containsKey(type)) {
                        value = FBMConfig.triangleMarkers.get(type);
                        triangle = true;
                    }
                    else if (!MapUtils.hasDecorations(map) && FBMConfig.markers.containsKey(type)) {
                        value = FBMConfig.markers.get(type);
                    }

                    if (value != -1) {
                        MapMeta meta = (MapMeta) map.getItemMeta();
                        MapView view = meta.getMapView() != null && triangle ? MapUtils.copyView(meta.getMapView(), true) : meta.getMapView();
                        if (view != null) {
                            if (FBMConfig.rename && meta.hasDisplayName()) {
                                view.addRenderer(new MapRenderer() {
                                    final String name = SerializeUtils.componentSerialize(meta.displayName());
                                    final String mapText = SerializeUtils.mapTextSerialize(name);

                                    @Override
                                    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                                        try {
                                            canvas.drawText(FBMConfig.offset_x, FBMConfig.offset_z, MinecraftFont.Font, mapText);
                                        } catch (IllegalArgumentException exception) {
                                            view.removeRenderer(this);
                                            player.kick(Component.text("Invalid map text!"), PlayerKickEvent.Cause.PLUGIN);
                                        }
                                    }
                                });
                            }
                            meta.setMapView(view);
                        }
                        if (FBMConfig.color_map)
                            meta.setColor(marker.getDyeColor().getColor());

                        map.setItemMeta(meta);

                        if (triangle) {
                            NBT.modify(map, nbt -> {
                                nbt.setBoolean("locked", true);
                            });
                        }
                        marker.decorate(map, value, triangle);

                        ItemStack firstSlot = inventory.getItem(1);
                        ItemStack newCurrent = cursor.clone();
                        switch (event.getClick()) {
                            case LEFT -> {
                                event.setCurrentItem(newCurrent);
                                player.setItemOnCursor(firstSlot);
                            }
                            case RIGHT -> {
                                if (firstSlot != null) {
                                    event.setCurrentItem(newCurrent);
                                    player.setItemOnCursor(firstSlot);
                                }
                                else {
                                    newCurrent.setAmount(1);
                                    event.setCurrentItem(newCurrent);

                                    cursor.setAmount(cursor.getAmount() - 1);
                                    player.setItemOnCursor(cursor);
                                }
                            }
                            default -> {
                                return;
                            }
                        }

                        inventory.setItem(2, map);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
