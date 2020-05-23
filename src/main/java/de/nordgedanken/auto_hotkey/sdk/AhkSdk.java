package de.nordgedanken.auto_hotkey.sdk;

import com.google.common.flogger.FluentLogger;
import com.intellij.openapi.projectRoots.*;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class AhkSdk extends SdkType {
	private static final FluentLogger logger = FluentLogger.forEnclosingClass();

	public AhkSdk() {
		super("AutoHotkey");
	}

	@Override
	public @Nullable String suggestHomePath() {
		return "C:\\Program Files\\AutoHotkey";
	}

	@Override
	public boolean isValidSdkHome(String selectedSdkPath) {
		try (Stream<Path> paths = Files.walk(Paths.get(selectedSdkPath))) {
			paths.filter(Files::isRegularFile)
					.forEach(file -> logger.atInfo().log(file.getFileName().toString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public @NotNull String suggestSdkName(@Nullable String currentSdkName, String sdkHome) {
		return "AutoHotkey v1";
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
		SdkModificator modificator = sdk.getSdkModificator();
//		modificator.setVersionString(getVersionString(sdk));
//		modificator.commitChanges(); // save
		return true;
	}
}
