package de.cubenation.api.bedrock.helper.design;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.pageablelist.AbstractPageableListService;
import de.cubenation.api.bedrock.service.pageablelist.PageableListStorable;
import de.cubenation.api.bedrock.translation.JsonMessage;
import de.cubenation.api.bedrock.translation.parts.BedrockJson;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */

public class PageableMessageHelper {

    public static final String PAGE_PLACEHOLDER = "%page%";

    private final BasePlugin plugin;
    private final AbstractPageableListService listService;
    private final String command;
    private String headline;
    private ArrayList<BedrockJson> jsonHeadline;

    public PageableMessageHelper(BasePlugin plugin, String command, AbstractPageableListService listService) {
        this.plugin = plugin;
        this.command = command;
        this.listService = listService;
    }

    public void paginate(CommandSender sender, int pageIndex) {
        if (command == null || !command.contains(PAGE_PLACEHOLDER)) {
            plugin.log(Level.INFO, "No placeholder for RunCommand index");
            return;
        }

        if (listService == null) {
            return;
        }

        displayPageableList(plugin, pageIndex, command, sender, headline, jsonHeadline, listService);
    }

    private void displayPageableList(BasePlugin plugin, int pageIndex, String pageExecutionCmd,
                                     CommandSender sender,
                                     String headline,
                                     ArrayList<BedrockJson> jsonHeadline,

                                     AbstractPageableListService listService) {
        int itemsPerPage = listService.getGeneralPageSize();
        int totalPages = listService.getPages();
        List<PageableListStorable> pageableList = listService.getPage(pageIndex);

        if (jsonHeadline != null) {
            JsonMessage jsonMessage = new JsonMessage(plugin,
                    "json.page.design.header",
                    "pageheader", "%pageheader%",
                    "currentpagecount", pageIndex + "",
                    "totalpagecount", totalPages + "");
            getFullJsonHeader(plugin, jsonMessage, jsonHeadline).send(sender);
        } else if (headline != null) {
            new JsonMessage(plugin,
                    "json.page.design.header",
                    "pageheader", headline,
                    "currentpagecount", pageIndex + "",
                    "totalpagecount", totalPages + "").send(sender);
        }

        // Send entries of page
        if (!pageableList.isEmpty()) {
            for (PageableListStorable storable : pageableList) {
                if (storable.get() instanceof JsonMessage) {
                    ((JsonMessage) storable.get()).send(sender);
                }
            }
        }

        // Display Navigation
        ComponentBuilder pagination = getPagination(plugin, pageIndex, totalPages, pageExecutionCmd, itemsPerPage);
        if (pagination != null) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.spigot().sendMessage(pagination.create());
            }
        }
    }


    private ComponentBuilder getPagination(BasePlugin plugin, int page, int totalPages, String pageExecutionCmd, Integer itemsPerPage) {
        if (totalPages == 1) {
            // No Pagination needed
            return null;
        }

        ChatColor secondary = plugin.getColorSchemeService().getColorScheme().getSecondary();
        ChatColor flag = plugin.getColorSchemeService().getColorScheme().getFlag();

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

        if (totalPages <= itemsPerPage) {
            //Display all, no formating logic needed.
            for (int i = 1; i <= totalPages; i++) {
                addPageNumber(i, page, pageExecutionCmd, pagination, secondary, flag, i != totalPages, false);
            }

        } else {
            addCalculatedPagination(page, totalPages, pageExecutionCmd, secondary, flag, pagination);
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

    private static JsonMessage getFullJsonHeader(BasePlugin plugin, JsonMessage jsonMessage, ArrayList<BedrockJson> messages) {

        JSONParser parser = new JSONParser();
        try {
            JSONObject parse = (JSONObject) JSONValue.parse(jsonMessage.getJson());

            int position = -1;

            ArrayList<BedrockJson> extra = (ArrayList<BedrockJson>) parse.get("extra");
            if (extra != null) {
                for (JSONObject json : extra) {
                    if (((String) (json.get("text"))).equalsIgnoreCase("%pageheader%")) {
                        position = extra.indexOf(json);
                        break;
                    }
                }

                if (position != -1) {
                    extra.remove(position);
                    extra.addAll(position, messages);
                    parse.put("extra", extra);
                    return new JsonMessage(plugin, parse);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JsonMessage(plugin, BedrockJson.JsonWithText(""));
    }

    private void addCalculatedPagination(int page, int totalPages, String pageExecutionCmd, ChatColor secondary, ChatColor flag, ComponentBuilder pagination) {

        if (totalPages == 1) {
            // 1 Page, No pagination needed.
            return;
        }

        ArrayList<Integer> pages = new ArrayList<>();
        pages.add(1);
        pages.add(totalPages);

        int maxpages = totalPages >= 7 ? 7 : totalPages;
        int additional = maxpages - 2;

        if (page == 1) {
            // Easy, add 5 following
            for (int i = 1; i <= additional; i++) {
                pages.add(page + i);
            }
        } else if (page == totalPages) {
            // Easy, add 5 previous
            for (int i = 1; i <= additional; i++) {
                pages.add(page - i);
            }
        } else {
            // Current is not first or last, just add it
            pages.add(page);

            // Now check the 4 missing
            int missing1 = 2;
            int missing2 = 2;
            while (pages.size() < maxpages) {
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
            if (!(p == (pages.size() - 1))) {
                if ((addPage + 1) == pages.get(p + 1)) {
                    pipe = true;
                }
            }

            if (p == (pages.size() - 1)) {
                addPageNumber(addPage, page, pageExecutionCmd, pagination, secondary, flag, false, false);
            } else {
                addPageNumber(addPage, page, pageExecutionCmd, pagination, secondary, flag, pipe, !pipe);
            }
        }
    }


    private boolean addPageNumber(int page,
                                  int currentPage,
                                  String pageExecutionCmd,
                                  ComponentBuilder pagination,
                                  ChatColor secondary,
                                  ChatColor flag,
                                  boolean pipeSeparator,
                                  boolean dashSeparator) {
        boolean isCurrent = false;

        pagination.event(
                new ClickEvent(ClickEvent.Action.RUN_COMMAND, pageExecutionCmd.replace("%page%", page + "")));


        if (page == currentPage) {
            pagination.append("[").color(secondary);
        }
        pagination.append(page + "").color(secondary);
        if (page == currentPage) {
            pagination.bold(true);
            pagination.append("]").color(secondary);
            pagination.bold(false);
            pagination.append("").reset();
            isCurrent = true;
        }

        if (pipeSeparator) {
            pagination.append("|").color(flag);
        } else if (dashSeparator) {
            pagination.append("...").color(flag);
        }

        return isCurrent;
    }

    // Getter & Setter

    public BasePlugin getPlugin() {
        return plugin;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setJsonHeadline(ArrayList<BedrockJson> jsonHeadline) {
        this.jsonHeadline = jsonHeadline;
    }

}
