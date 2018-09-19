package com.pencil.pencil.businessbook.Business.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pencil.pencil.businessbook.R;
import com.pencil.pencil.businessbook.Util.FileUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapSearchActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private ImageButton mSearchBtn;
    private View mMapView;
    private GoogleMap mMainMap;
    private Button mConfirmBtn;
    protected GoogleApiClient mGoogleApiClient;
    // Stores parameters for requests to the FusedLocationProviderApi
    private LocationManager mLocationManager;
    protected LocationRequest mLocationRequest;
    //static
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int LOCATION = 2;
    // The desired interval for location updates. Inexact. Updates may be more or less frequent.
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    //The fastest rate for active location updates. Exact. Updates will never be more frequent than this value
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static int LOCATION_PERMISION = 1;
    //variable
    private String TAG = "map";
    private Double lat;
    private Double longtitude;
    private Marker mMarker;
    private Context mContext;
    private Boolean mCurrentLocation;
    private String region="";
    private String city="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);
        mContext = MapSearchActivity.this;
        initializeMap();
        bindvariable();
        listener();
        mCurrentLocation = getIntent().getBooleanExtra("currentLocation", false);
        if (mCurrentLocation) {
        if (!FileUtils.isLocationAvailable(mContext)) {
            // show mDialog to inform the user to open his GPS
            showCheckGPSLocation(mContext);
            // check if the user didn't open GPS
        }

            //connect
            buildGoogleApiClient();

        }
    }

    private void bindvariable() {
        mSearchBtn = findViewById(R.id.searchBtn);
        mConfirmBtn = findViewById(R.id.confirmBtn);
    }

    private void listener() {
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapSearchActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lat != null) {
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = null;
                    String locality="";
                    try {
                        addresses = geocoder.getFromLocation(lat, longtitude, 1);
                        Log.d("adddress", addresses.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(addresses!=null)
                        if (addresses.size() > 0) {
                            String country = addresses.get(0).getCountryName();
                            if(addresses.get(0).getAdminArea()!=null)
                             city = addresses.get(0).getAdminArea().toString();
                            Log.i(TAG, "Place: " + city);

                            String sublocality = addresses.get(0).getSubLocality();
                             locality = addresses.get(0).getLocality();

                        }
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("lat", lat);
                    returnIntent.putExtra("long", longtitude);
                    returnIntent.putExtra("city", city);
                    returnIntent.putExtra("region", locality);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(mContext, getResources().getText(R.string.choose_location), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapSearchActivity.this);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mCurrentLocation) {
            // start location manager
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISION);
            } else {
                // connect to google api client
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCurrentLocation) {
            if (mGoogleApiClient.isConnected()) {
                stopLocationUpdates();
            }
        }

    }

    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) MapSearchActivity.this);

        }
        //
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        //set current location


        // Enable MyLocation Button in the Map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //    ActivityCompat.requestPermissions(MapViewActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        } else {


            // Get the button view
            mMainMap = googleMap;
            mMainMap.setMyLocationEnabled(true);
            //setCurrentLocation();
//            mMainMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
//                @Override
//                public boolean onMyLocationButtonClick() {
//                    if (mMainMap.getMyLocation() != null) {
//                        lat = mMainMap.getMyLocation().getLatitude();
//                        longtitude = mMainMap.getMyLocation().getLongitude();
//
//                        LatLng latLng = new LatLng(lat, longtitude);
//
//                        setMarker(latLng);
//                    }
//                    //TODO: Any custom actions
//                    return false;
//                }
//            });


            mMainMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    lat = latLng.latitude;
                    longtitude = latLng.longitude;
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    mMarker = mMainMap.addMarker(new MarkerOptions().position(latLng)
                            .title("selected Location"));
                    //mMainMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng).zoom(15).build();

                }
            });


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                setMarker(place.getLatLng());
                lat = place.getLatLng().latitude;
                longtitude = place.getLatLng().longitude;



            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == LOCATION) {
            //connect
            if (mCurrentLocation) {
                // start location manager
                mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISION);
                } else {
                    // connect to google api client
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private void setMarker(LatLng latLng) {
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mMainMap.addMarker(new MarkerOptions().position(latLng)
                .title("selected Location"));
        //mMainMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng).zoom(15).build();
        mMainMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    //    private void setCurrentLocation() {
//        LocationManager locationManager = (LocationManager)
//                getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(locationManager
//                .getBestProvider(criteria, false));
//        lat  = location.getLatitude();
//        longtitude  = location.getLongitude();
//        LatLng latLng = new LatLng(lat, longtitude);
//        setMarker(latLng);
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISION) {
            mLocationManager = (LocationManager) mContext.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
            startLocationUpdates();

        }

    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    @SuppressLint("RestrictedApi")
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (mContext != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MapSearchActivity.this);

            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        longtitude = location.getLongitude();
        LatLng latLng = new LatLng(lat, longtitude);
        setMarker(latLng);
        stopLocationUpdates();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showCheckGPSLocation(Context context) {

        AlertDialog.Builder keyBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_request_gps, null);
        keyBuilder.setView(view);

        final Dialog dialog = keyBuilder.create();

        TextView messageTv = (TextView) view.findViewById(R.id.message);
        messageTv.setText(getResources().getText(R.string.open_location));
        Button openGpsBtn = (Button) view.findViewById(R.id.openGpsBtn);
        openGpsBtn.setText(getResources().getText(R.string.ok));
        openGpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(myIntent, LOCATION);
                dialog.dismiss();
            }
        });
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        cancelBtn.setText(getResources().getText(R.string.cancel));
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();


    }
}
