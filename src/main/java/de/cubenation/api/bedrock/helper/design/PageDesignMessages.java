package de.cubenation.api.bedrock.helper.design;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.helper.MessageHelper;
import de.cubenation.api.bedrock.translation.Translation;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by BenediktHr on 30.07.15.
 * Project: Bedrock
 */
public class PageDesignMessages extends MessageHelper{

    public static TextComponent getHeader(BasePlugin plugin, int from, int to) {
        return new TextComponent(new Translation(
                plugin,
                "page.design.header",
                new String[]{"from", String.valueOf(from),
                        "to", String.valueOf(to)}
        ).getTranslation());
    }

}
