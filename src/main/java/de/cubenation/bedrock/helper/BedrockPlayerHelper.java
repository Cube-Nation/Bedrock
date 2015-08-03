package de.cubenation.bedrock.helper;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.ebean.BedrockPlayer;
import de.cubenation.bedrock.exception.BedrockPlayerNotFoundException;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by Tristan Cebulla <equinox@lichtspiele.org> on 03.08.2015.
 */
@SuppressWarnings({"unused", "DefaultFileTemplate"})
public class BedrockPlayerHelper {

    /**
     * Returns a BedrockPlayer object for the given org.bukkit.entity.Player
     *
     * @param player A org.bukkit.entity.Player object
     * @return BedrockPlayer
     * @throws BedrockPlayerNotFoundException
     */
    public static BedrockPlayer getBedrockPlayer(Player player) throws BedrockPlayerNotFoundException {
        return getBedrockPlayer(player.getUniqueId());
    }

    /**
     * Returns a BedrockPlayer object for the given java.util.UUID
     *
     * @param uuid A UUID
     * @return BedrockPlayer
     * @throws BedrockPlayerNotFoundException
     */
    public static BedrockPlayer getBedrockPlayer(UUID uuid) throws BedrockPlayerNotFoundException {
        return getBedrockPlayer(uuid.toString());
    }

    /**
     * Returns a BedrockPlayer object for the given uuid String
     *
     * @param uuid A string representing a UUID
     * @return BedrockPlayer
     * @throws BedrockPlayerNotFoundException
     */
    public static BedrockPlayer getBedrockPlayer(String uuid) throws BedrockPlayerNotFoundException {
        BedrockPlayer player = BedrockPlugin.getInstance().getDatabase()
                .find(BedrockPlayer.class)
                .where()
                .eq("uuid", uuid)
                .findUnique();

        if (player == null)
            throw new BedrockPlayerNotFoundException(uuid);

        return player;
    }

}
