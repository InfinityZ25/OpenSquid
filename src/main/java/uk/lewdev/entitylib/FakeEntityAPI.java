package uk.lewdev.entitylib;

import uk.lewdev.entitylib.utils.EntityIdProvider;

public interface FakeEntityAPI {

    static FakeEntityAPI get() {
        return FakeEntityPlugin.instance;
    }

    EntityIdProvider getEntityIdProvider();
}
