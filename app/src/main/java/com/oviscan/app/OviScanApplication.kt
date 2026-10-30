package com.oviscan.app

import android.app.Application
import com.oviscan.app.data.local.OviScanDatabase
import com.oviscan.app.data.local.repository.ScreeningRepository

class OviScanApplication : Application() {

    val database: OviScanDatabase by lazy {
        OviScanDatabase.getDatabase(this)
    }

    val repository: ScreeningRepository by lazy {
        ScreeningRepository(
            sheepDao = database.sheepDao(),
            measurementDao = database.measurementDao(),
            context = this
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: OviScanApplication
            private set
    }
}
