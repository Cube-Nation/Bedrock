package de.cubenation.bedrock.service.pageablelist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class PageableListService {

	private int next										= 10;

	private int page										= 1;

	private List<PageableListStorable> storage				= new ArrayList<PageableListStorable>();

	public PageableListService() {
		this(10);
	}
	
	public PageableListService(int next) {
		this.next = next;
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

	public List<PageableListStorable> next() {
		List<PageableListStorable> list = this.next(this.next, this.page);
		this.page++;
		return list;
	}

	public List<PageableListStorable> next(int next, int page) {
		List<PageableListStorable> list = new ArrayList<PageableListStorable>();

		int start 	= (page - 1) * next;
		int end		= start + next - 1;

		for (int i = start; i <= end; i++) {
			list.add(this.storage.get(i));
		}

		return list;
	}

}