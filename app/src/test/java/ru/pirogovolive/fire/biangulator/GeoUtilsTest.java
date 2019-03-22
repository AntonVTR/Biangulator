package ru.pirogovolive.fire.biangulator;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GeoUtilsTest {

    @Test
    public void latLonCalc() {
        GeoUtils t = new GeoUtils();
        assertEquals(new LatLng(55.75003721967313, 37.61320899547458), GeoUtils.LatLonCalc(new LatLng(59.9199375, 30.3410583), 134.0, 632.0));
    }

    @Test
    public void getDistance() {
        assertEquals("Distance calculation is wrong", 632.0d, GeoUtils.GetDistance(new LatLng(59.9199375, 30.3410583), new LatLng(55.75003721967313, 37.61320899547458)), 10);
        assertEquals("Distance calculation is wrong", 111.0d, GeoUtils.GetDistance(new LatLng(59.0, 30.0), new LatLng(60.0, 30.0)), 0);
        assertEquals("Distance calculation is wrong", 57d, GeoUtils.GetDistance(new LatLng(59.0, 30.0), new LatLng(59.0, 31.0)), 0);
        assertEquals("Distance calculation is wrong", 111d, GeoUtils.GetDistance(new LatLng(59.0, 30.0), new LatLng(58.0, 30.0)), 0);
        assertEquals("Distance calculation is wrong", 57d, GeoUtils.GetDistance(new LatLng(59.0, 30.0), new LatLng(59.0, 29.0)), 0);

    }

    @Test
    public void getDirection() {

//        assertEquals("Bearing calculation is wrong",98, GeoUtils.GetDirection(new LatLng(39.099912, -94.581213),new LatLng(38.627089, -90.200203)));

//        assertEquals("Bearing calculation is wrong",128, GeoUtils.GetDirection(new LatLng(59.9199375, 30.3410583),new LatLng(55.75003721967313,37.61320899547458)),0);
//        assertEquals("Bearing calculation is wrong",0, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(60.343223,30.0)),0);
//        assertEquals("Bearing calculation is wrong",90, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(59.0,31.5245)));
//        assertEquals("Bearing calculation is wrong",152, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(55.52,33.5245)));
//        assertEquals("Bearing calculation is wrong",180, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(58.111,30.0)),0);
//        assertEquals("Bearing calculation is wrong",90, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(59.0,29.111)));
//        assertEquals("Bearing calculation is wrong",113, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(57.0,25.111)));
//        assertEquals("Bearing calculation is wrong",50, GeoUtils.GetDirection(new LatLng(59.0, 30.0),new LatLng(63.0,25.111)));

    }

    @Test
    public void computeHeading() {
        assertEquals("Bearing calculation is wrong", 97, GeoUtils.ComputeHeading(new LatLng(39.099912, -94.581213), new LatLng(38.627089, -90.200203)), 0);

        assertEquals("Bearing calculation is wrong", 134, GeoUtils.ComputeHeading(new LatLng(59.9199375, 30.3410583), new LatLng(55.75003721967313, 37.61320899547458)), 0);
        assertEquals("Bearing calculation is wrong", 0, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(60.343223, 30.0)), 0);
        assertEquals("Bearing calculation is wrong", 89, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(59.0, 31.5245)), 0);
        assertEquals("Bearing calculation is wrong", 150, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(55.52, 33.5245)), 0);
        assertEquals("Bearing calculation is wrong", 180, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(58.111, 30.0)), 0);
        assertEquals("Bearing calculation is wrong", 270, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(59.0, 29.111)), 0);
        assertEquals("Bearing calculation is wrong", 234, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(57.0, 25.111)), 0);
        assertEquals("Bearing calculation is wrong", 331, GeoUtils.ComputeHeading(new LatLng(59.0, 30.0), new LatLng(63.0, 25.111)), 0);
    }

    @Test
    public void calculateBearing() {
        GeoUtils t = new GeoUtils();
        //assertEquals("Bearing calculation is wrong",97, GeoUtils.CalculateBearing(new LatLng(39.099912, -94.581213),new LatLng(38.627089, -90.200203)),0);

        //assertEquals("Bearing calculation is wrong",134, GeoUtils.CalculateBearing(new LatLng(59.9199375, 30.3410583),new LatLng(55.75003721967313,37.61320899547458)),0);
        //assertEquals("Bearing calculation is wrong",0, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(60.343223,30.0)),0);
        //assertEquals("Bearing calculation is wrong",89, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(59.0,31)),0);
        //assertEquals("Bearing calculation is wrong",150, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(55.52,33.5245)),0);
        //assertEquals("Bearing calculation is wrong",180, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(58.111,30.0)),0);
        //assertEquals("Bearing calculation is wrong",270, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(59.0,29.111)),0);
        //assertEquals("Bearing calculation is wrong",234, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(57.0,25.111)),0);
        //assertEquals("Bearing calculation is wrong",331, GeoUtils.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(63.0,25.111)),0);

    }

    @Test
    public void modTest() {
        GeoUtils t = new GeoUtils();
        assertEquals("Bearing calculation is wrong", 97, GeoUtils.ComputeHeading(new LatLng(39.099912, -94.581213), new LatLng(38.627089, -90.200203)), 0);

    }
}