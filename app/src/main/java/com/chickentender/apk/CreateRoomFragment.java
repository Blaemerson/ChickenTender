package com.chickentender.apk;

import static com.chickentender.apk.BuildConfig.MAPS_API_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.chickentender.apk.databinding.FragmentCreateRoomBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CreateRoomFragment extends Fragment {
    private FragmentCreateRoomBinding binding;
    private FusedLocationProviderClient flpc;
    private JSONObject[] jsonObjects;
    private double lat;
    private double lng;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        flpc = LocationServices.getFusedLocationProviderClient(this.getActivity());
        requestNewLocationData();
//        getLastLocation();

        binding = FragmentCreateRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                flpc.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }
                    }
                });
            } else {
                Toast.makeText(this.getActivity(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        flpc = LocationServices.getFusedLocationProviderClient(this.getActivity());
        flpc.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            System.out.println(mLastLocation.getLatitude());
            System.out.println(mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }
    @SuppressLint("MissingPermission")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(lat);
                System.out.println(lng);
                NavHostFragment.findNavController(CreateRoomFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        binding.buildRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PlaceFetcher(view.getContext()).execute();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onBackgroundTaskDataObtained(JSONObject[] results) throws JSONException {
        Restaurant[] restaurants = new Restaurant[results.length];
        if (results.length == 0) {
            ((MainActivity)getActivity()).createRoom("No results", restaurants, lat, lng);
        } else{
            for (int i = 0; i < results.length; i++) {
                restaurants[i] = new Restaurant((String) results[i].get("name"), 0, 0, "");
            }
            ((MainActivity)getActivity()).createRoom(restaurants[0].getName(), restaurants, lat, lng);
            ((MainActivity)getActivity()).setText(String.valueOf(restaurants.length));
        }
    }

    public String buildQuery() {
        String _apiKey = MAPS_API_KEY;

        return "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + lat + "," + lng
                + "&radius=5000&type=restaurant&key="
                + _apiKey + "\n";
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        new PlaceFetcher(getContext()).execute();
    }

    class PlaceFetcher extends AsyncTask<String, Void, JSONObject[]> {

        public JSONObject[] request(String query) {
            JSONObject[] places = null;
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = FormBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(query)
                    .addHeader("Accept", "application/json")
                    .method("GET", null)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject Jobject = new JSONObject(jsonData);
                JSONArray Jarray = Jobject.getJSONArray("results");
                places = new JSONObject[Jarray.length()];

                for (int i = 0; i < Jarray.length(); i++) {
                    JSONObject object     = Jarray.getJSONObject(i);

                    places[i] = object;
                    System.out.println(places[i].get("name") + " @ " + places[i].get("vicinity"));
                }
            }  catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return places;
        }
        private final Context context;

        public PlaceFetcher(Context c){

            this.context = c;
        }


        @Override
        protected JSONObject[] doInBackground(String... params) {
            JSONObject[] obs = request(buildQuery());
            System.out.println(obs.length);
            return obs;
        }

        @Override
        protected void onPostExecute(JSONObject[] result) {
            try {
                CreateRoomFragment.this.onBackgroundTaskDataObtained(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}