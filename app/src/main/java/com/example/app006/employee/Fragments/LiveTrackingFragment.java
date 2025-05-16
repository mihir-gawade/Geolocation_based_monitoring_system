package com.example.app006.employee.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.app006.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class LiveTrackingFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnRefresh, btnMarkIn, btnMarkOut;
    private TextView locationStatus;
    private SharedPreferences prefs;
    private String currentEmail;

    private static final LatLng GEOFENCE_CENTER = new LatLng(37.4220, -122.0841);
    private static final float GEOFENCE_RADIUS = 200; // meters
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LAST_LOGIN = "last_login_time_";    // suffix with email
    private static final String KEY_LAST_LOGOUT = "last_logout_time_";  // suffix with email
    private static final long DISABLE_DURATION_MS = 12 * 60 * 60 * 1000; // 12 hours

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_live_tracking, container, false);

        // Init
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        prefs = requireContext().getSharedPreferences("LoginPrefs", 0);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        currentEmail = sharedPreferences.getString("email", "johndoe@example.com");

        // Map
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        // UI
        btnRefresh = rootView.findViewById(R.id.btn_refresh_location);
        btnMarkIn = rootView.findViewById(R.id.btn_mark_in);
        btnMarkOut = rootView.findViewById(R.id.btn_mark_out);
        locationStatus = rootView.findViewById(R.id.location_status);

        btnRefresh.setOnClickListener(v -> refreshLocation());
        btnMarkIn.setOnClickListener(v -> {
            markAttendance("login");
            disableButton(btnMarkIn, KEY_LAST_LOGIN + currentEmail);
        });
        btnMarkOut.setOnClickListener(v -> {
            markAttendance("logout");
            disableButton(btnMarkOut, KEY_LAST_LOGOUT + currentEmail);
        });

        // Per-user button state
        setupButtonState(btnMarkIn, KEY_LAST_LOGIN + currentEmail);
        setupButtonState(btnMarkOut, KEY_LAST_LOGOUT + currentEmail);

        return rootView;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mMap.setMyLocationEnabled(true);
        refreshLocation();
    }

    private void setupButtonState(Button button, String key) {
        long lastTime = prefs.getLong(key, 0);
        if (System.currentTimeMillis() - lastTime < DISABLE_DURATION_MS) {
            button.setEnabled(false);
        }
    }

    private void disableButton(Button button, String key) {
        prefs.edit().putLong(key, System.currentTimeMillis()).apply();
        button.setEnabled(false);
    }

    private void refreshLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null && mMap != null) {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(current).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));
                mMap.addCircle(new CircleOptions()
                        .center(GEOFENCE_CENTER)
                        .radius(GEOFENCE_RADIUS)
                        .strokeColor(Color.BLUE)
                        .fillColor(0x220000FF)
                        .strokeWidth(4f));
                locationStatus.setText(String.format(Locale.getDefault(),
                        "Location: %.5f, %.5f", current.latitude, current.longitude));
            }
        });
    }

    private void markAttendance(String type) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location == null) {
                Toast.makeText(getContext(), "Location unavailable", Toast.LENGTH_SHORT).show();
                return;
            }
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            float[] dist = new float[1];
            Location.distanceBetween(lat, lng,
                    GEOFENCE_CENTER.latitude, GEOFENCE_CENTER.longitude, dist);
            boolean inside = dist[0] <= GEOFENCE_RADIUS;
            saveAttendance(type, lat, lng, inside);
        });
    }

    private void saveAttendance(String type, double lat, double lng, boolean inside) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", currentEmail);
        data.put("type", type);
        data.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        data.put("time", new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));
        data.put("latitude", lat);
        data.put("longitude", lng);
        data.put("insideGeofence", inside);
        FirebaseFirestore.getInstance()
                .collection("attendance-data")
                .add(data)
                .addOnSuccessListener(doc -> Toast.makeText(getContext(),
                        "Attendance marked", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getContext(),
                        "Failed to mark attendance", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) onMapReady(mMap);
        }
    }
}


//package com.example.app006.employee.Fragments;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.app006.R;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CircleOptions;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Locale;
//
//public class LiveTrackingFragment extends Fragment implements OnMapReadyCallback {
//
//    private GoogleMap mMap;
//    private FusedLocationProviderClient fusedLocationClient;
//    private Button btnRefresh, btnMarkIn, btnMarkOut;
//    private TextView locationStatus;
//
//    private static final LatLng GEOFENCE_CENTER = new LatLng(37.4220, -122.0841); // example
//    private static final float GEOFENCE_RADIUS = 200; // in meters
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_live_tracking, container, false);
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        btnRefresh = rootView.findViewById(R.id.btn_refresh_location);
//        btnMarkIn = rootView.findViewById(R.id.btn_mark_in);
//        btnMarkOut = rootView.findViewById(R.id.btn_mark_out);
//        locationStatus = rootView.findViewById(R.id.location_status);
//
//        btnRefresh.setOnClickListener(v -> refreshLocation());
//        btnMarkIn.setOnClickListener(v -> markAttendance("login"));
//        btnMarkOut.setOnClickListener(v -> markAttendance("logout"));
//
//        return rootView;
//    }
//
//    @SuppressLint("MissingPermission")
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        }
//
//        mMap.setMyLocationEnabled(true);
//        refreshLocation();
//    }
//
//    private void refreshLocation() {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//            return;
//        }
//
//        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
//            if (location != null && mMap != null) {
//                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
//
//                mMap.clear();
//                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Location"));
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
//
//                mMap.addCircle(new CircleOptions()
//                        .center(GEOFENCE_CENTER)
//                        .radius(GEOFENCE_RADIUS)
//                        .strokeColor(Color.BLUE)
//                        .fillColor(0x220000FF)
//                        .strokeWidth(4f));
//
//                locationStatus.setText("Location updated: " + currentLocation.latitude + ", " + currentLocation.longitude);
//            }
//        });
//    }
//
//    private void markAttendance(String type) {
//        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getContext(), "Permission required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
//            if (location != null) {
//                double latitude = location.getLatitude();
//                double longitude = location.getLongitude();
//
//                float[] distance = new float[1];
//                Location.distanceBetween(
//                        latitude, longitude,
//                        GEOFENCE_CENTER.latitude, GEOFENCE_CENTER.longitude,
//                        distance
//                );
//                boolean insideGeofence = distance[0] <= GEOFENCE_RADIUS;
//
//                saveAttendance(type, latitude, longitude, insideGeofence);
//            } else {
//                Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void saveAttendance(String type, double latitude, double longitude, boolean insideGeofence) {
//        String email = FirebaseAuth.getInstance().getCurrentUser() != null ?
//                FirebaseAuth.getInstance().getCurrentUser().getEmail() : "unknown@example.com";
//
//        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
//
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("email", email);
//        data.put("type", type);
//        data.put("date", date);
//        data.put("time", time);
//        data.put("latitude", latitude);
//        data.put("longitude", longitude);
//        data.put("insideGeofence", insideGeofence);
//
//        FirebaseFirestore.getInstance()
//                .collection("attendance-data")
//                .add(data)
//                .addOnSuccessListener(documentReference -> {
//                    Toast.makeText(getContext(), "Attendance marked", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Failed to mark attendance", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            onMapReady(mMap);
//        }
//    }
//}
