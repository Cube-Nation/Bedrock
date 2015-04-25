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
import java.util.Set;

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
                System.out.println("Add Permission: " + subCommand.getPermission().getPermission());
                Permission  permission = subCommand.getPermission();
                if (permission != null) {
                    plainOldPermissions.add(permission);
                }
            }
        }

        writePermission();
        loadPermission();

    }

    public void writePermission() {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(permissionsFile);

        for (Permission permission : plainOldPermissions) {
            Boolean contains = false;
            for (String key : configuration.getKeys(false)) {
                ConfigurationSection configurationSection = configuration.getConfigurationSection(key);
                if (configurationSection.contains(permission.getPermission())) {
                    contains = true;
                }
            }
            if (!contains) {
                try {
                    addPermissionToDefault(permission);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void addPermissionToDefault(Permission permission) throws IOException {
        System.out.println("addPermissionToDefault: " + permission.getPermission());
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(permissionsFile);
        configuration.set(Const.NO_ROLE + "." +  permission.getPermission(), null);
        configuration.save(permissionsFile);
    }

    public void loadPermission() {
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(permissionsFile);

        for (String key : configuration.getKeys(false)) {
            ConfigurationSection configurationSection = configuration.getConfigurationSection(key);

            Set<String> permissions = configurationSection.getKeys(false);

            for (String permission : permissions) {
                System.out.println("Add new RolePermission:");
                System.out.println("From: " + permission);
                if (key.equals(Const.ROLE_PLACEHOLDER)) {
                    String rolePermission = permission.replace("." + Const.ROLE_PLACEHOLDER, "");
                    roledPermissions.put(permission, new Permission(rolePermission));
                    System.out.println("to: " + rolePermission);
                } else {
                    String rolePermission = permission.replace(Const.ROLE_PLACEHOLDER, key);
                    roledPermissions.put(permission, new Permission(rolePermission));
                    System.out.println("to: " + rolePermission);
                }
            }
        }

    }


    public Permission getPermissionWithRole(Permission permission) {

        return roledPermissions.get(permission.getPermission());
    }


}
