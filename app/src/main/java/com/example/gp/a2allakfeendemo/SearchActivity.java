//TODO: idon't know what is this class and how to integrate it (it's related to the suggestions)
//package com.example.gp.a2allakfeendemo;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
//import com.google.android.gms.location.places.ui.PlaceSelectionListener;
//
//public class SearchActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("TAG", "Place: " + place.getName());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("TAG", "An error occurred: " + status);
//            }
//        });
//    }
//}
