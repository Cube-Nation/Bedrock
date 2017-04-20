package de.cubenation.api.bedrock.service.pageablelist;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.helper.design.PageableMessageHelper;
import de.cubenation.api.bedrock.registry.Registerable;
import de.cubenation.api.bedrock.service.AbstractService;
import de.cubenation.api.bedrock.translation.parts.BedrockJson;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * @author Cube-Nation
 * @version 1.0
 */
@SuppressWarnings("unused")
public abstract class AbstractPageableListService extends AbstractService implements Registerable {

    private Integer generalPageSize = 10;

    private List<PageableListStorable> storage;

    public AbstractPageableListService(BasePlugin plugin) {
        super(plugin);
        this.init();
    }

    public AbstractPageableListService(BasePlugin plugin, int generalPageSize) {
        super(plugin);
        this.init(generalPageSize);
    }

    private void init(int next) {
        this.storage = new ArrayList<>();
        this.generalPageSize = next;
    }

    @Override
    public void init() {
        this.init((Integer) this.getConfigurationValue("service.pageablelist.next_amount", 10));
    }

    @Override
    public void reload() {
        this.getPlugin().log(Level.WARNING, "Reloading of service pageablelist not supported");
    }

    public void store(PageableListStorable<?> cs) {
        this.storage.add(cs);
    }

    public void store(Set<PageableListStorable> list) {
        for (PageableListStorable<?> pls : list) {
            this.store(pls);
        }
    }

    public void paginate(CommandSender sender, String command, String header, int page) {
        PageableMessageHelper pageableMessageHelper = new PageableMessageHelper(getPlugin(), command, this);

        pageableMessageHelper.setHeadline(header);
        pageableMessageHelper.paginate(sender, page);
    }

    public void paginate(CommandSender sender, String command, ArrayList<BedrockJson> header, int page) {
        PageableMessageHelper pageableMessageHelper = new PageableMessageHelper(getPlugin(), command, this);

        pageableMessageHelper.setJsonHeadline(header);
        pageableMessageHelper.paginate(sender, page);
    }

    public int size() {
        return this.storage.size();
    }

    public List<PageableListStorable> getPage(int page) {
        return this.getPage(this.generalPageSize, page);
    }

    public int getPages() {
        double factor = 0.5;
        if (storage.size() % generalPageSize == 0) {
            factor = 0.0;
        }
        return (int) Math.round(storage.size() / generalPageSize + factor);
    }

    @SuppressWarnings("WeakerAccess")
    protected int getPageSize(int page) throws IndexOutOfBoundsException {
        if (page > this.getPages()) {
            throw new IndexOutOfBoundsException("page " + page + " is greater than " + this.getPages());
        } else if (page < this.getPages()) {
            return this.generalPageSize;
        } else {
            return size() - ((page - 1) * generalPageSize);
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected List<PageableListStorable> getPage(int next, int page) {
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

    public List<PageableListStorable> getStorage() {
        return storage;
    }

    public Integer getGeneralPageSize() {
        return generalPageSize;
    }

    public boolean isEmpty() {
        return storage == null || storage.isEmpty();
    }

    @Override
    public String toString() {
        return "AbstractPageableListService{" +
                "generalPageSize=" + generalPageSize +
                "getPages()" + getPages() +
                "size" + size() +
                ", storage=" + storage +
                '}';
    }
}