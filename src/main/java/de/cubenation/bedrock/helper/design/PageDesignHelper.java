package de.cubenation.bedrock.helper.design;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.pageablelist.PageableListService;
import de.cubenation.bedrock.service.pageablelist.PageableListStorable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

/**
 * Created by BenediktHr on 25.07.15.
 * Project: Bedrock
 * Package: de.cubenation.bedrock.helper
 */
public class PageDesignHelper {

    private static final int NAVIGATIONSIZE = 7;

    public static void pagination(BasePlugin plugin, PageableListService service, int page, String pageExecutionCmd, Player player) {
        // Can't show pages, cause its empty
        if (service.isEmpty()) {
            plugin.log(Level.INFO, "Empty PageableListService. Can't display pages.");
            return;
        }

        // Can only display TextComponents
        for (int i = 0; i < service.size(); i++) {
            if (!(service.getStorableAtIndex(i).get() instanceof TextComponent)) {
                plugin.log(Level.INFO, "Empty PageableListService. Can't display pages.");
                return;
            }
        }

        if (!pageExecutionCmd.contains("%page%")) {
            plugin.log(Level.INFO, "No Placeholder for PageableListService");
            return;
        }

        List<PageableListStorable> list = service.getPage(page);

        ChatColor primary = plugin.getPrimaryColor();
        ChatColor secondary = plugin.getSecondaryColor();
        ChatColor flag = plugin.getFlagColor();


        ComponentBuilder header = new ComponentBuilder("====== ").color(flag)
                .append(page + "/" + service.getPages()).color(primary)
                .append(" ======").color(flag);
        player.spigot().sendMessage(header.create());


        // Send entries of page
        for (PageableListStorable storable : list) {
            TextComponent component = (TextComponent) storable.get();
            if (component != null) {
                player.spigot().sendMessage(component);
            }
        }

        // Display Navigation

        ComponentBuilder pagination = getPagination(plugin, page, service.getPages(), pageExecutionCmd);
        if (pagination != null) {
            player.spigot().sendMessage(pagination.create());
        }
    }


    private static ComponentBuilder getPagination(BasePlugin plugin, int page, int totalPages, String pageExecutionCmd) {
        if (totalPages == 1) {
            // No Pagination needed
            return null;
        }

        ChatColor primary = plugin.getPrimaryColor();
        ChatColor secondary = plugin.getSecondaryColor();
        ChatColor flag = plugin.getFlagColor();

        ComponentBuilder pagination = new ComponentBuilder("");
        if (page > 1) {
            // Prev available
            pagination.append("<=Prev=").color(flag).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", (page - 1) + "")));
            pagination.append("");
        }


        if (totalPages <= NAVIGATIONSIZE) {
            //Display all, no formating logic needed.
            for (int i = 1; i <= totalPages; i++) {

                pagination.append(i + "").color(secondary);
                if (i == page) {
                    pagination.bold(true);
                    pagination.underlined(true);
                }

                pagination.event(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", i + "")));

                if (i != totalPages) {
                    pagination.append("|").color(flag);
                }
            }

        } else {

        }


        if (page < totalPages) {
            // Next available
            pagination.append(" ");
            pagination.append(" -Next->").color(flag).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replaceAll("%page%", (page + 1) + "")));
        }
        return pagination;
    }


    private static double getSteps(int start, int end) {
        return (end - start) / (NAVIGATIONSIZE);
    }

}
