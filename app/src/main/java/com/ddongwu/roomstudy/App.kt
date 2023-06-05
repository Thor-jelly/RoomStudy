package com.ddongwu.roomstudy

import android.app.Application

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/6/5 11:12 <br/>
 */
class App : Application() {
    companion object {
        lateinit var app: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}