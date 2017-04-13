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
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by B1acksheep on 25.04.15.
 * Project: Bedrock
 */
public class PermissionService extends AbstractService implements ServiceInterface {

    private ConfigService configService;

    private ArrayList<Permission> externalPermissions = new ArrayList<>();

    private ArrayList<Permission> localPermissionCache = new ArrayList<>();

    public PermissionService(BasePlugin plugin) {
        super(plugin);
        this.configService = plugin.getConfigService();
    }

    public String getPermissionPrefix() {
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

    @SuppressWarnings("unused")
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

    private void addPermission(Permission permission) {
        if (permission.getName() == null) return;

        List<Permission> exists = this.localPermissionCache.stream()
                .filter(cachedPermission ->
                        cachedPermission.getName().equals(permission.getName())
                ).collect(Collectors.toList());

        if (exists.size() == 0) {
            this.localPermissionCache.add(permission);
        }
    }

    private void initializePermissions() {
        // re-initialize local permission cache
        this.localPermissionCache = new ArrayList<>();

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

            permissions.getAll().forEach(
                    (commandRole, permissionList) -> permissionList.forEach(permission -> {

                // To self-repair NO_ROLE permissions we ignore them here.
                // They will be added later to their approriate role (or NO_ROLE) again
                if (commandRole.equals(CommandRole.NO_ROLE)) {
                    return;
                }

                this.addPermission(new Permission(permission, commandRole));
            }));
        } catch (InvalidConfigurationException e) {
            plugin.log(Level.SEVERE, "While reloading permissions: " + e.getCause(), e);
            return;
        }

        // collect permissions from commands (and arguments)
        this.getPlugin().getCommandService().getCommandManagers().forEach(commandManager ->
                commandManager.getCommands().forEach(abstractCommand -> {

            abstractCommand.getRuntimePermissions().forEach(this::addPermission);

            abstractCommand.getArguments().forEach(argument -> {
                if (argument.getPermission() != null) {
                    this.addPermission(argument.getPermission());
                }
            });

        }));

        // collect externally registered permissions from userland
        this.externalPermissions.forEach(this::addPermission);

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
    }

    public boolean hasPermission(CommandSender sender, Permission permission) {
        Boolean op_has_permission = (Boolean) this.getConfigurationValue("service.permission.grant_all_permissions_to_op", true);
        return (sender.isOp() && op_has_permission) ||
                sender.hasPermission(permission.getPermissionNode());
    }

    @Deprecated
    public boolean hasPermission(CommandSender sender, String permission) {
        List<Permission> filtered = this.localPermissionCache.stream()
                .filter(cachedPermission -> cachedPermission.getName().equals(permission))
                .collect(Collectors.toList());

        for (Permission filteredPermission : filtered) {
            if (this.hasPermission(sender, filteredPermission))
                return true;
        }

        return false;
    }

    public List<Permission> getPermissions() {
        return this.localPermissionCache;
    }

    public List<Permission> getPermissions(Player player) throws PlayerNotFoundException {
        if (player == null)
            throw new PlayerNotFoundException();

        return this.localPermissionCache.stream()
                .filter(permission -> permission.userHasPermission(player))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "PermissionService{" +
                "configService=" + configService +
                ", localPermissionCache=" + localPermissionCache.stream().map(Permission::toString) +
                ", externalPermissions=" + externalPermissions.stream().map(Permission::toString) +
                '}';
    }
}