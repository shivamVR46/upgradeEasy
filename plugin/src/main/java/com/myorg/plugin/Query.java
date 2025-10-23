package com.myorg.plugin;

import com.intellij.ide.BrowserUtil;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Query extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            Messages.showErrorDialog("No project is currently open.", "Error");
            return;
        }

        List<String> dependencies = new ArrayList<>();

        //V1
        // Loop through each module and collect its library dependencies
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            OrderEnumerator.orderEntries(module)
                    .withoutSdk()
                    .withoutModuleSourceEntries()
                    .forEachLibrary(library -> {
                        if (library.getName() != null) {
                            dependencies.add(library.getName());
                        }
                        return true;
                    });
        }

        // Remove duplicates and sort
        String dependencyList = dependencies.stream()
                .filter(name -> name != null && !name.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.joining("\n"));

        if (dependencyList.isEmpty()) {
            dependencyList = "No dependencies found.";
        }

        Messages.showInfoMessage( project, dependencyList, "Number of Dependencies in the project");
    }
}