package com.autohotkey.util

import com.intellij.util.concurrency.AppExecutorUtil
import java.util.concurrent.Future

fun <T> runInBg(runnable: () -> T): Future<T> = AppExecutorUtil.getAppExecutorService().submit(runnable)
