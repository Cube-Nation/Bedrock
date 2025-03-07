/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.api.bedrock.service.inventory;

import com.google.gson.JsonSyntaxException;
import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.helper.InventoryUtil;
import de.cubenation.api.bedrock.helper.MapUtil;
import de.cubenation.api.bedrock.service.AbstractService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class InventoryService extends AbstractService {

    private File inventoryDirectory;

    private HashMap<String, Long> inventories;

    public InventoryService(BasePlugin plugin) {
        super(plugin);
        this.inventoryDirectory = new File(plugin.getDataFolder() + File.separator + "inventories");
    }

    /**
     * Initialize the Config Service
     *
     * @throws ServiceInitException if the initialization fails.
     */
    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        this.createDataFolder();
        this.loadInventories();
        this.deleteOutdatedInventories();
    }

    public void upgrade() {
        plugin.getLogger().info("Starting to upgrade files to new Inventory version...");

        File fileBase = plugin.getDataFolder();
        File fileInventoriesOld = new File(fileBase, "inventories_old");
        if (!fileInventoriesOld.exists() && !fileInventoriesOld.mkdirs()) {
            throw new IllegalStateException("Could not create directory: "+fileInventoriesOld);
        }

        File fileInventoriesNew = new File(fileBase, "inventories");
        if (!fileInventoriesNew.exists() && !fileInventoriesNew.mkdirs()) {
            throw new IllegalStateException("Could not create directory: "+fileInventoriesOld);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Files.walkFileTree(fileInventoriesOld.toPath(),
                        new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                                String relative = fileInventoriesOld.toURI().relativize(dir.toUri()).getPath();
                                plugin.getLogger().info("Upgrading "+relative+"...");
                                //noinspection ResultOfMethodCallIgnored
                                new File(fileInventoriesOld, relative).mkdirs();
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                                String relative = fileInventoriesOld.toURI().relativize(dir.toUri()).getPath();
                                plugin.getLogger().info("\nSuccessfully upgraded "+relative+"!");
                                return FileVisitResult.CONTINUE;
                            }

                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                                String relative = fileInventoriesOld.toURI().relativize(file.toUri()).getPath();
                                try {
                                    plugin.getLogger().info("* upgrading "+relative+"...");

                                    File fileOld = new File(fileInventoriesOld, relative);
                                    String identifier = fileOld.getName().replaceFirst("[.][^.]+$", "");;

                                    ItemStack[] itemStacks = getLegacy(fileOld);
                                    create(identifier, itemStacks, null);
                                } catch (Exception e) {
                                    plugin.getLogger().severe("Encountered exception while parsing inventory file ("+relative+"):");
                                    e.printStackTrace();
                                }
                                return FileVisitResult.CONTINUE;
                            }
                        });
            } catch (Exception e) {
                plugin.getLogger().severe("Encountered exception while parsing inventory file:");
                e.printStackTrace();
            }
        });
    }

    private void deleteOutdatedInventories() {
        long now = new Date().getTime();

        //noinspection ConstantConditions
        for (File file : this.inventoryDirectory.listFiles()) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);

            MemorySection meta = (MemorySection) yc.get("meta");
            int lifetime = (int) meta.get("lifetime");
            long creation_date = (long) meta.get("creation_date");

            if (lifetime == -1)
                continue;

            if (now - lifetime > creation_date) {
                this.delete(file.getName().replaceAll("\\.yaml$", ""));
            }
        }
    }

    /**
     * create plugin inventory folder
     *
     * @throws ServiceInitException
     */
    private void createDataFolder() throws ServiceInitException {
        if (!this.inventoryDirectory.exists() && !this.inventoryDirectory.mkdirs())
            throw new ServiceInitException("Could not create folder " + this.inventoryDirectory.getName());
    }

    /**
     *
     */
    @SuppressWarnings("ConstantConditions")
    private void loadInventories() {
        this.inventories = new HashMap<>();
        for (File file : this.inventoryDirectory.listFiles()) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);

            this.inventories.put(file.getName().replaceAll("\\.yaml$", ""), (long) yc.get("meta.creation_date"));
        }
    }

    /**
     * Reload the Config Service
     */
    @Override
    public void reload() throws ServiceReloadException {
        this.loadInventories();
    }

    public Boolean create(String identifier, ItemStack[] itemStacks, HashMap<String, Object> customMeta, final long lifetime) {
        File f = new File(this.inventoryDirectory, identifier + ".yaml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory", InventoryUtil.serializeContainerInventory(itemStacks));

        final long time = new Date().getTime();
        c.set("meta", new HashMap<String, Object>() {{
            put("lifetime", lifetime);
            put("creation_date", time);
            put("size", itemStacks.length);
        }});

        if (customMeta != null) {
            c.set("custom", customMeta);
        }

        try {
            c.save(f);
        } catch (IOException e) {
            return false;
        }

        this.inventories.put(identifier, time);
        return true;
    }

    @SuppressWarnings("unused")
    public Boolean create(String identifier, ItemStack[] itemStacks, HashMap<String, Object> customMeta) {
        return this.create(identifier, itemStacks, customMeta, -1);
    }

    public ItemStack[] get(String identifier) throws IOException {
        File f = new File(this.inventoryDirectory, identifier + ".yaml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        if (c.get("meta.size") == null) throw new IOException("Inventory is malformed");
        if (c.getConfigurationSection("inventory") == null) throw new IOException("Inventory is empty");

        return InventoryUtil.deserializeContainerInventory(
                Objects.requireNonNull(c.getConfigurationSection("inventory")).getValues(false),
                c.getInt("meta.size")
        );
    }

    @SuppressWarnings("unchecked")
    public ItemStack[] getLegacy(File file) throws IOException {
        FileConfiguration c = YamlConfiguration.loadConfiguration(file);

        if (c.get("inventory") == null) throw new IOException("Inventory is empty");

        List<ItemStack> itemList = ((List<ItemStack>) c.get("inventory"));

        assert itemList != null;
        return itemList.toArray(new ItemStack[0]);
    }

    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public boolean delete(String identifier) {
        return new File(this.inventoryDirectory, identifier + ".yaml").delete();
    }

    public HashMap<String, Long> list() {
        return this.list(0, new Date().getTime());
    }

    public HashMap<String, Long> list(long timestamp_start) {
        return this.list(timestamp_start, new Date().getTime());
    }

    public HashMap<String, Long> list(long timestamp_start, long timestamp_end) {
        HashMap<String, Long> list = new HashMap<>();
        for (Map.Entry entry : this.inventories.entrySet()) {

            if ((long) entry.getValue() < timestamp_start && (long) entry.getValue() > timestamp_end)
                continue;

            list.put((String) entry.getKey(), (Long) entry.getValue());
        }
        return (HashMap<String, Long>) MapUtil.sortByValue(list);
    }

}