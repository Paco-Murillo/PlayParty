package mx.itesm.gbvm.playparty

import android.app.Application
import android.content.Context

class AppPlayParty: Application() {
    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}