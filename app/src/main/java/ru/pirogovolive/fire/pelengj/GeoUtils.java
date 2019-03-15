package ru.pirogovolive.fire.pelengj;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.asin;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;



public class GeoUtils {
    public static final int R = 6371;

    public  double ComputeHeading(LatLng from, LatLng to) {
        // http://williams.best.vwh.net/avform.htm#Crs
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double dLng = toLng - fromLng;
        double heading = atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        heading =round(wrap(toDegrees(heading), -180, 180));
        if (heading<0){
            heading=360+heading;
        }
        return heading;
    }

    private double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    private double mod(double x, double m) {
        return ((x % m) + m) % m;
    }

    public LatLng LatLonCalc(LatLng pos0, double bearing, double dist) {
        double s = dist / R;
        double latD = (double) Deg2rad(pos0.latitude);
        double bearD = Deg2rad(bearing);
        double lat1 = Rad2deg(asin(cos(s) * sin(latD) + sin(s) * cos(latD) * cos(bearD)));
        double lon1 = pos0.longitude + Rad2deg(atan(sin(s) * sin(bearD) / (cos(s) * cos(latD) - sin(s) * sin(latD) * cos(bearD))));
        return new LatLng(lat1, lon1);
    }

    public double GetDistance(LatLng p0, LatLng p1) {
        //code for Distance in Kilo Meter
        double theta = p0.longitude - p1.longitude;
        double dist = sin(Deg2rad(p0.latitude)) * sin(Deg2rad(p1.latitude)) + cos(Deg2rad(p0.latitude)) * cos(Deg2rad(p1.latitude)) * cos(Deg2rad(theta));
        dist = abs(round(Rad2deg(acos(dist)) * 60 * 1.1515 * 1.609344));
        return (dist);
    }

    public long GetDirection(LatLng p0, LatLng p1) {
        double dlon = Deg2rad((p1.longitude)) - Deg2rad((p0.longitude));
        double y = sin(dlon) * cos(Deg2rad( p1.latitude));
        double x = cos(Deg2rad(p0.latitude)) * sin(Deg2rad(p1.latitude)) - sin(Deg2rad(p0.latitude)) *
                cos(Deg2rad(p1.latitude)) * cos(dlon);
        long direct = (round(Rad2deg(atan2(y, x))));
        direct = (direct + 360) % 360;
        direct = 360 - direct;

        return (direct);
    }
    public double CalculateBearing(LatLng p1, LatLng p0){
        double lat1= toRadians(p0.latitude);
        double lon1=toRadians(p0.longitude);
        double lat2=toRadians(p1.longitude);
        double lon2=toRadians(p1.longitude);

        double dLon = toRadians(lon2-lon1);

        double y = sin(dLon) * cos(lat2);
        double x = cos(lat1)*sin(lat2) - sin(lat1)*cos(lat2)*cos(dLon);
        double brng = atan2(y, x);
        brng = toDegrees(brng);
        brng = (brng+360.0)% 360; // normalize
        return brng;
    }
    public double ModTest(double d0, double d1){
        return d0%d1;
    }
    private double Rad2deg(double rad) {
        return rad * 180 / PI;

    }

    private double Deg2rad(double deg) {
        return deg * PI / 180;
    }

}
