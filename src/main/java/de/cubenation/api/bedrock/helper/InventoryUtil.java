package de.cubenation.api.bedrock.helper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryUtil {

    public static Map<String, byte[]> serializeContainerInventory(final ItemStack[] inventoryContents) {
        return serializeContents(inventoryContents, "container");
    }

    public static Map<String, byte[]> serializeContents(final ItemStack[] inventoryContents, final @NotNull String prefix) {
        HashMap<String, byte[]> items = new HashMap<>();
        for (int i = 0; i < inventoryContents.length; i++) {
            if (inventoryContents[i] == null || inventoryContents[i].getType() == Material.AIR) {
                continue;
            }
            items.put(String.format("%s-%d", prefix, i), inventoryContents[i].serializeAsBytes());
        }
        return items;
    }

    public static ItemStack[] deserializeContainerInventory(final Map<String, Object> map, final @Nullable Integer size) {
        Map<String, Object> containerContents = map.entrySet().stream().filter(e -> e.getKey().startsWith("container")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return deserializeContents(containerContents, size != null ? size : 27);
    }

    public static ItemStack[] deserializeContents(final Map<String, Object> map, int size) {
        ItemStack[] items = new ItemStack[size];
        map.forEach((key, value) -> {
            String[] keyParts = key.split("-");
            if (keyParts.length < 1) {
                throw new IllegalStateException("Malformed inventory slot key");
            }
            int i = Integer.parseInt(keyParts[keyParts.length - 1]);
            if (value instanceof byte[]) {
                byte[] bytes = (byte[]) value;
                items[i] = ItemStack.deserializeBytes(bytes);
            } else {
                throw new IllegalStateException("ItemStack in FileConfiguration does not meet correct structuring.");
            }
        });
        return items;
    }
}
