package br.com.breakpoint.breakpoint;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.BreakpointManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * Created by DEK on 22/04/2017.
 */
public class SimpleBracketBreakpointInput {

    static public void execute(Project project, PsiFile psiFile){
        BreakpointManager debugManager =  DebuggerManagerEx.getInstanceEx(project).getBreakpointManager();
        final PsiDocumentManager manager = PsiDocumentManager.getInstance(project);
        final Document document = manager.getDocument(psiFile);
        String[] textSpl = document.getText().split("\n");
        int bracketCount = 0;
        String className = null;
        boolean waitToPutBreakpoint = false;

        for(int i = 0; i< textSpl.length;i++){
            String currentLine = textSpl[i];

            int thisLineBracketCount = bracketCount(currentLine);
            bracketCount +=thisLineBracketCount;

            if(thisLineBracketCount == 1 && bracketCount==1){
                //In the class
                className = getClassName(currentLine);
            }

            if(thisLineBracketCount == 1 && bracketCount==2){
                //In the method
                waitToPutBreakpoint = true;
            }
            if(thisLineBracketCount == -1 && bracketCount==1){
                //Out of method
                waitToPutBreakpoint = false;
            }

            if(waitToPutBreakpoint){
                if(isValidLine(currentLine)){
                    waitToPutBreakpoint = false;
                    GeneralConfig.addBreakpoint(debugManager.addLineBreakpoint(document,i));
                }
            }
        }
    }

    static private int bracketCount(String line){
        int count = 0;
        for(char chr : line.toCharArray()){
            if(chr == '{'){
                count++;
            }
            if(chr == '}'){
                count--;
            }
        }
        return count;
    }

    static private String getClassName(String currentLine){
        String className = null;
        if(currentLine.contains(" class ")){
            className =  currentLine.split("class")[1].replaceAll("\\{","").split(" ")[1];
        }
        if(currentLine.contains(" enum ")){
            className =  currentLine.split("enum")[1].replaceAll("\\{","").split(" ")[1];
        }
        return className;
    }

    static private boolean isValidLine(String line){
        boolean if_value = line.contains("if ") || line.contains(" if ") || line.contains("if(");
        boolean for_value = line.contains("for ") || line.contains(" for ") || line.contains("for(");
        boolean try_value = line.contains("try ") || line.contains(" try ") || line.contains("try{");
        boolean comment_value = line.contains("//");
        if(!(try_value || comment_value) && (line.contains(";") || if_value || for_value || line.contains("while") || line.contains("return "))){
            return true;
        }
        return false;
    }


}
