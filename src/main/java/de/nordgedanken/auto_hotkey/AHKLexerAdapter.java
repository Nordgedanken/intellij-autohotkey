package de.nordgedanken.auto_hotkey;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class AHKLexerAdapter extends FlexAdapter {
    public AHKLexerAdapter() {
        super(new AHKLexer((Reader) null));
    }
}
