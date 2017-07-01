/*
 * Bedrock
 *
 * Copyright (c) 2017 Cube-Nation (Benedikt Hruschka, Tristan Cebulla)
 *
 * Permission is hereby granted, free of charge,
 * to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.cubenation.bedrock.bukkit.api.ebean;

import de.cubenation.bedrock.bukkit.plugin.BedrockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import javax.persistence.*;
import java.util.UUID;

/**
 * Represents a BedrockWorld instance.
 *
 * @author Cube-Nation
 * @version 1.0
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
