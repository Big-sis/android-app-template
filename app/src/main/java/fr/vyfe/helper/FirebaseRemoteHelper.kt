package fr.vyfe.helper

import android.app.Activity
import android.content.pm.PackageManager
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings


class FirebaseRemoteHelper  {

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var IS_UPLOAD = "force_upload"
    private var VERSION_UPLOAD = "version_forced_upload"
    private var FORCED_UPLOAD = false

    fun initRemote(activity: Activity): Boolean {

        remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                //TODO : delete DeveloperMode
                // .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .setMinimumFetchIntervalInSeconds(4200)
                .build()
        remoteConfig.setConfigSettings(configSettings)
        remoteConfig.setDefaults(fr.vyfe.R.xml.remote_config_defaults)

        fetchUpload(activity)

        return FORCED_UPLOAD
    }

    fun fetchUpload(activity: Activity) {
        var versionStore: String
        remoteConfig.fetchAndActivate()
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        if (remoteConfig.getBoolean(IS_UPLOAD)) {
                            versionStore = remoteConfig.getString(VERSION_UPLOAD)
                            var versionApp = getVersionInfo(activity)
                            if (versionApp != versionStore) {
                                FORCED_UPLOAD = true
                            }
                        }
                    }

                }
    }

    fun getVersionInfo(activity: Activity): String {
        var versionName = ""
            val packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0)
            versionName = packageInfo.versionName
        return versionName
    }
}