package uk.whitecrescent.waqti.android

import android.app.Application
import uk.whitecrescent.waqti.model.persistence.Caches
import uk.whitecrescent.waqti.model.persistence.Database
import com.jakewharton.threetenabp.AndroidThreeTen
import uk.whitecrescent.waqti.android.views.clearDB
import uk.whitecrescent.waqti.android.views.seedDB
import uk.whitecrescent.waqti.model.task.Priority

//import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Database.build(this)
        Caches.initialize()
    }
}