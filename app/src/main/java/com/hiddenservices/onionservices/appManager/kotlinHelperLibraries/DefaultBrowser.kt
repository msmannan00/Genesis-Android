package com.hiddenservices.onionservices.appManager.kotlinHelperLibraries

import android.app.Activity
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.core.os.bundleOf

class DefaultBrowser {

    fun Activity.getabcEnabledValue(): Boolean {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                getSystemService(RoleManager::class.java).also {
                    if (it.isRoleAvailable(RoleManager.ROLE_BROWSER) && !it.isRoleHeld(
                            RoleManager.ROLE_BROWSER
                        )
                    ) {
                        return true
                    }
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                navigateToDefaultBrowserAppsSettings()
            }
            else -> {
            }
        }
        return false
    }

    fun Activity.openSetDefaultBrowserOption() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                getSystemService(RoleManager::class.java).also {
                    if (it.isRoleAvailable(RoleManager.ROLE_BROWSER) && !it.isRoleHeld(
                            RoleManager.ROLE_BROWSER
                        )
                    ) {
                        startActivityForResult(
                            it.createRequestRoleIntent(RoleManager.ROLE_BROWSER),
                            REQUEST_CODE_BROWSER_ROLE
                        )
                    } else {
                        navigateToDefaultBrowserAppsSettings()
                    }
                }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                navigateToDefaultBrowserAppsSettings()
            }
            else -> {
            }
        }
    }

    private fun Activity.navigateToDefaultBrowserAppsSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            intent.putExtra(
                SETTINGS_SELECT_OPTION_KEY,
                DEFAULT_BROWSER_APP_OPTION
            )
            intent.putExtra(
                SETTINGS_SHOW_FRAGMENT_ARGS,
                bundleOf(SETTINGS_SELECT_OPTION_KEY to DEFAULT_BROWSER_APP_OPTION)
            )
            startActivity(intent)
        }
    }


}

const val REQUEST_CODE_BROWSER_ROLE = 1
const val SETTINGS_SELECT_OPTION_KEY = ":settings:fragment_args_key"
const val SETTINGS_SHOW_FRAGMENT_ARGS = ":settings:show_fragment_args"
const val DEFAULT_BROWSER_APP_OPTION = "default_browser"
