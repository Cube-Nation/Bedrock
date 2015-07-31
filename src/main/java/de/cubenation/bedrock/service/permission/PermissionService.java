package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.config.Permissions;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.config.ConfigService;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

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

    private ArrayList<String> unregistered_permissions = new ArrayList<>();

    private HashMap<String, String> active_permissions = new HashMap<>();

    private HashMap<String, ArrayList<String>> permission_dump = new HashMap<>();


    public PermissionService(BasePlugin plugin) {
        super(plugin);
        this.config_service = plugin.getConfigService();
    }

    protected String getPermissionFilename() {
        return (String) this.getConfigurationValue("service.permission.file_name", "permissions.yml");
    }

    protected String getPermissionPrefix() {
        return (String) this.getConfigurationValue("service.permission.prefix", "no_role");
    }

    @Override
    public void init() throws ServiceInitException {
        this.getPlugin().log(Level.INFO, "  permission service: setting up " + this.toString());

        try {
            this.config_service.registerFile(
                    this.getPermissionFilename(),
                    new Permissions(this.getPlugin(), this.getPermissionFilename())
            );
        } catch (Exception e) {
            throw new ServiceInitException(e.getMessage());
        }

        this.initializeCommandPermissions();
        this.saveUnregisteredPermissions();
        this.loadPermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.getPlugin().log(Level.INFO, "  permission service: reloading " + this.toString());

        try {
            this.config_service.registerFile(
                    this.getPermissionFilename(),
                    new Permissions(this.getPlugin(), this.getPermissionFilename())
            );
        } catch (Exception e) {
            throw new ServiceReloadException(e.getMessage());
        }

        this.initializeCommandPermissions();
        this.saveUnregisteredPermissions();
        this.loadPermissions();
    }

    public void initializeCommandPermissions() {
        for (CommandManager manager : this.getPlugin().getCommandService().getCommandMamagers()) {
            for (AbstractCommand command : manager.getCommands()) {
                String permission = command.getPermission().getName();
                if (permission == null)
                    continue;

                this.plugin.log(Level.INFO, "  permission service: Registering permission " + permission);
                this.unregistered_permissions.add(permission);
            }
        }
    }

    public void saveUnregisteredPermissions() {
        YamlConfiguration permissions = this.config_service.getConfig(this.getPermissionFilename());

        if (permissions == null) {
            try {
                this.config_service.saveConfig(this.getPermissionFilename());
            } catch (InvalidConfigurationException e) {
                this.plugin.disable(
                        new InvalidConfigurationException("  permission service: Could not save empty permission file from config service")
                );
            }
        }

        // this is where the plugin starts the first time (or someone deleted/emptied the permissions file)
        boolean initial_state = false;
        try {
            initial_state = (
                    permissions.getConfigurationSection("permissions").getKeys(false) == null ||
                    permissions.getConfigurationSection("permissions").getKeys(false).size() == 0
            );
        } catch (NullPointerException e) {
            initial_state = true;
        }

        if (!initial_state)
            return;

        this.plugin.log(Level.INFO, "  permission service: Creating permissions from scratch");
        try {
            this.saveRolePermissions(permissions, this.getPermissionPrefix(), this.unregistered_permissions);

        } catch (InvalidConfigurationException e) {
            this.plugin.log(Level.SEVERE, "  permission service: Unable to save permissions");
            this.plugin.disable(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void fixMissingPermissions() {
        YamlConfiguration permissions = this.config_service.getConfig(this.getPermissionFilename());

        if (permissions == null) {
            this.plugin.disable(
                    new InvalidConfigurationException("Could not read permission file from config service")
            );
            return;
        }

        ArrayList<String> missing_permissions = new ArrayList<>();

        for (String permission : this.unregistered_permissions) {
            boolean permission_exists = false;

            for (String role : permissions.getConfigurationSection("permissions").getKeys(false)) {
                if (
                        permissions.getConfigurationSection("permissions").getList(role) == null ||
                        permissions.getConfigurationSection("permission").getList(role).size() == 0
                        ) {
                    this.plugin.log(Level.WARNING, "  permission service: Empty permission role " + role + ". Ignoring role");
                    continue;
                }

                if (permissions.getConfigurationSection("permissions").getString(role).contains(permission))
                    permission_exists = true;
            }

            if (!permission_exists)
                missing_permissions.add(permission);
        }

        if (missing_permissions.size() > 0) {
            this.plugin.log(Level.WARNING, String.format("  permission service: Assigning %d permission to role %s",
                    missing_permissions.size(),
                    this.getPermissionPrefix()
            ));

            try {
                // save this section (role)
                saveRolePermissions(permissions, this.getPermissionPrefix(), missing_permissions);
            } catch (InvalidConfigurationException e) {
                this.plugin.log(Level.SEVERE, "  permission service: Could not save permissions");
                this.plugin.disable(e);
            }
        }
    }

    private void saveRolePermissions(YamlConfiguration file, String role, ArrayList<String> permissions) throws InvalidConfigurationException {
        if (permissions == null || permissions.size() == 0)
            return;

        file.set("permissions." + role, permissions);
        this.config_service.saveConfig(this.getPermissionFilename());
    }

    @SuppressWarnings("unchecked")
    private void loadPermissions() {
        // set permission prefix
        String permission_prefix = (String) this.getConfigurationValue("service.permission.prefix", null);
        if (permission_prefix == null || permission_prefix.isEmpty())
            permission_prefix = this.getPlugin().getDescription().getName().toLowerCase();

        // load permissions configuration file
        YamlConfiguration permissions = this.config_service.getConfig(this.getPermissionFilename());

        if (permissions == null) {
            this.plugin.disable(new InvalidConfigurationException("Could not read permissions file from config service"));
            return;
        }

        // Load formatted permissions
        // & create permission_dump
        this.active_permissions = new HashMap<>();
        this.permission_dump = new HashMap<>();

        for (String role : permissions.getConfigurationSection("permissions").getKeys(false)) {
            ArrayList<String> perm = (ArrayList<String>) permissions.getList(role);

            ArrayList<String> formatted = new ArrayList<>();

            for (String p : perm) {
                if (role.equalsIgnoreCase(this.getPermissionPrefix())) {
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

    @Override
    public String toString() {
        return String.format("(filename: %s - prefix: %s)", this.getPermissionFilename(), this.getPermissionPrefix());
    }

}