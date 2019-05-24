package com.ng.thanoslayout

import android.util.Log

inline fun Any.log(message: String) = Log.d(this::class.simpleName, message)