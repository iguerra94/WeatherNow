package com.iguerra94.weathernow.utils.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.iguerra94.weathernow.utils.location.callbacks.ILocationTaskResponse;

public class LocationUtils {

    public static void getDeviceLocation(Context context, boolean mLocationPermissionGranted, ILocationTaskResponse locationCallback) {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = LocationServices.getFusedLocationProviderClient(context).getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location mLastKnownLocation = task.getResult();

                        if (mLastKnownLocation != null) {
                            locationCallback.onLocationTaskDone(mLastKnownLocation);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
