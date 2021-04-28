package com.example.galhorin.findplacenew;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSData extends Service implements LocationListener {

    //Location listener
    LocationManager locationManager;
    Location location;
    private Boolean isGPSEnabled;
    private Boolean isNetworkEnabled;
    //Properties:
    private Double Lat, Lng;
    Context mContext;


    public GPSData(Context context) {
        this.mContext = context;
        isGPSEnabled = false;
        isNetworkEnabled = false;
        //Get initial GPS and Network status (on/off)
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE); //start location listener
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if gps is enabled
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);//check if network is available
        //if one of the location providers is enable update the location
        if (isGPSEnabled || isNetworkEnabled)
            updateLocation();
        else {
            Toast toast = Toast.makeText(context, "GPS and Network providers are disabled",
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public void updateLocation() {
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 400, this);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //get last known gps location
            if (location != null) {
                //if location not null get coordinates
                Lat = location.getLatitude();
                Lng = location.getLongitude();
            } else {
                Toast.makeText(mContext, "GPS crushed, please turn it off and try sendong again we will use your network location", Toast.LENGTH_LONG).show();
                isGPSEnabled = false;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 400, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //get last known Network location
                    if (location != null) {
                        //if location not null get coordinates
                        Lat = location.getLatitude();
                        Lng = location.getLongitude();
                    } else {
                        Toast.makeText(mContext, "Network and GPS crushed", Toast.LENGTH_LONG).show();
                        isNetworkEnabled = false;

                    }
                }
            }
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50, 400, this);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //get last known Network location
            if (location != null) {
                //if location not null get coordinates
                Lat = location.getLatitude();
                Lng = location.getLongitude();
            } else {
                Toast.makeText(mContext, "Network/Network location service crushed, please check your network connection", Toast.LENGTH_LONG).show();
                isNetworkEnabled = false;

            }
        }
    }

    //Getters and Setters
    public Double getLat() {
        return this.Lat;
    }

    public Double getLng() {
        return this.Lng;
    }

    public Boolean getIsGPSEnabled() {
        return this.isGPSEnabled;
    }

    public Boolean getIsNetworkEnabled() {
        return this.isNetworkEnabled;
    }

    @Override
    public void onProviderDisabled(String provider) {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if gps is enabled
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);//check if network is available
        if (!isNetworkEnabled && !isGPSEnabled) {
            Toast.makeText(mContext, "GPS and network are disabled", Toast.LENGTH_LONG).show();
            isGPSEnabled = false;
            isNetworkEnabled = false;
        } else {
            if (!isNetworkEnabled) {
                Toast.makeText(mContext, "network is disabled", Toast.LENGTH_LONG).show();
                isNetworkEnabled = false;
            }
            if (!isGPSEnabled) {
                Toast.makeText(mContext, "GPS is disabled", Toast.LENGTH_LONG).show();
                isGPSEnabled = false;
            }
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER); //check if gps is enabled
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);//check if network is available
        if (isNetworkEnabled && isGPSEnabled) {
            Toast.makeText(mContext, "GPS and Network are enabled", Toast.LENGTH_LONG).show();
            isGPSEnabled = true;
            isNetworkEnabled = true;
        } else {
            if (isNetworkEnabled) {
                Toast.makeText(mContext, "Network is enabled", Toast.LENGTH_LONG).show();
                isNetworkEnabled = true;
            }
            if (isGPSEnabled) {
                Toast.makeText(mContext, "GPS is enabled", Toast.LENGTH_LONG).show();
                isGPSEnabled = true;
            }
        }
    }

    public void close() {
        //Close LocationListener
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
            location = null;
        }
    }

    public String getCity() {
        String city = "Error";

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(this.Lat, this.Lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            city = addresses.get(0).getLocality();
        }


        return city;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
