package de.cubenation.bedrock.core.database;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.DatabaseEntity;
import de.cubenation.bedrock.core.model.BedrockOfflinePlayer;

/**
 * @author Cube-Nation
 * @version 2.0
 */

@DatabaseEntity(BedrockOfflinePlayer.class)
public class BedrockDatabase extends CustomDatabase {

    public BedrockDatabase(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "bedrock";
    }
}
