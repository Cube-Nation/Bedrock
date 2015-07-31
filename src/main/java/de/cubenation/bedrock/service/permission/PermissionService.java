package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.config.Permissions;
import de.cubenation.bedrock.exception.NoSuchRegisterableException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.config.ConfigService;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
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
public class PermissionService extends AbstractService implements ServiceInterface {

    private ConfigService config_service;

    private String permissions_filename;

    private final String no_role = "no_role";

    private ArrayList<String> unregistered_permissions = new ArrayList<>();

    private HashMap<String, String> active_permissions = new HashMap<>();

    private HashMap<String, ArrayList<String>> permission_dump = new HashMap<>();


    public PermissionService(BasePlugin plugin) {
        super(plugin);
        this.config_service = plugin.getConfigService();
    }

    private void setPermissionsFilename(String permissions_filename) {
        this.permissions_filename = permissions_filename;
    }

    public String getPermissionsFilename() {
        return this.permissions_filename;
    }

    @Override
    public void init() throws ServiceInitException {
        CustomConfigurationFile permissions;
        try {
            permissions = new Permissions(this.getPlugin());
            this.setPermissionsFilename(permissions.getFilename());
        } catch (IOException e) {
            throw new ServiceInitException(e.getMessage());
        }

        this.config_service.registerFile(permissions);

        this.initializeCommandPermissions();
        this.saveUnregisteredPermissions();
        this.loadPermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.saveUnregisteredPermissions();
        this.fixMissingPermissions();
        this.loadPermissions();
    }

    public void initializeCommandPermissions() {
        for (CommandManager manager : this.getPlugin().getCommandService().getCommandMamagers()) {
            for (AbstractCommand command : manager.getCommands()) {
                String permission = command.getPermission().getName();
                if (permission == null)
                    continue;

                this.plugin.log(Level.INFO, "Registering permission " + permission);
                this.unregistered_permissions.add(permission);
            }
        }
    }

    public void saveUnregisteredPermissions() {
        YamlConfiguration permissions;
        try {
            permissions = this.config_service.getConfig(this.getPermissionsFilename());
        } catch (NullPointerException e) {
            this.plugin.log(Level.SEVERE, "Could not read permission file from Custom Configuration File Registry");
            this.plugin.disable(e);
            return;
        }

        // this is where the plugin starts the first time (or someone deleted/emptied the permissions file)
        boolean initial_state = (permissions.getKeys(false) == null || permissions.getKeys(false).size() == 0);

        if (!initial_state)
            return;

        this.plugin.log(Level.INFO, "Creating permissions from scratch");
        try {
            this.saveRolePermissions(permissions, this.no_role, this.unregistered_permissions);
        } catch (IOException e) {
            this.plugin.log(Level.SEVERE, "Unable to save permissions");
            this.plugin.disable(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void fixMissingPermissions() {
        YamlConfiguration permissions;
        try {
            permissions = this.config_service.getConfig(this.getPermissionsFilename());
        } catch (NullPointerException e) {
            this.plugin.log(Level.SEVERE, "Could not read permission file from Custom Configuration File Registry");
            this.plugin.disable(e);
            return;
        }

        ArrayList<String> missing_permissions = new ArrayList<>();

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
        // TODO: geht garantiert nicht so ..
        this.config_service.saveConfig();
    }

    @SuppressWarnings("unchecked")
    private void loadPermissions() {
        // set permission prefix
        String permission_prefix = (String) this.getConfigurationValue("service.permission.prefix", null);
        if (permission_prefix == null || permission_prefix.isEmpty())
            permission_prefix = this.getPlugin().getDescription().getName().toLowerCase();

        // load permissions configuration file
        YamlConfiguration permissions;
        try {
            permissions = this.config_service.getConfig(this.getPermissionsFilename());
        } catch (NullPointerException e) {
            this.plugin.log(Level.SEVERE, "Could not read permission file from Custom Configuration File Registry");
            this.plugin.disable(e);
            return;
        }

        // Load formatted permissions
        // & create permission_dump
        this.active_permissions = new HashMap<>();
        this.permission_dump = new HashMap<>();

        for (String role : permissions.getKeys(false)) {
            ArrayList<String> perm = (ArrayList<String>) permissions.getList(role);
            ArrayList<String> formatted = new ArrayList<>();

            for (String p : perm) {
                if (role.equalsIgnoreCase(no_role)) {
                    String formattedPermission = String.format("%s.%s", permission_prefix, p);
                    active_permissions.put(p, formattedPermission);
                    formatted.add(formattedPermission);
                } else {
                    String formattedPermission = String.format("%s.%s.%s", permission_prefix, role, p);
                    active_permissions.put(p, formattedPermission);
                    formatted.add(formattedPermission);
                }
            }

            this.permission_dump.put(role, formatted);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean hasPermission(CommandSender sender, String permission) {
        Boolean op_has_permission = (Boolean) this.getConfigurationValue("service.permission.grant_all_permissions_to_op", true);

        return  sender.isOp() && op_has_permission ||
                permission == null ||
                permission.isEmpty() ||
                this.active_permissions.containsKey(permission) && sender.hasPermission(this.active_permissions.get(permission));
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, ArrayList<String>> getPermissionRoleDump() {
        return this.permission_dump;
    }

}