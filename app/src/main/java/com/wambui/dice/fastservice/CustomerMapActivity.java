package com.wambui.dice.fastservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CustomerMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;



    private FusedLocationProviderClient mFusedLocationClient;

    private Button mLogout,mRequest,mSettings,mHistory;
    private LatLng serviceDesLocation;
    private Boolean requestBol = false;
    private Marker serviceDesMarker;

    private SupportMapFragment mapFragment;

    private String destination;

    private LatLng destinationLatlng;

    private RatingBar mRatingBar;

    private LinearLayout mMechanicInfo;

    private TextView mMechanicName, mMechanicPhone;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        destinationLatlng = new LatLng(0.0, 0.0);

        mMechanicInfo = (LinearLayout) findViewById(R.id.mechanicInfo);

        mMechanicName= (TextView) findViewById(R.id.mechanicName);
        mMechanicPhone = (TextView) findViewById(R.id.mechanicPhone);


        mRequest = (Button) findViewById(R.id.request);
        mSettings = (Button) findViewById(R.id.settings);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mHistory = (Button) findViewById(R.id.history);
        mLogout = (Button) findViewById(R.id.logout);





        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapActivity.this, CustomerHomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }

        });




        mRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requestBol){
                    endService();


                }else {
                }
                requestBol = true;
                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                serviceDesLocation = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                serviceDesMarker = mMap.addMarker(new MarkerOptions().position(serviceDesLocation).title("Your mechanic is getting to you").icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.mipmap.ic_launcher))));

                mRequest.setText("Getting Your Mechanic");

                getClosestMechanic();
            }
        });


        mSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(CustomerMapActivity.this,CustomerSettingActivity.class);
                startActivity(intent);
                return;
            }

        });


        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CustomerMapActivity.this,HistoryActivity.class);
                intent.putExtra("CustomersOrMechanics","Customers");
                startActivity(intent);
                return;

            }
        });

        Places.initialize(getApplicationContext(), "APIKEY", Locale.US);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {

            }

            @Override
            public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place place) {
                destination = place.getName().toString();
                destinationLatlng = place.getLatLng();
            }
        });
    }

    private int radius=1;
    private Boolean mechanicFound = false;
    private String mechanicFoundID;

    GeoQuery geoQuery;

    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(this, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(20, 20, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    private void getClosestMechanic(){
        DatabaseReference mechanicLocation = FirebaseDatabase.getInstance().getReference().child("mechanicAvailable");

        GeoFire geoFire = new GeoFire(mechanicLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(serviceDesLocation.latitude, serviceDesLocation.longitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!mechanicFound && requestBol){
                    DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(key);
                    mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){

                                if (mechanicFound){
                                    return;
                                }


                                mechanicFound = true;
                                mechanicFoundID = dataSnapshot.getKey();

                                DatabaseReference mechanicRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID).child("customerRequest");
                                String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                HashMap map = new HashMap();
                                map.put("customerServiceId", customerId);
                                map.put("destination", destination);
                                map.put("destinationLat", destinationLatlng.latitude);
                                map.put("destinationLng", destinationLatlng.longitude);
                                mechanicRef.updateChildren(map);

                                getMechanicLocation();
                                getMechanicInfo();
                                getHasServiceEnded();
                                mRequest.setText("Looking for Mechanic Location....");
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (!mechanicFound)
                {
                    radius++;
                    getClosestMechanic();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
//firebase snap shot
    private Marker mMechanicMarker;
    private DatabaseReference mechanicLocationRef;
    private ValueEventListener mechanicLocationRefListener;
    private void getMechanicLocation(){

        //pass a mthod to the listner
        mechanicLocationRef = FirebaseDatabase.getInstance().getReference().child("Mechanic is Working").child(mechanicFoundID).child("l");
        mechanicLocationRefListener=mechanicLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && requestBol){
                    List<Object> map=(List<Object>) dataSnapshot.getValue();
                    double LocationLat = 0;
                    double LocationLng = 0;
                    // mRequest.setText("Mechanic Found");
                    if(map.get(0)!= null){
                        LocationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1)!= null){
                        LocationLng = Double.parseDouble(map.get(1).toString());
                    }
                    LatLng mechanicLatLng = new LatLng(LocationLat, LocationLng);
                    if(mMechanicMarker!=null){
                        mMechanicMarker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(serviceDesLocation.latitude);
                    loc1.setLongitude(serviceDesLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(mechanicLatLng.latitude);
                    loc2.setLongitude(mechanicLatLng.longitude);

                    float distance = loc1.distanceTo(loc2);
                    if(distance<100){
                        mRequest.setText("Mechanic  Arrived");
                    }else{
                        int dis = (int)distance/1000;
                        mRequest.setText("Mechanic Found: "+ String.valueOf(dis)+ " Kms away...");

                    }

                    mMechanicMarker= mMap.addMarker(new MarkerOptions().position(mechanicLatLng).title("Your Mechanic").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
//Make the mechanic imformation visible

    private void getMechanicInfo(){
        mMechanicInfo.setVisibility(View.VISIBLE);
        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID);
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("Name")!=null){
                        mMechanicName.setText(dataSnapshot.child("Name").getValue().toString());
                    }
                    if(dataSnapshot.child("Phone")!=null){
                        mMechanicPhone.setText(dataSnapshot.child("Phone").getValue().toString());
                    }
//get the rating

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
//location request on Android Google Map ,set Interval
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//location request permission on run time
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }else{
                checkLocationPermission();
            }
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);

    }
//onlocation result
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {

                mLastLocation = location;

                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                if(!getMechanicAroundStarted)
                    getMechanicAround();

            }
        }

    };
//get one near you
    boolean getMechanicAroundStarted = false;
    List<Marker> markers = new ArrayList<Marker>();
    private void getMechanicAround(){
        getMechanicAroundStarted = true;
        DatabaseReference mechanicLocation = FirebaseDatabase.getInstance().getReference().child("mechanicsAvailable");

        GeoFire geoFire = new GeoFire(mechanicLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLongitude(), mLastLocation.getLatitude()), 999999999);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {



                LatLng mechanicLocation = new LatLng(location.latitude, location.longitude);

                Marker mMechanicMarker = mMap.addMarker(new MarkerOptions().position(mechanicLocation).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                mMechanicMarker.setTag(key);

                markers.add(mMechanicMarker);

                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key))
                        return;
                }





            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.remove();
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIt : markers){
                    if(markerIt.getTag().equals(key)){
                        markerIt.setPosition(new LatLng(location.latitude, location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new  android.app.AlertDialog.Builder(this)
                        .setTitle("Please give permission...")
                        .setMessage("Please give permission...")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(CustomerMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission...", Toast.LENGTH_LONG).show();
                }
                break;
            }


        }}

    private DatabaseReference serviceHasEndedRef;
    private ValueEventListener serviceHasEndedRefListener;
    private void getHasServiceEnded(){

        serviceHasEndedRef  = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID).child("customerRequest").child("customerServiceId");
        serviceHasEndedRefListener = serviceHasEndedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){


                }else{

                    endService();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void endService()
    {

        requestBol = false;
        if(geoQuery != null){
            geoQuery.removeAllListeners();
        }
        if(mechanicLocationRefListener != null&& mechanicLocationRefListener != null){
            mechanicLocationRef.removeEventListener(mechanicLocationRefListener);
            serviceHasEndedRef.removeEventListener(serviceHasEndedRefListener);
        }


        if(mechanicFoundID!=null){
            DatabaseReference mechanicRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(mechanicFoundID).child("customerRequest");
            mechanicRef.removeValue();
            mechanicFoundID = null;

        }
        mechanicFound = false;
        radius = 1;

        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId);
        if( serviceDesMarker!=null){
            serviceDesMarker.remove();
        }
        if (serviceDesMarker != null){
            serviceDesMarker.remove();
        }
        mRequest.setText("Request For Mechanic");

        mMechanicInfo.setVisibility(View.GONE);
        mMechanicName.setText("");
        mMechanicPhone.setText("");

    }


}



