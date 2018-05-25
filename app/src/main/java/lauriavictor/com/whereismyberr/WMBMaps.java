package lauriavictor.com.whereismyberr;


import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WMBMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, LocationListener, GoogleMap.OnPoiClickListener {

    private GoogleMap mMap;
    private LatLng currentLocationLatLong;
    private Marker currentLocationMarker;

    LocationManager locationManager;
    String provider;

    int location = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wmbmaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //When creating the view it will ask what type of location service is enabled and will save it to the provider.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        Log.i("LogX", Integer.toHexString(intent.getIntExtra("iLocation", -1)));
        location = intent.getIntExtra("iLocation", -1);

        //If the location is other than -1 and 0, do not use the locationManager, add a marker on the map, and animate the camera to that marker.
        if(location != -1 && location != 0) {
            locationManager.removeUpdates(this);

            mMap.addMarker(new MarkerOptions().position(WMB.locations.get(location)).title(WMB.places.get(location)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(WMB.locations.get(location), 10));
        } else {
            //If not, use the locationManager (refresh location of 400 in 400 milliseconds).
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }

        //Quando clicar na descrição do marcador, a informação do lugar irá para a outra Acitivity.
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(WMBMaps.this, WMBLugares.class);

                intent.putExtra("placeName", marker.getTitle());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "Clicou em: " +
                        poi.name + "\nID do local:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapLongClick(LatLng point) {
        //This geocoder returns the location after the click.
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        //Based on the geocoder the maker will display a series of information about the location.
        String marker = new Date().toString();

        //Saving location data in the list.
        try {
            List<Address> listOfPlaces = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            if(listOfPlaces != null && listOfPlaces.size() > 0) {
                marker = listOfPlaces.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Adding data to variables.
        WMB.locations.add(point);
        WMB.places.add(marker);
        WMB.arrayAdapter.notifyDataSetChanged();

        //Displaying marker with position (point) and title (marker).
        mMap.addMarker(new MarkerOptions()
                       .position(point)
                       .title(marker)
                       .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
    }

    @Override
    public void onLocationChanged(Location location) {
        //Moving the camera to the user's location.
        currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationLatLong);
        markerOptions.title("Localização atual.");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 18));

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
}
