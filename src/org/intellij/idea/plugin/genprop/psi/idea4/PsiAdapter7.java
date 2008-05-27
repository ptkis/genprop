package org.intellij.idea.plugin.genprop.psi.idea4;

import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import org.intellij.idea.plugin.genprop.psi.PsiAdapter;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * IDEA 4.x version of the PsiAdapter.
 *
 * @author Claus Ibsen
 */
public class PsiAdapter7
		extends PsiAdapter {

	@Nullable
	@Override
	public String getPluginFilename() {
		Application application = ApplicationManager.getApplication();
		IdeaPluginDescriptor[] decs = application.getPlugins();

		for (IdeaPluginDescriptor dec : decs) {
			if ("GeneratePropertyNames".equals(dec.getName())) {
				return PathManager.getPluginsPath() + File.separatorChar + dec.getPath().getName();
			}
		}

		return null;
	}
}
