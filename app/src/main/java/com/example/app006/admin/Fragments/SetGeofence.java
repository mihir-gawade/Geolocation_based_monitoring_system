package com.example.app006.admin.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.app006.R;
import com.example.app006.models.GeofenceModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SetGeofence extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText radiusInput;
    private Button saveButton, deleteButton;
    private LatLng selectedLocation;
    private FirebaseFirestore db;
    private String adminEmail;
    private String existingGeofenceId = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_set_geofence, container, false);

        mapView = view.findViewById(R.id.mapView);
        radiusInput = view.findViewById(R.id.radius_input);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);

        db = FirebaseFirestore.getInstance();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        adminEmail = sharedPref.getString("email", null);

        if (adminEmail != null) {
            loadExistingGeofence();
        }

        saveButton.setOnClickListener(v -> saveOrUpdateGeofence());
        deleteButton.setOnClickListener(v -> deleteGeofence());

        return view;
    }

    private void loadExistingGeofence() {
        db.collection("set-geofence")
                .whereEqualTo("adminEmail", adminEmail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        GeofenceModel geofence = querySnapshot.getDocuments().get(0).toObject(GeofenceModel.class);
                        existingGeofenceId = geofence.getGeofenceId();

                        selectedLocation = new LatLng(geofence.getLatitude(), geofence.getLongitude());
                        radiusInput.setText(String.valueOf(geofence.getRadius()));

                        if (googleMap != null) {
                            googleMap.clear();
                            googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Existing Geofence"));
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                        }

                        saveButton.setText("Update");
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;

        map.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            map.clear();
            map.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        });

        // Reload marker if geofence already exists
        if (selectedLocation != null) {
            map.addMarker(new MarkerOptions().position(selectedLocation).title("Existing Geofence"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
        }
    }

    private void saveOrUpdateGeofence() {
        if (selectedLocation == null) {
            Toast.makeText(getActivity(), "Please select a location", Toast.LENGTH_SHORT).show();
            return;
        }

        String radiusText = radiusInput.getText().toString();
        if (radiusText.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a radius", Toast.LENGTH_SHORT).show();
            return;
        }

        int radius = Integer.parseInt(radiusText);
        String geofenceId = existingGeofenceId != null ? existingGeofenceId : db.collection("set-geofence").document().getId();

        GeofenceModel geofence = new GeofenceModel(
                selectedLocation.latitude,
                selectedLocation.longitude,
                adminEmail,
                radius,
                geofenceId
        );

        db.collection("set-geofence")
                .document(geofenceId)
                .set(geofence)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), existingGeofenceId != null ? "Geofence updated!" : "Geofence saved!", Toast.LENGTH_SHORT).show();
                    saveButton.setText("Update");
                    deleteButton.setVisibility(View.VISIBLE);
                    existingGeofenceId = geofenceId;

                    // Add red circle around the geofence location
                    if (googleMap != null) {
                        googleMap.clear();  // Clear existing markers and circles
                        googleMap.addMarker(new MarkerOptions().position(selectedLocation).title("Geofence Location"));
                        googleMap.addCircle(new CircleOptions()
                                .center(selectedLocation)
                                .radius(radius)  // Use the radius from input
                                .strokeColor(0xFFFF0000)  // Red color for the stroke
                                .fillColor(0x44FF0000)  // Red fill color with some transparency
                                .strokeWidth(5));  // Stroke width for the circle
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to save geofence", Toast.LENGTH_SHORT).show());
    }

    private void deleteGeofence() {
        if (existingGeofenceId == null) {
            Toast.makeText(getActivity(), "No geofence to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("set-geofence")
                .document(existingGeofenceId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Geofence deleted", Toast.LENGTH_SHORT).show();
                    selectedLocation = null;
                    radiusInput.setText("");
                    googleMap.clear();
                    saveButton.setText("Save");
                    deleteButton.setVisibility(View.GONE);
                    existingGeofenceId = null;
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Failed to delete geofence", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
