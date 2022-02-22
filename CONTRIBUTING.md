# Contributing to the IntelliJ AutoHotkey Plugin

First, thanks for taking the time to contribute! This entire plugin is built from open-source code with the efforts of developers like you who want to make better software for the benefit of everyone (in their spare time, no less!)

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
This project and all its participants are governed by the [Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code. Please report unacceptable behavior by posting an issue to the repo.

## I don't want to read this whole thing I just have a question!!!
Please check the [issues](https://github.com/Nordgedanken/intellij-autohotkey/issues) and [discussions](https://github.com/Nordgedanken/intellij-autohotkey/discussions) to see whether your question has already been answered. If not, please open a new discussion if you have a simple question, or raise a new issue if you're having a problem.

## What should I know before I get started?
### Plugin Feature Locations
The package structure has been designed to separate each plugin feature into its own package.

_All packages listed below are prefixed with `src\main\kotlin\de\nordgedanken\auto_hotkey`_

#### Language-related Features
| Feature                                                                             | Location                                |
|-------------------------------------------------------------------------------------|-----------------------------------------|
| Ahk token lexer (parses text to see if it's a comment, linefeed, etc)               | `lang.lexer.AutoHotkey.flex`            |
| Ahk grammar parser (groups lexer tokens into elements. Eg `'#' + word = directive`) | `lang.parser.AutoHotkey.bnf`            |
| Syntax Highlighting - simple (requires token lexer)                                 | `ide.highlighter.AhkSyntaxHighlighter`  |
| Syntax Highlighting - advanced (requires parser)                                    | `ide.highlighter.AhkHighlightAnnotator` |

#### IDE Actions-related Features
| Feature                                                 | Location                                                |
|---------------------------------------------------------|---------------------------------------------------------|
| Line/Block commenting via shortcut                      | `ide.commenter.AhkCommenter`                            |
| New Ahk File action in project tree context menu        | `ide.actions.AhkCreateFileAction`                       |
| Compile to exe action in project tree context menu      | `ide.actions.AhkCompileToExeAction`                     |
| Run button in editor gutter                             | `ide.linemarkers.AhkExecutableRunLineMarkerContributor` |
| Notification when editor opened without Ahk runners set | `ide.notifications.MissingAhkSdkNotificationProvider`   |
| Quick Documentation popup                               | `ide.documentation.AhkDocumentationProvider`            |


#### Execution-related Features
| Feature                                                                       | Location                                  |
|-------------------------------------------------------------------------------|-------------------------------------------|
| Run configuration definition                                                  | `runconfig.core.AhkRunConfig`             |
| Run configuration producer (shows run option when right-clicking an Ahk file) | `runconfig.producer.AhkRunConfigProducer` |
| Run configuration UI                                                          | `runconfig.ui.AhkRunConfigSettingsEditor` |
| Ahk runner definition                                                         | `sdk.AhkSdkType`                          |
| Ahk runner renderer (for UI elements where you display a runner)              | `sdk.ui.*`                                |

#### Miscellaneous Features
| Feature              | Location                                      |
|----------------------|-----------------------------------------------|
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
You can start by looking through `beginner` and `help-wanted` issues in the issues list. (If none are present, you can look at other existing issues.)

To contribute:
1. Please find the issue that you want to work on (or create a new one). 
1. Ask for the issue to be assigned to you (this helps all collaborators know who is working on it). 
1. Fork the project
1. Create your feature branch with the issue number (`git checkout -b feature/31-add-new-option`)
1. Commit your changes (`git commit -m '#31: Adding a new option for the thing'`)
1. Run the pre-PR checks specified in the [Pull Requests](#pull-requests) section below.
1. Push to the branch (`git push origin feature/31-add-new-option`)
1. Open a pull request from your fork

#### Code requirements: 
- All classes must have documentation
- New code must have tests written such that the code coverage does not fall below the configured threshold.
- The changelog must be updated
- This contributing file must be updated if a new feature or extension point is added

### Local Development
After cloning the repo, you should be able to run the "Run Plugin in test IDE" run configuration to start up a test instance of the IDE with the Ahk plugin.

### Pull Requests
Before opening a PR, please run the following PR checks (saves time and headache with failed PR builds):

- Check that any new file extensions you've added comply with the [`.editorconfig`](.editorconfig) file in the repo, or otherwise add a new exception.
- Run the "Run KtLinter" run configuration.
- Run the "Run Test Coverage" run configuration.
- Run the "Run Plugin Verifier" run configuration.

All PR checks must pass, and a contributor must approve your PR before it is merged. Please add a description to your PR of what is being changed/fixed. 

**The title of your PR must adhere to the [git commit message format](#git-commit-messages).** Your PR will be squash-merged, so your PR's title will become the message of the squashed commit. 

## Styleguides
### Git Commit Messages
All git commit messages in a pull request must follow this format:

`#<issue number>: <description of what the commit pertains to (2+ words, no newlines)>`

This format enables linking commit changes with the issue they are associated with and helps keep the git
history readable when performing searches.

> As an example, if you are working on issue [#49](https://github.com/Nordgedanken/intellij-autohotkey/issues/49) to enable hyperlinks in console messages, your commit message should be something like 
>
> `#49: Added console filter to enable file hyperlinks in the console when an error is printed`

### Code Styleguide
This plugin is written purely in Kotlin; please do not commit java files or your PR will be rejected. 

The style of all Kotlin code adheres to the [official Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html). You can use the `ktlint` IntelliJ plugin from the marketplace to auto-format your code.

### Documentation Styleguide
All classes should have documentation, and any special or potentially-confusing code should have an appropriate comment as well describing the reasoning. Please raise an issue if you see a class that doesn't have documentation. 

Note: Documentation is not required on test classes. The test names should explain the purpose of the test.

