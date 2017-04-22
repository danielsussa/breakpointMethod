package br.com.breakpoint.breakpoint;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * Created by DEK on 21/04/2017.
 */
public class SimpleBreakpointInput {

    static public void execute(Project project, PsiFile psiFile){
        final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        final Document document = manager.getDocument(psiFile);

        if(document != null){
            String[] docLines = document.getText().split("\n");
            String className = null;
            for(int i = 0; i<docLines.length;i++){
                String line = docLines[i];
                if(line.contains("(")){
                    if(!line.contains(";")){
                        if(line.contains("private") || line.contains("public") || line.contains("protected")){
                            if(!line.contains(className)){
                                DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().addMethodBreakpoint(document,i);
                            }
                        }
                    }
                }

                if(line.contains("class ")){
                    className =  line.split("class")[1].replaceAll("\\{","").split(" ")[1];
                }
            }
        }
    }
}
