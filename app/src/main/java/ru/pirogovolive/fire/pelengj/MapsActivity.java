package ru.pirogovolive.fire.pelengj;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;


    private static final String KEY_LOCATION = "location";
    private static final float SMOOTHING_FACTOR_COMPASS = 0.5f;
    /**
     * Initialize the Sensors (Gravity and magnetic field, required as a compass
     * sensor)
     */
    float[] mGravity = null;
    float[] mMagnetic = null;
    private Polyline polyline0, polyline1;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private boolean secondAz;
    private LocationManager lm;
    private float compass_last_measured_bearing;
    private Context ctx;
    private LatLng firstPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        //if (savedInstanceState != null) {
        //    mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        //mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        //}

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        ctx = this;
        // Construct a GeoDataClient.
        //mGeoDataClient = Places.getGeoDataClient(this, null);
        initSensors();
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

    private void initSensors() {

        //LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor mSensorGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        Sensor mSensorMagneticField = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        SensorEventListener mSensorEventListener;
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                Toast.makeText(ctx, "Accuracy is " + accuracy, Toast.LENGTH_LONG).show();
            }

            //@Override
            public void onSensorChanged(SensorEvent event) {

                if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {

                    mGravity = event.values.clone();

                } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

                    mMagnetic = event.values.clone();

                }

                if (mGravity != null && mMagnetic != null) {
                    /* Create rotation Matrix */
                    float[] rotationMatrix = new float[9];
                    if (SensorManager.getRotationMatrix(rotationMatrix, null,
                            mGravity, mMagnetic)) {

                        /* Compensate device orientation */
                        // http://android-developers.blogspot.de/2010/09/one-screen-turn-deserves-another.html
                        float[] remappedRotationMatrix = new float[9];
                        switch (getWindowManager().getDefaultDisplay()
                                .getRotation()) {
                            case Surface.ROTATION_0:
                                SensorManager.remapCoordinateSystem(rotationMatrix,
                                        SensorManager.AXIS_X, SensorManager.AXIS_Y,
                                        remappedRotationMatrix);
                                break;
                            case Surface.ROTATION_90:
                                SensorManager.remapCoordinateSystem(rotationMatrix,
                                        SensorManager.AXIS_Y,
                                        SensorManager.AXIS_MINUS_X,
                                        remappedRotationMatrix);
                                break;
                            case Surface.ROTATION_180:
                                SensorManager.remapCoordinateSystem(rotationMatrix,
                                        SensorManager.AXIS_MINUS_X,
                                        SensorManager.AXIS_MINUS_Y,
                                        remappedRotationMatrix);
                                break;
                            case Surface.ROTATION_270:
                                SensorManager.remapCoordinateSystem(rotationMatrix,
                                        SensorManager.AXIS_MINUS_Y,
                                        SensorManager.AXIS_X, remappedRotationMatrix);
                                break;
                        }

                        /* Calculate Orientation */
                        float results[] = new float[3];
                        SensorManager.getOrientation(remappedRotationMatrix,
                                results);

                        /* Get measured value */
                        float current_measured_bearing = (float) (results[0] * 180 / Math.PI);
                        if (current_measured_bearing < 0) {
                            current_measured_bearing += 360;
                        }

                        /* Smooth values using a 'Low Pass Filter' */
                        current_measured_bearing = current_measured_bearing
                                + SMOOTHING_FACTOR_COMPASS
                                * (current_measured_bearing - compass_last_measured_bearing);

                        /* Update normal output */
                        TextView visual_compass_value = (TextView) findViewById(R.id.tx_azimuth);
                        visual_compass_value.setText("Azimuth is " + String.valueOf(Math
                                .round(current_measured_bearing))
                                + getString(R.string.degrees));

                        /*
                         * Update variables for next use (Required for Low Pass
                         * Filter)
                         */
                        compass_last_measured_bearing = current_measured_bearing;

                    }
                }
            }
        };
        /* Initialize the gravity sensor */
        if (mSensorGravity != null) {
            Log.i(TAG, "Gravity sensor available. (TYPE_GRAVITY)");
            sensorManager.registerListener(mSensorEventListener,
                    mSensorGravity, 1000000, 1000000);// SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        } else {
            Log.i(TAG, "Gravity sensor unavailable. (TYPE_GRAVITY)");
        }

        /* Initialize the magnetic field sensor */
        if (mSensorMagneticField != null) {
            Log.i(TAG, "Magnetic field sensor available. (TYPE_MAGNETIC_FIELD)");
            sensorManager.registerListener(mSensorEventListener,
                    mSensorMagneticField, 1000000, 1000000);//SensorManager.SENSOR_STATUS_ACCURACY_HIGH);
        } else {
            Log.i(TAG,
                    "Magnetic field sensor unavailable. (TYPE_MAGNETIC_FIELD)");
        }

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            //outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            //outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
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

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
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

    private void drawTheLine(double dist) {
        //TODO check accuracy and bearing accuracy
        LatLng point0 = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
        double b = Math.round(compass_last_measured_bearing);//mLastKnownLocation.getBearing();
        //b = 54;
        LatLng point1 = new GeoUtils().LatLonCalc(point0, b, dist);
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(point0).add(point1)
                .color(Color.MAGENTA).width(4);
        if (polyline0 == null) {
            firstPoint = point0;
            polyline0 = mMap.addPolyline(polylineOptions);
            addMarker(point0, "0"+getMarkerName(point0), String.valueOf(b));

        } else if (polyline1 != null) {

            polyline1.remove();
            polyline1 = mMap.addPolyline(polylineOptions);
            addMarker(point0, "1"+getMarkerName(point0), String.valueOf(b));

        } else {
            polyline1 = mMap.addPolyline(polylineOptions);
            GeoUtils g = new GeoUtils();
            addMarker(point0, "1"+getMarkerName(point0), String.valueOf(b) + " " + String.valueOf(g.GetDistance(firstPoint, point0)) + " " + String.valueOf(g.ComputeHeading(firstPoint, point0)));

        }

    }
    private String getMarkerName(LatLng p){
        return String.valueOf(p.latitude)+ " "+ String.valueOf(p.longitude);
    }
    private void addMarker(LatLng p, String name, String addText) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(p)
                .title(name).snippet(addText));
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
                setAcuracy();
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

    private void setAcuracy() {

        TextView tAcuracy = (TextView) findViewById(R.id.tx_acuracy);
        TextView tAzimuth = (TextView) findViewById(R.id.tx_azimuth);

        //TextView tAzAcuracy = (TextView) findViewById(R.id.tx_aziacuracy);
        if (mLastKnownLocation != null) {
            tAcuracy.setText(getResources().getString(R.string.t_accuracy) + String.valueOf(mLastKnownLocation.getAccuracy()));
            //tAzimuth.setText("Azimuth is "+String.valueOf(compass_last_measured_bearing));
            //tAzAcuracy.setText(getResources().getString(R.string.t_az_accuracy)+mLastKnownLocation.getBearingAccuracyDegrees());
        }

    }

    public void onClickMapClean(View v) {
        mMap.clear();
        Button btn = (Button) findViewById(R.id.btn_getazimuth);
        Button btnClean = (Button) findViewById(R.id.btn_clean);

        btn.setText(getResources().getString(R.string.btn_get_azimuth));
        btnClean.setEnabled(false);
        polyline0 = null;
        polyline1 = null;
    }

    public void onClickGetAzimuth(View v) {
        Button btn = (Button) findViewById(R.id.btn_getazimuth);
        Button btnClean = (Button) findViewById(R.id.btn_clean);

        ImageView img = (ImageView) findViewById(R.id.img_arrow);

        if (btn.getText() == getResources().getString(R.string.btn_get_azimuth)) {
            //Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();
            //mMap.clear();
            btnClean.setEnabled(false);

            img.setVisibility(View.VISIBLE);
            btn.setText(getResources().getString(R.string.btn_set));
            setAcuracy();
        } else if (btn.getText() == getResources().getString(R.string.btn_set)) {
            btnClean.setEnabled(true);

            img.setVisibility(View.INVISIBLE);
            btn.setText(getResources().getString(R.string.btn_get_sazimuth));
            drawTheLine(100);
            setAcuracy();

        } else if (btn.getText() == getResources().getString(R.string.btn_get_sazimuth)) {
            img.setVisibility(View.VISIBLE);
            btn.setText(getResources().getString(R.string.btn_set));
            //secondAz=true;
            setAcuracy();
            btnClean.setEnabled(false);

        }

    }

    /**
     * Enable button get azimuth if accuracy is better then 14 it means you have proper GPS cover
     */
    private void enableGetAzimuthBtn() {
        if (mLastKnownLocation.getAccuracy() < 14) {
            Button btn = (Button) findViewById(R.id.btn_getazimuth);
            btn.setEnabled(true);
        }

    }



}
