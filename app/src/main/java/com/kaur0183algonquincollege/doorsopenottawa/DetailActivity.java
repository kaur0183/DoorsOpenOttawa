package com.kaur0183algonquincollege.doorsopenottawa;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

/**
 * DetailActivity is showing all the details of particular building on which we click and also pin the location on the google maps.
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */


public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private TextView buildingName;
    private TextView buildingDescription;
    private TextView buildingAddress;
    private TextView buildingOpenHours;

    private void pin(String locationName) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(ll).title(locationName));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(ll));
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        buildingName = (TextView) findViewById(R.id.name);

        buildingDescription = (TextView) findViewById(R.id.description);
        buildingAddress = (TextView) findViewById(R.id.address);
        buildingOpenHours = (TextView) findViewById(R.id.openhrs);
        Bundle bundle = getIntent().getExtras();

        buildingName.setText(bundle.getString("buildingname"));
        buildingAddress.setText(bundle.getString("buildingaddress"));
        buildingDescription.setText(bundle.getString("buildingdescription"));
        List<String> temp = bundle.getStringArrayList("buildingopenhrs");
        StringBuilder mtempBuilder = new StringBuilder();
        for (int i = 0; i < temp.size(); i++) {
            mtempBuilder.append(temp.get(i) + "\n");
        }
        buildingOpenHours.setText(mtempBuilder.toString());

        mGeocoder = new Geocoder(this, Locale.CANADA);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        pin(buildingAddress.getText().toString());
    }


}
