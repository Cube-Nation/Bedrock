package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.CommandManager;
import de.cubenation.bedrock.command.SubCommand;
import de.cubenation.bedrock.helper.Const;
import de.cubenation.bedrock.permission.Permission;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.service.permission
 */
public class PermissionService {

    private final BasePlugin plugin;

    private final File permissionsFile;

    ArrayList<Permission> plainOldPermissions = new ArrayList<>();

    HashMap<String,Permission> roledPermissions = new HashMap<>();

    public PermissionService(BasePlugin plugin, ArrayList<CommandManager> commandManagers) {
        this.plugin = plugin;
        permissionsFile = new File(plugin.getDataFolder().getAbsolutePath(), Const.PERMISSIONS_FILE_NAME);

        // Get all placeholder Permissions
        for (CommandManager commandManager : commandManagers) {
            for (SubCommand subCommand : commandManager.getSubCommands()) {
                Permission  permission = subCommand.getPermission();

                if (permission != null) {
                    if (permission.getPermission() != null) {
                        plainOldPermissions.add(permission);
                        System.out.println("Add Permission: " + subCommand.getPermission().getPermission());
                    }
                }

            }
        }

        writePermission();
        loadPermission();

    }

    @SuppressWarnings("unchecked")
    private void writePermission() {
        plugin.log(Level.INFO, "Write default Permissions!");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(permissionsFile);

        ArrayList<String> defaultPermission = new ArrayList<>();

        for (Permission permission : plainOldPermissions) {
            Boolean contains = false;
            for (String key : configuration.getKeys(false)) {
                ArrayList<String> configList = (ArrayList<String>) configuration.get(key);
                if (configList.contains(permission.getPermission())) {
                    contains = true;
                }
            }
            if (!contains) {
                defaultPermission.add(permission.getPermission());
            }
        }


        try {
            if (!defaultPermission.isEmpty()) {
                configuration.set(Const.NO_ROLE, defaultPermission);
                configuration.save(permissionsFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    private void loadPermission() {
        plugin.log(Level.INFO, "Load Permissions!");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(permissionsFile);

        HashMap<String,Permission> loadedPermission = new HashMap<>();

        for (String key : configuration.getKeys(false)) {
            ArrayList<String> configList = (ArrayList<String>) configuration.get(key);

            String replacementKey = "";
            if (!key.equalsIgnoreCase(Const.NO_ROLE)) {
                replacementKey = "." + key;
            }

            for (String permission : configList) {

                String rolePermission = permission.replace("." + Const.ROLE_PLACEHOLDER, replacementKey);
                // If list contains permission -> replace to prevent Issues!
                if (loadedPermission.containsKey(permission)) {
                    loadedPermission.replace(permission, new Permission(rolePermission));
                } else {
                    loadedPermission.put(permission, new Permission(rolePermission));
                }
            }
        }

        roledPermissions = loadedPermission;

        for (Map.Entry<String, Permission> entry : loadedPermission.entrySet()) {
            plugin.log(Level.INFO, "Perm: " + entry.getKey() + "    ->  " + entry.getValue().getPermission());
        }

    }


    public Permission getPermissionWithRole(Permission permission) {

        return roledPermissions.get(permission.getPermission());
    }

    //TODO: Error Handling
    // File corrupted?
    // No R/W access?
    // Permissions if Services disabled?


}
