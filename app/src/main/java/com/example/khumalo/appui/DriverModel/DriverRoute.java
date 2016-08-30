package com.example.khumalo.appui.DriverModel;

import com.example.khumalo.appui.Utils.Utils;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

import static com.google.maps.android.PolyUtil.decode;

/**
 * Created by KHUMALO on 8/25/2016.
 */
public class DriverRoute {
    private String routePolylineCode;
    private String key;
    private List<LatLng> wayLatLongPolyline;

    public DriverRoute() {
    }

    public DriverRoute(String routePolylineCode, String key) {
        this.routePolylineCode = routePolylineCode;
        this.key = key;
        this.wayLatLongPolyline = decode(routePolylineCode);
    }

    public String getRoutePolylineCode() {
        return routePolylineCode;
    }

    public List<LatLng> getWayLatLongPolyline() {
        return wayLatLongPolyline;
    }

    public String getKey() {
        return key;
    }

    public boolean isMatch(LatLng currentLocation, LatLng destinationLocation){
      return   Utils.isFoundAlongTheJourney(wayLatLongPolyline,currentLocation,destinationLocation);
     }
}
