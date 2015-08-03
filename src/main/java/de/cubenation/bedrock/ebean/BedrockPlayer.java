package de.cubenation.bedrock.ebean;

import de.cubenation.bedrock.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.persistence.*;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UUID getUUID() {
        return UUID.fromString(uuid);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

}
