package com.example.gangaprasadkoturwar.heyywaiter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StartScreen extends Activity {

    SQLiteDatabase sqLite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        //initializing the action bar and hiding it.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();


        //create a database if it doesnt exist, if it exists, it will open the database.

        sqLite = this.openOrCreateDatabase("basketbuddy", MODE_PRIVATE, null);

        sqLite.execSQL( "CREATE TABLE IF NOT EXISTS RESTAURANTS ( RESID INTEGER, RESNAME VARCHAR, ADDRESS VARCHAR,CITY VARCHAR, TYPE VARCHAR, PRICERANGE INTEGER, AVGRATING REAL, OPENTIME TIME, CLOSETIME TIME )");
        sqLite.execSQL( "CREATE TABLE IF NOT EXISTS USER_PREF ( CITY_NAME VARCHAR )" );
        sqLite.execSQL( "CREATE TABLE IF NOT EXISTS CITY_LIST ( CITY_NAME VARCHAR )" );
        sqLite.execSQL( "CREATE TABLE IF NOT EXISTS PREV_SEARCH ( SEARCH VARCHAR )" );

        //call asynctask to fetch list of city to populate the dropdown.

        updateList ul = new updateList();
        ul.execute();

    }

    class updateList extends AsyncTask<Void, Void, String> {
        JSONParser jParser;
        String getCitiesUrl = new String();
        String getRestaurantsUrl = new String();
        String searchStr = new String();
        String defaultCity = new String();
        SQLiteDatabase sqLite;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sqLite = getApplicationContext().openOrCreateDatabase("basketbuddy", MODE_PRIVATE, null);
            getCitiesUrl = "http://yourmenu.comuf.com/getCities.php";
            jParser = new JSONParser();
            try {
                JSONObject cities = jParser.getJSONFromUrl(getCitiesUrl);
                // Json format for url is :
                // {"cityNames":[{"city":"bengaluru"},{"city":"Mumbai"}]}
                try {
                    JSONArray jar = cities.getJSONArray("cityNames");
                    Log.d("arindam", "" + jar.length());
                    sqLite.execSQL("TRUNCATE TABLE CITY_LIST;");
                    for (int i = 0; i < jar.length(); i++) {

                        JSONObject j = jar.getJSONObject(i);
                        // each JSONObject is of the form {"city":"bengaluru"}
                        String city = j.getString("city");
                        Log.d("City Name", city);
                        sqLite.execSQL("INSERT INTO CITY_LIST VALUES ( '" + city + "' );");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    //return "perror";
                }

            } catch (Exception e) {
                e.printStackTrace();
                //return "error";
            }
        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                // assign the user search string here

                searchStr = "sarj";
                defaultCity = "Bengaluru";
                getRestaurantsUrl = "http://yourmenu.comuf.com/getRestaurants.php?resname=" + searchStr + "&defaultCity=" + defaultCity;
                JSONObject restaurants = jParser.getJSONFromUrl(getRestaurantsUrl);
                // Json format for url2 is :
                // {"Restaurants":[{"resid":"3","resname":"bheemas","address":"sarjapur road","city":"bengaluru","type":"south-indian, chinese","pricerange":"500","avgrating":"0","opentime":"10:00:00","closetime":"23:00:00"}]}

                try {
                    JSONArray jar2 = restaurants.getJSONArray("Restaurants");
                    Log.d("arindam2", "" + jar2.length());

                    for (int i = 0; i < jar2.length(); i++) {

                        JSONObject j = jar2.getJSONObject(i);
                        // each JSONObject is of the form {"resid":"3","resname":"bheemas","address":"sarjapur road","city":"bengaluru","type":"south-indian, chinese","pricerange":"500","avgrating":"0","opentime":"10:00:00","closetime":"23:00:00"}
                        String restaurantName = j.getString("resname");
                        //Integer resid = j.getInt("resid");
                        Log.d("Restaurant Name", restaurantName);
                        //Log.d("Restaurant Id",resid.toString() );
                        String query = "INSERT INTO RESTAURANT VALUES('"+ j.getString("resid")+"','"+j.getString("resname")+"','"+j.getString("address")+"','"+j.getString("city")+"','"+j.getString("type")+"','"+j.getString("pricerange")+"','"+j.getString("opentime")+"','"+j.getString("closetime")+ "');";
                        Log.d(" insert query ",query);
                        sqLite.execSQL( query );
                        Toast.makeText(getApplicationContext(),"Done execution",Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    return "perror";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }

            return "success";
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("perror")) {
                moveTaskToBack(true);
                Toast.makeText(getApplicationContext(), "Error!!! Oops..that was humiliating. Could you please try again", Toast.LENGTH_LONG).show();
            } else if (result.equals("error")) {
                moveTaskToBack(true);
                Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Cursor c = sqLite.rawQuery("SELECT CITY_NAME FROM USER_PREF", null);
                        Log.d("arindam", "c count" + c.getCount());
                        if (c.getCount() == 0) {
                            sqLite.execSQL("INSERT INTO USER_PREF (CITY_NAME) VALUES('NONE')");
                        }


                        Cursor d = sqLite.rawQuery("SELECT CITY_NAME FROM USER_PREF", null);
                        Log.d("arindam", "d count" + d.getCount());

                        if (d.moveToFirst()) {
                            Log.d("arindam", "d NONE" + d.getString(0));
                            //Intent intent = new Intent(StartScreen.this, CityScreen.class);
                            //startActivity(intent);
                            if (d.getString(0).equals("NONE")) {
                                Intent intent = new Intent(StartScreen.this, CityScreen.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(StartScreen.this, HomeScreen.class);
                                startActivity(intent);
                            }
                            finish();
                        }
                    }
                }, 1000);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sqLite.close();
    }

}

