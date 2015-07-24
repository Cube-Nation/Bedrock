package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.helper.Const;
import de.cubenation.bedrock.service.ServiceInterface;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.service.permission
 */
public class PermissionServiceInterface implements ServiceInterface {

    private final BasePlugin plugin;

    private final File permissionsFile;

    private Boolean activePermissionService                 = true;

    ArrayList<String> plainOldPermissions                   = new ArrayList<>();

    HashMap<String, String> roledPermissions                = new HashMap<>();

    HashMap<String, ArrayList<String>> permissionRoleDump   = new HashMap<>();


    public PermissionServiceInterface(BasePlugin plugin) throws ServiceInitException {
        this.plugin = plugin;
        permissionsFile = new File(plugin.getDataFolder().getAbsolutePath(), Const.PERMISSIONS_FILE_NAME);

        this.init();

        try {
            this.reload();
        } catch (ServiceReloadException e) {
            throw new ServiceInitException(e.getMessage());
        }
    }

    @Override
    public void init() throws ServiceInitException {
        // check if file is writeable
        if (!permissionsFile.canWrite()) {
            throw new ServiceInitException("Cannot write to file " + permissionsFile.getName());
        }
    }


    @Override
    public void reload() throws ServiceReloadException {
        if (plugin.getConfig().get(Const.PERMISSION_ROLE_KEY) == null) {
            plugin.getConfig().set(Const.PERMISSION_ROLE_KEY, activePermissionService);
            plugin.saveConfig();
        }
        plugin.reloadConfig();
        activePermissionService = plugin.getConfig().getBoolean(Const.PERMISSION_ROLE_KEY);


        if (activePermissionService) {
            writePermission();
            loadPermission();
        }
    }

    /*
     * Write permissions to file
     */
    @SuppressWarnings("unchecked")
    private void writePermission() {
        plugin.log(Level.INFO, "Write default Permissions!");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(permissionsFile);

        ArrayList<String> defaultPermission = (ArrayList<String>) configuration.get(Const.NO_ROLE);
        if (defaultPermission == null) {
            defaultPermission = new ArrayList<>();
        }

        for (String permission : plainOldPermissions) {
            Boolean contains = false;
            for (String key : configuration.getKeys(false)) {
                ArrayList<String> configList = (ArrayList<String>) configuration.get(key);
                if (configList.contains(permission)) {
                    contains = true;
                }
            }
            if (!contains) {
                defaultPermission.add(permission);
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

        HashMap<String, String> loadedPermission = new HashMap<>();
        HashMap<String, ArrayList<String>> roleDump = new HashMap<>();

        for (String key : configuration.getKeys(false)) {
            ArrayList<String> configList = (ArrayList<String>) configuration.get(key);
            roleDump.put(key, new ArrayList<String>());

            String replacementKey = plugin.getExplicitPermissionPrefix() + ".";
            if (!key.equalsIgnoreCase(Const.NO_ROLE)) {
                replacementKey += key + ".";
            }

            for (String permission : configList) {

                String rolePermission = replacementKey + permission;

                roleDump.get(key).add(rolePermission);

                // If list contains permission -> replace to prevent Issues!
                if (loadedPermission.containsKey(permission)) {
                    loadedPermission.replace(permission, rolePermission);
                } else {
                    loadedPermission.put(permission, rolePermission);
                }
            }
        }

        roledPermissions = loadedPermission;
        permissionRoleDump = roleDump;

        for (Map.Entry<String, String> entry : loadedPermission.entrySet()) {
            plugin.log(Level.INFO, "Perm: " + entry.getKey() + "    ->  " + entry.getValue());
        }

    }


    public String getPermissionWithRole(String permission) {

        if (activePermissionService == null || !activePermissionService) {
            return plugin.getExplicitPermissionPrefix() + "." + permission;
        } else {
            return roledPermissions.get(permission);
        }

    }

    public void registerPermission(String permission) {
        if (permission != null) {
            if (!plainOldPermissions.contains(permission)) {
                plainOldPermissions.add(permission);
            }
        }
    }


    @SuppressWarnings("unused")
    public Boolean getActivePermissionService() {
        return activePermissionService;
    }

    public HashMap<String, ArrayList<String>> getPermissionRoleDump() {
        return permissionRoleDump;
    }

}
