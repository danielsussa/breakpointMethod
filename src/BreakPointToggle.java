import com.intellij.CommonBundle;
import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.MethodBreakpoint;
import com.intellij.icons.AllIcons;
import com.intellij.ide.projectView.ProjectViewSettings;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;

import java.util.*;

/**
 * Created by DEK on 21/04/2017.
 */
public class BreakPointToggle extends AnAction {

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
    public boolean isActive;


    public void actionPerformed(AnActionEvent event) {
        psiMethods = new ArrayList<>();

        Project project = event.getData(PlatformDataKeys.PROJECT);
        VirtualFile virtualFile = project.getWorkspaceFile();

        event.getData(LangDataKeys.PSI_FILE);


        if(isActive){
            isActive = false;
            removeAllMethodBreakpoint(project);
        }else {
            isActive = true;
            removeAllMethodBreakpoint(project);
            getPackages(project);
        }




    }

    public void removeAllMethodBreakpoint(Project project){
        List<Breakpoint> breakpoints = DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().getBreakpoints();
        for(Breakpoint breakpoint : breakpoints){
            DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().removeBreakpoint(breakpoint);
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
            final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
            final Document document = manager.getDocument(psiFile);

            if(psiFile.getFileType().getDefaultExtension().equals("java")){
                getAllMethods(psiFile);
            }
            for(PsiMethod method : psiMethods){
                String name = method.getName();
                String type = method.getReturnType().getCanonicalText();
                String modif = method.getModifierList().getText();
                if(document != null){
                    String[] docLines = document.getText().split("\n");
                    for(int i = 0; i<docLines.length;i++){
                        String line = docLines[i];
                        if(line.contains(name) && line.contains(type) && line.contains(modif)){
                            DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().addMethodBreakpoint(document,i);
                        }
                    }
                }
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