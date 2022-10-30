package com.chickentender.apk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.chickentender.apk.databinding.ActivityMainBinding;
import com.chickentender.apk.databinding.ActivityRestaurantListBinding;

import java.util.ArrayList;

public class RestaurantListActivity extends ListActivity {
    ArrayList<String> listItems=new ArrayList<String>();
    private ActivityRestaurantListBinding binding;
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;

    private void fillList() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        adapter.addAll(bundle.getStringArrayList("listview"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);
        fillList();

        setContentView(R.layout.activity_restaurant_list);
    }
}