package com.example.practicadegeolocalizacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final String TAG = "estilo del mapa";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LatLng japon = new LatLng(35.680513, 139.769051);
    private LatLng alemania = new LatLng(52.516934, 13.403190);
    private LatLng italia = new LatLng(41.902609, 12.494847);
    private LatLng francia = new LatLng(48.843489, 2.355331);
    private LatLng posicionaactual;
    private Location locacioninicio = new Location("Origen");
    private Location locacionfin = new Location("Destino");
    private double latitudactual = 0;
    private double longitudactual = 0;
    private double latitudfinal = 0;
    private double longitudfinal = 0;
    private String messageAlert = "";
    private boolean isAlertDisplayed = false;
    private int seleccion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentmap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean success = mMap .setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(),R.raw.map_style));
            if (!success){
                Log.e(TAG,"Fallo al cargar estilo del mapa");
            }
        }catch (Resources.NotFoundException e) {
            Log.e(TAG,"No es posible hallar el estilo. Error: ", e);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();

        if (seleccion != 0){
            eleccionmapa(seleccion);
        }
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seleccion=position;
                isAlertDisplayed=true;
                if (!enableMyLocation()){ return; }
                eleccionmapa(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void eleccionmapa(int eleccion){
        locacioninicio.setLatitude(latitudactual);
        locacioninicio.setLongitude(longitudactual);
        locacionfin.setLatitude(latitudfinal);
        locacionfin.setLongitude(longitudfinal);
        mMap.clear();
        switch(eleccion){
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                latitudfinal = japon.latitude;
                longitudfinal = japon.longitude;
                iconomarcador(japon,R.drawable.mundo);
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                latitudfinal = alemania.latitude;
                longitudfinal = alemania.longitude;
                iconomarcador(alemania,R.drawable.satelite);
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                latitudfinal = italia.latitude;
                longitudfinal = italia.longitude;
                iconomarcador(italia, R.drawable.montana);
                break;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                latitudfinal = francia.latitude;
                longitudfinal = francia.longitude;
                iconomarcador(francia,R.drawable.plano);
                break;
            default:
                break;
        }
        if (eleccion != 0){
            colorearruta();
            if (isAlertDisplayed){
                messageAlert = obtenerDistancia(locacioninicio, locacionfin);
                mostrarMensaje(messageAlert);
            }
        }
        obtenerUbicacionActual();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocation();
                    break;
                }
        }
    }

    private boolean enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            obtenerUbicacionActual();
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return false;
        }
    }

    public void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0, locationListener);
        latitudactual = location.getLatitude();
        longitudactual = location.getLongitude();
        posicionaactual = new LatLng(latitudactual, longitudactual);
        marcador(posicionaactual);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onProviderDisabled(String provider) { }
    };

    private String obtenerDistancia(Location locationinicio, Location locationfin) {
        double distancia = locationinicio.distanceTo(locationfin);
        distancia /= 1000;
        return  getString(R.string.diferencia) + distancia + getString(R.string.km);
    }

    private void colorearruta() {
        Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(latitudactual, longitudactual), new LatLng(latitudfinal, longitudfinal)).width(15).color(Color.BLUE).geodesic(true));
    }

    private void mostrarMensaje(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.distancia);
        alert.setMessage(message);
        alert.setCancelable(false);

        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isAlertDisplayed=false;
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void iconomarcador(LatLng location, int icono) {
        CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(location, 16);
        mMap.addMarker(new MarkerOptions().position(location).title(getString(R.string.destino)).icon(BitmapDescriptorFactory.fromResource(icono)));
        mMap.animateCamera(ubicacion);
    }

    private void marcador(LatLng location) {
        CameraUpdate ubicacion = CameraUpdateFactory.newLatLngZoom(location, 16);
        mMap.addMarker(new MarkerOptions().position(location).title(getString(R.string.actual)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.animateCamera(ubicacion);
    }
}