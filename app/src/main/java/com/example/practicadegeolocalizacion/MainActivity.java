package com.example.practicadegeolocalizacion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.widget.Spinner;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "estilo del mapa";
    static final String LATITUD_FINAL = "latitud_final";
    static final String LONGITUD_FINAL = "longitud_final";
    private LatLng japon = new LatLng(35.680513, 139.769051);
    private LatLng alemania = new LatLng(52.516934, 13.403190);
    private LatLng italia = new LatLng(41.902609, 12.494847);
    private LatLng francia = new LatLng(48.843489, 2.355331);

    fragmentnormal fragmentnormal1;
    fragmentsatelite fragmentsatelite1;
    fragmenthibrido fragmenthibrido1;
    fragmentterrarian fragmentterrarian1;
    private double latitudActual = 0;
    private double longitudActual = 0;
    private double latitudFinal = 0;
    private double longitudFinal = 0;
    Spinner spinner;
    List<String> maps;
    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frameLayout,mapFragment)
                .commit();
        mapFragment.getMapAsync(this);

        if (savedInstanceState != null) {
            longitudFinal = savedInstanceState.getDouble(LONGITUD_FINAL);
            latitudFinal = savedInstanceState.getDouble(LATITUD_FINAL);
        }

        spinner = findViewById(R.id.spinner);
        fragmentnormal1 = new fragmentnormal();
        fragmentsatelite1 = new fragmentsatelite();
        fragmenthibrido1 = new fragmenthibrido();
        fragmentterrarian1 = new fragmentterrarian();

        maps = new ArrayList<>();
        maps.add("Normal");
        maps.add("Satelite");
        maps.add("Hibrido");
        maps.add("Terrarian");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,R.layout.item,maps);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

    }


    private void selectFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Spinner spnTipoMapa = (Spinner)findViewById(R.id.spinner);

        spnTipoMapa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                seleccion=position;
                isAlertDisplayed=true;
                if (!enableMyLocation()){ return; }
                seleccionMapa(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void seleccionMapa(int seleccion){

        map.clear();
        switch(seleccion){
            case 1:
                //paisOverlay.image(BitmapDescriptorFactory.fromResource(R.drawable.world)).position(japon,100);
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                latitudFinal = japon.latitude;
                longitudFinal = japon.longitude;
                agregarMarcadorIcono(japon,R.drawable.mundo);
                break;
            case 2:
                //paisOverlay.image(BitmapDescriptorFactory.fromResource(R.drawable.satellite)).position(alemania,100);
                map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                latitudFinal = alemania.latitude;
                longitudFinal = alemania.longitude;
                agregarMarcadorIcono(alemania,R.drawable.satelite);
                break;
            case 3:
                //paisOverlay.image(BitmapDescriptorFactory.fromResource(R.drawable.mountain)).position(italia,100);
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                latitudFinal = italia.latitude;
                longitudFinal = italia.longitude;
                agregarMarcadorIcono(italia, R.drawable.montana);
                break;
            case 4:
                //paisOverlay.image(BitmapDescriptorFactory.fromResource(R.drawable.plane)).position(francia,100);
                map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                latitudFinal = francia.latitude;
                longitudFinal = francia.longitude;
                agregarMarcadorIcono(francia,R.drawable.plano);
                break;
            default:
                break;
        }

    }

    private void agregarMarcadorIcono(LatLng location, int icono) {
        CameraUpdate miUbicacion = CameraUpdateFactory.newLatLngZoom(location, 16);
        map.addMarker(new MarkerOptions().position(location).title(getString(R.string.destino)).icon(BitmapDescriptorFactory.fromResource(icono)));
        map.animateCamera(miUbicacion);
    }




}

