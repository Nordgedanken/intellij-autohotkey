package de.nordgedanken.auto_hotkey.ide.notifications

import com.intellij.ui.EditorNotificationPanel

/**
 * A simple editor notification panel that can be instantiated when a notification
 * needs to be shown at the top of the editor.
 */
class AhkEditorNotificationPanel(val debugId: String) : EditorNotificationPanel()
