package ru.pirogovolive.fire.pelengj;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import static org.junit.Assert.*;

public class GeoUtilsTest {

    @Test
    public void latLonCalc() {
        GeoUtils t = new GeoUtils();

        assertEquals(new LatLng(55.75003721967313,37.61320899547458), t.LatLonCalc(new LatLng(59.9199375, 30.3410583),134.0,632.0));
    }

    @Test
    public void getDistance() {
        GeoUtils t = new GeoUtils();

        assertEquals("Distance calculation is wrong",632.0d, t.GetDistance(new LatLng(59.9199375, 30.3410583),new LatLng(55.75003721967313,37.61320899547458)),10);
        assertEquals("Distance calculation is wrong",111.0d, t.GetDistance(new LatLng(59.0, 30.0),new LatLng(60.0,30.0)),0);
        assertEquals("Distance calculation is wrong",57d, t.GetDistance(new LatLng(59.0, 30.0),new LatLng(59.0,31.0)),0);
        assertEquals("Distance calculation is wrong",111d, t.GetDistance(new LatLng(59.0, 30.0),new LatLng(58.0,30.0)),0);
        assertEquals("Distance calculation is wrong",57d, t.GetDistance(new LatLng(59.0, 30.0),new LatLng(59.0,29.0)),0);

    }

    @Test
    public void getDirection() {
        GeoUtils t = new GeoUtils();
//        assertEquals("Bearing calculation is wrong",98, t.GetDirection(new LatLng(39.099912, -94.581213),new LatLng(38.627089, -90.200203)));

//        assertEquals("Bearing calculation is wrong",128, t.GetDirection(new LatLng(59.9199375, 30.3410583),new LatLng(55.75003721967313,37.61320899547458)),0);
//        assertEquals("Bearing calculation is wrong",0, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(60.343223,30.0)),0);
//        assertEquals("Bearing calculation is wrong",90, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(59.0,31.5245)));
//        assertEquals("Bearing calculation is wrong",152, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(55.52,33.5245)));
//        assertEquals("Bearing calculation is wrong",180, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(58.111,30.0)),0);
//        assertEquals("Bearing calculation is wrong",90, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(59.0,29.111)));
//        assertEquals("Bearing calculation is wrong",113, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(57.0,25.111)));
//        assertEquals("Bearing calculation is wrong",50, t.GetDirection(new LatLng(59.0, 30.0),new LatLng(63.0,25.111)));

    }

    @Test
    public void computeHeading() {
        GeoUtils t = new GeoUtils();
        assertEquals("Bearing calculation is wrong",97, t.ComputeHeading(new LatLng(39.099912, -94.581213),new LatLng(38.627089, -90.200203)),0);

        assertEquals("Bearing calculation is wrong",134, t.ComputeHeading(new LatLng(59.9199375, 30.3410583),new LatLng(55.75003721967313,37.61320899547458)),0);
        assertEquals("Bearing calculation is wrong",0, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(60.343223,30.0)),0);
        assertEquals("Bearing calculation is wrong",89, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(59.0,31.5245)),0);
        assertEquals("Bearing calculation is wrong",150, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(55.52,33.5245)),0);
        assertEquals("Bearing calculation is wrong",180, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(58.111,30.0)),0);
        assertEquals("Bearing calculation is wrong",270, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(59.0,29.111)),0);
        assertEquals("Bearing calculation is wrong",234, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(57.0,25.111)),0);
        assertEquals("Bearing calculation is wrong",331, t.ComputeHeading(new LatLng(59.0, 30.0),new LatLng(63.0,25.111)),0);
    }

    @Test
    public void calculateBearing() {
        GeoUtils t = new GeoUtils();
        //assertEquals("Bearing calculation is wrong",97, t.CalculateBearing(new LatLng(39.099912, -94.581213),new LatLng(38.627089, -90.200203)),0);

        //assertEquals("Bearing calculation is wrong",134, t.CalculateBearing(new LatLng(59.9199375, 30.3410583),new LatLng(55.75003721967313,37.61320899547458)),0);
        //assertEquals("Bearing calculation is wrong",0, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(60.343223,30.0)),0);
        //assertEquals("Bearing calculation is wrong",89, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(59.0,31)),0);
        //assertEquals("Bearing calculation is wrong",150, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(55.52,33.5245)),0);
        assertEquals("Bearing calculation is wrong",180, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(58.111,30.0)),0);
        assertEquals("Bearing calculation is wrong",270, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(59.0,29.111)),0);
        assertEquals("Bearing calculation is wrong",234, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(57.0,25.111)),0);
        assertEquals("Bearing calculation is wrong",331, t.CalculateBearing(new LatLng(59.0, 30.0),new LatLng(63.0,25.111)),0);

    }

    @Test
    public void modTest() {
        GeoUtils t = new GeoUtils();
        assertEquals("Bearing calculation is wrong",97, t.ComputeHeading(new LatLng(39.099912, -94.581213),new LatLng(38.627089, -90.200203)),0);

    }
}