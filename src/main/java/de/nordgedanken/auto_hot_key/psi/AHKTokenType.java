package de.nordgedanken.auto_hot_key.psi;

import com.intellij.psi.tree.IElementType;
import de.nordgedanken.auto_hot_key.AHKLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class AHKTokenType extends IElementType {
    public AHKTokenType(@NotNull @NonNls String debugName) {
        super(debugName, AHKLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "AHKTokenType." + super.toString();
    }
}
