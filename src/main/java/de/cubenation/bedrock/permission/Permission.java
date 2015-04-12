package de.cubenation.bedrock.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;

/**
 * Created by B1acksheep on 04.04.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.permission
 */
public class Permission {

    //region Properties
    private String permission;
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
    //endregion

    @Nullable
    public boolean userHasPermission(CommandSender sender) {
        return !(sender instanceof Player) || sender.hasPermission(getPermission());
    }

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
