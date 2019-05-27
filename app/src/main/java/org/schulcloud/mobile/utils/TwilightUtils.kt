@file:Suppress("MagicNumber", "TooGenericExceptionCaught")

package org.schulcloud.mobile.utils

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.text.format.DateUtils
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresPermission
import androidx.core.content.PermissionChecker
import androidx.core.content.getSystemService
import java.util.*
import kotlin.math.*

/**
 * Class which managing whether we are in the night or not.
 *
 * Source: androidx.appcompat.app.TwilightManager
 */
class TwilightManager private constructor(val context: Context) {
    companion object : SingletonHolder<TwilightManager, Context>({ TwilightManager(it.applicationContext) }) {
        private val TAG = TwilightManager::class.simpleName!!

        private const val SUNRISE = 6 // 6am
        private const val SUNSET = 22 // 10pm
    }

    private val locationManager = context.getSystemService<LocationManager>()!!
    private var twilightState = TwilightState()


    /**
     * Returns true we are currently in the 'night'.
     *
     * @return true if we are at night, false if the day.
     */
    fun isNight(): Boolean {
        // If the current twilight state is still valid, use it
        if (isStateValid()) return twilightState.isNight

        // Else, we will try and grab the last known location
        getLastKnownLocation()?.also {
            updateState(it)
            return twilightState.isNight
        }

        Log.i(TAG, "Could not get last known location. This is probably because the app does not"
                + " have any location permissions. Falling back to hardcoded"
                + " sunrise/sunset values.")

        // If we don't have a location, we'll use our hardcoded sunrise/sunset values.
        // These aren't great, but it's better than nothing.
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour < SUNRISE || hour >= SUNSET
    }

    @SuppressLint("MissingPermission") // permissions are checked for the needed call.
    private fun getLastKnownLocation(): Location? {
        val coarsePermission = PermissionChecker.checkSelfPermission(this.context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        val coarseLoc = if (coarsePermission == PermissionChecker.PERMISSION_GRANTED)
            getLastKnownLocationForProvider(LocationManager.NETWORK_PROVIDER)
        else null

        val finePermission = PermissionChecker.checkSelfPermission(this.context,
                Manifest.permission.ACCESS_FINE_LOCATION)
        val fineLoc = if (finePermission == PermissionChecker.PERMISSION_GRANTED)
            getLastKnownLocationForProvider(LocationManager.GPS_PROVIDER)
        else null

        // If we have both a fine and coarse location, use the latest
        return if (fineLoc != null && coarseLoc != null)
            if (fineLoc.time > coarseLoc.time) fineLoc else coarseLoc
        // Else, return the non-null one (if there is one)
        else
            fineLoc ?: coarseLoc
    }

    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    private fun getLastKnownLocationForProvider(provider: String): Location? {
        try {
            if (locationManager.isProviderEnabled(provider))
                return locationManager.getLastKnownLocation(provider)
        } catch (e: SecurityException) {
            Log.d(TAG, "Failed to get last known location: missing permission", e)
        } catch (e: Exception) {
            Log.d(TAG, "Failed to get last known location", e)
        }
        return null
    }

    private fun isStateValid() = twilightState.nextUpdate > System.currentTimeMillis()

    private fun updateState(@NonNull location: Location) {
        val now = System.currentTimeMillis()
        val calculator = TwilightCalculator.getInstance(context)

        // calculate yesterday's twilight
        calculator.calculateTwilight(now - DateUtils.DAY_IN_MILLIS, location.latitude, location.longitude)
        val yesterdaySunset = calculator.sunset

        // calculate today's twilight
        calculator.calculateTwilight(now, location.latitude, location.longitude)
        val isNight = calculator.state == TwilightCalculator.State.NIGHT
        val todaySunrise = calculator.sunrise
        val todaySunset = calculator.sunset

        // calculate tomorrow's twilight
        calculator.calculateTwilight(now + DateUtils.DAY_IN_MILLIS,
                location.latitude, location.longitude)
        val tomorrowSunrise = calculator.sunrise

        // Set next update
        val nextUpdate = if (todaySunrise == -1L || todaySunset == -1L)
        // In the case the day or night never ends the update is scheduled 12 hours later.
            now + 12 * DateUtils.HOUR_IN_MILLIS
        else
            when {
                now > todaySunset -> tomorrowSunrise
                now > todaySunrise -> todaySunset
                else -> todaySunrise
                // add some extra time to be on the safe side.
            } + DateUtils.MINUTE_IN_MILLIS

        // Update the twilight state
        twilightState = TwilightState(isNight, yesterdaySunset, todaySunrise, todaySunset, tomorrowSunrise, nextUpdate)
    }

    /**
     * Describes whether it is day or night.
     */
    private data class TwilightState(
        val isNight: Boolean = false,
        val yesterdaySunset: Long = 0,
        val todaySunrise: Long = 0,
        val todaySunset: Long = 0,
        val tomorrowSunrise: Long = 0,
        val nextUpdate: Long = 0
    )
}

/**
 * Imported from frameworks/base/services/core/java/com/android/server/TwilightCalculator.java
 *
 *
 * Calculates the sunrise and sunsets times for a given location.
 *
 * Source: androidx.appcompat.app.TwilightManager
 */
class TwilightCalculator private constructor(val context: Context) {
    companion object : SingletonHolder<TwilightCalculator, Context>({ TwilightCalculator(it.applicationContext) }) {
        private const val DEGREES_TO_RADIANS = (Math.PI / 180.0f).toFloat()

        // element for calculating solar transit.
        private const val J0 = 0.0009f

        // correction for civil twilight
        private const val ALTITUDE_CORRECTION_CIVIL_TWILIGHT = -0.104719755f

        // coefficients for calculating Equation of Center.
        private const val C1 = 0.0334196f
        private const val C2 = 0.000349066f
        private const val C3 = 0.000005236f

        private const val OBLIQUITY = 0.40927971f

        // Java time on Jan 1, 2000 12:00 UTC.
        private const val UTC_2000 = 946728000000L
    }

    enum class State {
        DAY, NIGHT
    }

    /**
     * Time of sunset (civil twilight) in milliseconds or -1 in the case the day
     * or night never ends.
     */
    var sunset: Long = 0
        private set

    /**
     * Time of sunrise (civil twilight) in milliseconds or -1 in the case the
     * day or night never ends.
     */
    var sunrise: Long = 0
        private set

    /**
     * Current state
     */
    var state: State = State.DAY
        private set

    /**
     * Calculates the civil twilight based on time and geo-coordinates.
     *
     * @param time time in milliseconds.
     * @param latitude latitude in degrees.
     * @param longitude latitude in degrees.
     */
    fun calculateTwilight(time: Long, latitude: Double, longitude: Double) {
        val daysSince2000 = (time - UTC_2000).toFloat() / DateUtils.DAY_IN_MILLIS

        // mean anomaly
        val meanAnomaly = 6.240059968 + daysSince2000 * 0.01720197

        // true anomaly
        val trueAnomaly = meanAnomaly + C1 * sin(meanAnomaly) + C2 * sin(2 * meanAnomaly) + C3 * sin(3 * meanAnomaly)

        // ecliptic longitude
        val solarLng = trueAnomaly + 1.796593063 + Math.PI

        // solar transit in days since 2000
        val arcLongitude = -longitude / 360
        val n = round(daysSince2000.toDouble() - J0.toDouble() - arcLongitude).toFloat()
        val solarTransitJ2000 = (n.toDouble() + J0.toDouble() + arcLongitude + 0.0053 * sin(meanAnomaly)
                + -0.0069 * sin(2 * solarLng))

        // declination of sun
        val solarDec = asin(sin(solarLng) * sin(OBLIQUITY.toDouble()))

        val latRad = latitude * DEGREES_TO_RADIANS

        val cosHourAngle = (sin(ALTITUDE_CORRECTION_CIVIL_TWILIGHT.toDouble()) - sin(latRad) * sin(solarDec)) / (cos(
                latRad) * cos(solarDec))
        // The day or night never ends for the given date and location, if this value is out of
        // range.
        if (cosHourAngle >= 1) {
            state = State.NIGHT
            sunset = -1
            sunrise = -1
            return
        } else if (cosHourAngle <= -1) {
            state = State.DAY
            sunset = -1
            sunrise = -1
            return
        }

        val hourAngle = (acos(cosHourAngle) / (2 * Math.PI)).toFloat()

        sunset = Math.round((solarTransitJ2000 + hourAngle) * DateUtils.DAY_IN_MILLIS) + UTC_2000
        sunrise = Math.round((solarTransitJ2000 - hourAngle) * DateUtils.DAY_IN_MILLIS) + UTC_2000

        state = if (time in (sunrise + 1)..(sunset - 1)) State.DAY else State.NIGHT
    }
}
