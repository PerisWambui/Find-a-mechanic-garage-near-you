package com.wambui.dice.fastservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class History extends AppCompatActivity implements OnMapReadyCallback, RoutingListener {
    private String serviceId, currentUserId, customerId, mechanicId, userMechanicsOrCustomers;

    private TextView serviceLocation;
    private TextView serviceDistance;
    private TextView serviceDate;
    private TextView userName;
    private TextView userPhone;

    private Double servicePrice;

    private RatingBar mRatingBar;

    private DatabaseReference historyServiceInfoDb;

    private LatLng destinationLatLng, pickupLatLng;
    private String distance;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        polylines = new ArrayList<>();

        serviceId = getIntent().getExtras().getString("serviceId");

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        serviceLocation = (TextView) findViewById(R.id.serviceLocation);
        serviceDistance = (TextView) findViewById(R.id.serviceDistance);
        serviceDate = (TextView) findViewById(R.id.serviceDate);
        userName = (TextView) findViewById(R.id.userName);
        userPhone = (TextView) findViewById(R.id.userPhone);


        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        historyServiceInfoDb = FirebaseDatabase.getInstance().getReference().child("History").child(serviceId);

        getServiceInformation();

    }

    private void getServiceInformation() {

        historyServiceInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (child.getKey().equals("Customers")) {
                            customerId = child.getValue().toString();
                            if (!customerId.equals(currentUserId)) {
                                userMechanicsOrCustomers = "Mechanics";
                                getUserInformation("Customers", customerId);


                            }
                        }
                        if (child.getKey().equals("Mechanics")) {
                            mechanicId = child.getValue().toString();
                            if (!mechanicId.equals(currentUserId)) {
                                userMechanicsOrCustomers = "Customers";
                                getUserInformation("Mechanics",mechanicId);
                                displayCustomerRelatedObjects();

                            }
                        }
                        if (child.getKey().equals("timestamp")) {

                            serviceDate.setText(getDate(Long.valueOf(child.getValue().toString())));
                        }
                        if (child.getKey().equals("rating")) {

                            mRatingBar.setRating(Integer.valueOf(child.getValue().toString()));
                        }
                        if (child.getKey().equals("distance")) {
                            distance = child.getValue().toString();
                            serviceDistance.setText(distance.substring(0, Math.min(distance.length(), 5)) + "km");
                            servicePrice = Double.valueOf(distance) * 0.5;
                        }
                        if (child.getKey().equals("destination")) {

                            serviceLocation.setText(child.getValue().toString());
                        }
                        if (child.getKey().equals("location")) {

                            //rideLocation.setText(getDate(Long.valueOf(child.getValue().toString())));
                            pickupLatLng = new LatLng(Double.valueOf(child.child("from").child("lat").getValue().toString()), Double.valueOf(child.child("from").child("lng").getValue().toString()));
                            destinationLatLng = new LatLng(Double.valueOf(child.child("to").child("lat").getValue().toString()), Double.valueOf(child.child("to").child("lng").getValue().toString()));
                            if (destinationLatLng != new LatLng(0, 0)) {
                                getRouteToMarker();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayCustomerRelatedObjects() {
        mRatingBar.setVisibility(View.VISIBLE);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                historyServiceInfoDb.child("rating").setValue(rating);
                DatabaseReference mMechanicRatingDb = FirebaseDatabase.getInstance().getReference().child("Users").child("Mechanics").child(serviceId).child("rating");
                mMechanicRatingDb.child(serviceId).setValue(rating);
            }
        });
    }

    private void getUserInformation(String otherUserMechanicsOrCustomers, String otherUserId) {
        DatabaseReference mOtherUSerDB = FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserMechanicsOrCustomers).child(otherUserId);
        mOtherUSerDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null) {
                        userName.setText(map.get("Name").toString());
                    }
                    if (map.get("Phone") != null) {
                        userPhone.setText(map.get("Phone").toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }

    private void getRouteToMarker() {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.BIKING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(pickupLatLng, destinationLatLng)
                .build();
        routing.execute();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private List<Polyline> polylines;
    private static final int[] COLORS = new int[]{R.color.black};

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(pickupLatLng);
        builder.include(destinationLatLng);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) (width * 0.2);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.animateCamera(cameraUpdate);



        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

}


