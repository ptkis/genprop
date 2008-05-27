package org.intellij.idea.plugin.genprop;

import com.intellij.codeInspection.InspectionToolProvider;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.config.Config;
import org.intellij.idea.plugin.genprop.inspection.PropertyHasNoNameConstantInspection;
import org.intellij.idea.plugin.genprop.template.TemplateResourceLocator;
import org.intellij.idea.plugin.genprop.view.ConfigUI;

import javax.swing.*;

/**
 * The IDEA component for this plugin.
 *
 * @author Claus Ibsen
 */
public class GeneratePropertyNamePlugin implements ApplicationComponent, Configurable, JDOMExternalizable, InspectionToolProvider {

    private static Logger log = Logger.getLogger(GeneratePropertyNamePlugin.class);
    private ConfigUI configUI;
    public Config config = new Config();

    public String getComponentName() {
        return "GeneratePropertyNames";
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    public String getDisplayName() {
        return "GeneratePropertyNames";
    }

    public Icon getIcon() {
        java.net.URL resource = getClass().getResource("/resources/configurableToStringPlugin.png");
        if (resource != null) {
            return new ImageIcon(resource);
        }
        return null;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        return configUI = new ConfigUI(config);
    }

    public boolean isModified() {
        return ! config.equals(configUI.getConfig());
    }

    public void apply() throws ConfigurationException {
        config = configUI.getConfig();
        GeneratePropertyNameContext.setConfig(config); // update context

        // update menus accoring the settings
        if (config.isDisableActionInMenus())
            GeneratePropertyNameAction.disableActionInMenus();
        else
            GeneratePropertyNameAction.enableActionsInMenus();

        if (log.isDebugEnabled()) log.debug("Config updated:\n" + config);
    }

    public void reset() {
        configUI.setConfig(config);
    }

    public void disposeUIResources() {
        configUI = null;
    }

    public Config getConfig() {
        return config;
    }

    public void readExternal(org.jdom.Element element) throws InvalidDataException {
        config.readExternal(element);

        // autosave current template as a kind of backup file
        if (config.getMethodBody() != null)
            TemplateResourceLocator.autosaveActiveTemplate(config.getMethodBody());

        // set config on context
        GeneratePropertyNameContext.setConfig(config);

        if (log.isDebugEnabled()) log.debug("Config loaded at startup:\n" + config);
    }

    public void writeExternal(org.jdom.Element element) throws WriteExternalException {
        config.writeExternal(element);

        // autosave current template as a kind of backup file
        TemplateResourceLocator.autosaveActiveTemplate(config.getMethodBody());
    }                                    

    public Class[] getInspectionClasses() {
        // register our inspection classes
        return new Class[] { PropertyHasNoNameConstantInspection.class,
		};
    }


}