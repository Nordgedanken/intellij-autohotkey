package de.nordgedanken.auto_hotkey;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AHKFileType extends LanguageFileType {
    public static final AHKFileType INSTANCE = new AHKFileType();

    private AHKFileType() {
        super(AHKLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "AHK Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "AutoHotKey script file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "ahk";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return AHKIcons.FILE;
    }

}
