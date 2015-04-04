package de.cubenation.bedrock.permission;

/**
 * Created by B1acksheep on 04.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.permission
 */
public class Permission {

    //region Properties
    private String permission;

    private Role role;
    //endregion


    //region Constructors
    /**
     * Instantiates a new Permission.
     *
     * @param permission the permission
     */
    public Permission(String permission) {
        this.permission = permission;
    }

    /**
     * Instantiates a new Permission.
     *
     * @param permission the permission
     * @param role       the role
     */
    public Permission(String permission, Role role) {
        this.permission = permission;
        this.role = role;
    }
    //endregion


    //region Role 1-n Permission
    /**
     * Gets role.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(Role role) {
        if (this.role != role) {
            Role oldRole = role;
            if (oldRole != null) {
                this.role = null;
                oldRole.removePermission(this);
            }
            this.role = role;
            if (role != null) {
                role.addPermission(this);
            }
        }
    }

    /**
     * With role.
     *
     * @param role the role
     * @return the permission
     */
    public Permission withRole(Role role) {
        setRole(role);
        return this;
    }
    //endregion


    //region Getter
    /**
     * Gets permission.
     *
     * @return the permission
     */
    public String getPermission() {
        return permission;
    }
    //endregion


}
