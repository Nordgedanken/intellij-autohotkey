package de.nordgedanken.auto_hotkey.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import de.nordgedanken.auto_hotkey.lang.core.AhkFileType;
import de.nordgedanken.auto_hotkey.lang.core.AhkLanguage;
import org.jetbrains.annotations.NotNull;

public class AHKFile extends PsiFileBase {
    public AHKFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, AhkLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return AhkFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "AutoHotKey Script";
    }
}
