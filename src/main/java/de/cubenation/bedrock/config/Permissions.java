package de.cubenation.bedrock.config;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.config.CustomConfigurationFile;
import net.cubespace.Yamler.Config.Path;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class Permissions extends CustomConfigurationFile {

    public Permissions(BasePlugin plugin, String name) throws IOException {
        CONFIG_FILE = new File(plugin.getDataFolder(), name);
    }

    @Path("permissions")
    private HashMap<String, List<String>> permissions = new HashMap<String, List<String>>() {{

    }};

    /**
     * roles
     */

    public HashMap<String, List<String>> getAll() {
        return this.permissions;
    }

    public boolean hasRoles() {
        return (this.permissions.size() != 0);
    }

    public Set<String> getRoles() {
        return this.permissions.keySet();
    }

    private boolean roleExists(String role) {
        return this.permissions.containsKey(role);
    }

    private void createRole(String role) {
        if (this.roleExists(role))
            return;

        this.permissions.put(role, new ArrayList<String>());
    }

    public void removeRole(String role) {
        if (!this.roleExists(role))
            return;
        this.permissions.remove(role);
    }


    /**
     * permissions (role based)
     */

    public void addPermission(String role, String permission) {
        if (permission == null)
            return;

        if (!this.roleExists(role))
            this.createRole(role);

        if (this.getPermissionsFor(role).contains(permission))
            return;

        this.permissions.get(role).add(permission);
    }

    public void addPermissions(String role, List<String> permissions) {
        if (permissions == null)
            return;

        for (String permission : permissions)
            this.addPermission(role, permission);
    }


    public void removePermission(String role, String permission) {
        if (!this.roleExists(role))
            return;

        this.permissions.get(role).remove(permission);
    }

    public void removePermissions(String role, List<String> permissions) {
        if (permissions == null)
            return;

        for (String permission : permissions)
            this.removePermission(role, permission);
    }


    public boolean PermissionExists(String role, String permission) {
        return this.roleExists(role) && this.permissions.get(role).contains(permission);
    }

    public String getRoleForPermission(String permission) {
        if (!this.hasRoles())
            return null;

        for (String role : this.getRoles()) {
            if (this.permissions.get(role).contains(permission))
                return role;
        }

        return null;
    }

    public List<String> getPermissionsFor(String role) {
        if (!this.roleExists(role))
            return new ArrayList<>();

        return this.permissions.get(role);
    }

}
