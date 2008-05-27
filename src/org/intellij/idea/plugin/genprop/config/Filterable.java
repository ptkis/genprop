package org.intellij.idea.plugin.genprop.config;

/**
 * Gives the ability to perform a matching agaist a class field to be used in a filtering process for unwanted fields.
 *
 * @author Claus Ibsen
 */
public interface Filterable {

    /**
     * Performs the filter process and returns true if the field matches the filtering patterns.
     *
     * @param pattern filter patterns.
     * @return true if the field matches the patterns.
     */
    boolean applyFilter(FilterPattern pattern);

}