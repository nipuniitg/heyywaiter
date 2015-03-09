package com.example.gangaprasadkoturwar.heyywaiter;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by edaranipun on 3/4/15.
 */
public class SearchableActivity extends ListActivity {
    // Get the intent, verify the action and get the query
    public ArrayList<Restaurant> results = new ArrayList<Restaurant>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    public ArrayList<Restaurant> doMySearch( String query){
         try{

             String defaultCity = "";
             JSONParser jParser = new JSONParser();
             SQLiteDatabase sqLite = this.openOrCreateDatabase("basketbuddy", MODE_PRIVATE, null);
             //sqLite.execSQL("CREATE TABLE IF NOT EXISTS RESTAURANTS ( RESID INTEGER, RESNAME VARCHAR, ADDRESS VARCHAR,CITY VARCHAR, TYPE VARCHAR, PRICERANGE INTEGER, AVGRATING REAL, OPENTIME TIME, CLOSETIME TIME )");
             sqLite.execSQL("CREATE TABLE IF NOT EXISTS USER_PREF ( CITY_NAME VARCHAR )");
             sqLite.execSQL("CREATE TABLE IF NOT EXISTS CITY_LIST ( CITY_NAME VARCHAR )");
             sqLite.execSQL("CREATE TABLE IF NOT EXISTS PREV_SEARCH ( SEARCH VARCHAR )");

             Cursor d = sqLite.rawQuery("SELECT CITY_NAME FROM USER_PREF", null);
             Log.d("arindam", "d count" + d.getCount());

             if (d.moveToFirst()) {
                 Log.d("arindam", "d NONE" + d.getString(0));

                 if (d.getString(0).equals("NONE")) {
                     defaultCity = "bengaluru";
                 } else {
                     defaultCity = d.getString(0);
                 }
             }

                String getRestaurantsUrl = "http://yourmenu.comuf.com/getRestaurants.php?resname=" + query + "&defaultCity=" + defaultCity;
                JSONObject restaurants = jParser.getJSONFromUrl(getRestaurantsUrl);

                // Json format for url2 is :
                // {"Restaurants":[{"resid":"3","resname":"bheemas","address":"sarjapur road","city":"bengaluru","type":"south-indian, chinese","pricerange":"500","avgrating":"0","opentime":"10:00:00","closetime":"23:00:00"}]}
                try {
                    JSONArray jar2 = restaurants.getJSONArray("Restaurants");
                    Log.d("arindam2", "" + jar2.length());
                    //sqLite.delete("RESTAURANTS",null,null);
                    for (int i = 0; i < jar2.length(); i++) {

                        JSONObject j = jar2.getJSONObject(i);
                        // each JSONObject is of the form {"resid":"3","resname":"bheemas","address":"sarjapur road","city":"bengaluru","type":"south-indian, chinese","pricerange":"500","avgrating":"0","opentime":"10:00:00","closetime":"23:00:00"}
                        String restaurantName = j.getString("resname");
                        //Integer resid = j.getInt("resid");
                        Log.d("Restaurant Name", restaurantName);
                        Restaurant newRest = new Restaurant();
                        newRest.setRestaurantCode(j.getString("resid"));
                        newRest.setRestaurantName(j.getString("resname"));
                        newRest.setRestaurantLocation(j.getString("address"));
                        newRest.setRestaurantFoodType(j.getString("type"));
                        newRest.setRestaurantRating(Double.parseDouble(j.getString("avgrating")));
                        results.add(newRest);
                        //Log.d("Restaurant Id",resid.toString() );
                        //String query = "INSERT INTO RESTAURANT VALUES('"+ j.getString("resid")+"','"+j.getString("resname")+"','"+j.getString("address")+"','"+j.getString("city")+"','"+j.getString("type")+"','"+j.getString("pricerange")+"','"+j.getString("opentime")+"','"+j.getString("closetime")+ "');";
                        /*
                        ContentValues val = new ContentValues();
                        val.put("RESID",j.getString("resid"));
                        val.put("RESNAME",j.getString("resname"));
                        val.put("ADDRESS",j.getString("address"));
                        val.put("CITY",j.getString("city"));
                        val.put("TYPE",j.getString("type"));
                        val.put("PRICERANGE",j.getString("pricerange"));
                        val.put("AVGRATING",j.getString("avgrating"));
                        val.put("OPENTIME",j.getString("opentime"));
                        val.put("CLOSETIME",j.getString("closetime"));
                        //Log.d(" insert query ",query);
                        //sqLite.execSQL( query );
                        sqLite.insert("RESTAURANTS",null,val);
                        */
                    }
                    /*
                    Cursor cur =  sqLite.rawQuery("SELECT * FROM RESTAURANTS;",null);
                    CursorAdapter cursorAdapter = new CursorAdapter(getApplicationContext(),cur,CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {

                        @Override
                        public View newView(Context context, Cursor cursor, ViewGroup parent) {
                            return null;
                        }

                        @Override
                        public void bindView(View view, Context context, Cursor cursor) {

                        }
                    };
                    cur.close();
                    */
                    d.close();
                    return results;

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return null;
                    //return "perror";
                    //return "success";
                }

         } catch (Exception e) {
             e.printStackTrace();
             return null;
             //return "error";
         }

    }
}


