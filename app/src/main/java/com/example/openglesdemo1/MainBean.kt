package com.example.openglesdemo1

import android.app.Activity

data class MainBean<T : Activity>(val name: String, val clazz: Class<T>?)