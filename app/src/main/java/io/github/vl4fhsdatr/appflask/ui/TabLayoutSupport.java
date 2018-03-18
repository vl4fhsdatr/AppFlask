package io.github.vl4fhsdatr.appflask.ui;

/**
 *
 * TabLayout support for ActionBar.
 *
 * ActionBar has a default elevation(shadow). It is necessary to set ActionBar#elevation to 0 if you
 * want to add a TabLayout below ActionBar.
 *
 * references: https://stackoverflow.com/questions/34675791/how-to-add-bottom-shadow-to-tab-layout
 *
 */
public interface TabLayoutSupport {

    /**
     * Notify entering a fragment with TabLayout.
     */
    void enterTabLayout();

    /**
     * Notify exiting a fragment with TabLayout.
     */
    void exitTabLayout();

}
