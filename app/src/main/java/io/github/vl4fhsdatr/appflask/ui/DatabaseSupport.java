package io.github.vl4fhsdatr.appflask.ui;

import io.github.vl4fhsdatr.appflask.database.AppDatabase;

/**
 * Retrieve Database reference
 */
public interface DatabaseSupport {

    AppDatabase getDatabase();

}
