package com.demoapps.weather.utils

fun Double.formatAccuracy(decimals: Int) = "%.${decimals}f".format(this)