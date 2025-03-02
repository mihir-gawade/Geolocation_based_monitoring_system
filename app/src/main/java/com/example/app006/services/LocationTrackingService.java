package com.example.app006.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.app006.models.EmployeeLocationDatabase;
import com.example.app006.services.GeofenceUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.util.Calendar;

public class LocationTrackingService extends Service {
    private static final String TAG = "LocationTrackingService";
    private static final long INTERVAL = 60 * 60  *5 * 1000; // 5 seconds for testing (adjust as needed)
    private static final String CHANNEL_ID = "LocationServiceChannel";

    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler = new Handler(Looper.getMainLooper());
    private EmployeeLocationDatabase database;
    private String employeeId; // Stores the logged-in employee's username

    @Override
    public void onCreate() {
        super.onCreate();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        database = new EmployeeLocationDatabase();
        createNotificationChannel();
        Log.d(TAG, "Service created.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        employeeId = sharedPreferences.getString("username", "");

        if (employeeId.isEmpty()) {
            Log.e(TAG, "No employee ID found. Stopping service.");
            stopSelf();
            return START_NOT_STICKY;
        }

        Log.d(TAG, "Service started for employee: " + employeeId);
        startTracking();
        return START_STICKY;
    }

    private void startTracking() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isWithinWorkingHours()) {
                    fetchLocation();
                } else {
                    Log.d(TAG, "Outside working hours. Skipping location update.");
                }
                handler.postDelayed(this, INTERVAL);
            }
        }, INTERVAL);
    }

    private boolean isWithinWorkingHours() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour >= 9 && hour < 17;
    }

    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sendLocationOffNotification();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                saveLocation(location);
            } else {
                Log.e(TAG, "Last location is null, requesting new location.");
                requestNewLocation();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to fetch last location", e);
            sendLocationOffNotification();
        });
    }

    private void requestNewLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sendLocationOffNotification();
            return;
        }

        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        saveLocation(location);
                    } else {
                        Log.e(TAG, "Still unable to fetch location");
                        sendLocationOffNotification();
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "Failed to request new location", e));
    }

    private void saveLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        boolean insideGeofence = GeofenceUtils.isInsideGeofence(lat, lon);

        Log.d(TAG, "Location saved: " + lat + ", " + lon);
        database.saveLocation(employeeId, lat, lon, insideGeofence);
        showToast("Location saved: " + lat + ", " + lon);
    }

    private void showToast(String message) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> Toast.makeText(LocationTrackingService.this, message, Toast.LENGTH_SHORT).show());
    }

    private void sendLocationOffNotification() {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Location Required")
                .setContentText("Please enable location services for attendance tracking.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(1, builder.build());
        }
    }

    private void createNotificationChannel() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID, "Location Service Channel",
                        NotificationManager.IMPORTANCE_HIGH
                );
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
