package de.cubenation.bedrock.service.pageablelist;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.registry.Registerable;
import de.cubenation.bedrock.service.ServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PageableListService implements ServiceInterface, Registerable {

    private int next = 10;

    private List<PageableListStorable> storage;

    @SuppressWarnings("unused")
    public PageableListService() {
        this(BedrockPlugin.getInstance().getConfig().getInt("service.pageablelist.next_amount"));
    }

    public PageableListService(int next) {
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

    @SuppressWarnings("unused")
    public void store(Set<PageableListStorable> list) {
        for (PageableListStorable<?> pls : list) {
            this.store(pls);
        }
    }

    @SuppressWarnings("unused")
    public int size() {
        return this.storage.size();
    }

    @SuppressWarnings("unused")
    public List<PageableListStorable> getPage(int page) {
        return this.getPage(this.next, page);
    }

    @SuppressWarnings("unused")
    public int getPages() {
        return (int) Math.round(this.storage.size() / this.next + 0.5);
    }

    public int getPageSize(int page) throws IndexOutOfBoundsException {
        if (page == 0)
            page = 1;

        if (page > this.getPages())
            throw new IndexOutOfBoundsException("page " + page + " is greater than " + this.getPages());

        if (page < this.getPages())
            return this.next;

        return this.size() % ((page - 1) * this.next);
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