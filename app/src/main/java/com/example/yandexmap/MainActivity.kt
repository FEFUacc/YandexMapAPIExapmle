package com.example.yandexmap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


class MainActivity : AppCompatActivity() {

    private val mapView: MapView by lazy { findViewById(R.id.mapview) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this);

        setContentView(R.layout.activity_main)

        val permissionsGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!permissionsGranted) {
            requestPermission()
        } else {
            showLocation()
        }


    }

    fun requestPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_LOCATION_REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    fun showLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener {
                val currentPoint = Point(it.latitude, it.longitude)

                mapView.map.move(
                    CameraPosition(currentPoint, 11.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 0F),
                    null
                )
                mapView.map.mapObjects.addPlacemark(currentPoint)
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_LOCATION_REQUEST_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showLocation()
                } else {
                    Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }


    companion object {
        private const val PERMISSION_LOCATION_REQUEST_CODE = 101
    }


}

