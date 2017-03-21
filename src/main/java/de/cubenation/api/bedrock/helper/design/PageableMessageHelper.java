package de.cubenation.api.bedrock.helper.design;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.service.pageablelist.PageableListStorable;
import de.cubenation.api.bedrock.translation.JsonMessage;
import de.cubenation.api.bedrock.service.pageablelist.AbstractPageableListService;
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
 * Created by BenediktHr on 04.03.16.
 * Project: Bedrock
 */

public class PageableMessageHelper {

    private static final int DEFAULT_NAVIGATIONSIZE = 7;
    public static final String PAGE_PLACEHOLDER = "%page%";

    private final BasePlugin plugin;
    private final AbstractPageableListService listService;
    private final List<JsonMessage> jsonMessageList;
    private final String command;
    private int page;
    private int totalPages;
    private String headline;
    private ArrayList<BedrockJson> jsonHeadline;

    public PageableMessageHelper(BasePlugin plugin, String command, AbstractPageableListService listService) {
        this.plugin = plugin;
        this.command = command;
        this.listService = listService;
        this.jsonMessageList = null;
    }

    public PageableMessageHelper(BasePlugin plugin, String command, List<JsonMessage> jsonMessageList) {
        this.plugin = plugin;
        this.command = command;
        this.listService = null;
        this.jsonMessageList = jsonMessageList;
    }

    public void paginate(CommandSender sender) {
        paginate(sender, DEFAULT_NAVIGATIONSIZE);
    }

    public void paginate(CommandSender sender, Integer itemsPerPage) {
        // Can't show pages
        if (listService == null && jsonMessageList == null) {
            return;
        }

        if (!command.contains("%page%")) {
            plugin.log(Level.INFO, "No placeholder for RunCommand index");
            return;
        }

        List<PageableListStorable> page = null;
        try {
            page = listService.getPage(this.page);
        } catch (Exception ignored) {
        }
        display(plugin, this.page, command, sender, headline, jsonHeadline, page, jsonMessageList, totalPages, itemsPerPage);
    }

    @Deprecated
    public static void pagination(BasePlugin plugin, AbstractPageableListService service, int page, String pageExecutionCmd, CommandSender sender, String headline) {
        pagination(plugin, service, page, pageExecutionCmd, sender, headline, DEFAULT_NAVIGATIONSIZE);
    }

    @Deprecated
    public static void pagination(BasePlugin plugin, AbstractPageableListService service, int page, String pageExecutionCmd, CommandSender sender, String headline, Integer itemsPerPage) {
        // Can't show pages, cause its empty
        if (service.isEmpty()) {
            plugin.log(Level.INFO, "Space PageableListService. Can't display pages.");
            return;
        }

        if (!pageExecutionCmd.contains(PAGE_PLACEHOLDER)) {
            plugin.log(Level.INFO, "No placeholder for RunCommand index");
            return;
        }

        Integer items = service.getItemsPerPage() == null ? itemsPerPage : service.getItemsPerPage();
        try {
            List<PageableListStorable> list = service.getPage(page);
            int totalPages = service.getPages();
            display(plugin, page, pageExecutionCmd, sender, headline, null, list, null, totalPages, items);
        } catch (IndexOutOfBoundsException e) {
            new JsonMessage(plugin, "json.page.notexsist").send(sender);
        }
    }

    @Deprecated
    public static void pagination(BasePlugin plugin, List<JsonMessage> list, int page, int totalPages, String pageExecutionCmd, CommandSender sender, String headline) {
        pagination(plugin, list, page, totalPages, pageExecutionCmd, sender, headline, DEFAULT_NAVIGATIONSIZE);
    }

    @Deprecated
    public static void pagination(BasePlugin plugin, List<JsonMessage> list, int page, int totalPages, String pageExecutionCmd, CommandSender sender, String headline, Integer itemsPerPage) {
        // Can't show pages, cause its empty
        if (list.isEmpty()) {
            plugin.log(Level.INFO, "Space PageableListService. Can't display pages.");
            return;
        }

        if (!pageExecutionCmd.contains("%page%")) {
            plugin.log(Level.INFO, "No placeholder for RunCommand index");
            return;
        }

        display(plugin, page, pageExecutionCmd, sender, headline, null, null, list, totalPages, itemsPerPage);
    }

    private static void display(BasePlugin plugin, int page, String pageExecutionCmd,
                                CommandSender sender,
                                String headline,
                                ArrayList<BedrockJson> jsonHeadline,
                                List<PageableListStorable> pageableList,
                                List<JsonMessage> jsonList,
                                int totalPages, int itemsPerPage) {
        if (headline != null) {
            new JsonMessage(plugin,
                    "json.page.design.header",
                    "pageheader", headline,
                    "currentpagecount", page + "",
                    "totalpagecount", totalPages + "").send(sender);
        } else if (jsonHeadline != null) {
            JsonMessage jsonMessage = new JsonMessage(plugin,
                    "json.page.design.header",
                    "pageheader", "%pageheader%",
                    "currentpagecount", page + "",
                    "totalpagecount", totalPages + "");
            getFullJsonHeader(plugin, jsonMessage, jsonHeadline).send(sender);

        }

        // Send entries of page
        if (pageableList != null && !pageableList.isEmpty()) {
            for (PageableListStorable storable : pageableList) {
                if (storable.get() instanceof JsonMessage) {
                    ((JsonMessage) storable.get()).send(sender);
                }
            }
        } else if (jsonList != null && !jsonList.isEmpty()) {
            for (JsonMessage jsonMessage : jsonList) {
                jsonMessage.send(sender);
            }
        }


        // Display Navigation
        ComponentBuilder pagination = getPagination(plugin, page, totalPages, pageExecutionCmd, itemsPerPage);
        if (pagination != null)

        {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.spigot().sendMessage(pagination.create());
            }
        }

    }


    public static ComponentBuilder getPagination(BasePlugin plugin, int page, int totalPages, String pageExecutionCmd, Integer itemsPerPage) {
        if (totalPages == 1) {
            // No Pagination needed
            return null;
        }

        //ChatColor primary =     plugin.getColorSchemeService().getColorScheme().getPrimary();
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
            addCalculatedPagination(plugin, page, totalPages, pageExecutionCmd, secondary, flag, pagination, itemsPerPage);
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

    public static JsonMessage getFullJsonHeader(BasePlugin plugin, JsonMessage jsonMessage, ArrayList<BedrockJson> messages) {

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

    private static void addCalculatedPagination(BasePlugin plugin, int page, int totalPages, String pageExecutionCmd, ChatColor secondary, ChatColor flag, ComponentBuilder pagination, Integer itemsPerPage) {

        if (totalPages <= itemsPerPage) {
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


    public static boolean addPageNumber(int page,
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

    private static double getSteps(int start, int end) {
        return (end - start) / (DEFAULT_NAVIGATIONSIZE);
    }


    // Getter & Setter


    public BasePlugin getPlugin() {
        return plugin;
    }

    public AbstractPageableListService getListService() {
        return listService;
    }

    public List<JsonMessage> getJsonMessageList() {
        return jsonMessageList;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public ArrayList<BedrockJson> getJsonHeadline() {
        return jsonHeadline;
    }

    public void setJsonHeadline(ArrayList<BedrockJson> jsonHeadline) {
        this.jsonHeadline = jsonHeadline;
    }

    public static class Builder {

        private PageableMessageHelper pageableMessageHelper;

        public Builder(BasePlugin plugin, String command, AbstractPageableListService listService) {
            pageableMessageHelper = new PageableMessageHelper(plugin, command, listService);
        }

        public Builder(BasePlugin plugin, String command, List<JsonMessage> jsonMessageList) {
            pageableMessageHelper = new PageableMessageHelper(plugin, command, jsonMessageList);
        }

        public Builder setPages(int currentPage, int totalPages) {
            pageableMessageHelper.setPage(currentPage);
            pageableMessageHelper.setTotalPages(totalPages);
            return this;
        }

        public Builder setHeadline(String headline) {
            pageableMessageHelper.setHeadline(headline);
            return this;
        }

        public Builder setHeadline(ArrayList<BedrockJson> headline) {
            pageableMessageHelper.setJsonHeadline(headline);
            return this;
        }

        public PageableMessageHelper build() {
            return pageableMessageHelper;
        }

    }
}
