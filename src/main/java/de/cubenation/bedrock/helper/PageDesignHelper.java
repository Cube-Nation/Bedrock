package de.cubenation.bedrock.helper;

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

    private static final int NAVIGATIONSIZE = 6;

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


        ComponentBuilder header = new ComponentBuilder("=== ").color(flag)
                .append(page + "/" + service.getPages()).color(primary)
                .append(" ===").color(flag);
        player.spigot().sendMessage(header.create());


        // Send entries of page
        for (PageableListStorable storable : list) {
            TextComponent component = (TextComponent) storable.get();
            if (component != null) {
                player.spigot().sendMessage(component);
            }
        }

        // Display Navigation
        ComponentBuilder navigation = new ComponentBuilder("");
        if (page == 0) {
            // No Prev available
            navigation.append("<-Prev- ").color(primary).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", (page - 1) + "")));
        }

        double currentPage = 1;
        double step = getSteps(1, service.getPages());
        for (int i = 0; i < NAVIGATIONSIZE;i++) {
            int p = (int) currentPage;

            navigation.append(" ");
            navigation.append(p + "").color(secondary).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", (page - 1) + "")));
            navigation.append(" ");

            if (p != service.getPages()) {
                navigation.append("|").color(flag);
            }

            currentPage += step;
        }

        if (page < service.getPages()) {
            // No Prev available
            navigation.append(" -Next->").color(primary).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replaceAll("%page%", (page + 1) + "")));
        }
    }


    private static double getSteps(int start, int end) {
        return (end - start) / (NAVIGATIONSIZE);
    }

}
