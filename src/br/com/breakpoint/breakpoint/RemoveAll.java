package br.com.breakpoint.breakpoint;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.openapi.project.Project;

import java.util.List;

/**
 * Created by DEK on 21/04/2017.
 */
public class RemoveAll {

    static public void execute(Project project){
        List<Breakpoint> breakpoints = DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().getBreakpoints();
        for(Breakpoint breakpoint : breakpoints){
            if(breakpoint.getCategory().toString().equals("method_breakpoints")){
                DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().removeBreakpoint(breakpoint);
            }
        }
    }
}
