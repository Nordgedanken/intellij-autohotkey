package de.nordgedanken.auto_hotkey;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class AHKColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Key", AHKSyntaxHighlighter.KEY),
            new AttributesDescriptor("OPERATION", AHKSyntaxHighlighter.OPERATION),
            new AttributesDescriptor("Value", AHKSyntaxHighlighter.STRING),
            new AttributesDescriptor("Bad Value", AHKSyntaxHighlighter.BAD_CHARACTER)
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return AHKIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new AHKSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "\n" +
                "Menu, Tray, Icon, % A_WinDir \"\\system32\\netshell.dll\" , 86 ; Shows a world icon in the system tray\n" +
                "\n" +
                "ModernBrowsers := \"ApplicationFrameWindow,Chrome_WidgetWin_0,Chrome_WidgetWin_1,Maxthon3Cls_MainFrm,MozillaWindowClass,Slimjet_WidgetWin_1\"\n" +
                "LegacyBrowsers := \"IEFrame,OperaWindowClass\"\n" +
                "\n" +
                ";^+!u:: \n" +
                ";\tnTime := A_TickCount\n" +
                ";\tsURL := GetActiveBrowserURL()\n" +
                ";\tWinGetClass, sClass, A\n" +
                ";\tIf (sURL != \"\")\n" +
                ";\t\tMsgBox, % \"The URL is \"\"\" sURL \"\"\"`nEllapsed time: \" (A_TickCount - nTime) \" ms (\" sClass \")\"\n" +
                ";\tElse If sClass In % ModernBrowsers \",\" LegacyBrowsers\n" +
                ";\t\tMsgBox, % \"The URL couldn't be determined (\" sClass \")\"\n" +
                ";\tElse\n" +
                ";\t\tMsgBox, % \"Not a browser or browser not supported (\" sClass \")\"\n" +
                ";Return";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "AutoHotKey";
    }
}
