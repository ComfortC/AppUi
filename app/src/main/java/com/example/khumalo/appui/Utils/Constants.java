package com.example.khumalo.appui.Utils;

/**
 * Created by KHUMALO on 8/15/2016.
 */
public final class Constants {

    public static final String PACKAGE_NAME = "com.example.khumalo.dire";
    public static final String LISTEN_FOR_DRIVER_CHANGES_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";
    public static final String RESULT_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";
    public static final String SERVICE_RESULT_FLAG = PACKAGE_NAME+"Service_Flag";


    public static final String FORECAST_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    public static final String REGION_PARAM = "za";
    public static final String DESTINATION_EXTRA = "WHERE_TO";
    public static final String isLoggedIn = "CHECK";
    public static final String CURRENT_LOCATION = "CURRENT_LOCATION";
    public static final String CAR_LOCATION = "CarLocation";


    public static final String FIREBASE_URL = "https://handy-sensor-136618.firebaseio.com/";
    public static final String DRIVERS_URL = "Drivers";
    public static final String LOCATIONS_URL = "Locations";
    public static final String ROUTES_URL= "Routes";
    public static final String CLIENTS_URL = "Clients";

    public static final String FIREBASE_DRIVERS_URL = FIREBASE_URL+DRIVERS_URL;
    public static final String FIREBASE_LOCATIONS_URAL = FIREBASE_URL+LOCATIONS_URL;
    public static final String FIREBASE_ROUTES_URL = FIREBASE_URL+ROUTES_URL;

    public static final String DRIVER_KEY = PACKAGE_NAME+"KEY";
    public static final String CLIENT_KEY = PACKAGE_NAME+"CLIENTKEY";
    public static final String CLIENT_RECEIVED_DRIVER_KEY = PACKAGE_NAME+"Client_Received_Driver_Key";
    public static final String ROUTE_POLYLINE_STRING = PACKAGE_NAME+"Route_Polyline_Code";

    public static final double TOLERANCE_IN_METERS = 300;

    public static final String USER_STATUS = "userStatus";
    public static final String LOCATION_SHARE_STATUS = "locationShareStatus";
    public static final String LOCATION_SHARE_STATUS_FLAG = "locationStatusFlag" ;
    public static final String DESTINATION_FLAG = "destinationSetFlag";

    public static final int MY_LOCATION_REQUEST_CODE = 25;
    //Location Services constants
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Stores the lat / long pairs in a text file
    public static final String LOCATION_FILE = "sdcard/location.txt";
    // Stores the connect / disconnect data in a text file
    public static final String LOG_FILE = "sdcard/log.txt";


   // Notification Center
    public static final String FIREBASE_NOTIFICATION_TEST_MESSAGE = FIREBASE_URL+"Notification";
    public static final int AVAILABLE_DRIVER_NOTIFICATION = 133;



    public static final String CLIENT_DESTINATION_LATITUDE = "Client_Destination_latitude";
    public static final String CLIENT_DESTINATION_LONGITUDE = "Client_Destination_longitude";
    public static final String CLIENT_FULL_NAME =  "Clientele_full_name";

    public static final String CLIENT_LOCATION_LATITUDE = "Client_Location_latitude";
    public static final String CLIENT_LOCATION_LONGITUDE = "Client_Location_longitude";

    public static final String DRIVER_DESTINATION_LATITUDE = "Driver_Destination_latitude";
    public static final String DRIVER_DESTINATION_LANGITUDE = "Driver_Destination_longitude";


    public static final String CLIENT_IMAGE_URI = "Client_Image_Uri";


    /**
     * Suppress default constructor for noninstantiability
     */
    private Constants() {
        throw new AssertionError();
    }

}

