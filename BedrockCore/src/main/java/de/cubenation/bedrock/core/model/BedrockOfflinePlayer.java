package de.cubenation.bedrock.core.model;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;
import de.cubenation.bedrock.core.wrapper.BedrockPlayer;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a BedrockPlayer instance.
 *
 * @author Cube-Nation
 * @version 2.0
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
@ToString @NoArgsConstructor @AllArgsConstructor
@Entity()
@Table(name = "bedrock_players")
public class BedrockOfflinePlayer {

    @Getter @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @Getter @Setter
    @NotNull
    private String username;

    @Getter @Setter
    @Column(length = 45)
    private String ip;

    @Getter @Setter
    private Date lastlogin;


    public BedrockOfflinePlayer(String uuid, String username, String ip, Date lastlogin) {
        this.uuid = uuid;
        this.username = username;
        this.ip = ip;
        this.lastlogin = lastlogin;
    }


    public UUID getUUID() {
        return UUID.fromString(this.uuid);
    }

    public void setUUID(UUID uuid) {
        this.setUuid(uuid.toString());
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void save(EbeanServer database) {
        database.save(this);
    }

    public void update(EbeanServer database) {
        database.update(this);
    }


    public boolean isEqual(BedrockPlayer player) {
        return this.getUUID().equals(player.getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BedrockOfflinePlayer that = (BedrockOfflinePlayer) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }
}
