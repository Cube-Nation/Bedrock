package de.cubenation.bedrock.core.model;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

/**
 * Represents a BedrockPlayer instance.
 *
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Entity()
@Table(name = "bedrock_players")
public class BedrockPlayer {

    public BedrockPlayer() {
    }

    public BedrockPlayer(String uuid, String username, String ip, Date lastlogin) {
        this.uuid = uuid;
        this.username = username;
        this.ip = ip;
        this.lastlogin = lastlogin;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String uuid;

    @NotNull
    private String username;

    @Column(length = 45)
    private String ip;

    private Date lastlogin;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    public void setPlayer(UUID uuid) {
        this.setUuid(uuid.toString());
    }

    public void save(EbeanServer database) {
        database.save(this);
    }

    public void update(EbeanServer database) {
        database.update(this);
    }

}

