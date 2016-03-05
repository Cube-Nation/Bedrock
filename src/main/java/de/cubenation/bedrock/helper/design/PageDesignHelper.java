package de.cubenation.bedrock.helper.design;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.helper.MessageHelper;
import de.cubenation.bedrock.helper.TextHolder;
import de.cubenation.bedrock.service.pageablelist.AbstractPageableListService;
import de.cubenation.bedrock.service.pageablelist.PageableListStorable;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by BenediktHr on 25.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.helper
 */

// TODO: remove colors and use a locale template instead

@SuppressWarnings("unused")
@Deprecated
public class PageDesignHelper {

    private static final int NAVIGATIONSIZE = 7;

    public static void pagination(BasePlugin plugin, AbstractPageableListService service, int page, String pageExecutionCmd, CommandSender sender, String headline) {
        // Can't show pages, cause its empty
        if (service.isEmpty()) {
            plugin.log(Level.INFO, "Space PageableListService. Can't display pages.");
            return;
        }

        // Can only display TextHolder
        for (int i = 0; i < service.size(); i++) {
            if (!(service.getStorableAtIndex(i).get() instanceof TextHolder)) {
                plugin.log(Level.INFO, "Space PageableListService. Can't display pages.");
                return;
            }
        }

        if (!pageExecutionCmd.contains("%page%")) {
            plugin.log(Level.INFO, "No Placeholder for RunCommand Index");
            return;
        }

        List<PageableListStorable> list = service.getPage(page);

        if (headline != null) {
            MessageHelper.send(plugin, sender, headline);
        }

        // Send entries of page
        for (PageableListStorable storable : list) {
            TextHolder holder = (TextHolder) storable.get();
            if (holder != null) {
                PageDesignMessages.send(plugin, sender, holder.getTextComponent(), holder.getHoverEvent(), holder.getClickEvent());
            }
        }

        // Display Navigation

        ComponentBuilder pagination = PageableMessageHelper.getPagination(plugin, page, service.getPages(), pageExecutionCmd);
        if (pagination != null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.spigot().sendMessage(pagination.create());
            }
        }
    }

}
