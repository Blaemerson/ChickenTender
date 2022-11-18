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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CreateRoomFragment extends Fragment {
    private FragmentCreateRoomBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double lat;
    private double lng;
    private String roomName;
    private double radiusMeters = 1500;
    private double radiusMiles;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        binding = FragmentCreateRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last location from FusedLocationClient object
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
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
                Toast.makeText(this.getActivity(), "Please turn on" + " your location...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
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
        // on Android 10.0 and higher, use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

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

//        Spinner spinner = (Spinner) findViewById(R.id.distance_spinner);
        Spinner spinner = binding.distanceSpinner;
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.distances_array, R.layout.spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(CreateRoomFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
        binding.buildRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("here1");
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
                    requestNewLocationData();
                    new PlaceFetcher(view.getContext()).execute();
                }
            }
        });
        binding.distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                radiusMiles = Integer.parseInt(selected.split(" ")[0]);
                System.out.println("Selected: " + radiusMiles);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Disable build room button
//        binding.buildRoom.setAlpha(.4f);
//        binding.buildRoom.setClickable(false);
//        binding.editTextNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//                    InputMethodManager imm = (InputMethodManager)textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
//                    radiusMiles = Integer.parseInt(textView.getText().toString());
//                    System.out.println(radiusMiles);
//                    // Enable build room button
//                    if (textView.getText().length() > 0) {
//                        binding.buildRoom.setAlpha(1f);
//                        binding.buildRoom.setClickable(true);
//                    }
//                    return true;
//                }
//
//                return true;
//            }
//        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onBackgroundTaskDataObtained(JSONObject[] results) throws JSONException {
        Restaurant[] restaurants = extractRestaurantData(results);
        if (restaurants.length > 0) {
            ((MainActivity) getActivity()).createRoom("Room1: Restaurants: " + restaurants.length, restaurants, radiusMiles, lat, lng);
            Toast.makeText(this.getContext(), "Room Created", Toast.LENGTH_LONG).show();
            NavHostFragment.findNavController(CreateRoomFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        } else {
            Toast.makeText(this.getContext(), "No restaurants found", Toast.LENGTH_LONG).show();
        }
    }

    // Construct a query for to submit to Google Maps
    // Input: a page token for which page of results to pull
    // Output: a query be to sent to Google
    public String buildQuery(String pageToken) {
        String query = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
                + "location=" + lat + "," + lng
                + "&radius=" + radiusMiles * 1609.34
                + "&type=restaurant"
                + "&pageToken=" + pageToken
                + "&key=" + MAPS_API_KEY
                + "\n";
        System.out.println(query);
        return query;
    }

    // Parse JSON data obtained from Google and store it into a list of restaurants
    // Input: an array of Place data in JSON format.
    // Output: an array of Restaurants parsed from JSON data.
    public Restaurant[] extractRestaurantData(JSONObject[] jsonData) throws JSONException {
        Restaurant[] restaurants = new Restaurant[jsonData.length];
        for (int i = 0; i < jsonData.length; i++) {

            // Get data fields
            String name = jsonData[i].get("name").toString();
            System.out.println(name);
            String vicinity = jsonData[i].get("vicinity").toString();
            JSONObject location = jsonData[i].getJSONObject("geometry")
                                             .getJSONObject("location");
            double latitude = (double) location.get("lat");
            double longitude = (double) location.get("lat");
            String userRating;

            String photo;
            try {
                userRating = jsonData[i].get("rating").toString();
            } catch (Exception e) {
                userRating = "";
            }
            try {
                photo = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photo_reference=" +
                        jsonData[i].getJSONArray("photos").getJSONObject(0).get("photo_reference") + "&key=" + MAPS_API_KEY;
            } catch (Exception e) {
                photo = "";
            }

            restaurants[i] = new Restaurant(
                    name,
                    vicinity,
                    longitude,
                    latitude,
                    userRating,
                    photo
            );
        }
        return restaurants;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Since HTTP requests can't be made on the main thread,
    // this class is used to make the request asynchronously
    private class PlaceFetcher extends AsyncTask<String, Void, JSONObject[]> {

        // Create the HTTP Connection
        // Input: a query to be sent to Google Places API
        // Output: an array of Places as JSONObjects
        public JSONObject[] request() {
            int i = 0;
            String nextPageToken = "";
            String query = buildQuery(nextPageToken);
            JSONObject[] places = null;
            while (true) {
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

                    for (; i < Jarray.length(); i++) {
                        JSONObject object     = Jarray.getJSONObject(i);

                        places[i] = object;
//                    System.out.println(places[i]) + " @ " + places[i].get("vicinity"));
                        System.out.println(places[i]);
                    }
                    nextPageToken = Jobject.get("next_page_token").toString();
                    if (nextPageToken == "") {
                        break;
                    } else {
                        break;
//                        query = buildQuery(nextPageToken);
                    }
                }  catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("IT WORKED" + places.length);
            return places;
        }

        // Make the request in the background
        // Output: an list of JSON objects
        @Override
        protected JSONObject[] doInBackground(String... params) {
            JSONObject[] places = request();
            return places;
        }

        // Update the user interface with obtained data
        @Override
        protected void onPostExecute(JSONObject[] result) {
            try {
                CreateRoomFragment.this.onBackgroundTaskDataObtained(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private final Context context;

        public PlaceFetcher(Context c){
            this.context = c;
        }



    }
}