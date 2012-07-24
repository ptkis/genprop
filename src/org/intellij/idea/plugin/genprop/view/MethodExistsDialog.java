package org.intellij.idea.plugin.genprop.view;

import com.intellij.openapi.ui.Messages;
import org.intellij.idea.plugin.genprop.config.CancelPolicy;
import org.intellij.idea.plugin.genprop.config.ConflictResolutionPolicy;
import org.intellij.idea.plugin.genprop.config.DuplicatePolicy;
import org.intellij.idea.plugin.genprop.config.ReplacePolicy;

import javax.swing.*;

/**
 * This is a dialog when the <code>toString()</code> method already exists.
 * <p/>
 * The user now has the choices to either:
 * <ul>
 * <li/>Replace existing method
 * <li/>Create a duplicate method
 * <li/>Cancel
 * </ul>
 *
 * @author Claus Ibsen
 */
public class MethodExistsDialog {

    /**
     * Options for this dialog
     */
    private static ConflictResolutionPolicy[] options = {ReplacePolicy.getInstance(),
            DuplicatePolicy.getInstance(),
            CancelPolicy.getInstance()};

    /**
     * Get's the options for this dialog.
     *
     * @return the options.
     */
    public static ConflictResolutionPolicy[] getOptions() {
        return options;
    }

    /**
     * Shows this dialog.
     * <p/>
     * The user now has the choices to either:
     * <ul>
     * <li/>Replace existing method
     * <li/>Create a duplicate method
     * <li/>Cancel
     * </ul>
     *
     * @param parentComponent determines the Frame in which the dialog is displayed; if null, or if the parentComponent has no Frame, a default Frame is used.
     * @param initialOption   initial option selected.
     * @return conflict resolution policy (never null)
     */
    public static ConflictResolutionPolicy showDialog(JComponent parentComponent, ConflictResolutionPolicy initialOption) {
        int exit = JOptionPane.showOptionDialog(parentComponent,
                "One or more properties are already exist", "Properties already exist",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                Messages.getQuestionIcon(), options, initialOption);
        if (exit == JOptionPane.CLOSED_OPTION || exit == JOptionPane.CANCEL_OPTION) {
            return CancelPolicy.getInstance();
        } else {
            return options[exit];
        }
    }


}
