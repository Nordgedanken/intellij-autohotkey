# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- New page added to Settings dialog for AutoHotkey settings
- Sdk-management toolbar (eg add/edit/remove sdk) added to AutoHotkey settings

### Changed
- Settings button next to "Script Runner" field in run config UI now points to new AutoHotkey page in Settings
- Updated compatibility to work with IntelliJ 2020.2

### Fixed
- Removed/commented classes that prevented the plugin from working in non-IDEA IDEs 

## [0.3.0] - 2020-07-23
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
