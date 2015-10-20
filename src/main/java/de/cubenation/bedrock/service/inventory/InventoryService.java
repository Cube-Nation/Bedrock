package de.cubenation.bedrock.service.inventory;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class InventoryService extends AbstractService implements ServiceInterface {

    private File inventoryDirectory;

    private HashMap<String,Long> inventories;

    public InventoryService(BasePlugin plugin) {
        super(plugin);
        this.inventoryDirectory = new File(plugin.getDataFolder() + File.separator + "inventories");
    }

    /**
     * Initialize the Config Service
     *
     * @throws ServiceInitException
     */
    @Override
    public void init() throws ServiceInitException {
        // first create the plugin data folder
        this.createDataFolder();
        this.loadInventories();
        this.deleteOutdatedInventories();
    }

    private void deleteOutdatedInventories() {
        long now = new Date().getTime();

        //noinspection ConstantConditions
        for (File file : this.inventoryDirectory.listFiles()) {
            YamlConfiguration yc = YamlConfiguration.loadConfiguration(file);

            YamlConfiguration meta = (YamlConfiguration) yc.get("meta");
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


    public void create(String identifier, ItemStack[] itemStacks, final int lifetime) throws IOException {
        File f = new File(this.inventoryDirectory, identifier + ".yaml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory", itemStacks);

        c.set("meta", new HashMap<String, Object>() {{
            put("lifetime",         lifetime);
            put("creation_date",    new Date().getTime());
        }});
        c.save(f);
    }


    @SuppressWarnings("unused")
    public void create(String identifier, ItemStack[] itemStacks) throws IOException {
        this.create(identifier, itemStacks, -1);
    }

    @SuppressWarnings("unchecked")
    public ItemStack[] get(String identifier) throws IOException {
        File f = new File(this.inventoryDirectory, identifier + ".yaml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        if (c.get("inventory") == null) throw new IOException("Inventory is empty");

        return ((List<ItemStack>) c.get("inventory")).toArray(new ItemStack[((List<ItemStack>) c.get("inventory")).size()]);
    }

    public boolean delete(String identifier) {
        return new File(this.inventoryDirectory, identifier + ".yaml").delete();
    }

    public ArrayList<String> list() {
        return this.list(0, new Date().getTime());
    }

    public ArrayList<String>  list(long timestamp_start) {
        return this.list(timestamp_start, new Date().getTime());
    }

    public ArrayList<String>  list(long timestamp_start, long timestamp_end) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry entry : this.inventories.entrySet()) {

            if ( (long) entry.getValue() < timestamp_start && (long) entry.getValue() > timestamp_end)
                continue;

            list.add((String) entry.getKey());
        }
        return list;
    }

}