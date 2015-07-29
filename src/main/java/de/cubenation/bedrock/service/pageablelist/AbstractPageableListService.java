package de.cubenation.bedrock.service.pageablelist;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.registry.Registerable;
import de.cubenation.bedrock.service.ServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class AbstractPageableListService implements ServiceInterface, Registerable {

    public static int FIRSTPAGE = 1;

    private int next = 10;

    private List<PageableListStorable> storage;

    public AbstractPageableListService() {
        // TODO: reload nicht "schnell" genug
        // TODO: BedrockPlugin? das nix gut.
        this(BedrockPlugin.getInstance().getPluginConfigService().getConfig().getInt("service.pageablelist.next_amount"));
    }

    public AbstractPageableListService(int next) {
        this.next = next;
        this.init();
    }

    @Override
    public void init() {
        this.storage = new ArrayList<>();
    }

    @Override
    public void reload() {
        this.init();
    }

    public void store(PageableListStorable<?> cs) {
        this.storage.add(cs);
    }

    public void store(Set<PageableListStorable> list) {
        for (PageableListStorable<?> pls : list) {
            this.store(pls);
        }
    }

    public int size() {
        return this.storage.size();
    }

    public List<PageableListStorable> getPage(int page) {
        return this.getPage(this.next, page);
    }

    public int getPages() {
        return (int) Math.round(this.storage.size() / this.next + 0.5);
    }

    public int getPageSize(int page) throws IndexOutOfBoundsException {
        if (page > this.getPages()) {
            throw new IndexOutOfBoundsException("page " + page + " is greater than " + this.getPages());
        } else if (page < this.getPages()) {
            return this.next;
        } else {
            return size() - ((page - 1) * next);
        }
    }

    public List<PageableListStorable> getPage(int next, int page) {
        List<PageableListStorable> list = new ArrayList<>();

        int start = (page - 1) * next;
        int end = start + this.getPageSize(page) - 1;

        for (int i = start; i <= end; i++) {
            list.add(this.storage.get(i));
        }

        return list;
    }

    public PageableListStorable getStorableAtIndex(int i) {
        return storage.get(i);
    }

    public boolean isEmpty() {
        return storage == null || storage.isEmpty();
    }
}