package zook.indiamoves.in.zook.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import zook.indiamoves.in.zook.R;
import zook.indiamoves.in.zook.Utils.AlertDialogMessages;
import zook.indiamoves.in.zook.Utils.RateUsMessage;
import zook.indiamoves.in.zook.activities.DrawerActivities.AboutUsActivity;
import zook.indiamoves.in.zook.activities.DrawerActivities.CancleRideActivity;
import zook.indiamoves.in.zook.activities.DrawerActivities.FeedbackActivity;
import zook.indiamoves.in.zook.activities.DrawerActivities.PaymentModeActivity;
import zook.indiamoves.in.zook.activities.DrawerActivities.ProfileActivity;
import zook.indiamoves.in.zook.activities.DrawerActivities.SummaryAndTripsActivity;
import zook.indiamoves.in.zook.activities.DrawerActivities.TrackYourRideActivity;
import zook.indiamoves.in.zook.adapters.PlaceCompleteAdapter;

public class MainMapActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, android.location.LocationListener, GoogleMap.OnCameraChangeListener, GoogleApiClient.OnConnectionFailedListener, PlaceSelectionListener, GoogleMap.OnMarkerClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationDrawer;
    private AutoCompleteTextView mAutocompleteViewSource, mAutoCompleteViewDestination;
    private GoogleApiClient mGoogleApiClient;
    private PlaceCompleteAdapter mAdapter;
    private SupportMapFragment sMapfragment;
    private Button btn_book_now, btn_quickride;
    private LatLng source, dst;
    private GoogleMap mMap;
    private ImageView get_current_location, icList;
    private TextView mPlaceDetailsText, mPlaceDetailsAttribution;
    private Marker marker_src, marker_dst;
    private String final_address;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(7.194256, 60.069114), new LatLng(35.581157, 97.308106));
    private LinearLayout navHeaderLayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        sMapfragment = SupportMapFragment.newInstance();
        RateUsMessage.app_launched(this);

        // Setting Drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.openDrawer, R.string.closeDrawer);
        // Setting navigation view

        navigationDrawer = (NavigationView) findViewById(R.id.nvView);
        navigationDrawer.setNavigationItemSelectedListener(this);
        View headerView = navigationDrawer.getHeaderView(0);
        navHeaderLayout = (LinearLayout) headerView.findViewById(R.id.nav_header_layout);
        navHeaderLayout.setOnClickListener(this);

        mPlaceDetailsText = (TextView)findViewById(R.id.place_details);
        mPlaceDetailsAttribution = (TextView)findViewById(R.id.place_attribution);
        //Setting navigation drawer
        icList = (ImageView) findViewById(R.id.ic_list);
        icList.setOnClickListener(this);

        //get current location vector image
        get_current_location = (ImageView) findViewById(R.id.get_current_location);
        get_current_location.setOnClickListener(this);

        //Buttons
        btn_book_now = (Button) findViewById(R.id.btn_book_now);
        btn_quickride = (Button) findViewById(R.id.btn_quickride);
        btn_book_now.setOnClickListener(this);
        btn_quickride.setOnClickListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //GoogleAPI
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,0/*client id */,this)
                .addApi(Places.GEO_DATA_API)
                .build();

        // LocationManager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 200, (android.location.LocationListener) this);

        mAdapter = new PlaceCompleteAdapter(this, mGoogleApiClient, BOUNDS_INDIA, null);

        mAutocompleteViewSource = (AutoCompleteTextView) findViewById(R.id.autocomplete_source);
        mAutoCompleteViewDestination = (AutoCompleteTextView) findViewById(R.id.autocomplete_destination);
        mAutocompleteViewSource.setHint("Getting Your Location");
        mAutoCompleteViewDestination.setHint("Please Wait...");

        mAutocompleteViewSource.setAdapter(mAdapter);
        mAutoCompleteViewDestination.setAdapter(mAdapter);

        mAutocompleteViewSource.setOnItemClickListener(mAutocompleteClickListener);
        mAutoCompleteViewDestination.setOnItemClickListener(mAutocompleteClickListenerDestination);

        sMapfragment.getMapAsync(this);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            // Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);


            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            // Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };


    private AdapterView.OnItemClickListener mAutocompleteClickListenerDestination
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            // Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackDestination);


            Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();
            // Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                // Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            source = place.getLatLng();
            if (marker_src != null) {
                marker_src.remove();


            }
            marker_src = mMap.addMarker(new MarkerOptions().position(source));
            marker_src.setDraggable(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source, 16));


            mPlaceDetailsText.setText(formatPlaceDetails(getResources(),place.getName(),place.getId(),place.getAddress()
            ,place.getPhoneNumber(),place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            //Log.i(TAG, "Place details received: " + place.getName());
            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources resources, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
    return Html.fromHtml(resources.getString(R.string.PlaceDetail,name,id,address,phoneNumber,websiteUri));
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackDestination
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                // Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            dst = place.getLatLng();
            if (marker_dst != null) {
                marker_dst.remove();


            }
            marker_dst = mMap.addMarker(new MarkerOptions().position(dst).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dst,12));

            //  mMap.addPolyline(new PolylineOptions().add(dst).add(source).color(Color.BLUE));


            mPlaceDetailsText.setText(formatPlaceDetails(getResources(),place.getName(),place.getId(),place.getAddress()
                    ,place.getPhoneNumber(),place.getWebsiteUri()));

            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }

            //Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in India and move the camera
        LatLng India = new LatLng(22, 82);


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(India, 4));
    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        mAutocompleteViewSource.setHint("Enter Your Pickup Location");


        mAutoCompleteViewDestination.setHint("Enter Your Drop Location");
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                final String address = addresses.get(0).getAddressLine(0);
                final String city = addresses.get(0).getLocality();
                final String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                final String knownName = addresses.get(0).getFeatureName();
                final String subAdminArea = addresses.get(0).getSubAdminArea();
                final String subLocality = addresses.get(0).getSubLocality();
                final String a3 = addresses.get(0).getSubThoroughfare();
                final String a4 = addresses.get(0).getThoroughfare();

                get_current_location.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final_address = knownName + ", " + subLocality + ", " + subAdminArea + ", " + city;
                        mAutocompleteViewSource.setText(final_address);
                    }
                });

                mAutocompleteViewSource.clearFocus();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        MarkerOptions marker_centre = new MarkerOptions();
        LatLng centreofmap = mMap.getCameraPosition().target;
        mMap.addMarker(marker_centre);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceSelected(Place place) {

    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }


    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;

            case R.id.ic_list:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                drawerLayout.setDrawerListener(actionBarDrawerToggle);
                navigationDrawer.setNavigationItemSelectedListener(this);
                navigationDrawer.getMenu();
                break;

            case R.id.nav_header_layout:
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                break;

            case R.id.btn_quickride:
                AlertDialogMessages.quickride_options(this);
                break;

            case R.id.btn_book_now:
                 Intent submit=new Intent(MainMapActivity.this,BookRideActivity.class);
                 startActivity(submit);
                break;

        }


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int ids = item.getItemId();
        switch (ids) {

            case R.id.summary_and_trips:
                Intent intent = new Intent(this, SummaryAndTripsActivity.class);
                startActivity(intent);
                break;

            case R.id.payment:
                Intent intent1 = new Intent(this, PaymentModeActivity.class);
                startActivity(intent1);
                break;

            case R.id.track_your_ride:
                Intent intent2 = new Intent(this, TrackYourRideActivity.class);
                startActivity(intent2);
                break;

            case R.id.cancel_ride:
                Intent intent3 = new Intent(this, CancleRideActivity.class);
                startActivity(intent3);
                break;

            case R.id.about_us:
                Intent intent4 = new Intent(this, AboutUsActivity.class);
                startActivity(intent4);
                break;

            case R.id.feedback:
                Intent intent5 = new Intent(this, FeedbackActivity.class);
                startActivity(intent5);
                break;

            case R.id.logout:
                break;

        }
        item.setChecked(true);
        drawerLayout.closeDrawers();

        return true;
    }
}

