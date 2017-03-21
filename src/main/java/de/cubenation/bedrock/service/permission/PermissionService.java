package de.cubenation.bedrock.service.permission;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.command.AbstractCommand;
import de.cubenation.bedrock.command.CommandRole;
import de.cubenation.bedrock.command.argument.Argument;
import de.cubenation.bedrock.command.manager.CommandManager;
import de.cubenation.bedrock.config.Permissions;
import de.cubenation.bedrock.exception.PlayerNotFoundException;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.permission.Permission;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;
import de.cubenation.bedrock.service.config.ConfigService;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.service.permission
 */
public class PermissionService extends AbstractService implements ServiceInterface {

    private ConfigService configService;

    private ArrayList<Permission> unregisteredPermissions = new ArrayList<>();

    private HashMap<String, String> activePermissions = new HashMap<>();

    private HashMap<String, ArrayList<String>> permissionDump = new HashMap<>();

    private HashMap<String, ArrayList<String>> externalPermissions = new HashMap<>();

    public final static String no_role = "no_role";


    public PermissionService(BasePlugin plugin) {
        super(plugin);
        this.configService = plugin.getConfigService();
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
            this.configService.registerFile(
                    Permissions.class,
                    new Permissions(this.getPlugin())
            );
        } catch (Exception e) {
            throw new ServiceInitException(e.getMessage());
        }

        this.initializeCommandPermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.initializeCommandPermissions();
    }

    private void initializeCommandPermissions() {
        this.unregisteredPermissions = new ArrayList<>();

        for (CommandManager manager : this.getPlugin().getCommandService().getCommandManagers()) {
            for (AbstractCommand command : manager.getCommands()) {

                for (Permission permission : command.getRuntimePermissions()) {
                    String stringPermission = permission.getName();
                    if (stringPermission == null) {
                        continue;
                    }

                    this.unregisteredPermissions.add(permission);
                }

                for (Argument argument : command.getArguments()) {
                    if (argument.getPermission() != null) {
                        String stringPermission = argument.getPermission().getName();
                        if (stringPermission == null) {
                            continue;
                        }

                        this.unregisteredPermissions.add(argument.getPermission());
                    }
                }
            }
        }

        this.fixPermissions((Permissions) this.configService.getConfig(Permissions.class));
    }


    @SuppressWarnings("unused")
    public void registerPermission(String permission) {
        this.registerPermission(PermissionService.no_role, permission);
    }

    @SuppressWarnings("unused")
    public void registerPermission(String permission, CommandRole role) {
        registerPermission(role.getType(), permission);
    }

    @Deprecated
    public void registerPermission(String role, String permission) {
        //this.getPlugin().log(Level.INFO, "Registering permission " + permission + " in role " + role);

        // save for later in fixPermissions()
        if (!this.externalPermissions.containsKey(role))
            this.externalPermissions.put(role, new ArrayList<>());

        if (!this.externalPermissions.get(role).contains(permission))
            this.externalPermissions.get(role).add(permission);

        Permissions permissions = (Permissions) this.configService.getConfig(Permissions.class);
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
            permissions = (Permissions) this.configService.getConfig(Permissions.class);

        // clean up default role - will be restored if necessary
        permissions.removeRole(PermissionService.no_role);


        // 1) create all permissions from scratch in default role if
        //   - the current permission file has no roles       (AND)
        //   - plugin commands use permissions
        if (permissions.getRoles().size() == 0 && this.unregisteredPermissions.size() != 0) {
            // no roles -> create all permissions in the default role

            for (Permission permission : this.unregisteredPermissions) {
                permissions.addPermission(permission.getRoleName(), permission.getName());
            }
        }

        // 2) check external permissions
        // if they have no role assigned, save them in the desired role
        for (String role : this.externalPermissions.keySet()) {
            for (String permission : this.externalPermissions.get(role)) {
                if (permissions.getRoleForPermission(permission) == null)
                    permissions.addPermission(role, permission);
            }
        }

        // 3) there is at least one role in the file available.
        // check if all permissions are assigned to a role and add missing roles to the default role
        if (permissions.getRoles().size() > 0) {
            for (Permission permission : this.unregisteredPermissions) {
                if (permissions.getRoleForPermission(permission.getName()) == null) {
                    permissions.addPermission(PermissionService.no_role, permission.getName());
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

        Permissions permissions = (Permissions) this.configService.getConfig(Permissions.class);
        HashMap<String, List<String>> roled_permissions = permissions.getAll();

        this.permissionDump = new HashMap<>();
        this.activePermissions = new HashMap<>();

        for (final String role : roled_permissions.keySet()) {

            String formaatted_role = (role.equals(PermissionService.no_role))
                    ? role
                    : String.format("%s.%s", this.getPermissionPrefix(), role);

            for (String permission : permissions.getPermissionsFor(role)) {

                String formatted_permission = (role.equals(PermissionService.no_role))
                        ? String.format("%s.%s", this.getPermissionPrefix(), permission)
                        : String.format("%s.%s.%s", this.getPermissionPrefix(), role, permission);

                this.activePermissions.put(permission, formatted_permission);


                if (!this.permissionDump.containsKey(formaatted_role))
                    this.permissionDump.put(formaatted_role, new ArrayList<>());

                this.permissionDump.get(formaatted_role).add(formatted_permission);
            } // for permission
        } // for role

    }

    @SuppressWarnings("unchecked")
    public boolean hasPermission(CommandSender sender, String permission) {
        Boolean op_has_permission = (Boolean) this.getConfigurationValue("service.permission.grant_all_permissions_to_op", true);

        return (sender.isOp() && op_has_permission) ||
                permission == null ||
                permission.isEmpty() ||
                (this.activePermissions.containsKey(permission) && sender.hasPermission(this.activePermissions.get(permission)));
    }

    @SuppressWarnings("unchecked")
    public HashMap<String, ArrayList<String>> getPermissionRoleDump() {
        return this.permissionDump;
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

        for (Object o : this.permissionDump.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            String role = (String) pair.getKey();

            for (String permission : (ArrayList<String>) pair.getValue()) {
                if (!player.hasPermission(permission)) {
                    continue;
                }

                if (!dump.containsKey(role))
                    dump.put(role, new ArrayList<>());

                dump.get(role).add(permission);
            }
        }
        return dump;
    }

    @Override
    public String toString() {
        return "PermissionService{" +
                "configService=" + configService +
                ", unregisteredPermissions=" + unregisteredPermissions +
                ", activePermissions=" + activePermissions +
                ", permissionDump=" + permissionDump +
                ", externalPermissions=" + externalPermissions +
                '}';
    }
}