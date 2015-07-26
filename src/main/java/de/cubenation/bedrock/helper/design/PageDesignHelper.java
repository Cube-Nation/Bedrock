package de.cubenation.bedrock.helper.design;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.service.pageablelist.PageableListService;
import de.cubenation.bedrock.service.pageablelist.PageableListStorable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
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


        ComponentBuilder header = new ComponentBuilder("======= ").color(flag)
                .append(page + "/" + service.getPages()).color(primary)
                .append(" =======").color(flag);
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
            pagination.append("<=").color(flag).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", (page - 1) + "")));
            pagination.append("Prev").color(secondary);
            pagination.append("=").color(flag);
        } else {
            pagination.append("=======").color(flag);
        }

        pagination.append(" ").reset();


        if (totalPages <= NAVIGATIONSIZE) {
            //Display all, no formating logic needed.
            for (int i = 1; i <= totalPages; i++) {
                addPageNumber(i, page, pageExecutionCmd, pagination, secondary, flag, i != totalPages, false);
            }

        } else {
            addCalculatedPagination(plugin, page, totalPages, pageExecutionCmd, secondary, flag, pagination);
        }


        pagination.append(" ").reset();

        if (page < totalPages) {
            // Next available
            pagination.append("=").color(flag).event(
                    new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replaceAll("%page%", (page + 1) + "")));
            pagination.append("Next").color(secondary);
            pagination.append("=>").color(flag);
        } else {
            pagination.append("=======").color(flag);
        }
        return pagination;
    }

    private static void addCalculatedPagination(BasePlugin plugin, int page, int totalPages, String pageExecutionCmd, ChatColor secondary, ChatColor flag, ComponentBuilder pagination) {

        if (totalPages <= NAVIGATIONSIZE) {
            plugin.log(Level.WARNING, "totalPages (" + totalPages + ") <= 7 should not happen! Check your code!");
            return;
        }

        ArrayList<Integer> pages = new ArrayList<>();
        pages.add(1);
        pages.add(totalPages);

        if (page == 1) {
            // Easy, add 5 following
            for (int i = 1; i <= 5; i++) {
                pages.add(page + i);
            }
        } else if (page == totalPages) {
            // Easy, add 5 previous
            for (int i = 1; i <= 5; i++) {
                pages.add(page - i);
            }
        } else {
            // Current is not first or last, just add it
            pages.add(page);

            // Now check the 4 missing
            int missing1 = 2;
            int missing2 = 2;
            while (pages.size() < 7) {
                for (int i = 1; i <= missing1; i++) {
                    int pageToAdd = page - i;
                    if (!pages.contains(pageToAdd) && pageToAdd > 1) {
                        pages.add(pageToAdd);
                    } else {
                        missing2++;
                    }
                }

                for (int i = 1; i <= missing2; i++) {
                    int pageToAdd = page + i;
                    if (!pages.contains(pageToAdd) && pageToAdd < totalPages) {
                        pages.add(pageToAdd);
                    } else {
                        missing1++;
                    }
                }
            }
        }

        Collections.sort(pages);

        // Now, just print it
        for (int p = 0; p < pages.size(); p++) {
            int addPage = pages.get(p);
            boolean pipe = false;
            if ((addPage + 1) == pages.get(p + 1)) {
                pipe = true;
            }

            if (p == (pages.size() - 1)) {
                addPageNumber(p, page, pageExecutionCmd, pagination, secondary, flag, false, false);
            } else {
                addPageNumber(p, page, pageExecutionCmd, pagination, secondary, flag, pipe, !pipe);
            }
        }
    }


    private static boolean addPageNumber(int page,
                                         int currentPage,
                                         String pageExecutionCmd,
                                         ComponentBuilder pagination,
                                         ChatColor secondary,
                                         ChatColor flag,
                                         boolean pipeSeparator,
                                         boolean dashSeparator) {

        boolean isCurrent = false;

        pagination.append(page + "").color(secondary);
        if (page == currentPage) {
            pagination.bold(true);
            pagination.append("").reset();
            isCurrent = true;
        }

        pagination.event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", page + "")));

        if (pipeSeparator) {
            pagination.append("|").color(flag);
        } else if (dashSeparator) {
            pagination.append("...").color(flag);
        }

        return isCurrent;

    }

    private static double getSteps(int start, int end) {
        return (end - start) / (NAVIGATIONSIZE);
    }

}
