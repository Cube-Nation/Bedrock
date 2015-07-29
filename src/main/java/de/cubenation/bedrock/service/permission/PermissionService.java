package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.config.Permissions;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationFileService;
import de.cubenation.bedrock.service.customconfigurationfile.CustomConfigurationRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.service.permission
 */
public class PermissionService implements ServiceInterface {

    private BasePlugin plugin;

    private CustomConfigurationFileService ccf_service;

    private String permissions_filename;

    private final String no_role                            = "no_role";

    private ArrayList<String> unregistered_permissions      = new ArrayList<>();

    private String permission_prefix;

    private YamlConfiguration permissions;


    public PermissionService(BasePlugin plugin) {
        this.setPlugin(plugin);
        this.ccf_service = plugin.getCustomConfigurationFileService();
    }

    private void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }

    private BasePlugin getPlugin() {
        return this.plugin;
    }

    private void setPermissionsFilename(String permissions_filename) {
        this.permissions_filename = permissions_filename;
    }

    public String getPermissionsFilename() {
        return this.permissions_filename;
    }

    @Override
    public void init() throws ServiceInitException {
        Permissions permissions;
        try {
            permissions = new Permissions(this.getPlugin());
            this.setPermissionsFilename(permissions.getFilename());
        } catch (IOException e) {
            throw new ServiceInitException(e.getMessage());
        }

        this.ccf_service.register(permissions);
        this.saveUnregisteredPermissions();
        this.loadPermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.fixMissingPermissions();
        this.loadPermissions();
    }

    public void registerPermission(String permission) {
        if (permission != null && !permission.isEmpty() && !this.unregistered_permissions.contains(permission)) {
            this.plugin.log(Level.INFO, "Registering permission " + permission);
            this.unregistered_permissions.add(permission);
        }
    }

    public void saveUnregisteredPermissions() {
        YamlConfiguration permissions;
        try {
            permissions = CustomConfigurationRegistry.get(this.getPlugin(), this.getPermissionsFilename(), null).load();
        } catch (NoSuchRegisterableException e) {
            this.plugin.log(Level.SEVERE, "Could not read permission file from Custom Configuration File Registry");
            this.plugin.disable(e);
            return;
        }

        // this is where the plugin starts the first time (or someone deleted/emptied the permissions file)
        boolean initial_state = (permissions.getKeys(false) == null || permissions.getKeys(false).size() == 0);

        if (initial_state) {
            this.plugin.log(Level.INFO, "Creating permissions from scratch");
            try {
                this.saveRolePermissions(permissions, this.no_role, this.unregistered_permissions);
            } catch (IOException e) {
                this.plugin.log(Level.SEVERE, "Unable to save permissions");
                this.plugin.disable(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void fixMissingPermissions() {
        YamlConfiguration permissions;
        try {
            permissions = CustomConfigurationRegistry.get(this.getPlugin(), this.getPermissionsFilename(), null).load();
        } catch (NoSuchRegisterableException e) {
            this.plugin.log(Level.SEVERE, "Could not read permission file from Custom Configuration File Registry");
            this.plugin.disable(e);
            return;
        }
        
        ArrayList<String> missing_permissions = new ArrayList<>();

        // TODO get all permissions from commandManager

        for (String permission : this.unregistered_permissions) {
            boolean permission_exists = false;

            for (String role : permissions.getKeys(false)) {
                if (permissions.getList(role) == null || permissions.getList(role).size() == 0) {
                    this.plugin.log(Level.WARNING, "Empty permission role " + role);
                    continue;
                }

                if (permissions.getString(role).contains(permission))
                    permission_exists = true;
            }

            if (!permission_exists)
                missing_permissions.add(permission);
        }

        if (missing_permissions.size() > 0) {
            this.plugin.log(Level.WARNING, String.format("Assigning %d permission to role %s",
                    missing_permissions.size(),
                    this.no_role
            ));
            try {
                // add other permissions from the default role to missing commands
                missing_permissions.addAll((ArrayList<String>) permissions.getList(this.no_role));
                // and save this section again
                saveRolePermissions(permissions, this.no_role, missing_permissions);
            } catch (IOException e) {
                this.plugin.log(Level.SEVERE, "Unable to save permissions");
                this.plugin.disable(e);
            }
        }
    }

    private void saveRolePermissions(YamlConfiguration file, String role, ArrayList<String> permissions) throws IOException {
        if (permissions == null || permissions.size() == 0)
            return;

        file.set(role, permissions);
        file.save(CustomConfigurationRegistry.get(this.getPlugin(), this.permissions_filename, null).getFile());
    }

    private void loadPermissions() {
        // set permission prefix
        this.permission_prefix = this.getPlugin().getPluginConfigService().getConfig().getString("service.permission.prefix");
        if (permission_prefix == null || permission_prefix.isEmpty())
            permission_prefix = this.getPlugin().getDescription().getName().toLowerCase();

        // load permissions configuration file
        YamlConfiguration  permissions;
        try {
            permissions = CustomConfigurationRegistry.get(this.getPlugin(), this.getPermissionsFilename(), null).load();
        } catch (NoSuchRegisterableException e) {
            this.plugin.log(Level.SEVERE, "Could not read permission file from Custom Configuration File Registry");
            this.plugin.disable(e);
            return;
        }

        this.permissions = permissions;
    }

    @SuppressWarnings("unchecked")
    public boolean hasPermission(CommandSender sender, String permission) {
        boolean op_has_permission = this.getPlugin().getPluginConfigService().getConfig().getBoolean(
                "service.permission.grant_all_permissions_to_op",
                BedrockPlugin.getInstance().getPluginConfigService().getConfig().getBoolean(
                        "service.permission.grant_all_permissions_to_op",
                        true
                )
        );

        if (sender.isOp() && op_has_permission)
            return true;


        for (String role : this.permissions.getKeys(false)) {
            for (String role_permission : (ArrayList<String>) this.permissions.getList(role)) {

                if (!role_permission.equalsIgnoreCase(permission))
                    continue;

                if (role.equalsIgnoreCase(this.no_role)) {
                    if (sender.hasPermission(this.permission_prefix + "." + role_permission))
                        return true;
                } else {
                    if (sender.hasPermission(this.permission_prefix + "." + role + "." + role_permission))
                        return true;
                }
            } // for
        } // for
        return false;

    }

    @SuppressWarnings("unchecked")
    public HashMap<String, ArrayList<String>> getPermissionRoleDump() {
        HashMap<String, ArrayList<String>> dump = new HashMap<>();

        for (String role : this.permissions.getKeys(false)) {
            dump.put(role, (ArrayList<String>) this.permissions.getList(role));
        }

        return dump;
    }

}