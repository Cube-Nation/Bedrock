package de.cubenation.api.bedrock.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by BenediktHr on 22.09.15.
 * Project: Bedrock
 */
public class PlayerChangesNameEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private String oldName;

    private String newName;


    public PlayerChangesNameEvent(Player player, String oldName, String newName) {
        this.player = player;
        this.oldName = oldName;
        this.newName = newName;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
