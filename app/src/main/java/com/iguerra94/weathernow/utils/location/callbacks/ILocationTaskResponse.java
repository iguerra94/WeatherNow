package com.iguerra94.weathernow.utils.location.callbacks;

import android.location.Location;

public interface ILocationTaskResponse {
    void onLocationTaskDone(Location currentLocation);
}
