package de.cubenation.api.bedrock.service.permission;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.command.CommandRole;
import de.cubenation.api.bedrock.config.Permissions;
import de.cubenation.api.bedrock.exception.PlayerNotFoundException;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.permission.Permission;
import de.cubenation.api.bedrock.service.AbstractService;
import de.cubenation.api.bedrock.service.ServiceInterface;
import de.cubenation.api.bedrock.service.config.ConfigService;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 */
@SuppressWarnings("unused")
public class PermissionService extends AbstractService implements ServiceInterface {

    private ConfigService configService;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ArrayList<Permission> externalPermissions = new ArrayList<>();

    private ArrayList<Permission> localPermissionCache = new ArrayList<>();

    private HashMap<String, String> activePermissions = new HashMap<>();

    private HashMap<String, ArrayList<String>> permissionDump = new HashMap<>();

    public PermissionService(BasePlugin plugin) {
        super(plugin);
        this.configService = plugin.getConfigService();
    }

    private String getPermissionPrefix() {
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

        this.initializePermissions();
    }

    @Override
    public void reload() throws ServiceReloadException {
        this.initializePermissions();
    }

    @Deprecated
    public void registerPermission(String role, String permission) throws NullPointerException {
        this.registerPermission(permission, CommandRole.valueOf(role));
    }

    public void registerPermission(String permission) {
        this.registerPermission(new Permission(permission));
    }

    @SuppressWarnings("WeakerAccess")
    public void registerPermission(String permission, CommandRole role) {
        this.registerPermission(new Permission(permission, role));
    }

    @SuppressWarnings("WeakerAccess")
    public void registerPermission(Permission permission) {
        List<Permission> exists = this.externalPermissions.stream()
                .filter(permissionObject ->
                        permissionObject.getName().equals(permission.getName()) && permissionObject.getRole().equals(permission.getRole()))
                .collect(Collectors.toList());

        if (exists.size() == 0) {
            this.externalPermissions.add(permission);
            this.initializePermissions();
        }
    }

    private void initializePermissions() {
        /*
        We need to reload the permission file first to make sure the Permissions class knows its current
        content.
        This must be done for the case a permission has been moved to another role or removed from a role.
        The YamlConfiguration object would not know about the move/remove and would still have the permission
        in it's cache, so the move/remove would not be recognized.
        */
        Permissions permissions = (Permissions) this.configService.getConfig(Permissions.class);
        try {
            permissions.reload();
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "While reloading permissions: " + e.getCause(), e);
            return;
        }

        // re-initialize local permission cache
        this.localPermissionCache = new ArrayList<>();

        // collect permissions from commands (and arguments)
        this.getPlugin().getCommandService().getCommandManagers().forEach(commandManager ->
                commandManager.getCommands().forEach(abstractCommand -> {

            abstractCommand.getRuntimePermissions().forEach(permission -> {
                if (permission.getName() != null) {
                    this.localPermissionCache.add(permission);
                }
            });

            abstractCommand.getArguments().forEach(argument -> {
                if (argument.getPermission() != null && argument.getPermission().getName() != null) {
                    this.localPermissionCache.add(argument.getPermission());
                }
            });

        }));

        // collect externally registered permissions from userland
        this.externalPermissions.forEach(permission -> {
            if (permission.getName() != null) {
                this.localPermissionCache.add(permission);
            }
        });

        this.savePermissions(permissions);
    }

    private void savePermissions(Permissions permissions) {
        // clean up default role - will be restored if necessary
        permissions.removeRole(CommandRole.NO_ROLE);

        // restore missing permissions
        this.localPermissionCache.forEach(permission -> {
            if (permissions.getRoleForPermission(permission.getName()) == null) {
                permissions.addPermission(permission);
            }
        });

        // save
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
        HashMap<CommandRole, ArrayList<String>> configuredPermissions = permissions.getAll();

        this.permissionDump = new HashMap<>();
        this.activePermissions = new HashMap<>();

        for (final CommandRole role : configuredPermissions.keySet()) {
            String roleName = role.getType().toLowerCase();
            String formaatted_role = (role.equals(CommandRole.NO_ROLE))
                    ? roleName
                    : String.format("%s.%s", this.getPermissionPrefix(), roleName);

            for (String permission : permissions.getPermissionsFor(role)) {

                String formatted_permission = (role.equals(CommandRole.NO_ROLE))
                        ? String.format("%s.%s", this.getPermissionPrefix(), permission)
                        : String.format("%s.%s.%s", this.getPermissionPrefix(), roleName, permission);

                this.activePermissions.put(permission, formatted_permission);

                if (!this.permissionDump.containsKey(formaatted_role))
                    this.permissionDump.put(formaatted_role, new ArrayList<>());

                this.permissionDump.get(formaatted_role).add(formatted_permission);
            } // for permission
        } // for role

    }

    public boolean hasPermission(CommandSender sender, Permission permission) {
        return this.hasPermission(sender, permission.getName());
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
                ", localPermissionCache=" + localPermissionCache +
                ", activePermissions=" + activePermissions +
                ", permissionDump=" + permissionDump +
                ", externalPermissions=" + externalPermissions +
                '}';
    }
}