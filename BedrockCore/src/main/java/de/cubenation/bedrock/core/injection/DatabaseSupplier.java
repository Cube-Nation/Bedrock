package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Name;
import de.cubenation.bedrock.core.config.CustomConfigurationFile;
import de.cubenation.bedrock.core.database.Database;
import de.cubenation.bedrock.core.service.config.ConfigService;
import de.cubenation.bedrock.core.service.database.DatabaseService;
import org.hibernate.Session;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class DatabaseSupplier extends InstanceSupplier<Database> {

    public DatabaseSupplier(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Database getInstance(Field field) {
        Name dbIdentifierAnnotation = field.getAnnotation(Name.class);
        String dbIdentifier;
        if (dbIdentifierAnnotation == null) {
            plugin.log(Level.FINE, "No database name specified for injected database %s in %s. Using bedrock database as a fallback.");
            dbIdentifier = "bedrock";
        } else {
            dbIdentifier = dbIdentifierAnnotation.value();
        }

        DatabaseService service = (DatabaseService) plugin.getServiceManager().getService(DatabaseService.class);
        return service.getDatabase(dbIdentifier);
    }
}
