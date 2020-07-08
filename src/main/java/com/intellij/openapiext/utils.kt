package com.intellij.openapiext

import com.intellij.openapi.application.ApplicationManager

val isUnitTestMode: Boolean get() = ApplicationManager.getApplication().isUnitTestMode
