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


public class GeoUtils {
    private static final int R = 6371;

    /**
     * This method calculate a point far from zero position with azimuth and distance
     *
     * @param pos0    init position define by GPS
     * @param bearing azimuth to any point
     * @param dist    some distance, length of the line
     * @return coordinates of second point that far from @pos0 on @dist and @bearing
     */
    static LatLng LatLonCalc(LatLng pos0, double bearing, double dist) {
        double s = dist / R;
        double latD = toRadians(pos0.latitude);
        double bearD = toRadians(bearing);
        double lat1 = toDegrees(asin(cos(s) * sin(latD) + sin(s) * cos(latD) * cos(bearD)));
        double lon1 = pos0.longitude + toDegrees(atan(sin(s) * sin(bearD) / (cos(s) * cos(latD) - sin(s) * sin(latD) * cos(bearD))));
        return new LatLng(lat1, lon1);
    }

    public static long GetDirection(LatLng p0, LatLng p1) {
        double dlon = toRadians((p1.longitude)) - toRadians((p0.longitude));
        double y = sin(dlon) * cos(toRadians(p1.latitude));
        double x = cos(toRadians(p0.latitude)) * sin(toRadians(p1.latitude)) - sin(toRadians(p0.latitude)) *
                cos(toRadians(p1.latitude)) * cos(dlon);
        long direct = (round(toDegrees(atan2(y, x))));
        direct = (direct + 360) % 360;
        direct = 360 - direct;

        return (direct);
    }

    private static double toRadians(double deg) {
        return deg * PI / 180;
    }

    static double ComputeHeading(LatLng from, LatLng to) {
        // http://williams.best.vwh.net/avform.htm#Crs
        double fromLat = toRadians(from.latitude);
        double fromLng = toRadians(from.longitude);
        double toLat = toRadians(to.latitude);
        double toLng = toRadians(to.longitude);
        double dLng = toLng - fromLng;
        double heading = atan2(
                sin(dLng) * cos(toLat),
                cos(fromLat) * sin(toLat) - sin(fromLat) * cos(toLat) * cos(dLng));
        heading = round(wrap(toDegrees(heading), -180, 180));
        if (heading < 0) {
            heading = 360 + heading;
        }
        return heading;
    }

    private static double wrap(double n, double min, double max) {
        return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
    }

    private static double mod(double x, double m) {
        return ((x % m) + m) % m;
    }

    /**
     * This method calculate a distance between 2 points
     *
     * @param p0 zero point coordinates
     * @param p1 another coordinates
     * @return distance in km
     */
    static double GetDistance(LatLng p0, LatLng p1) {
        //code for Distance in Kilo Meter
        double theta = p0.longitude - p1.longitude;
        double dist = sin(toRadians(p0.latitude)) * sin(toRadians(p1.latitude)) + cos(toRadians(p0.latitude)) * cos(toRadians(p1.latitude)) * cos(toRadians(theta));
        dist = abs(round(toDegrees(acos(dist)) * 60 * 1.1515 * 1.609344));
        return (dist);
    }

    public double CalculateBearing(LatLng p1, LatLng p0) {
        double lat1 = toRadians(p0.latitude);
        double lon1 = toRadians(p0.longitude);
        double lat2 = toRadians(p1.longitude);
        double lon2 = toRadians(p1.longitude);

        double dLon = toRadians(lon2 - lon1);

        double y = sin(dLon) * cos(lat2);
        double x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon);
        double brng = atan2(y, x);
        brng = toDegrees(brng);
        brng = (brng + 360.0) % 360; // normalize
        return brng;
    }

}
