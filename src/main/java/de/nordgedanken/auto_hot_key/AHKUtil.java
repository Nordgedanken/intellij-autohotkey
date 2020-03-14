package de.nordgedanken.auto_hot_key;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import de.nordgedanken.auto_hot_key.psi.AHKFile;
import de.nordgedanken.auto_hot_key.psi.AHKProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AHKUtil {
    // Searches the entire project for Simple language files with instances of the Simple property with the given key
    public static List<AHKProperty> findProperties(Project project, String key) {
        List<AHKProperty> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(AHKFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            AHKFile simpleFile = (AHKFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                AHKProperty[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, AHKProperty.class);
                if (properties != null) {
                    for (AHKProperty property : properties) {
                        if (key.equals(property.getKey())) {
                            result.add(property);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static List<AHKProperty> findProperties(Project project) {
        List<AHKProperty> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(AHKFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            AHKFile simpleFile = (AHKFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (simpleFile != null) {
                AHKProperty[] properties = PsiTreeUtil.getChildrenOfType(simpleFile, AHKProperty.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}
