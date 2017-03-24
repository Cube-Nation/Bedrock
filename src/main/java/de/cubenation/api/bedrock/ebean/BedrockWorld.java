package de.cubenation.api.bedrock.ebean;

import de.cubenation.api.bedrock.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 04.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
@Entity()
@Table(name="bedrock_worlds")
public class BedrockWorld {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @Column(unique=true, nullable=false)
    private String uuid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return this.uuid;
    }


    public void setWorld(World world) {
        this.setUuid(world.getUID().toString());
    }

    public World getWorld() {
        return Bukkit.getWorld(UUID.fromString(this.uuid));
    }

    public String getWorldName() {
        return this.getWorld().getName();
    }


    public void save() {
        BedrockPlugin.getInstance().getDatabase().save(this);
    }

    @Override
    public String toString() {
        return "BedrockWorld{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}
