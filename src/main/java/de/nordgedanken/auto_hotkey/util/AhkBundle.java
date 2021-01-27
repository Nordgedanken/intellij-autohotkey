package de.nordgedanken.auto_hotkey.util;

import com.intellij.CommonBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.ResourceBundle;

public class AhkBundle {
	@NonNls
	protected static final String BUNDLE_NAME = "localization.AhkBundle";

	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private AhkBundle() {

	}

	/**
	 * Retrieve property value from bundle file
	 *
	 * Ex: AhkBundle.msg("runconfig.configtab.scriptpath.label")
	 */
	public static String msg(@PropertyKey(resourceBundle = BUNDLE_NAME) String key, Object... params) {
		return CommonBundle.message(BUNDLE, key, params);
	}
}
