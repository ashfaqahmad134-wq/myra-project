package com.myra.ai.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import timber.log.Timber

class PermissionManager(private val context: Context) {

    fun getMissingPermissions(requiredPermissions: List<String>): List<String> {
        val missingPermissions = mutableListOf<String>()
        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(context, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
                Timber.d("Missing permission: $permission")
            }
        }
        return missingPermissions
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) 
            == PackageManager.PERMISSION_GRANTED
    }

    fun areAllPermissionsGranted(permissions: List<String>): Boolean {
        return permissions.all { isPermissionGranted(it) }
    }
}
