package org.intellij.idea.plugin.genprop;

import com.intellij.jam.view.ui.SelectElementsDialog;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.util.IncorrectOperationException;
import org.apache.log4j.Logger;
import org.intellij.idea.plugin.genprop.config.Config;
import org.intellij.idea.plugin.genprop.config.ConflictResolutionPolicy;
import org.intellij.idea.plugin.genprop.config.DuplicatePolicy;
import org.intellij.idea.plugin.genprop.config.FilterPattern;
import org.intellij.idea.plugin.genprop.element.ElementFactory;
import org.intellij.idea.plugin.genprop.element.FieldElement;
import org.intellij.idea.plugin.genprop.element.MethodElement;
import org.intellij.idea.plugin.genprop.exception.GenerateCodeException;
import org.intellij.idea.plugin.genprop.exception.PluginException;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.intellij.idea.plugin.genprop.util.StringUtil;
import org.intellij.idea.plugin.genprop.view.MethodExistsDialog;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;

/**
 * The action-handler that does the code generation.
 *
 * @author Claus Ibsen
 * @since 2.13
 */
public class GeneratePropertyNameActionHandler
        extends EditorWriteActionHandler {

    private static final Logger log = Logger.getLogger(GeneratePropertyNameActionHandler.class);
    private static final PsiMethod[] EMPTY_PSI_METHODS = new PsiMethod[0];
    private Editor editor;
    private Project project;
    private PsiAdapter psi;
    private PsiManager manager;
    private PsiElementFactory elementFactory;
    private CodeStyleManager codeStyleManager;
    private Config config;
    private PsiJavaFile javaFile;

    public GeneratePropertyNameActionHandler() {
        psi = GeneratePropertyNameContext.getPsi();
    }

    /**
     * The action that does the actual generation of the code.
     *
     * @param editor1     the current editor.
     * @param dataContext the current data context.
     */
    @Override
    public void executeWriteAction(Editor editor1, DataContext dataContext) {
        log.debug("executeWriteAction - START");
        editor = editor1;

        project = editor1.getProject();
        if (project == null) {
            return; // silently ignore since no project is opened.
        }

        manager = psi.getPsiManager(project);
        elementFactory = psi.getPsiElemetFactory(manager);
        codeStyleManager = psi.getCodeStyleManager(project);

        javaFile = psi.getSelectedJavaFile(project, manager);
        if (javaFile == null) {
            return; // silently ignore since it's not a javafile.
        }

        final PsiClass clazz = psi.getCurrentClass(javaFile, editor1);
        if (clazz == null) {
            return; // silently ignore since current selected class in editor not found.
        }

        executeAction(project, clazz);
        log.debug("executeWriteAction - END");
    }

    /**
     * The action that does the actual generation of the code.
     *
     * @param project1 the current project.
     * @since 2.20
     */
    public void executeAction(Project project1, final PsiClass clazz) {
        log.debug("executeAction - START");
        if (project1 == null || clazz == null) {
            return; // silently ignore since no project is opened or clazz not provided.
        }

        project = project1;
        manager = psi.getPsiManager(project1);
        elementFactory = psi.getPsiElemetFactory(manager);
        codeStyleManager = psi.getCodeStyleManager(project1);

        javaFile = psi.getSelectedJavaFile(project1, manager);
        if (javaFile == null) {
            return; // silently ignore since it's not a javafile.
        }

        // get editor
        Editor[] editors = EditorFactory.getInstance().getAllEditors();
        for (Editor ed : editors) {
            if (project1 == ed.getProject()) {
                editor = ed;
                break;
            }
        }
        if (editor == null) {
            return; // silently ignore since we could not find our editor
        }

        // keep context updated
        GeneratePropertyNameContext.setProject(project1);
        GeneratePropertyNameContext.setManager(manager);
        GeneratePropertyNameContext.setElementFactory(elementFactory);
        config = GeneratePropertyNameContext.getConfig(); // use latest config

        try {
            PsiField[] filteredFields = filterAvailableFields(clazz, config.getFilterPattern());
            if (log.isDebugEnabled()) {
                log.debug("Number of fields after filtering: " + filteredFields.length);
            }

            PsiMethod[] filteredMethods = EMPTY_PSI_METHODS;
            if (config.enableMethods) {
                // filter methods as it is enabled from config
                filteredMethods = filterAvailableMethods(clazz, config.getFilterPattern());
                if (log.isDebugEnabled()) {
                    log.debug("Number of methods after filtering: " + filteredMethods.length);
                }
            }

            if (displayMememberChooser(filteredFields.length, filteredMethods.length)) {
                log.debug("Displaying member chooser dialog");
                PsiMember[] dialogMembers = combineToMemberList(filteredFields, filteredMethods);
                List<PsiElement> psiElements = new ArrayList<PsiElement>();
                psiElements.addAll(Arrays.asList(dialogMembers));
                final SelectElementsDialog dialog = new SelectElementsDialog(
                        project1,
                        psiElements, "Choose members to be included in generated properties", "Fields");
                dialog.setSize(200, 200);
                List<PsiElement> preselected = new ArrayList<PsiElement>();
                for (PsiField psiField : filteredFields) {
                    if (psi.findAllFieldByName(clazz, StringUtil.generatePropertyConstantName(psiField.getName())) == null) {
                        preselected.add(psiField);
                    }
                }
                dialog.getSelectedItems().addAll(preselected);
                SwingUtilities.invokeLater(
                        new Runnable() {
                            public void run() {
                                dialog.show();
                                if (SelectElementsDialog.CANCEL_EXIT_CODE == dialog.getExitCode()) {
                                    return;  // stop action, since user clicked cancel in dialog
                                }
                                Collection<PsiElement> selectedMembers = dialog.getSelectedItems();
                                executeGenerateActionLater(clazz, selectedMembers);
                            }
                        });
            } else {

                // no dialog, so select all fields (filtered) and methods (filtered)
                log.debug(
                        "Member chooser dialog not used - either disabled in settings or no fields/methods to select after filtering");

                Collection<PsiMember> selectedMembers = Arrays.asList(combineToMemberList(filteredFields, filteredMethods));
                executeGenerateAction(clazz, selectedMembers);
            }
        } catch (IncorrectOperationException e) {
            handleExeption(e);
        } catch (GenerateCodeException e) {
            handleExeption(e);
        }

        log.debug("executeAction - END");
    }

    /**
     * Combines the two lists into one list of members.
     *
     * @param filteredFields  fields to be included in the dialog
     * @param filteredMethods methods to be included in the dialog
     * @return the combined list
     */
    private PsiMember[] combineToMemberList(PsiField[] filteredFields, PsiMethod[] filteredMethods) {
        PsiMember[] members = new PsiMember[filteredFields.length + filteredMethods.length];
        System.arraycopy(filteredFields, 0, members, 0, filteredFields.length);
        System.arraycopy(filteredMethods, 0, members, filteredFields.length, filteredMethods.length);

        return members;
    }

    /**
     * Should the memeber chooser dialog be shown to the user?
     *
     * @param numberOfFields  number of fields to be avail for selection
     * @param numberOfMethods number of methods to be avail for selection
     * @return true if the dialog should be shown, false if not.
     */
    private boolean displayMememberChooser(int numberOfFields, int numberOfMethods) {

        // do not show if disabled in settings
        if (!config.isUseFieldChooserDialog()) {
            return false;
        }

        // if using reflection in toString() body code then do not display dialog
        if (config.getMethodBody() != null && config.getMethodBody().indexOf("getDeclaredFields()") != -1) {
            return false;
        }

        // must be at least one field for selection
        if (!config.enableMethods && numberOfFields == 0) {
            return false;
        }

        // must be at least one field or method for selection
        return !(config.enableMethods && Math.max(numberOfFields, numberOfMethods) == 0);

    }

    /**
     * Generates the toString() code for the specified class and selected fields and methods.
     *
     * @param clazz           the class
     * @param selectedMembers the selected members as both {@link com.intellij.psi.PsiField} and {@link
     *                        com.intellij.psi.PsiMethod}.
     */
    private void executeGenerateAction(PsiClass clazz, Collection<? extends PsiElement> selectedMembers)
            throws IncorrectOperationException, GenerateCodeException {

        // decide what to do if the method already exists
        ConflictResolutionPolicy policy = exitsMethodDialog(clazz, selectedMembers);

        // user didn't click cancel so go on
        Map<String, String> params = new HashMap<String, String>();

        // before
        beforeCreateToStringMethod(clazz, params);

        // generate fields
        PsiField[] fields = createPropertyConstantFields(clazz, selectedMembers, policy, params);

        // after, if method was generated (not cancel policy)
        if (fields != null && fields.length > 0) {
        }
    }

    /**
     * Generates the toString() code for the specified class and selected fields, doing the work through a WriteAction ran
     * by a CommandProcessor
     */
    private void executeGenerateActionLater(final PsiClass clazz, final Collection<PsiElement> selectedMemebers) {
        Runnable writeCommand = new Runnable() {
            public void run() {
                ApplicationManager.getApplication().runWriteAction(
                        new Runnable() {
                            public void run() {
                                try {
                                    executeGenerateAction(clazz, selectedMemebers);
                                } catch (GenerateCodeException e) {
                                    handleExeption(e);
                                }
                            }
                        });
            }
        };

        psi.executeCommand(project, writeCommand);
    }

    /**
     * Handles any exception during the executing on this plugin.
     *
     * @param e the caused exception.
     * @throws RuntimeException is thrown for severe exceptions
     */
    private void handleExeption(Exception e) {
        e.printStackTrace(); // must print stacktrace to see caused in IDEA log / console
        log.error("", e);

        if (e instanceof GenerateCodeException) {
            // code generation error - display velocity errror in error dialog so user can identify problem quicker
            Messages.showMessageDialog(
                    project,
                    "Velocity error generating code - see IDEA log for more details (stacktrace should be in idea.log):\n" +
                            e.getMessage(),
                    "Warning",
                    Messages.getWarningIcon());
        } else if (e instanceof PluginException) {
            // plugin related error - could be recoverable.
            Messages.showMessageDialog(
                    project,
                    "A PluginException was thrown while performing the action - see IDEA log for details (stacktrace should be in idea.log):\n" +
                            e.getMessage(),
                    "Warning",
                    Messages.getWarningIcon());
        }
    }

    /**
     * This method get's the choise if there is an existing <code>toString</code> method. <br/> 1) If there is a settings
     * to always override use this. <br/> 2) Prompt a dialog and let the user decide.
     *
     * @param clazz the class.
     * @return the policy the user selected (never null)
     */
    private ConflictResolutionPolicy exitsMethodDialog(PsiClass clazz, Collection<? extends PsiElement> selectedMembers) {
        boolean exists = false;
        for (PsiElement member : selectedMembers) {
            if (member instanceof PsiField) {
                PsiField field = (PsiField) member;
                if (psi.findFieldByName(clazz, StringUtil.generatePropertyConstantName(field.getName())) != null) {
                    exists = true;
                    break;
                }
            }
        }
        if (exists) {
            ConflictResolutionPolicy def = config.getReplaceDialogInitialOption();
            // is always use default set in config?
            if (config.isUseDefaultAlways()) {
                return def;
            } else {
                // no, so ask user what to do
                return MethodExistsDialog.showDialog(editor.getComponent(), def);
            }
        }

        // If there is no conflict, duplicate policy will do the trick
        return DuplicatePolicy.getInstance();
    }

    /**
     * This method is executed just before the <code>toString</code> method is created or updated.
     *
     * @param clazz  the class.
     * @param params additional parameters stored with key/value in the map.
     */
    private void beforeCreateToStringMethod(PsiClass clazz, Map<String, String> params) {
        PsiMethod toStringMethod = psi.findMethodByName(clazz, "toString"); // find the existing toString method
        if (toStringMethod != null) {
            PsiDocComment docComment = toStringMethod.getDocComment();
            if (docComment != null) {
                params.put("existingJavaDoc", docComment.getText());
            }
        }
    }

    /**
     * Creates the property constant fields.
     *
     * @param clazz           the PsiClass object.
     * @param selectedMembers the selected members as both {@link com.intellij.psi.PsiField} and {@link
     *                        com.intellij.psi.PsiMethod}.
     * @param policy          conflict resolution policy
     * @param params          additional parameters stored with key/value in the map.
     * @return the created method, null if the method is not created due the user cancels this operation
     * @throws GenerateCodeException is thrown when there is an error generating the javacode.
     */
    @Nullable
    private PsiField[] createPropertyConstantFields(PsiClass clazz,
                                                    Collection<? extends PsiElement> selectedMembers,
                                                    ConflictResolutionPolicy policy,
                                                    Map params)
            throws IncorrectOperationException, GenerateCodeException {
        // generate code using velocity
        List<PsiField> newFields = new LinkedList<PsiField>();
        List<FieldElement> fields = getOnlyAsFieldElements(selectedMembers);
        if (log.isDebugEnabled()) {
            log.debug("The fields are " + fields);
        }
        for (FieldElement fieldElement : fields) {
            String fieldName = StringUtil.generatePropertyConstantName(fieldElement.getName());
            String declaration = "public static final String " +
                    fieldName + " = \"" + fieldElement.getName() + "\";";
            if (log.isDebugEnabled()) {
                log.debug("Creating field is: " + declaration);
            }


            // applyField conflict resolution policy (add/replace, duplicate, cancel)
            PsiField existingField = psi.findFieldByName(clazz, fieldName);
            PsiField newField = elementFactory.createFieldFromText(declaration, null);
            boolean operationExecuted = policy.applyField(
                    editor,
                    clazz, existingField, newField);
            if (existingField != null && !operationExecuted) {
                //noinspection ReturnOfNull
                return null; // user cancelled so return null
            }

            String existingJavaDoc = (String) params.get("existingJavaDoc");
            String newJavaDoc = "/** Property name constant for {@code " + fieldElement.getName() + "}. */";
            PsiField propertyNameField = psi.findFieldByName(
                    clazz,
                    fieldName); // must find again to be able to add javadoc (IDEA does not add if using method parameter)
            policy.applyJavaDoc(
                    clazz, propertyNameField,
                    codeStyleManager, elementFactory, existingJavaDoc, newJavaDoc);

            // reformat code style
            codeStyleManager.reformat(newField);
            newFields.add(newField);
        }

        // return the created fields
        return newFields.toArray(new PsiField[newFields.size()]);

    }

    /**
     * Get's the list of members to be put in the VelocityContext.
     *
     * @param members a list of {@link com.intellij.psi.PsiMember} objects.
     * @return a filtered list of only the fields as {@link org.intellij.idea.plugin.genprop.element.FieldElement}
     *         objects.
     */
    private List<FieldElement> getOnlyAsFieldElements(Collection<? extends PsiElement> members) {
        List<FieldElement> fieldElementList = new ArrayList<FieldElement>();

        for (PsiElement member : members) {
            if (member instanceof PsiField) {
                PsiField field = (PsiField) member;
                FieldElement fe = ElementFactory.newFieldElement(
                        field, elementFactory, psi);
                fieldElementList.add(fe);
                if (log.isDebugEnabled()) {
                    log.debug(fe);
                }
            }
        }

        return fieldElementList;
    }

    /**
     * Filters the list of fields from the class with the given parameters from the {@link
     * org.intellij.idea.plugin.genprop.config.Config config} settings.
     *
     * @param clazz   The class to filter it's fields
     * @param pattern the filter pattern to filter out unwanted fields
     * @return fields avaiable for this action after the filter process.
     */
    private PsiField[] filterAvailableFields(PsiClass clazz, FilterPattern pattern) {
        if (log.isDebugEnabled()) {
            log.debug("Filtering fields using the pattern: " + pattern);
        }
        List<PsiField> availableFields = new ArrayList<PsiField>();

        // performs til filtering process
        PsiField[] fields = psi.getFields(clazz);
        for (PsiField field : fields) {
            FieldElement fe = ElementFactory.newFieldElement(field, elementFactory, psi);
            if (log.isDebugEnabled()) {
                log.debug("Field being filtered: " + fe);
            }

            // if the field matches the pattern then it shouldn't be in the list of avaialble fields
            if (!fe.applyFilter(pattern)) {
                availableFields.add(field);
            }
        }

        return availableFields.toArray(new PsiField[availableFields.size()]);
    }


    /**
     * Filters the list of methods from the class to be <ul> <li/>a getter method (java bean compliant) <li/>should not be
     * a getter for an existing field <li/>public, non static, non abstract <ul/>
     *
     * @param clazz   the class to filter it's fields
     * @param pattern the filter pattern to filter out unwanted fields
     * @return methods avaiable for this action after the filter process.
     */
    private PsiMethod[] filterAvailableMethods(PsiClass clazz, FilterPattern pattern) {
        if (log.isDebugEnabled()) {
            log.debug("Filtering methods using the pattern: " + pattern);
        }
        List<PsiMethod> availableMethods = new ArrayList<PsiMethod>();

        PsiMethod[] methods = psi.getMethods(clazz);
        for (PsiMethod method : methods) {
            MethodElement me = ElementFactory.newMethodElement(method, elementFactory, psi);
            if (log.isDebugEnabled()) {
                log.debug("Method being filtered: " + me);
            }

            // the method should be a getter
            if (!me.isGetter()) {
                continue;
            }

            // must not return void
            if (me.isReturnTypeVoid()) {
                continue;
            }

            // method should be public, non static, non abstract
            if (!me.isModifierPublic() || me.isModifierStatic() || me.isModifierAbstract()) {
                continue;
            }

            // method should not be a getter for an existing field
            if (psi.findFieldByName(clazz, me.getFieldName()) != null) {
                continue;
            }

            // must not be named toString or getClass
            if ("toString".equals(me.getMethodName()) || "getClass".equals(me.getMethodName())) {
                continue;
            }

            // if the method matches the pattern then it shouldn't be in the list of avaialble methods
            if (!me.applyFilter(pattern)) {
                if (log.isDebugEnabled()) {
                    log.debug("Adding the method " + method.getName() + " as there is not a field for this getter");
                }
                availableMethods.add(method);
            }
        }

        return availableMethods.toArray(new PsiMethod[availableMethods.size()]);
    }

}
