package com.example.mappsapp

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mappsapp.databinding.ActivityMapsBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    var takipBoolean: Boolean? = null
    private lateinit var sharedPrefererences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        sharedPrefererences = getSharedPreferences("com.example.mappsapp", MODE_PRIVATE)
        takipBoolean = false
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        //41.08455027943167, 28.89351494628031
        // Add a marker in Sydney and move the camera
        val yeniMahalle = LatLng(41.08455027943167, 28.89351494628031)
        mMap.addMarker(MarkerOptions().position(yeniMahalle).title("Evim Evim Güzel Evim"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(yeniMahalle,15f))


        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                takipBoolean = sharedPrefererences.getBoolean("takipBoolean",false)
                if (!takipBoolean!!){
                    mMap.clear()
                    val kullaniciKonumu = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(kullaniciKonumu).title("Konumunuz"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kullaniciKonumu,15f))
                    sharedPrefererences.edit().putBoolean("takipBoolean",true).apply()
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"Konumu Almsk İzin Gerekli",Snackbar.LENGTH_INDEFINITE).setAction(
                    "İzin Ver"
                ){
                    //izni isteyecegiz
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

                }.show()
            }else{
                //izni isteyecegiz
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
    }else{
        //kullanıcı izin vermis ise
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
            val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (sonBilinenKonum != null){
                val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng,15f))
            }
        }
    }
    private fun registerLauncher(){
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if (result){
                if (ContextCompat.checkSelfPermission(this@MapsActivity,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val sonBilinenKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (sonBilinenKonum != null){
                        val sonBilinenLatLng = LatLng(sonBilinenKonum.latitude,sonBilinenKonum.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sonBilinenLatLng,15f))
                    }
                }
            }else{
                Toast.makeText(this@MapsActivity,"İzne ihtiyacımız Var",Toast.LENGTH_LONG).show()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()

        val geocoder = Geocoder(this, Locale.getDefault())
        var adres  = ""

        try {
            geocoder.getFromLocation(p0.latitude,p0.longitude,1,Geocoder.GeocodeListener {adresListesi ->
                val ilkAdres = adresListesi.first()
                val ulkeAdi = ilkAdres.countryName
                val cadde = ilkAdres.thoroughfare
                val sokak = ilkAdres.subThoroughfare
                adres += cadde
                adres += sokak
                println(adres)
            })
        }
        catch (e : Exception){
            e.printStackTrace()
        }
    }
}