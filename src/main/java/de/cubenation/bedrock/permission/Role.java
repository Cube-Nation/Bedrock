package de.cubenation.bedrock.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by B1acksheep on 04.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.permission
 */
public class Role {


    //region Properties
    private String name;

    private Permission rolePermission;

    private ArrayList<Permission> permissions = new ArrayList<>();

    private ArrayList<Role> parentRoles = new ArrayList<>();

    private ArrayList<Role> kindRoles = new ArrayList<>();

    //endregion


    //region Constructor

    /**
     * Instantiates a new Role.
     *
     * @param name           the name
     * @param rolePermission the role permission
     * @param permissions    the permissions
     */
    public Role(String name, Permission rolePermission, ArrayList<Permission> permissions) {
        this.name = name;
        this.rolePermission = rolePermission;
        this.permissions = permissions;
    }

    /**
     * Instantiates a new Role.
     *
     * @param name           the name
     * @param rolePermission the role permission
     * @param permissions    the permissions
     */
    public Role(String name, Permission rolePermission, Permission... permissions) {
        this.name = name;
        this.rolePermission = rolePermission;
        Collections.addAll(this.permissions, permissions);
    }
    //endregion


    //region ParentRole n-n KindRole

    /**
     * Gets parent roles.
     *
     * @return the parent roles
     */
    public ArrayList<Role> getParentRoles() {
        return parentRoles;
    }

    /**
     * Add parent role.
     *
     * @param role the role
     */
    public void addParentRole(Role role) {
        if (parentRoles.add(role)) {
            role.addKindRole(this);
        }
    }

    /**
     * Remove parent role.
     *
     * @param role the role
     */
    public void removeParentRole(Role role) {
        if (parentRoles.remove(role)) {
            role.removeKindRole(this);
        }
    }
    //endregion


    //region KindRole n-n ParentRole

    /**
     * Gets kind roles.
     *
     * @return the kind roles
     */
    public ArrayList<Role> getKindRoles() {
        return kindRoles;
    }

    /**
     * Add kind role.
     *
     * @param role the role
     */
    public void addKindRole(Role role) {
        if (kindRoles.add(role)) {
            role.addParentRole(this);
        }
    }

    /**
     * Remove kind role.
     *
     * @param role the role
     */
    public void removeKindRole(Role role) {
        if (kindRoles.remove(role)) {
            role.removeParentRole(this);
        }
    }
    //endregion

    @Nullable
    public boolean userHasRole(CommandSender sender) {
        if (sender instanceof Player) {
            PermissionUser user = PermissionsEx.getUser((Player) sender);
            if (checkRoleForUser(user)) return true;
        } else {
            return true;
        }
        return false;
    }

    private boolean checkRoleForUser(PermissionUser user) {
        if (user.has(getRolePermission().getPermission())) {
            return true;
        }

        if (getParentRoles() != null) {
            for (Role parentRole : getParentRoles()) {
                if (parentRole.checkRoleForUser(user)) {
                    return true;
                }
            }
        }

        return false;
    }


    //region Permission n-1 Role

    /**
     * Gets permissions.
     *
     * @return the permissions
     */
    public ArrayList<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Add permission.
     *
     * @param permission the permission
     */
    public void addPermission(Permission permission) {
        if (permissions.add(permission)) {
            permission.setRole(this);
        }
    }

    /**
     * Remove permission.
     *
     * @param permission the permission
     */
    public void removePermission(Permission permission) {
        if (permissions.remove(permission)) {
            permission.setRole(null);
        }
    }
    //endregion


    //region Getter

    /**
     * Gets role permission.
     *
     * @return the role permission
     */
    public Permission getRolePermission() {
        return rolePermission;
    }
    //endregion


}
