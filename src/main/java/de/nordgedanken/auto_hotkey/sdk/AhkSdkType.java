package de.nordgedanken.auto_hotkey.sdk;

import com.google.common.flogger.FluentLogger;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import de.nordgedanken.auto_hotkey.AHKIcons;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public final class AhkSdkType extends SdkType {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	public AhkSdkType() {
		super("AutoHotkeySDK");
	}

	@Override
	public Icon getIcon() {
		return AHKIcons.EXE;
	}

	@Override
	public @NotNull String suggestHomePath() {
		return "C:\\Program Files\\AutoHotkey";
	}

	@Override
	public boolean isValidSdkHome(String selectedSdkPath) {
		try (Stream<Path> paths = Files.walk(Paths.get(selectedSdkPath), 1)) {
			return paths.filter(Files::isRegularFile)
					.anyMatch(file -> "AutoHotkey.exe".equals(file.getFileName().toString()));
		} catch (IOException e) {
			logger.atSevere().withCause(e).log();
			return false;
		}
	}

	@Override
	public String getInvalidHomeMessage(String path) {
		return "AutoHotkey.exe could not be found in the selected folder. Please ensure that AutoHotkey.exe is within the folder that you are selecting";
	}

	@Override
	public @NotNull String suggestSdkName(@Nullable String currentSdkName, String sdkHome) {
		return "AutoHotkey";
	}

	@Override
	public final String getVersionString(String sdkHome) {
		return "unknown version";
	}

	@Override
	public @Nullable AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
		return null;
	}

	@Override
	public @NotNull String getPresentableName() {
		return "AutoHotkey SDK";
	}

	@Override
	public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {

	}

	@Override
	public boolean setupSdkPaths(@NotNull Sdk sdk, @NotNull SdkModel sdkModel) {
		//all of this seems to do nothing right now. Have to figure it out
		SdkModificator modificator = sdk.getSdkModificator();
		modificator.removeRoots(OrderRootType.CLASSES);
		modificator.addRoot(
				Paths.get(Objects.requireNonNull(sdk.getHomePath()), "AutoHotkey.exe").toAbsolutePath().toString(),
				OrderRootType.CLASSES);
		modificator.commitChanges(); // save
		return true;
	}
}
