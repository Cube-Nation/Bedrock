package de.cubenation.bedrock.core.service.colorscheme;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.exception.ServiceInitException;
import de.cubenation.bedrock.core.exception.ServiceReloadException;
import de.cubenation.bedrock.core.service.AbstractService;

/**
 * @author Cube-Nation
 * @version 1.0
 */
public class ColorSchemeService extends AbstractService {

    private ColorScheme scheme;

    public ColorSchemeService(FoundationPlugin plugin) {
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

