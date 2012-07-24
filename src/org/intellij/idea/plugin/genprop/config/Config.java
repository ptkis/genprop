package org.intellij.idea.plugin.genprop.config;

import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.intellij.idea.plugin.genprop.view.MethodExistsDialog;
import org.jdom.Element;

import java.io.Serializable;

/**
 * Configuration. <p/> The configuration is stored using {@link JDOMExternalizable} that automatically stores the state
 * of this classes public fields.
 *
 * @author Claus Ibsen
 */
public class Config
        implements JDOMExternalizable, Serializable {

    private static final long serialVersionUID = 776189577015485693L;
    private boolean useFullyQualifiedName = false;
    private boolean useFieldChooserDialog = true;
    private boolean useDefaultAlways = false;
    private ConflictResolutionPolicy replaceDialogInitialOption = MethodExistsDialog.getOptions()[0];
    private String methodBody = null;
    private boolean filterConstantField = true;
    private boolean filterTransientModifier = false;
    private boolean filterStaticModifier = true;
    private String filterFieldName = null;
    private String filterMethodName = null;
    private boolean addImplementSerializable = false;
    private boolean autoImports = false;
    private String autoImportsPackages = "java.util.*,java.text.*";
    private boolean disableActionInMenus = false;
    private boolean inspectionOnTheFly = false;
    private boolean enableMethods = false;

    public boolean isUseFullyQualifiedName() {
        return useFullyQualifiedName;
    }

    public void setUseFullyQualifiedName(boolean useFullyQualifiedName) {
        this.useFullyQualifiedName = useFullyQualifiedName;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public String getMethodBodyNoJavaDoc() {
        int i = methodBody.indexOf("*/");
        if (i == -1) {
            return methodBody;
        }

        return methodBody.substring(i + 2);
    }

    public String getFieldBodyJavaDocOnly() {
        int i = methodBody.indexOf("*/");
        if (i == -1) {
            return null;
        }

        return methodBody.substring(0, i + 2);
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }

    public boolean isUseFieldChooserDialog() {
        return useFieldChooserDialog;
    }

    public void setUseFieldChooserDialog(boolean useFieldChooserDialog) {
        this.useFieldChooserDialog = useFieldChooserDialog;
    }

    public boolean isUseDefaultAlways() {
        return useDefaultAlways;
    }

    public void setUseDefaultAlways(boolean useDefaultAlways) {
        this.useDefaultAlways = useDefaultAlways;
    }

    public ConflictResolutionPolicy getReplaceDialogInitialOption() {
        return replaceDialogInitialOption;
    }

    public void setReplaceDialogInitialOption(ConflictResolutionPolicy replaceDialogInitialOption) {
        this.replaceDialogInitialOption = replaceDialogInitialOption;
    }

    public boolean isFilterConstantField() {
        return filterConstantField;
    }

    public void setFilterConstantField(boolean filterConstantField) {
        this.filterConstantField = filterConstantField;
    }

    public boolean isFilterTransientModifier() {
        return filterTransientModifier;
    }

    public void setFilterTransientModifier(boolean filterTransientModifier) {
        this.filterTransientModifier = filterTransientModifier;
    }

    public boolean isFilterStaticModifier() {
        return filterStaticModifier;
    }

    public void setFilterStaticModifier(boolean filterStaticModifier) {
        this.filterStaticModifier = filterStaticModifier;
    }

    public String getFilterFieldName() {
        return filterFieldName;
    }

    public void setFilterFieldName(String filterFieldName) {
        this.filterFieldName = filterFieldName;
    }

    public boolean isAddImplementSerializable() {
        return addImplementSerializable;
    }

    public void setAddImplementSerializable(boolean addImplementSerializable) {
        this.addImplementSerializable = addImplementSerializable;
    }

    public boolean isAutoImports() {
        return autoImports;
    }

    public void setAutoImports(boolean autoImports) {
        this.autoImports = autoImports;
    }

    public String getAutoImportsPackages() {
        return autoImportsPackages;
    }

    public void setAutoImportsPackages(String autoImportsPackages) {
        this.autoImportsPackages = autoImportsPackages;
    }

    public boolean isDisableActionInMenus() {
        return disableActionInMenus;
    }

    public void setDisableActionInMenus(boolean disableActionInMenus) {
        this.disableActionInMenus = disableActionInMenus;
    }

    public boolean isInspectionOnTheFly() {
        return inspectionOnTheFly;
    }

    public void setInspectionOnTheFly(boolean inspectionOnTheFly) {
        this.inspectionOnTheFly = inspectionOnTheFly;
    }

    public boolean isEnableMethods() {
        return enableMethods;
    }

    public void setEnableMethods(boolean enableMethods) {
        this.enableMethods = enableMethods;
    }

    public String getFilterMethodName() {
        return filterMethodName;
    }

    public void setFilterMethodName(String filterMethodName) {
        this.filterMethodName = filterMethodName;
    }

    public void readExternal(Element element)
            throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(this, element);
    }

    public void writeExternal(Element element)
            throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(this, element);
    }

    /**
     * Get's the filter pattern that this configuration represent.
     *
     * @return the filter pattern.
     */
    public FilterPattern getFilterPattern() {
        FilterPattern pattern = new FilterPattern();
        pattern.setConstantField(filterConstantField);
        pattern.setTransientModifier(filterTransientModifier);
        pattern.setStaticModifier(filterStaticModifier);
        pattern.setFieldName(filterFieldName);
        pattern.setMethodName(filterMethodName);
        return pattern;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Config)) {
            return false;
        }

        final Config config = (Config) o;

        if (addImplementSerializable != config.addImplementSerializable) {
            return false;
        }
        if (autoImports != config.autoImports) {
            return false;
        }
        if (disableActionInMenus != config.disableActionInMenus) {
            return false;
        }
        if (enableMethods != config.enableMethods) {
            return false;
        }
        if (filterConstantField != config.filterConstantField) {
            return false;
        }
        if (filterStaticModifier != config.filterStaticModifier) {
            return false;
        }
        if (filterTransientModifier != config.filterTransientModifier) {
            return false;
        }
        if (inspectionOnTheFly != config.inspectionOnTheFly) {
            return false;
        }
        if (useDefaultAlways != config.useDefaultAlways) {
            return false;
        }
        if (useFieldChooserDialog != config.useFieldChooserDialog) {
            return false;
        }
        if (useFullyQualifiedName != config.useFullyQualifiedName) {
            return false;
        }
        if (autoImportsPackages != null ? !autoImportsPackages.equals(config.autoImportsPackages) :
                config.autoImportsPackages != null) {
            return false;
        }
        if (filterFieldName != null ? !filterFieldName.equals(config.filterFieldName) :
                config.filterFieldName != null) {
            return false;
        }
        if (filterMethodName != null ? !filterMethodName.equals(config.filterMethodName) :
                config.filterMethodName != null) {
            return false;
        }
        if (methodBody != null ? !methodBody.equals(config.methodBody) : config.methodBody != null) {
            return false;
        }
        if (!replaceDialogInitialOption.equals(config.replaceDialogInitialOption)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (useFullyQualifiedName ? 1 : 0);
        result = 29 * result + (useFieldChooserDialog ? 1 : 0);
        result = 29 * result + (useDefaultAlways ? 1 : 0);
        result = 29 * result + replaceDialogInitialOption.hashCode();
        result = 29 * result + (methodBody != null ? methodBody.hashCode() : 0);
        result = 29 * result + (filterConstantField ? 1 : 0);
        result = 29 * result + (filterTransientModifier ? 1 : 0);
        result = 29 * result + (filterStaticModifier ? 1 : 0);
        result = 29 * result + (filterFieldName != null ? filterFieldName.hashCode() : 0);
        result = 29 * result + (filterMethodName != null ? filterMethodName.hashCode() : 0);
        result = 29 * result + (addImplementSerializable ? 1 : 0);
        result = 29 * result + (autoImports ? 1 : 0);
        result = 29 * result + (autoImportsPackages != null ? autoImportsPackages.hashCode() : 0);
        result = 29 * result + (disableActionInMenus ? 1 : 0);
        result = 29 * result + (inspectionOnTheFly ? 1 : 0);
        result = 29 * result + (enableMethods ? 1 : 0);
        return result;
    }

    public String toString() {
        return "Config{" +
                "useFullyQualifiedName=" + useFullyQualifiedName +
                ", useFieldChooserDialog=" + useFieldChooserDialog +
                ", useDefaultAlways=" + useDefaultAlways +
                ", replaceDialogInitialOption=" + replaceDialogInitialOption +
                ", methodBody='" + methodBody + '\'' +
                ", filterConstantField=" + filterConstantField +
                ", filterTransientModifier=" + filterTransientModifier +
                ", filterStaticModifier=" + filterStaticModifier +
                ", filterFieldName='" + filterFieldName + '\'' +
                ", filterMethodName='" + filterMethodName + '\'' +
                ", addImplementSerializable=" + addImplementSerializable +
                ", autoImports=" + autoImports +
                ", autoImportsPackages='" + autoImportsPackages + '\'' +
                ", disableActionInMenus=" + disableActionInMenus +
                ", inspectionOnTheFly=" + inspectionOnTheFly +
                ", enableMethods=" + enableMethods +
                '}';
    }

}
