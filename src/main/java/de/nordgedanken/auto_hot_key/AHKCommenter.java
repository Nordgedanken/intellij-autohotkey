package de.nordgedanken.auto_hot_key;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

public class AHKCommenter  implements Commenter {
    @Nullable
    @Override
    public String getLineCommentPrefix() {
        return ";";
    }

    @Nullable
    @Override
    public String getBlockCommentPrefix() {
        return "";
    }

    @Nullable
    @Override
    public String getBlockCommentSuffix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentPrefix() {
        return null;
    }

    @Nullable
    @Override
    public String getCommentedBlockCommentSuffix() {
        return null;
    }
}
