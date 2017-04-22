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
            boolean newMethod = false;
            int totalBrackets = 0;
            for(int i = 0; i<docLines.length;i++){
                String line = docLines[i];
                if(line.contains("(")){
                    if(!line.contains(";")){
                        if(line.contains("private ") || line.contains("public ") || line.contains("protected ") || line.contains("throws ") || line.contains("void ")){
                            if(className != null){
                                if(!line.contains(className)){
                                    newMethod = true;
                                }
                            }
                        }
                    }
                }

                if(line.contains(" class ")){
                    className =  line.split("class")[1].replaceAll("\\{","").split(" ")[1];
                }
                if(line.contains(" enum ")){
                    className =  line.split("enum")[1].replaceAll("\\{","").split(" ")[1];
                }

                if(newMethod){
                    totalBrackets += bracketsCounter(line);
                    if(totalBrackets == 0){
                        newMethod = false;
                        while (i+1<docLines.length){
                            boolean if_value = line.contains("if ") || line.contains(" if ") || line.contains("if(");
                            boolean for_value = line.contains("for ") || line.contains(" for ") || line.contains("for(");
                            boolean try_value = line.contains("try ") || line.contains(" try ") || line.contains("try{");
                            if(!try_value && (line.contains(";") || if_value || for_value || line.contains("while") || line.contains("return "))){
                                GeneralConfig.addBreakpoint(DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().addLineBreakpoint(document,i));
                                break;
                            }else {
                                i++;
                                line = docLines[i];
                            }
                        }
                    }
                }
            }
        }
    }

    static private int bracketsCounter(String str){
        int k = 0;
        for(char chr : str.toCharArray()){
            if(chr == '('){
                k++;
            }
            if(chr == ')'){
                k--;
            }
        }
        return k;
    }
}
