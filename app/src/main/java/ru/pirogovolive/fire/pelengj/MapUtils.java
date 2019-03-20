package ru.pirogovolive.fire.pelengj;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

class MapUtils {
    private static final String TAG = MapUtils.class.getSimpleName();


    static void DrawTheLine(MapsActivity mapsActivity, double dist) {
        //TODO check accuracy and bearing accuracy
        LatLng point0 = new LatLng(mapsActivity.mLastKnownLocation.getLatitude(), mapsActivity.mLastKnownLocation.getLongitude());
        double b = Math.round(mapsActivity.compass_last_measured_bearing);//mLastKnownLocation.getBearing();
        //b = 54;
        LatLng point1 = GeoUtils.LatLonCalc(point0, b, dist);
        PolylineOptions polylineOptions = new PolylineOptions()
                .add(point0).add(point1)
                .color(Color.MAGENTA).width(4);
        if (mapsActivity.polyline0 == null) {
            mapsActivity.firstPoint = point0;
            mapsActivity.polyline0 = MapsActivity.mMap.addPolyline(polylineOptions);
            addMarker(point0, "0" + getMarkerName(point0), String.valueOf(b));

        } else if (mapsActivity.polyline1 != null) {

            mapsActivity.polyline1.remove();
            mapsActivity.polyline1 = MapsActivity.mMap.addPolyline(polylineOptions);
            addMarker(point0, "1" + getMarkerName(point0), String.valueOf(b));

        } else {
            mapsActivity.polyline1 = MapsActivity.mMap.addPolyline(polylineOptions);
            addMarker(point0, "1" + getMarkerName(point0), String.valueOf(b) + " " + String.valueOf(GeoUtils.GetDistance(mapsActivity.firstPoint, point0)) + " " + String.valueOf(GeoUtils.ComputeHeading(mapsActivity.firstPoint, point0)));

        }

    }

    private static String getMarkerName(LatLng p) {
        return String.valueOf(p.latitude) + " " + String.valueOf(p.longitude);
    }

    private static void addMarker(LatLng p, String name, String addText) {
        MapsActivity.mMap.addMarker(new MarkerOptions().position(p)
                .title(name).snippet(addText));
    }
}
