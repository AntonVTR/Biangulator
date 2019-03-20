package ru.pirogovolive.fire.pelengj;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_LOCATION = "location";
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MapsActivity.class.getSimpleName();
    public static GoogleMap mMap;
    public Polyline polyline0, polyline1;
    public Location mLastKnownLocation;
    public float compass_last_measured_bearing;
    public LatLng firstPoint;
    public float current_measured_bearing;
    /**
     * Initialize the Sensors (Gravity and magnetic field, required as a compass
     * sensor)
     */
    float[] mGravity = null;
    float[] mMagnetic = null;
    private Thread thread;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            //mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        ctx = this;
        // Construct a GeoDataClient.
        //mGeoDataClient = Places.getGeoDataClient(this, null);
        getDeviceLocation();
        // Construct a PlaceDetectionClient.
        //mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                mLastKnownLocation = location;
                updateLocationUI();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //TODO create dialog to enter point0, bearing and share it. via

    }

    protected void onResume() {
        super.onResume();
        SensorsUtils.InitMagnetSensors(this);
        updateBearing();
    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            //outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //LatLng sydney =new LatLng(-34.0, 151.0);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        //updateLocationUI();

        // Get the current location of the device and set the position of the map.
        //getDeviceLocation();

    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            //mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            //String str=getResources().getString(R.string.toast_cant_define);
                            Toast.makeText(ctx, "str", Toast.LENGTH_LONG).show();
                            //TODO what to do if don't have coordinates
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {

        if (mLastKnownLocation.hasAccuracy()) {
            enableGetAzimuthBtn();
        }
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
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
     * Update Bearing TextView time period
     */
    private void updateBearing() {
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
                                TextView visual_compass_value = findViewById(R.id.tx_azimuth);
                                visual_compass_value.setText("Azimuth is " + String.valueOf(Math
                                        .round(current_measured_bearing))
                                        + getString(R.string.degrees));
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
     * set textview Accuracy
     */
    private void setAccuracy() {

        TextView tAccuracy = findViewById(R.id.tx_acuracy);
        if (mLastKnownLocation != null) {
            tAccuracy.setText(getResources().getString(R.string.t_accuracy) + String.valueOf(mLastKnownLocation.getAccuracy()));
        }

    }

    /**
     * Button clean map
     */
    public void onClickMapClean(View v) {
        mMap.clear();
        Button btn = findViewById(R.id.btn_getazimuth);
        Button btnClean = findViewById(R.id.btn_clean);

        btn.setText(getResources().getString(R.string.btn_get_azimuth));
        btnClean.setEnabled(false);
        polyline0 = null;
        polyline1 = null;
    }

    /**
     * Button Get azimuth
     */
    public void onClickGetAzimuth(View v) {
        Button btn = findViewById(R.id.btn_getazimuth);
        Button btnClean = findViewById(R.id.btn_clean);

        ImageView img = findViewById(R.id.img_arrow);

        if (btn.getText() == getResources().getString(R.string.btn_get_azimuth)) {
            //Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
            //mMap.clear();
            btnClean.setEnabled(false);

            img.setVisibility(View.VISIBLE);
            btn.setText(getResources().getString(R.string.btn_set));
            setAccuracy();
        } else if (btn.getText() == getResources().getString(R.string.btn_set)) {
            btnClean.setEnabled(true);

            img.setVisibility(View.INVISIBLE);
            btn.setText(getResources().getString(R.string.btn_get_sazimuth));
            MapUtils.DrawTheLine(this, 100);
            setAccuracy();

        } else if (btn.getText() == getResources().getString(R.string.btn_get_sazimuth)) {
            img.setVisibility(View.VISIBLE);
            btn.setText(getResources().getString(R.string.btn_set));
            //secondAz=true;
            setAccuracy();
            btnClean.setEnabled(false);

        }

    }

    /**
     * Enable button get azimuth if accuracy is better then 14 it means you have proper GPS cover
     */
    private void enableGetAzimuthBtn() {
        if (mLastKnownLocation.getAccuracy() < 14) {
            Button btn = findViewById(R.id.btn_getazimuth);
            btn.setEnabled(true);
        }

    }


}
