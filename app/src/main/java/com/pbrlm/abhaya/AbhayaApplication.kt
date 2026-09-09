package com.pbrlm.abhaya

import android.app.Application
import com.pbrlm.abhaya.data.demo.DemoDataSeeder
import com.pbrlm.abhaya.data.user.UserProvider
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class AbhayaApplication : Application() {

    @Inject lateinit var demoDataSeeder: DemoDataSeeder
    @Inject lateinit var userProvider: UserProvider

    override fun onCreate() {
        super.onCreate()
        // Seed demo data in debug builds only
        if (BuildConfig.DEBUG) {
            CoroutineScope(Dispatchers.IO).launch {
                demoDataSeeder.seed(userProvider.getCurrentUserId())
            }
        }
    }
}
