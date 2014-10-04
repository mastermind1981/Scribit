package scribit.sjbodzo.com.scribit;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.util.Log;
import java.io.File;
import java.io.IOException;

public class ImageGPSUtility {
    private static double lattitude, longitude;
    private static Location location;

    //TODO: incorporate pref check

    public static void recordGPS(File file, Context context) {

        // Acquire a reference to the system Location Manager
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //Ensure GPS is enabled before continuing
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return;

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                location =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                lattitude = location.getLatitude();
                longitude = location.getLongitude();
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        updateLatAndLong(locationManager);

        // bake in EXIF data for GPS location to file
        if (file != null) {
            try {
                ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertGPSFormat(longitude));
                exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertGPSFormat(lattitude));

                //Set REF tags depending on if values >= 0
                if (longitude > 0) {
                    exifInterface.setAttribute(exifInterface.TAG_GPS_LONGITUDE_REF, "E");
                } else exifInterface.setAttribute(exifInterface.TAG_GPS_LONGITUDE_REF, "W");
                if (lattitude < 0) {
                    exifInterface.setAttribute(exifInterface.TAG_GPS_LATITUDE_REF, "S");
                } else exifInterface.setAttribute(exifInterface.TAG_GPS_LATITUDE_REF, "N");

                //Save EXIF information into file and log
                exifInterface.saveAttributes();
                Log.d("LAT: ", String.valueOf(lattitude));
                Log.d("LONG: ", String.valueOf(longitude));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public double getLat() { return lattitude; }
    public double getLon() { return longitude; }

    public static void updateLatAndLong(LocationManager locationManager) {
        location =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    public static final String convertGPSFormat(double gpsRef) {
        if (gpsRef < 0) gpsRef *= -1;
        String val0 = Integer.toString((int)gpsRef) + "/1,";
        gpsRef = (gpsRef % 1) * 60;
        val0 = val0 + Integer.toString((int)gpsRef) + "/1,";
        gpsRef = (gpsRef % 1) * 6000;
        val0 = val0 + Integer.toString((int)gpsRef) + "/1000";

        return val0;
    }
}