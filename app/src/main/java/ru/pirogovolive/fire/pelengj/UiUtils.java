package ru.pirogovolive.fire.pelengj;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import static com.google.android.gms.internal.zzahf.runOnUiThread;
import static ru.pirogovolive.fire.pelengj.MapsActivity.mLastKnownLocation;
import static ru.pirogovolive.fire.pelengj.MapsActivity.mMap;

class UiUtils {
    static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = UiUtils.class.getSimpleName();
    private final Activity activity;
    private final Context ctx;
    private Thread thread;


    UiUtils(MapsActivity activity, Context ctx) {
        this.activity = activity;
        this.ctx = ctx;

    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    void updateLocationUI() {

        if (mLastKnownLocation.hasAccuracy()) {
            enableGetAzimuthBtn();
        }
        if (mMap == null) {
            return;
        }
        try {
            if (MapsActivity.mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                setAccuracy();
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * set textview Accuracy
     */
    private void setAccuracy() {

        TextView tAccuracy = activity.findViewById(R.id.tx_acuracy);
        if (mLastKnownLocation != null) {
            tAccuracy.setText(activity.getResources().getString(R.string.t_accuracy, Math.round(mLastKnownLocation.getAccuracy())));
        }

    }

    /**
     * Update Bearing TextView time period
     */
    void updateBearing() {
        thread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                /* Update normal output */
                                TextView visual_compass_value = activity.findViewById(R.id.tx_azimuth);
                                visual_compass_value.setText(activity.getResources().getString(R.string.t_azimuth, Math
                                        .round(MapsActivity.current_measured_bearing), ctx.getString(R.string.degrees)));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Log.d(TAG, "Something went wrong during bearing update");
                }
            }
        };

        thread.start();
    }

    /**
     * Enable button get azimuth if accuracy is better then 14 it means you have proper GPS cover
     */
    private void enableGetAzimuthBtn() {
        if (mLastKnownLocation.getAccuracy() < 14) {
            Button btn = activity.findViewById(R.id.btn_getazimuth);
            btn.setEnabled(true);
        }

    }

    /**
     * Prompts the user for permission to use the device location.
     */
    void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(ctx,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            MapsActivity.mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

}
