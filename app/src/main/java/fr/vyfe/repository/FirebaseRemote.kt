package fr.vyfe.repository

import android.app.Activity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import fr.vyfe.R


class FirebaseRemote {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var IS_UPLOAD = "force_upload"
    private var FORCED_UPLOAD = false

    fun forceUpload(activity: Activity): Boolean {
        remoteConfig = FirebaseRemoteConfig.getInstance()

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .setMinimumFetchIntervalInSeconds(4200)
                .build()
        remoteConfig.setConfigSettings(configSettings)

        remoteConfig.setDefaults(R.xml.remote_config_defaults)

        fetchUpload(activity)

        return FORCED_UPLOAD
    }

    private fun fetchUpload(activity: Activity) {
        remoteConfig.getBoolean(IS_UPLOAD)
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        FORCED_UPLOAD = task.getResult()!!
                    }
                }
    }

}