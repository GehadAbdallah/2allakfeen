/*
 * Copyright (c) 2017. Truiton (http://www.truiton.com/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 * Mohit Gupt (https://github.com/mohitgupt)
 *
 */

package com.example.gp.a2allakfeendemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import com.example.gp.a2allakfeendemo.ViewAdapters.DataObject;
import com.example.gp.a2allakfeendemo.ViewAdapters.MyRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CardViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyRecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    private String result;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        result = getIntent().getStringExtra("Result");
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet(),getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        //lw darbet comment it
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                //Insert the rating value in the database
                int rate = Math.round(ratingBar.getRating());
                int user_id = WelcomeActivity.sharedpreferences.getInt("CurrentUser",-3);
                int route_id = 2;
                Controller c = new Controller();
                if (user_id != -3)
                    c.Insert_routeRate(user_id,route_id,rate);
            }
        });

        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
            }
        });
    }



    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<>();
        ArrayList<Boolean> thereIsAbus= new ArrayList<>();
        List<String> routes = Arrays.asList(result.split(";"));
        for (int i = 0; i < routes.size(); i++) {
            int index = i+1;
            String text1 = "Route " + index;
            String text2 = routes.get(i);
            String[]temp = routes.get(i).split(" ");
            if(temp[1].equals("Bus"))
                thereIsAbus.add(true);
            else
                thereIsAbus.add(false);
            Log.d("thereIsAbus",Boolean.toString(thereIsAbus.get(i)));
            DataObject obj = new DataObject(text1,text2);
            results.add(obj);
        }

//        String text1 = "Route 1";
//        String text2 = "Bus 990 - From Ain Shams to Cairo University";
//        DataObject obj = new DataObject(text1,
//                    text2);
//            results.add(obj);
//
//        text1 = "Route 2";
//        text2 = "Metro 1 - From Ain Shams to Shohadaa" + "\n" +
//                "Metro 2 - From Shohadaa to Cairo University";
//        obj = new DataObject(text1,
//                text2);
//        results.add(obj);
//
//        text1 = "Route 3";
//        text2 = "Bus 310 From Ain Shams to Al-Mahkama" + "\n" +
//                "Bus 137 From Al-Mahkama to Cairo University";
//        obj = new DataObject(text1,
//                text2);
//        results.add(obj);
//
//        text1 = "Route 4";
//        text2 = "Metro 1 - From Ain Shams to Ghamra" + "\n" +
//                "Bus 1030 - From Ghamra to Cairo University";
//        obj = new DataObject(text1,
//                text2);
//        results.add(obj);
//
//        text1 = "Route 5";
//        text2 = "Metro 1 - From Ain Shams to Al-Tahrir" + "\n" +
//                "Metro 2 - From Al-Tahrir to Cairo University";
//        obj = new DataObject(text1,
//                text2);
//        results.add(obj);

        return results;
    }
}
