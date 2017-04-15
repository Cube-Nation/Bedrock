package de.cubenation.api.bedrock.service.colorscheme;

import de.cubenation.api.bedrock.BasePlugin;
import de.cubenation.api.bedrock.exception.ServiceInitException;
import de.cubenation.api.bedrock.exception.ServiceReloadException;
import de.cubenation.api.bedrock.service.AbstractService;
import de.cubenation.api.bedrock.service.ServiceInterface;

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

    @Override
    public String toString() {
        return "ColorSchemeService{" +
                "scheme=" + scheme +
                '}';
    }

}
