package de.nordgedanken.auto_hot_key;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class AHKLexerAdapter extends FlexAdapter {
    public AHKLexerAdapter() {
        super(new AHKLexer((Reader) null));
    }
}
