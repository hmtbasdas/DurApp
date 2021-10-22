package com.hmtbasdas.durapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hmtbasdas.durapp.Models.FetchAddressIntentService;
import com.hmtbasdas.durapp.R;
import com.hmtbasdas.durapp.Utilities.Constants;
import com.hmtbasdas.durapp.Utilities.PreferenceManager;
import com.hmtbasdas.durapp.databinding.ActivityEmergencyBinding;

public class EmergencyActivity extends BaseActivity {

    private ActivityEmergencyBinding binding;
    private PreferenceManager preferenceManager;
    private ResultReceiver resultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmergencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkUserPermission();
        init();
        checkNumbers();
        setListeners();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        resultReceiver = new AddressResultReceiver(new Handler());
    }

    private void ToastMessage(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void checkNumbers(){
        if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_1)){
            binding.number1.setHint(preferenceManager.getString(Constants.KEY_USER_NUMBER_1));
        }
        if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_2)){
            binding.number2.setHint(preferenceManager.getString(Constants.KEY_USER_NUMBER_2));
        }
        if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_3)){
            binding.number3.setHint(preferenceManager.getString(Constants.KEY_USER_NUMBER_3));
        }
    }

    private void setListeners(){

        //save
        binding.saveNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.number1.getText().toString().length() == 10){
                    preferenceManager.putString(Constants.KEY_USER_NUMBER_1,"0" + binding.number1.getText().toString());
                    binding.number1.getText().clear();
                    binding.number1.setHint(preferenceManager.getString(Constants.KEY_USER_NUMBER_1));
                    ToastMessage("Kaydedildi");
                }
                else{
                    ToastMessage("Hatalı Numara Formatı");
                }
            }
        });

        binding.saveNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.number2.getText().toString().length() == 10){
                    preferenceManager.putString(Constants.KEY_USER_NUMBER_2,"0" + binding.number2.getText().toString());
                    binding.number2.getText().clear();
                    binding.number2.setHint(preferenceManager.getString(Constants.KEY_USER_NUMBER_2));
                    ToastMessage("Kaydedildi");
                }
                else{
                    ToastMessage("Hatalı Numara Formatı");
                }
            }
        });

        binding.saveNumber3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.number3.getText().toString().length() == 10){
                    preferenceManager.putString(Constants.KEY_USER_NUMBER_3,"0" + binding.number3.getText().toString());
                    binding.number3.getText().clear();
                    binding.number3.setHint(preferenceManager.getString(Constants.KEY_USER_NUMBER_3));
                    ToastMessage("Kaydedildi");
                }
                else{
                    ToastMessage("Hatalı Numara Formatı");
                }
            }
        });


        //Del
        binding.deleteNumber1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clearByKey(Constants.KEY_USER_NUMBER_1);
                binding.number1.getText().clear();
                binding.number1.setHint(R.string.put_number);
                ToastMessage("Silindi");
            }
        });

        binding.deleteNumber2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clearByKey(Constants.KEY_USER_NUMBER_2);
                binding.number2.getText().clear();
                binding.number2.setHint(R.string.put_number);
                ToastMessage("Silindi");
            }
        });

        binding.deleteNumber3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceManager.clearByKey(Constants.KEY_USER_NUMBER_3);
                binding.number3.getText().clear();
                binding.number3.setHint(R.string.put_number);
                ToastMessage("Silindi");
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmergency();
            }
        });
    }

    private void sendEmergency(){
        if(isLocationEnabled()){
            if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_1) || preferenceManager.checkString(Constants.KEY_USER_NUMBER_2) || preferenceManager.checkString(Constants.KEY_USER_NUMBER_3)){
                binding.emergencyButton.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
                getCurrentLocation();
            }
            else{
                ToastMessage("Kayıtlı Numara Bulunamadı");
            }
        }
        else{
            checkLocationService();
            ToastMessage("Konum Hizmetlerini Açınız");
        }
    }

    private void checkUserPermission(){
        if(ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(EmergencyActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(EmergencyActivity.this,
                            Manifest.permission.SEND_SMS)){

                AlertDialog.Builder builder = new AlertDialog.Builder(EmergencyActivity.this);
                builder.setTitle("İzinler Alınamadı");
                builder.setMessage("Sms ve konum");
                builder.setPositiveButton("Kabul", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                EmergencyActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.SEND_SMS
                                },
                                Constants.REQUEST_CHECK_CODE
                        );
                    }
                });
                builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else{
                ActivityCompat.requestPermissions(
                        EmergencyActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.SEND_SMS
                        },
                        Constants.REQUEST_CHECK_CODE
                );
            }
        }
    }

    public boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(EmergencyActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(@NonNull LocationResult locationResult) {
                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(EmergencyActivity.this)
                                .removeLocationUpdates(this);

                        if(locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            fetchAddressFromLatLong(location);
                        }
                        else{
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            binding.emergencyButton.setVisibility(View.VISIBLE);
                        }
                    }
                }, Looper.myLooper());
    }

    private void fetchAddressFromLatLong(Location location){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, resultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);
        startService(intent);
    }

    private class AddressResultReceiver extends ResultReceiver {

        AddressResultReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if(resultCode == Constants.SUCCESS_RESULT){
                sendSms(resultData.getString(Constants.RESULT_DATA_KEY));
            }
            else{
                ToastMessage("Konum Belirlenemedi Tekrar Deneyin");
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.emergencyButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void sendSms(String address){
        SmsManager smsManager = SmsManager.getDefault();

        if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_1)){
            smsManager.sendTextMessage(preferenceManager.getString(Constants.KEY_USER_NUMBER_1), null, "Acil bir durum söz konusu ve yardımına ihtiyacım var. Konumum: \n" + address, null, null);
        }
        if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_2)){
            smsManager.sendTextMessage(preferenceManager.getString(Constants.KEY_USER_NUMBER_2), null, "Acil bir durum söz konusu ve yardımına ihtiyacım var. Konumum: \n" + address, null, null);
        }
        if(preferenceManager.checkString(Constants.KEY_USER_NUMBER_3)){
            smsManager.sendTextMessage(preferenceManager.getString(Constants.KEY_USER_NUMBER_3), null, "Acil bir durum söz konusu ve yardımına ihtiyacım var. Konumum: \n" + address, null, null);
        }
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.emergencyButton.setVisibility(View.VISIBLE);
        ToastMessage("Gönderildi");

        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.emergencyButton.setVisibility(View.VISIBLE);
    }

    private void checkLocationService(){
        LocationRequest request = new LocationRequest()
                .setFastestInterval(10000)
                .setInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(request);

        Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    task.getResult(ApiException.class);
                }
                catch (ApiException e){
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(EmergencyActivity.this, Constants.REQUEST_CHECK_CODE);
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        {
                            break;
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    /*private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){
                    try {
                        Geocoder geocoder = new Geocoder(EmergencyActivity.this, Locale.getDefault());
                        addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1

                        );
                        String address = addresses.get(0).getAddressLine(0);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    ToastMessage("Konum Belirlenemedi Tekrar Deneyin");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                    binding.emergencyButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }*/

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CHECK_CODE) {
            if (!((grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                onBackPressed();
            }
        }
    }
}