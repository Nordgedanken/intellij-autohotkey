# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Syntax highlighting now supports hotstrings and normal labels
- Can now toggle block comments using the standard block-comment-toggle action
- Errors printed to the console should now show a hyperlink allowing you to jump to any files referenced in the error

## [0.6.0] - 2021-04-07 
#### (compatibility: 2020.1 - 2021.1.*) 
### Added
- Can now select a default Ahk sdk from the Ahk settings
- Syntax highlighting now supports hotkeys

### Changed
- Sdk renaming is now done in-line within the Ahk settings
- Can now produce run configs from empty Ahk files when right-clicking them in the project tree

### Fixed
- Fixed a bug with run configs where creating a run config with no ahk sdks declared, and then later adding an ahk sdk, would cause the config to show an error even though editing the config showed no error (since it was showing the project sdk by default)

## [0.5.0] - 2021-03-14
### Added
- Syntax highlighting now supports block comments
- Ide now alerts with a popup notification if there are no Ahk runners configured
- Right-clicking an Ahk file now offers a 'run' option (only if the file has code)
- A run icon will appear in the gutter for the first executable line in an Ahk file 

### Changed
- Modified the Ahk runconfig ID to match the format of the Rust plugin (you will need to modify the "type" in the run config's xml to "AhkRunConfiguration" or just re-create any Ahk run configs you saved) 
- Changed the AutoHotkey file icon to match the icon in Windows File Explorer

## [0.4.0] - 2021-02-14
### Added
- New option "Print errors to console" in run config

### Changed
- Modified syntax-highlighting to just comments. Commented other code to prevent errors.

### Fixed
- Fixed compatibility issues so it works with IntelliJ 2020.*

### Removed
- Removed support for IDEA 2019 and below to support newer IDEs

## [0.3.1] - 2020-08-17 - Replaces 0.3.0
### Added
- New page added to Settings dialog for AutoHotkey settings
- Sdk-management toolbar (eg add/edit/remove sdk) added to AutoHotkey settings

### Changed
- Settings button next to "Script Runner" field in run config UI now points to new AutoHotkey page in Settings
- Updated compatibility to work with IntelliJ 2020.2

### Fixed
- Removed/commented classes that prevented the plugin from working in non-IDEA IDEs 

## [0.3.0] - 2020-07-23 - Not released itself. Only availale bundled with 0.3.1
### Added
- The arguments text field in the run configuration is now expandable
- Added support for selecting a AutoHotkey project SDK in the run config to run the script with
- Ahk SDKs now show their version in the SDK combobox

### Changed
- Moved run config validity verifications from runtime to within the "Edit Configurations" dialog

### Removed
- Removed dependency on Java plugin

## [0.2.0] - 2020-06-07
### Added
- Added support for selecting an AutoHotkey exe file via setting an AutoHotkey project SDK
- Added AutoHotkey run configurations that allow you to select the script to run and provide runtime arguments

### Changed
- Moved Changelog descriptions out of the plugin.xml & build.gradle.kts file into CHANGELOG.md
- Updated plugin description

## [0.1.3] - 2020-05-11
### Fixed
- Crashes

### Added
- Basic Function support and Improved language grammar (Function Bodys are not yet working but WIP)

## [0.1.2] - 2020-05-08
### Fixed
- Make compatible with newer IDE versions

## [0.1.1] - 2020-03-15
### Fixed
- Fix "New File" action

## [0.1.0] 
### Added
- Initial Release
- Added most basic features
