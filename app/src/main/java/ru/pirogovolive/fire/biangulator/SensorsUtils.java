package ru.pirogovolive.fire.biangulator;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;


class SensorsUtils implements LocationListener {
    private static final float SMOOTHING_FACTOR_COMPASS = 0.1f;
    private static final String TAG = SensorsUtils.class.getSimpleName();
    private UiUtils ui;

    SensorsUtils(UiUtils ui) {
        this.ui = ui;
        ui.updateBearing();
    }

    static void InitMagnetSensors(final MapsActivity mapsActivity) {

        SensorManager sensorManager = (SensorManager) mapsActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor mSensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor mSensorMagneticField = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorEventListener mSensorEventListener;
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            //@Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {

                    mapsActivity.mGravity = event.values.clone();

                } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                    mapsActivity.mMagnetic = event.values.clone();

                }

                if (mapsActivity.mGravity != null && mapsActivity.mMagnetic != null) {
                    /* Create rotation Matrix */
                    float[] rotationMatrix = new float[9];
                    if (SensorManager.getRotationMatrix(rotationMatrix, null,
                            mapsActivity.mGravity, mapsActivity.mMagnetic)) {
                        /* Calculate Orientation */
                        float results[] = new float[3];
                        SensorManager.getOrientation(rotationMatrix,
                                results);

                        /* Get measured value */
                        MapsActivity.current_measured_bearing = (float) Math.toDegrees(results[0]);
                        if (MapsActivity.current_measured_bearing < 0) {
                            MapsActivity.current_measured_bearing += 360;
                        }

                        /* Smooth values using a 'Low Pass Filter' */
                        MapsActivity.current_measured_bearing = MapsActivity.current_measured_bearing
                                + SMOOTHING_FACTOR_COMPASS
                                * (MapsActivity.current_measured_bearing - mapsActivity.compass_last_measured_bearing);
                        //Log.i("Bearing", String.valueOf (current_measured_bearing));

                        /*
                         * Update variables for next use (Required for Low Pass
                         * Filter)
                         */
                        mapsActivity.compass_last_measured_bearing = MapsActivity.current_measured_bearing;

                    }
                }
            }
        };
        /* Initialize the gravity sensor */
        if (mSensorGravity != null) {
            Log.i(TAG, "Gravity sensor available. (TYPE_GRAVITY)");
            sensorManager.registerListener(mSensorEventListener,
                    mSensorGravity, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);// SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        } else {
            Log.i(TAG, "Gravity sensor unavailable. (TYPE_GRAVITY)");
        }

        /* Initialize the magnetic field sensor */
        if (mSensorMagneticField != null) {
            Log.i(TAG, "Magnetic field sensor available. (TYPE_MAGNETIC_FIELD)");
            sensorManager.registerListener(mSensorEventListener,
                    mSensorMagneticField, SensorManager.SENSOR_DELAY_UI, SensorManager.SENSOR_DELAY_UI);//SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        } else {
            Log.i(TAG,
                    "Magnetic field sensor unavailable. (TYPE_MAGNETIC_FIELD)");
        }

    }

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        //makeUseOfNewLocation(location);
        MapsActivity.mLastKnownLocation = location;
        ui.updateLocationUI();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }
}
