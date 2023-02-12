package de.cubenation.bedrock.core.injection;

import de.cubenation.bedrock.core.FoundationPlugin;
import de.cubenation.bedrock.core.annotation.injection.Name;
import de.cubenation.bedrock.core.datastore.Datastore;
import de.cubenation.bedrock.core.service.datastore.DatastoreService;

import java.lang.reflect.Field;

public class DatastoreSupplier extends InstanceSupplier<Datastore<?>> {

    public DatastoreSupplier(FoundationPlugin plugin) {
        super(plugin);
    }

    @Override
    public Datastore<?> getInstance(Field field) {
        Name datastoreIdentifierAnnotation = field.getAnnotation(Name.class);
        if (datastoreIdentifierAnnotation == null) {
            throw new IllegalArgumentException(
                    String.format("No database name specified for injected database %s in %s. Using bedrock database as a fallback.",
                            field.getName(),
                            field.getDeclaringClass().getName()
                    )
            );
        }

        DatastoreService service = (DatastoreService) plugin.getServiceManager().getService(DatastoreService.class);
        return service.getDatastore(datastoreIdentifierAnnotation.value());
    }
}
