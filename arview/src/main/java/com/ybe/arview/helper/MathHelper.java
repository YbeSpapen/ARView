package com.ybe.arview.helper;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.ar.core.Pose;
import com.ybe.arview.sensors.DeviceLocation;
import com.ybe.arview.sensors.DeviceOrientation;

public class MathHelper {
    private static final String TAG = "MathHelper";
    private Context context;
    private DeviceOrientation compassHelper;
    private Location currentLocation;
    private Location destination;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public MathHelper(Context context, Location destination) {
        this.context = context;
        this.destination = destination;
        compassHelper = new DeviceOrientation(context);
    }

    public void refreshLocation() {
        DeviceLocation.LocationResult locationResult = new DeviceLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                currentLocation = location;
            }
        };
        DeviceLocation myLocation = new DeviceLocation();
        myLocation.getLocation(context, locationResult);
    }

    public float getAngle() {
        float currentHeading = compassHelper.getAzimuth();

        GeomagneticField geoField = new GeomagneticField(
                Double.valueOf(destination.getLatitude()).floatValue(),
                Double.valueOf(destination.getLongitude()).floatValue(),
                Double.valueOf(destination.getAltitude()).floatValue(),
                System.currentTimeMillis());

        currentHeading += geoField.getDeclination();

        float bearing = getBearing();

        float angle = bearing - currentHeading;

        if (angle < 0) {
            angle += 360;
        }

        angle = 360 - angle;

        return angle;
    }

    public Pose getSignAnglePose() {
        float angleRad = (float) Math.toRadians(getAngle());
        float sinHalf = (float) Math.sin(angleRad / 2);
        float cosHalf = (float) Math.cos(angleRad / 2);
        return Pose.makeRotation(0, sinHalf, 0, cosHalf);
    }

    private float getBearing() {
        float bearing = currentLocation.bearingTo(destination);

        if (bearing < 0) {
            bearing += 360;
        }

        Log.i(TAG, "getBearing: " + bearing);

        return bearing;
    }

    public float getDistanceToPoi() {
        return currentLocation.distanceTo(destination);
    }

    public void onPause() {
        compassHelper.onPause();
    }

    public void onResume() {
        compassHelper.onResume();
    }
}
