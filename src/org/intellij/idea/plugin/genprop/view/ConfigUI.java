package org.intellij.idea.plugin.genprop.view;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.HyperlinkLabel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.intellij.idea.plugin.genprop.config.Config;
import org.intellij.idea.plugin.genprop.config.ConflictResolutionPolicy;
import org.intellij.idea.plugin.genprop.exception.PluginException;
import org.intellij.idea.plugin.genprop.exception.TemplateResourceException;
import org.intellij.idea.plugin.genprop.template.TemplateResource;
import org.intellij.idea.plugin.genprop.template.TemplateResourceLocator;
import org.intellij.idea.plugin.genprop.util.FileUtil;
import org.intellij.idea.plugin.genprop.util.StringUtil;
import org.intellij.idea.plugin.genprop.velocity.VelocityFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Vector;

/**
 * Configuration User Interface. </p> The configuration is in the menu <b>File - Settings - GenerateProperty</b>
 *
 * @author Claus Ibsen
 */
public class ConfigUI
        extends JPanel {

    private static final String DEFAULT_TEMPLATE_FILENAME_EXTENSION = ".vm";
    private static final TemplateResource activeTemplate = new TemplateResource(
            "--> Active Template <--",
            TemplateResourceLocator.getDefaultTemplateBody());

    private JCheckBox fullyQualifiedName = new JCheckBox("Use fully qualified classname ($classname)");
    private JCheckBox fieldChooseDialog = new JCheckBox("Use field chooser dialog");
    private JCheckBox useDefaultConflict = new JCheckBox(
            "Always use default conflict resolution policy (never show dialog)");
    private JCheckBox disableActionInMenus = new JCheckBox("Disable action in code and editor popup menus");
    private JCheckBox enableInspectionOnTheFly = new JCheckBox("Enable on-the-fly code inspection");
    private JCheckBox enableMethods = new JCheckBox("Enable getters in code generation ($methods)");

    private JRadioButton[] initialValueForReplaceDialog;

    private JCheckBox filterConstant = new JCheckBox("For constant fields");
    private JCheckBox filterStatic = new JCheckBox("For static fields");
    private JCheckBox filterTransient = new JCheckBox("For transient fields");
    private JTextField filterFieldName = new JTextField();
    private JTextField filterMethodName = new JTextField();

    private JCheckBox autoAddImplementsSerializable = new JCheckBox("Automatic add implements java.io.Serializable");
    private JCheckBox autoImport = new JCheckBox("Automatic import packages");
    private JTextField autoImportPackages = new JTextField();

    private JComboBox templates;
    private JButton activateNewTemplate = new JButton("Activate this template");
    private JButton saveTemplate = new JButton("Save template");
    private JButton syntaxCheck = new JButton("Syntax check");

    private JTextArea methodBody = new JTextArea();
    private TitledBorder templateBodyBorder;
    private JScrollPane templateBodyScrollPane;
    private static final String templateBodyBorderTitle = "Method body - Velocity Macro Language - ";

    private HyperlinkLabel hyperlink = new HyperlinkLabel("GenerateProperty homepage");


    /**
     * Constructor.
     *
     * @param config Configuration for this UI to display.
     */
    public ConfigUI(Config config) {
        init();
        setConfig(config);
    }

    /**
     * Initializes the GUI. <p/> Creating all the swing controls, panels etc.
     */
    private void init() {

        // Init of componenets
        methodBody.setRows(28);
        methodBody.setTabSize(3);
        filterFieldName.setPreferredSize(new Dimension(100, 18));

        Vector vector = new Vector();
        vector.add(activeTemplate);
        vector.addAll(Arrays.asList(TemplateResourceLocator.getAllTemplates()));
        templates = new JComboBox(vector);
        templates.addActionListener(new OnSelectTemplate());
        templates.setMaximumRowCount(20);

        ConflictResolutionPolicy[] options = MethodExistsDialog.getOptions();
        initialValueForReplaceDialog = new JRadioButton[options.length];
        ButtonGroup selection = new ButtonGroup();
        for (int i = 0; i < options.length; i++) {
            initialValueForReplaceDialog[i] = new JRadioButton(new OptionAction(options[i]));
            selection.add(initialValueForReplaceDialog[i]);
        }

        // UI Layout
        setLayout(new GridBagLayout());
        GridBagConstraints constraint = new GridBagConstraints();
        Border etched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);

        // UI Layout - Settigns
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(etched, "Settings"));
        panel.add(fullyQualifiedName);
        panel.add(fieldChooseDialog);
        panel.add(useDefaultConflict);
        panel.add(disableActionInMenus);
        panel.add(enableInspectionOnTheFly);
        panel.add(enableMethods);
        constraint.gridx = 0;
        constraint.gridy = 0;
        add(panel, constraint);

        // UI Layout - Conflict Resolution Policy
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(etched, "Conflict resolution policy"));
        for (int i = 0; i < initialValueForReplaceDialog.length; i++) {
            panel.add(initialValueForReplaceDialog[i]);
        }
        constraint.gridx = 1;
        add(panel, constraint);

        // UI Layout - Exclude fields
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(etched, "Exclude"));
        Container innerPanel = Box.createHorizontalBox();
        innerPanel.add(filterConstant);
        innerPanel.add(Box.createHorizontalGlue());
        panel.add(innerPanel);
        innerPanel = Box.createHorizontalBox();
        innerPanel.add(filterStatic);
        innerPanel.add(Box.createHorizontalGlue());
        panel.add(innerPanel);
        innerPanel = Box.createHorizontalBox();
        innerPanel.add(filterTransient);
        innerPanel.add(Box.createHorizontalGlue());
        panel.add(innerPanel);
        innerPanel = Box.createHorizontalBox();
        innerPanel.add(new JLabel("By fieldname (regexp)"));
        innerPanel.add(Box.createHorizontalStrut(3));
        innerPanel.add(filterFieldName);
        filterFieldName.setMinimumSize(new Dimension(100, 20)); // avoid input field to small
        panel.add(innerPanel);
        innerPanel = Box.createHorizontalBox();
        innerPanel.add(new JLabel("By methodname (regexp)"));
        innerPanel.add(Box.createHorizontalStrut(3));
        innerPanel.add(filterMethodName);
        filterMethodName.setMinimumSize(new Dimension(100, 20)); // avoid input field to small
        panel.add(innerPanel);
        constraint.gridx = 2;
        add(panel, constraint);

        // UI Layout - Automatic perform
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(etched, "Automatic"));
        innerPanel = Box.createHorizontalBox();
        innerPanel.add(autoAddImplementsSerializable);
        innerPanel.add(Box.createHorizontalGlue());
        panel.add(innerPanel);
        innerPanel = Box.createHorizontalBox();
        innerPanel.add(autoImport);
        autoImport.addActionListener(new OnSelectAutoImport());
        innerPanel.add(Box.createHorizontalStrut(3));
        innerPanel.add(autoImportPackages);
        panel.add(innerPanel);
        innerPanel = Box.createHorizontalBox();
        panel.add(innerPanel);

        constraint.gridx = 0;
        constraint.gridy++;
        constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.BOTH;
        add(panel, constraint);

        // UI Layout - Templates list

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(etched, "Templates"));
        panel.add(templates);
        panel.add(activateNewTemplate);
        activateNewTemplate.setEnabled(false); // should only be enabled if user selects a new template
        activateNewTemplate.addActionListener(new OnActivateNewTemplate());
        panel.add(saveTemplate);
        saveTemplate.addActionListener(new OnSaveTemplate());
        panel.add(syntaxCheck);
        syntaxCheck.addActionListener(new OnSyntaxCheck());
        constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.gridx = 0;
        constraint.gridy++;
        add(panel, constraint);

        // UI Layout - Veloctiy Body
        templateBodyScrollPane = new JScrollPane(methodBody);
        methodBody.addCaretListener(new OnCaretMoved());
        methodBody.setTabSize(1);
        templateBodyBorder = BorderFactory.createTitledBorder(etched, templateBodyBorderTitle);
        templateBodyScrollPane.setBorder(templateBodyBorder);
        templateBodyScrollPane.setMinimumSize(new Dimension(400, 300));
        constraint.gridx = 0;
        constraint.gridwidth = GridBagConstraints.REMAINDER;
        constraint.fill = GridBagConstraints.BOTH;
        constraint.gridy++;
        add(templateBodyScrollPane, constraint);

        add(hyperlink);
    }

    /**
     * Set's the GUI's controls to represent the given configuration.
     *
     * @param config configuration parameters.
     */
    public final void setConfig(Config config) {
        fullyQualifiedName.setSelected(config.isUseFullyQualifiedName());
        fieldChooseDialog.setSelected(config.isUseFieldChooserDialog());
        useDefaultConflict.setSelected(config.isUseDefaultAlways());
        disableActionInMenus.setSelected(config.isDisableActionInMenus());
        ConflictResolutionPolicy option = config.getReplaceDialogInitialOption();
        for (int i = 0; i < initialValueForReplaceDialog.length; i++) {
            if (initialValueForReplaceDialog[i].getText().equals(option.toString())) {
                initialValueForReplaceDialog[i].setSelected(true);
            }
        }

        // if no method body then use default
        if (StringUtil.isEmpty(config.getMethodBody())) {
            methodBody.setText(TemplateResourceLocator.getDefaultTemplateBody());
            activeTemplate.setTemplate(methodBody.getText());
        } else {
            methodBody.setText(config.getMethodBody());
            activeTemplate.setTemplate(methodBody.getText());
        }
        methodBody.setCaretPosition(0); // position 0 to keep the first text visible

        filterConstant.setSelected(config.isFilterConstantField());
        filterStatic.setSelected(config.isFilterStaticModifier());
        filterTransient.setSelected(config.isFilterTransientModifier());
        filterFieldName.setText(config.getFilterFieldName());
        filterMethodName.setText(config.getFilterMethodName());

        autoAddImplementsSerializable.setSelected(config.isAddImplementSerializable());
        autoImport.setSelected(config.isAutoImports());
        autoImportPackages.setText(config.getAutoImportsPackages());
        autoImportPackages.setEnabled(autoImport.isSelected());
        enableInspectionOnTheFly.setSelected(config.isInspectionOnTheFly());
        enableMethods.setSelected(config.isEnableMethods());
    }

    /**
     * Get's the configuration that the GUI controls represent right now.
     *
     * @return the configuration.
     */
    public final Config getConfig() {
        Config config = new Config();

        config.setUseFullyQualifiedName(fullyQualifiedName.isSelected());
        config.setUseFieldChooserDialog(fieldChooseDialog.isSelected());
        config.setUseDefaultAlways(useDefaultConflict.isSelected());
        config.setDisableActionInMenus(disableActionInMenus.isSelected());
        for (int i = 0; i < initialValueForReplaceDialog.length; i++) {
            if (initialValueForReplaceDialog[i].isSelected()) {
                config.setReplaceDialogInitialOption(((OptionAction) initialValueForReplaceDialog[i].getAction()).option);
            }
        }
        // only set text if selected template is on the active template (index 0)
        if (templates.getSelectedIndex() == 0) {
            config.setMethodBody(methodBody.getText());
        }
        config.setFilterConstantField(filterConstant.isSelected());
        config.setFilterTransientModifier(filterTransient.isSelected());
        config.setFilterStaticModifier(filterStatic.isSelected());
        config.setFilterFieldName(filterFieldName.getText());
        config.setFilterMethodName(filterMethodName.getText());

        config.setAddImplementSerializable(autoAddImplementsSerializable.isSelected());
        config.setAutoImportsPackages(autoImportPackages.getText());
        config.setAutoImports(autoImport.isSelected());
        config.setInspectionOnTheFly(enableInspectionOnTheFly.isSelected());
        config.setEnableMethods(enableMethods.isSelected());

        return config;
    }

    /**
     * Action for the options for the conflict resolution policy
     */
    private class OptionAction
            extends AbstractAction {
        public final ConflictResolutionPolicy option;

        OptionAction(ConflictResolutionPolicy option) {
            super(option.toString());
            this.option = option;
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    /**
     * Action listener for user selecting a new template in the combobox
     */
    private class OnSelectTemplate
            implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JComboBox cb = (JComboBox) e.getSource();
            if (cb.getSelectedIndex() == 0) {
                activateNewTemplate.setEnabled(false);
                saveTemplate.setEnabled(true);
                syntaxCheck.setEnabled(true);
                methodBody.setEditable(true);
                methodBody.setEnabled(true);
            } else {
                activateNewTemplate.setEnabled(true);
                saveTemplate.setEnabled(false);
                syntaxCheck.setEnabled(false);
                methodBody.setEditable(false);
                methodBody.setEnabled(false);
            }

            TemplateResource selected = (TemplateResource) cb.getSelectedItem();
            methodBody.setText(selected.getTemplate());
            methodBody.setCaretPosition(0); // position 0 to keep the first text visible
        }
    }

    /**
     * Action listener for user activating a new template
     */
    private class OnActivateNewTemplate
            implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // confirm dialog
            int exit = JOptionPane.showOptionDialog(
                    ConfigUI.this,
                    "Set this template as the active template?", "Activate new template",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                    Messages.getQuestionIcon(), null, null);

            if (exit == JOptionPane.YES_OPTION) {
                TemplateResource selected = (TemplateResource) templates.getSelectedItem();
                activeTemplate.setTemplate(selected.getTemplate());
                methodBody.setText(selected.getTemplate()); // update method body with new body
                templates.setSelectedIndex(0); // set index to active template
            }
        }
    }

    /**
     * Action listener for user saving the template
     */
    private class OnSaveTemplate
            implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            // create template plugin-folder if missing
            TemplateResourceLocator.createTemplateFolderIfMissing();

            // setup save dialog
            JFileChooser chooser = new JFileChooser(TemplateResourceLocator.getTemplateFolder());
            chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            chooser.setApproveButtonToolTipText("Save Template");
            chooser.setMultiSelectionEnabled(false);
            chooser.setDialogTitle("Save Template");
            chooser.setDragEnabled(false);

            // show dialog and save file if user click 'approve'
            int choiceSave = chooser.showSaveDialog(ConfigUI.this);
            if (choiceSave == JFileChooser.APPROVE_OPTION) {
                try {
                    // fix extension of file - append .vm if missing
                    String filename = chooser.getSelectedFile().getPath();
                    if (FileUtil.getFileExtension(filename) == null) {
                        filename += DEFAULT_TEMPLATE_FILENAME_EXTENSION;
                    }

                    // the template resource
                    TemplateResource res = new TemplateResource(FileUtil.stripFilename(filename), methodBody.getText());

                    // confirm overwrite dialog?
                    boolean existsTemplate = false;
                    int choiceOverwrite = JOptionPane.OK_OPTION; // preset choice to OK for saving file if file does not already exists
                    if (FileUtil.existsFile(filename)) {
                        existsTemplate = true;
                        choiceOverwrite = JOptionPane.showConfirmDialog(
                                ConfigUI.this,
                                "A template already exists with the filename. Overwrite existing template?",
                                "Template exists", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE, Messages.getQuestionIcon());
                    }

                    // save file if user clicked okay, or file does not eixsts
                    if (choiceOverwrite == JOptionPane.OK_OPTION) {

                        // save the file
                        FileUtil.saveFile(filename, res.getTemplate());

                        // if file does not already exists add it to the template combobox so it is updated
                        if (!existsTemplate) {
                            templates.addItem(res);
                        }
                    }

                } catch (IOException e) {
                    throw new TemplateResourceException("Error saving template", e);
                }
            }
        }
    }

    /**
     * Action listener for user selecting/deselecting auto import
     */
    private class OnSelectAutoImport
            implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JCheckBox cb = (JCheckBox) event.getSource();
            if (cb.isSelected()) {
                autoImportPackages.setEnabled(true);
            } else {
                autoImportPackages.setEnabled(false);
            }
        }
    }

    /**
     * Action listener for user clicking syntax check
     */
    private class OnSyntaxCheck
            implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                StringWriter sw = new StringWriter();
                VelocityContext vc = new VelocityContext();

                // velocity
                VelocityEngine velocity = VelocityFactory.newVeloictyEngine();
                velocity.evaluate(
                        vc,
                        sw,
                        "org.intellij.idea.plugin.genprop.view.ConfigUI$OnSyntaxCheck",
                        methodBody.getText());

                // no errors
                Messages.showMessageDialog(
                        "Syntax check complete - no errors found",
                        "Info",
                        Messages.getInformationIcon());

            } catch (ParseErrorException e) {
                // Syntax Error - display to user
                Messages.showMessageDialog("Syntax Error:\n" + e.getMessage(), "Warning", Messages.getWarningIcon());
            } catch (Exception e) {
                throw new PluginException("Error syntax checking template", e);
            }
        }
    }

    /**
     * Action listener for user moving caret in method body text area
     */
    private class OnCaretMoved
            implements CaretListener {
        public void caretUpdate(CaretEvent event) {
            try {
                int dot = event.getDot(); // dot is the index of the text where the caret is positioned

                // calculate current line and column in text area
                int line = methodBody.getLineOfOffset(dot);
                int col = dot - methodBody.getLineStartOffset(line);

                // (++) because line and column should start with from 1 as Velocty Syntax checker expects
                templateBodyBorder.setTitle(templateBodyBorderTitle + "(line " + ++line + ", column " + ++col + ")");
                templateBodyScrollPane.repaint(); // must repaint to update the border title

            } catch (BadLocationException e) {
                e.printStackTrace(); // must print stacktrace to see caused in IDEA log / console
                throw new RuntimeException(e);
            }
        }
    }

/*
    public static void main(String[] args) {
        // Tester for GUI
        JFrame test = new JFrame("Config test");
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel comp = new JPanel();
        comp.add(new ConfigUI(new Config()));
        test.getContentPane().add(comp);
        test.pack();
        test.show();
    }
*/

}
