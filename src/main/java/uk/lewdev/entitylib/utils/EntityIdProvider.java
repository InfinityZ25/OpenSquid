package uk.lewdev.entitylib.utils;

public interface EntityIdProvider {

    int nextAndIncrement();

    int getCurrentId();
}
