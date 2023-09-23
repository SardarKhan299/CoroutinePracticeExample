package com.qibla.qiblacompass.prayertime.coroutinepracticeexamples

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView


class Compass(context: Context) : SensorEventListener {
    interface CompassListener {
        fun onNewAzimuth(azimuth: Float)
    }

    private var listener: CompassListener? = null
    private val sensorManager: SensorManager
    private val asensor: Sensor
    private val msensor: Sensor
    private val aData = FloatArray(3)
    private val mData = FloatArray(3)
    private val R = FloatArray(9)
    private val I = FloatArray(9)
    private var azimuthFix = 0f
    private var currentAzimuth = 0f
    private val prefs: SharedPreferences

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        asensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        msensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        prefs = context.getSharedPreferences("", Context.MODE_PRIVATE)
    }

    fun start(context: Context) {
        sensorManager.registerListener(
            this, asensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            this, msensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        val manager = context.packageManager
        val haveAS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
        val haveCS = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)
        if (!haveAS || !haveCS) {
            sensorManager.unregisterListener(this, asensor)
            sensorManager.unregisterListener(this, msensor)
            Log.e(TAG, "Device don't have enough sensors")
            dialogError(context)
        }
    }

    private fun dialogError(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setCancelable(false)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setMessage("Sensor Not Exist")
        builder.setNegativeButton(
            "OK"
        ) { dialog, which ->
            dialog.dismiss()
            if (context is Activity) context.finish()
        }
        builder.create().show()
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun setAzimuthFix(fix: Float) {
        azimuthFix = fix
    }

    fun resetAzimuthFix() {
        setAzimuthFix(0f)
    }

    fun setListener(l: CompassListener?) {
        listener = l
    }

    override fun onSensorChanged(event: SensorEvent) {
        val alpha = 0.97f
        //boolean hasAS = false, hasMS = false;
        synchronized(this) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                aData[0] = alpha * aData[0] + (1 - alpha) * event.values[0]
                aData[1] = alpha * aData[1] + (1 - alpha) * event.values[1]
                aData[2] = alpha * aData[2] + (1 - alpha) * event.values[2]
            }
            if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                mData[0] = alpha * mData[0] + (1 - alpha) * event.values[0]
                mData[1] = alpha * mData[1] + (1 - alpha) * event.values[1]
                mData[2] = alpha * mData[2] + (1 - alpha) * event.values[2]
            }
            val success = SensorManager.getRotationMatrix(R, I, aData, mData)
            if (success) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                var azimuth =
                    Math.toDegrees(orientation[0].toDouble()).toFloat() // orientation
                azimuth = (azimuth + azimuthFix + 360) % 360
                //azimuth = (azimuth + azimuthFix + 360) % 294;

                // Log.d(TAG, "azimuth (deg): " + azimuth);
                if (listener != null) {
                    listener!!.onNewAzimuth(azimuth)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    fun adjustGambarDial(imageDial: ImageView, azimuth: Float) {
        // Log.d(TAG, "will set rotation from " + currentAzimuth + " to "                + azimuth);
        val an: Animation = RotateAnimation(
            -currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        imageDial.startAnimation(an)
    }

    fun adjustArrowQiblat(qiblatIndicator: ImageView, azimuth: Float) {
        //Log.d(TAG, "will set rotation from " + currentAzimuth + " to "                + azimuth);
        val kiblat_derajat = GetFloat("kiblat_derajat")
        val an: Animation = RotateAnimation(
            -currentAzimuth + kiblat_derajat, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        qiblatIndicator.startAnimation(an)
        if (kiblat_derajat > 0) {
            qiblatIndicator.visibility = View.VISIBLE
        } else {
            qiblatIndicator.visibility = View.INVISIBLE
            qiblatIndicator.visibility = View.GONE
        }
    }

    fun SaveFloat(Judul: String?, bbb: Float?) {
        val edit = prefs.edit()
        edit.putFloat(Judul, bbb!!)
        edit.apply()
    }

    fun GetFloat(Judul: String?): Float {
        return prefs.getFloat(Judul, 0f)
    }

    companion object {
        private val TAG = Compass::class.java.simpleName
    }
}