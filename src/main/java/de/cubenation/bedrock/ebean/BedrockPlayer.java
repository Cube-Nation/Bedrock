package de.cubenation.bedrock.ebean;

import com.avaje.ebean.validation.NotNull;
import de.cubenation.bedrock.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Entity()
@Table(name = "bedrock_players")
public class BedrockPlayer {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @Column(unique=true, nullable=false)
    private String uuid;

    @NotNull
    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUUID() {
        return UUID.fromString(this.uuid);
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPlayer(UUID uuid) {
        this.setUuid(uuid.toString());
    }

    public void setPlayer(Player player) {
        this.setUuid(player.getUniqueId().toString());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(this.uuid));
    }

    public void save() {
        BedrockPlugin.getInstance().getDatabase().save(this);
    }

    public void update() {
        BedrockPlugin.getInstance().getDatabase().update(this);
    }


    /*
     * Helper
     */

    public Date offlineTime() {
        boolean playerIsOnline = false;
        Player onlinePlayer = Bukkit.getServer().getPlayer(uuid);
        if (onlinePlayer != null) {
            playerIsOnline = onlinePlayer.isOnline();
        }

        if (playerIsOnline) {
            return new Date();
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(getUUID());

            if (offlinePlayer == null) {
                return null;
            } else {
                Long lastSeen = offlinePlayer.getLastPlayed();
                if (lastSeen != null) {
                    return new Date(lastSeen);
                } else {
                    return null;
                }
            }
        }
    }

    public boolean isEqual(Player player) {
        return this.getUUID().equals(player.getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BedrockPlayer that = (BedrockPlayer) o;

        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;

    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
