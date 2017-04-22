package br.com.breakpoint.action;

import br.com.breakpoint.ProjectViewSettingsImpl;
import br.com.breakpoint.Visitor;
import br.com.breakpoint.breakpoint.GeneralConfig;
import br.com.breakpoint.breakpoint.RemoveAll;
import br.com.breakpoint.breakpoint.SimpleBreakpointInput;
import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;

import java.util.*;

/**
 * Created by DEK on 21/04/2017.
 */
public class BreakPointToggleAll extends AnAction {

    @Override
    public void update(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        if (project == null)
            return;
        Object navigatable = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        anActionEvent.getPresentation().setEnabledAndVisible(navigatable != null);

        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        anActionEvent.getPresentation().setVisible(true);
        anActionEvent.getPresentation().setEnabled(editor != null);
        //anActionEvent.getPresentation().setIcon(AllIcons.General.);
    }


    List<PsiMethod> psiMethods;


    public void actionPerformed(AnActionEvent event) {
        psiMethods = new ArrayList<>();

        Project project = event.getData(PlatformDataKeys.PROJECT);
        VirtualFile virtualFile = project.getWorkspaceFile();

        event.getData(LangDataKeys.PSI_FILE);


        if(GeneralConfig.isActive){
            GeneralConfig.isActive = false;
            GeneralConfig.removeAll(project);
        }else {
            GeneralConfig.isActive = true;
            GeneralConfig.removeAll(project);
            getPackages(project);
        }




    }



    private List<PsiPackage> getPackages(Project project) {

        ProjectViewSettings viewSettings = new ProjectViewSettingsImpl();

        final List<VirtualFile> sourceRoots = new ArrayList<VirtualFile>();
        final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(project);
        ContainerUtil.addAll(sourceRoots, projectRootManager.getContentSourceRoots());

        final PsiManager psiManager = PsiManager.getInstance(project);
        final List<AbstractTreeNode> children = new ArrayList<AbstractTreeNode>();
        final Set<PsiPackage> topLevelPackages = new HashSet<PsiPackage>();

        for (final VirtualFile root : sourceRoots) {
            getAllJava(root,psiManager,project);
        }

        return new ArrayList<PsiPackage>(topLevelPackages);
    }

    private void getAllJava(VirtualFile file,PsiManager psiManager,Project project){
        final PsiDirectory psiDirectory = psiManager.findDirectory(file);
        final PsiFile psiFile = psiManager.findFile(file);
        if(psiDirectory != null){
            VirtualFile[] childFiles = file.getChildren();
            for(VirtualFile childFile : childFiles)
            getAllJava(childFile,psiManager,project);
        }
        if(psiFile != null){


            if(psiFile.getFileType().getDefaultExtension().equals("java")){
                SimpleBreakpointInput.execute(project,psiFile);
            }

        }
    }

    private void getAllMethods(PsiFile psiFile){
        PsiJavaFile psiJavaFile = (PsiJavaFile)psiFile;
        Visitor visitor = new Visitor();
        psiJavaFile.accept(visitor);
        psiMethods.addAll(visitor.getPsiMethods());
    }


}