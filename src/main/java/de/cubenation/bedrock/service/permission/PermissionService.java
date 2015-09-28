package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.config.Permissions;
import de.cubenation.bedrock.exception.PlayerNotFoundException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.config.ConfigService;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
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

    private HashMap<String, ArrayList<String>> external_permissions = new HashMap<>();

    private final String no_role    = "no_role";


    public PermissionService(BasePlugin plugin) {
        super(plugin);
        this.config_service = plugin.getConfigService();
    }

    protected String getPermissionFilename() {
        return (String) this.getConfigurationValue("service.permission.file_name", "permissions.yml");
    }

    protected String getPermissionPrefix() {
        return (String) this.getConfigurationValue(
                "service.permission.prefix",
                this.getPlugin().getDescription().getName().toLowerCase()
        );
    }

    @Override
    public void init() throws ServiceInitException {
        //this.getPlugin().log(Level.INFO, "  permission service: setting up " + this.toString());

        try {
            this.config_service.registerFile(
                    this.getPermissionFilename(),
                    new Permissions(this.getPlugin(), this.getPermissionFilename())
            );
        } catch (Exception e) {
            throw new ServiceInitException(e.getMessage());
        }

        this.initializeCommandPermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        //this.getPlugin().log(Level.INFO, "  permission service: reloading " + this.toString());
        this.initializeCommandPermissions();
    }

    private void initializeCommandPermissions() {
        this.unregistered_permissions = new ArrayList<>();

        for (CommandManager manager : this.getPlugin().getCommandService().getCommandMamagers()) {
            for (AbstractCommand command : manager.getCommands()) {
                String permission = command.getPermission().getName();
                if (permission == null)
                    continue;

                //this.plugin.log(Level.INFO, "  permission service: Registering permission " + permission);
                this.unregistered_permissions.add(permission);
            }
        }

        this.fixPermissions((Permissions) this.config_service.getConfig(this.getPermissionFilename()));
    }


    @SuppressWarnings("unused")
    public void registerPermission(String permission) {
        this.registerPermission(this.no_role, permission);
    }

    public void registerPermission(String role, String permission) {
        //this.getPlugin().log(Level.INFO, "Registering permission " + permission + " in role " + role);

        // save for later in fixPermissions()
        if (!this.external_permissions.containsKey(role))
            this.external_permissions.put(role, new ArrayList<String>());

        if (!this.external_permissions.get(role).contains(permission))
            this.external_permissions.get(role).add(permission);

        Permissions permissions = (Permissions) this.config_service.getConfig(this.getPermissionFilename());
        String saved_role = permissions.getRoleForPermission(permission);

        // permission is not assigned to any role -> save permission in given role
        if (saved_role == null) {
            permissions.addPermission(role, permission);

        // permission is already assigned to a role -> leave as is
        } else {
            return;
        }

        this.savePermissions(permissions);
    }

    private void fixPermissions(Permissions permissions) {
        if (permissions == null)
            permissions = (Permissions) this.config_service.getConfig(this.getPermissionFilename());

        // clean up default role - will be restored if necessary
        permissions.removeRole(this.no_role);


        // 1) create all permissions from scratch in default role if
        //   - the current permission file has no roles       (AND)
        //   - plugin commands use permissions
        if (permissions.getRoles().size() == 0 && this.unregistered_permissions.size() != 0) {
            // no roles -> create all permissions in the default role
            permissions.addPermissions(this.no_role, this.unregistered_permissions);
        }

        // 2) check external permissions
        // if they have no role assigned, save them in the desired role
        for (String role : this.external_permissions.keySet()) {
            for (String permission : this.external_permissions.get(role)) {
                if (permissions.getRoleForPermission(permission) == null)
                    permissions.addPermission(role, permission);
            }
        }

        // 3) there is at least one role in the file available.
        // check if all permissions are assigned to a role and add missing roles to the default role
        if (permissions.getRoles().size() > 0) {
            for (String permission : this.unregistered_permissions) {
                if (permissions.getRoleForPermission(permission) == null) {
                    permissions.addPermission(this.no_role, permission);
                }
            }
        }

        this.savePermissions(permissions);
    }

    private void savePermissions(Permissions permissions) {
        // save all changes
        try {
            //this.getPlugin().log(Level.INFO, "  permission service: saving permissions");
            permissions.save();
            permissions.reload();
        } catch (InvalidConfigurationException e) {
            this.getPlugin().log(Level.SEVERE, "  permission service: Could not save permission file", e);
        }

        this.loadPermissions();
    }

    @SuppressWarnings("unchecked")
    private void loadPermissions() {

        Permissions permissions = (Permissions) this.config_service.getConfig(this.getPermissionFilename());
        HashMap<String, List<String>> roled_permissions = permissions.getAll();

        this.permission_dump    = new HashMap<>();
        this.active_permissions = new HashMap<>();

        for (final String role : roled_permissions.keySet()) {

            String formaatted_role = (role.equals(this.no_role))
                    ? role
                    : String.format("%s.%s", this.getPermissionPrefix(), role);

            for (String permission : permissions.getPermissionsFor(role)) {

                String formatted_permission = (role.equals(this.no_role))
                        ? String.format("%s.%s", this.getPermissionPrefix(), permission)
                        : String.format("%s.%s.%s", this.getPermissionPrefix(), role, permission);

                this.active_permissions.put(permission, formatted_permission);


                if (!this.permission_dump.containsKey(formaatted_role))
                    this.permission_dump.put(formaatted_role, new ArrayList<String>());

                this.permission_dump.get(formaatted_role).add(formatted_permission);
            } // for permission
        } // for role

    }

    @SuppressWarnings("unchecked")
    public boolean hasPermission(CommandSender sender, String permission) {
        Boolean op_has_permission = (Boolean) this.getConfigurationValue("service.permission.grant_all_permissions_to_op", true);

        return  (sender.isOp() && op_has_permission) ||
                permission == null ||
                permission.isEmpty() ||
                (this.active_permissions.containsKey(permission) && sender.hasPermission(this.active_permissions.get(permission)));
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, ArrayList<String>> getPermissionRoleDump() {
        return this.permission_dump;
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, ArrayList<String>> getPermissionRoleDump(Player player) throws PlayerNotFoundException {

        // NOTE: Most permissions plugins using Bukkit Superperm register the permissions when a player joins
        //       and remove the permissions when they leave.
        //       So it is technically not possible to require permissions for an offline player without hooking
        //       into the plugins that handles permissions
        //       For that reason we only accept not null Player objects and throw a PlayerNotFoundException if null
        //       is given.
        if (player == null)
            throw new PlayerNotFoundException();

        HashMap<String, ArrayList<String>> dump = new HashMap<>();

        for (Object o : this.permission_dump.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String role = (String) pair.getKey();

            for (String permission : (ArrayList<String>) pair.getValue()) {
                if (!this.hasPermission(player, permission))
                    continue;

                if (!dump.containsKey(role))
                    dump.put(role, new ArrayList<String>());

                dump.get(role).add(permission);
            }
        }
        return dump;
    }

    @Override
    public String toString() {
        return String.format("(filename: %s - prefix: %s)", this.getPermissionFilename(), this.getPermissionPrefix());
    }

}