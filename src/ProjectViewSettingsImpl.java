import com.intellij.ide.projectView.ProjectViewSettings;

/**
 * Created by DEK on 21/04/2017.
 */
public class ProjectViewSettingsImpl implements ProjectViewSettings {

    @Override
    public boolean isShowExcludedFiles() {
        return false;
    }

    @Override
    public boolean isShowMembers() {
        return true;
    }

    @Override
    public boolean isStructureView() {
        return true;
    }

    @Override
    public boolean isShowModules() {
        return true;
    }

    @Override
    public boolean isFlattenPackages() {
        return true;
    }

    @Override
    public boolean isAbbreviatePackageNames() {
        return true;
    }

    @Override
    public boolean isHideEmptyMiddlePackages() {
        return false;
    }

    @Override
    public boolean isShowLibraryContents() {
        return true;
    }
}
