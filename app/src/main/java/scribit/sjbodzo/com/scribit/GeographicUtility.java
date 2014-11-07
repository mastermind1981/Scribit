package scribit.sjbodzo.com.scribit;

public class GeographicUtility {
    static final double E_RADIUS_MI = 3959;

    /** Given two points on a map, calculate the distance between them
        Haversine : a = sin^2(delta LAT / 2) + cos(LAT1)*cos(LAT2)*sin^2(delta LONGITUDE / 2)
                      = h1 + h2*h3
                    ^-- (chord length between the points / 2) ^ 2
        c = 2 * arctan2 ( sqrt(a), sqrt(1-a) )  <- angular distance in radians, atan2 respects signs of magnitudes
        R = 6,371km ~= 3959 mi                  <- mean radius of the Earth
        d = R * c                               <- distance is product of Earth's mean radius & angular distance
    **/
    public static double haversineFunction(double lat1, double lat2, double lon1, double lon2) {

        //NOTE: values must be in radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);

        double phi = lat2 - lat1; //difference in lattitudes
        double psi = lon2 - lon1; //difference in longitudes

        //Terms for the Haversine function
        double h1 = Math.sin(phi/2)*Math.sin(phi/2);
        double h2 = Math.cos(lat1)*Math.cos(lat2);
        double h3 = Math.sin(psi/2)*Math.sin(psi/2);
        double haversine = h1 + h2*h3;

        //Calculate angular distance
        double c1 = Math.sqrt(haversine);
        double c2 = Math.sqrt(1-haversine);
        double angularDistance = 2 * Math.atan2(c1, c2);

        //Finally, compute & return distance
        double d = GeographicUtility.E_RADIUS_MI * angularDistance;
        return d;
    }
}
