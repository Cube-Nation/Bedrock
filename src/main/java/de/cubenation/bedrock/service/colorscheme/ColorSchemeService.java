package de.cubenation.bedrock.service.colorscheme;

import de.cubenation.bedrock.BasePlugin;
import de.cubenation.bedrock.exception.ServiceInitException;
import de.cubenation.bedrock.exception.ServiceReloadException;
import de.cubenation.bedrock.service.AbstractService;
import de.cubenation.bedrock.service.ServiceInterface;

public class ColorSchemeService extends AbstractService implements ServiceInterface {

    private ColorScheme scheme;

    public ColorSchemeService(BasePlugin plugin) {
        super(plugin);
    }

    @Override
    public void init() throws ServiceInitException {
        this.setColorScheme(null);
    }

    @Override
    public void reload() throws ServiceReloadException {
        try {
            this.init();
        } catch (ServiceInitException e) {
            throw new ServiceReloadException(e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void setColorScheme(ColorScheme.ColorSchemeName name) {
        this.scheme = (name != null)
                ? ColorScheme.getColorScheme(this.getPlugin(), name)
                : ColorScheme.getColorScheme(this.getPlugin(), this.getPlugin().getConfigService().getReadOnlyConfig().getString("service.colorscheme.name"));
    }

    public ColorScheme getColorScheme() {
        return this.scheme;
    }

}
