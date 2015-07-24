package de.cubenation.bedrock.service.pageablelist;

import de.cubenation.bedrock.BedrockPlugin;
import de.cubenation.bedrock.registry.Registerable;
import de.cubenation.bedrock.service.ServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PageableListService implements ServiceInterface, Registerable {

	private int next                                        = 10;

	private int page										= 1;

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
	public List<PageableListStorable> next() {
		List<PageableListStorable> list = this.next(this.next, this.page);
		this.page++;
		return list;
	}

    @SuppressWarnings("unused")
    public List<PageableListStorable> next(int page) {
        return this.next(this.next, page);
    }

	public List<PageableListStorable> next(int next, int page) {
		List<PageableListStorable> list = new ArrayList<>();

		int start 	= (page - 1) * next;
		int end		= start + next - 1;

		for (int i = start; i <= end; i++) {
			list.add(this.storage.get(i));
		}

		return list;
	}

}