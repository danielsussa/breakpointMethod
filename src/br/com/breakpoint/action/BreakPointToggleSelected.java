package br.com.breakpoint.action;

import br.com.breakpoint.breakpoint.GeneralConfig;
import br.com.breakpoint.breakpoint.RemoveAll;
import br.com.breakpoint.breakpoint.SimpleBreakpointInput;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;

/**
 * Created by DEK on 21/04/2017.
 */
public class BreakPointToggleSelected extends AnAction {

    @Override
    public void update(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if(GeneralConfig.isActive){
            anActionEvent.getPresentation().setText("Clear All Breakpoints");
        }else {
            anActionEvent.getPresentation().setText("Toggle Methods Breakpoints");
        }

    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        PsiElement[] elements = event.getData(LangDataKeys.PSI_ELEMENT_ARRAY);

        if(GeneralConfig.isActive){
            GeneralConfig.isActive = false;
            GeneralConfig.removeAll(project);
        }else {
            GeneralConfig.isActive = true;
            GeneralConfig.removeAll(project);
            for(PsiElement element : elements){
                process(element);
            }
        }

    }

    private void process(PsiElement element){
        if(element instanceof PsiJavaFile || element instanceof PsiClass){
            SimpleBreakpointInput.execute(element.getProject(),element.getContainingFile());
        }
        if(element instanceof PsiDirectory){
            PsiElement[] psiElements = element.getChildren();
            for(PsiElement psiElement : psiElements){
                process(psiElement);
            }

        }
    }
}
