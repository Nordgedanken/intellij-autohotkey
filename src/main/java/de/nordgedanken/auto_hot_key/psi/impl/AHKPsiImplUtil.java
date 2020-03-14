package de.nordgedanken.auto_hot_key.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LeafElement;
import de.nordgedanken.auto_hot_key.psi.AHKProperty;
import de.nordgedanken.auto_hot_key.psi.AHKStringLiteral;
import de.nordgedanken.auto_hot_key.psi.AHKTypes;
import de.nordgedanken.auto_hot_key.util.AHKStringLiteralEscaper;
import org.jetbrains.annotations.NotNull;

public class AHKPsiImplUtil {
    public static boolean isValidHost(@NotNull AHKStringLiteral o) {
        return true;
    }

    @NotNull
    public static AHKStringLiteralImpl updateText(@NotNull AHKStringLiteral o, @NotNull String text) {
        if (text.length() > 2) {
            if (o.getString() != null) {
                StringBuilder outChars = new StringBuilder();
                AHKStringLiteralEscaper.escapeString(text.substring(1, text.length() - 1), outChars);
                outChars.insert(0, '"');
                outChars.append('"');
                text = outChars.toString();
            }
        }

        ASTNode valueNode = o.getNode().getFirstChildNode();
        assert valueNode instanceof LeafElement;

        ((LeafElement) valueNode).replaceWithText(text);
        return (AHKStringLiteralImpl) o;
    }

    @NotNull
    public static AHKStringLiteralEscaper createLiteralTextEscaper(@NotNull AHKStringLiteral o) {
        return new AHKStringLiteralEscaper(o);
    }

    public static String getKey(AHKProperty element) {
        ASTNode keyNode = element.getNode().findChildByType(AHKTypes.KEY);
        if (keyNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return keyNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static String getValue(AHKProperty element) {
        ASTNode valueNode = element.getNode().findChildByType(AHKTypes.STRING_LITERAL);
        if (valueNode != null) {
            return valueNode.getText();
        } else {
            return null;
        }
    }
}
