# Contributing to the IntelliJ AutoHotkey Plugin

First of all, thanks for taking the time to contribute! This entire plugin is built from open-source code with the efforts of developers like you who want to make better software for the benefit of everyone (in their spare time, no less!)

## Table of Contents
[Code of Conduct](#code-of-conduct)

[I don't want to read this whole thing, I just have a question!!!](#i-dont-want-to-read-this-whole-thing-i-just-have-a-question)

[What should I know before I get started?](#what-should-i-know-before-i-get-started)
* [Plugin Feature Locations](#plugin-feature-locations)
* [Design Decisions](#design-decisions)

[How Can I Contribute?](#how-can-i-contribute)
* [Reporting Bugs](#reporting-bugs)
* [Suggesting Enhancements](#suggesting-enhancements)
* [Your First Code Contribution](#your-first-code-contribution)
* [Pull Requests](#pull-requests)

[Styleguides](#styleguides)
* [Git Commit Messages](#git-commit-messages)
* [Code Styleguide](#code-styleguide)
* [Documentation Styleguide](#documentation-styleguide)

## Code of Conduct
This project and everyone participating in it is governed by the [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior by posting an issue to the repo.

## I don't want to read this whole thing I just have a question!!!
Please check the [issues](https://github.com/Nordgedanken/intellij-autohotkey/issues) and [discussions](https://github.com/Nordgedanken/intellij-autohotkey/discussions) to see whether your question has already been answered. If not, please open a new discussion if you have a simple question, or raise a new issue if you're having a problem.

## What should I know before I get started?
### Plugin Feature Locations
The package structure has been designed to separate each plugin feature into its own package.

_All package listed in the table below are prefixed with `src\main\kotlin\de\nordgedanken\auto_hotkey`._

#### Language-related Features
| Feature | Location |
| ------- | -------- |
| Ahk token lexer (parses text to see if it's a comment, linefeed, etc) | `lang.lexer.AutoHotkey.flex` |
| Ahk grammar parser (takes tokens from lexer and groups them into elements. Eg `'#' + word = directive`) | `lang.parser.AutoHotkey.bnf` |
| Syntax Highlighting - simple (requires token lexer) | `ide.highlighter.AhkSyntaxHighlighter` |
| Syntax Highlighting - advanced (requires parser) | `ide.highlighter.AhkHighlightAnnotator` |

#### IDE Actions-related Features
| Feature | Location |
| ------- | -------- |
| Line/Block Commenter via shortcut | `ide.commenter.AhkCommenter` |
| New Ahk File action in project tree context menu | `ide.actions.AhkCreateFileAction` |
| Run button in editor gutter | `ide.linemarkers.AhkExecutableRunLineMarkerContributor` |
| Notification when editor opened without Ahk runners set | `ide.notifications.MissingAhkSdkNotificationProvider` |


#### Execution-related Features
| Feature | Location |
| ------- | -------- |
| Run configuration definition | `runconfig.core.AhkRunConfig` |
| Run configuration producer for context menu (shows a run option when right-clicking an Ahk file in the context menu | `runconfig.producer.AhkRunConfigProducer` |
| Run configuration UI | `runconfig.ui.AhkRunConfigSettingsEditor` |
| Ahk runner definition | `sdk.AhkSdkType` |
| Ahk runner renderer (for UI elements where you display a runner) | `sdk.ui.*` |

#### Miscellaneous Features
| Feature | Location |
| ------- | -------- |
| Ahk project settings | `project.configurable.AhkProjectConfigurable` |


### Design Decisions
The package structure is built based off of the package structure of existing large IntelliJ plugins, namely the [intellij-rust plugin](https://github.com/intellij-rust/intellij-rust). Although some slight differences exist, you should follow the rust plugin's structure when adding new features to the repo.

## How Can I Contribute?
### Reporting Bugs
If you see a bug with the plugin, please report it as a new issue! Due to the large scope of the plugin, we can't test everything on each commit and need your help to make sure issues are resolved appropriately.

### Suggesting Enhancements
Before suggesting a feature, please check the issues list to make sure your request has not already been submitted (or closed). If you do not see it in the list, you can raise a new issue for it.
- Currently, you do not need to suggest enhancements related to syntax highlighting or error-checking; that is already on the roadmap 

### Your First Code Contribution
You can start by looking through `beginner` and `help-wanted` issues in the issues list. (If none are present, you can look at other existing issues.) If you want to work on an issue, please add a comment to the issue saying that you want to work on it. A repository collaborator will acknowledge your comment and assign the issue to you.

To contribute:
1. Fork the project
2. Create your feature branch with the issue number (`git checkout -b feature/31-add-new-option`)
3. Commit your changes (`git commit -m '#31: Adding a new option for the thing'`)
4. Push to the branch (`git push origin feature/31-add-new-option`)
5. Open a pull request from your fork

#### Code requirements: 
- All classes must have documentation
- New code must have tests written such that the code coverage does not fall below a certain threshold.
- The changelog must be updated
- This contributing file must be updated if a new feature is added

### Local Development
After cloning the repo, you should be able to run the "Run Plugin in test IDE" run configuration to start up a test instance of the IDE with the Ahk plugin.

### Pull Requests
All PR checks must pass, and a contributor must approve your PR before it is merged. Please add a description to your PR of what is being changed/fixed. All PRs will be squash-merged, so the title of your PR must adhere to the git commit message format too.

## Styleguides
### Git Commit Messages
All git commit messages should be prefixed with the issue nubmer that the commit is related to. 
- Ex: If working on issue #32, you commit message should be `#32: Added new feature x`

### Code Styleguide
This plugin is written purely in Kotlin; please do not commit java files or your PR will be rejected. The style of all Kotlin code adheres to the [official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html). You can use the `ktlint` IntelliJ plugin from the marketplace to auto-format your code.

### Documentation Styleguide
All classes should have documentation, and any special or potentially-confusing code should have an appropriate comment as well describing the reasoning. Please raise an issue if you see a class that doesn't have documentation. 

Note: Documentation is not required on test classes. The test names should explain the purpose of the test.

