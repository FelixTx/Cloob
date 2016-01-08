package fr.turfu.cloobi;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

/**
 * Created by FT on 08/01/2016.
 */
public class Station {
    public GeoPoint pos;
    public String nom;

    public Station(GeoPoint p) {
        pos=p;
        nom="Commerce";
    }
}

