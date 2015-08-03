package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.ebean.BedrockWorld;
import de.cubenation.bedrock.exception.BedrockEbeanEntityNotFoundException;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class BedrockEbeanHelper {

    /**
     * Returns a BedrockPlayer object for the given org.bukkit.entity.Player
     *
     * @param player A org.bukkit.entity.Player object
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    public static BedrockPlayer getBedrockPlayer(Player player) throws BedrockEbeanEntityNotFoundException {
        return getBedrockPlayer(player.getUniqueId());
    }

    /**
     * Returns a BedrockPlayer object for the given java.util.UUID
     *
     * @param uuid A UUID
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    public static BedrockPlayer getBedrockPlayer(UUID uuid) throws BedrockEbeanEntityNotFoundException {
        return getBedrockPlayer(uuid.toString());
    }

    /**
     * Returns a BedrockPlayer object for the given uuid String
     *
     * @param uuid A string representing a UUID
     * @return BedrockPlayer
     * @throws BedrockEbeanEntityNotFoundException
     */
    public static BedrockPlayer getBedrockPlayer(String uuid) throws BedrockEbeanEntityNotFoundException {
        BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockPlayer.class)
                .where()
                .eq("uuid", uuid)
                .findUnique();

        if (player == null)
            throw new BedrockEbeanEntityNotFoundException(BedrockPlayer.class, uuid);

        return player;
    }


    public static BedrockWorld getBedrockWorld(World world) throws BedrockEbeanEntityNotFoundException {
        return getBedrockWorld(world.getUID());
    }

    public static BedrockWorld getBedrockWorld(UUID uuid) throws BedrockEbeanEntityNotFoundException {
        return getBedrockWorld(uuid.toString());
    }

    public static BedrockWorld getBedrockWorld(String uuid) throws BedrockEbeanEntityNotFoundException {
        BedrockWorld world = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockWorld.class)
                .where()
                .eq("uuid", uuid)
                .findUnique();

        if (world == null)
            throw new BedrockEbeanEntityNotFoundException(BedrockWorld.class, uuid);

        return world;
    }

}