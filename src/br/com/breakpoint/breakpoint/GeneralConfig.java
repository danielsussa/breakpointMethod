package br.com.breakpoint.breakpoint;

import com.intellij.debugger.DebuggerManagerEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.breakpoints.LineBreakpoint;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEK on 21/04/2017.
 */
public class GeneralConfig {
    static public boolean isActive;
    static private List<LineBreakpoint> breakpoints = new ArrayList<>();


    static public void removeAll(Project project){
        for(Breakpoint breakpoint : breakpoints){
            DebuggerManagerEx.getInstanceEx(project).getBreakpointManager().removeBreakpoint(breakpoint);
        }
    }

    static public void addBreakpoint(LineBreakpoint lineBreakpoint){
        breakpoints.add(lineBreakpoint);
    }
}
