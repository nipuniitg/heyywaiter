package com.example.gangaprasadkoturwar.heyywaiter;

import android.app.Activity;
import android.content.ContentValues;
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

        //create a database if it doesnt exist, if it exists, it will open the database.

        sqLite = this.openOrCreateDatabase("basketbuddy", MODE_PRIVATE, null);

        //sqLite.execSQL("CREATE TABLE IF NOT EXISTS RESTAURANTS ( RESID INTEGER, RESNAME VARCHAR, ADDRESS VARCHAR,CITY VARCHAR, TYPE VARCHAR, PRICERANGE INTEGER, AVGRATING REAL, OPENTIME TIME, CLOSETIME TIME )");
        sqLite.execSQL("CREATE TABLE IF NOT EXISTS USER_PREF ( CITY_NAME VARCHAR )");
        sqLite.execSQL("CREATE TABLE IF NOT EXISTS CITY_LIST ( CITY_NAME VARCHAR )");
        sqLite.execSQL("CREATE TABLE IF NOT EXISTS PREV_SEARCH ( SEARCH VARCHAR )");

        //call asynctask to fetch list of city to populate the dropdown.

        updateList ul = new updateList();
        ul.execute();

    }

    class updateList extends AsyncTask<Void, Void, String> {
        JSONParser jParser;
        String getCitiesUrl;// = new String();
        //String getRestaurantsUrl = new String();
        //String searchStr = new String();
        //String defaultCity = new String();
        SQLiteDatabase sqLite;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params){
            sqLite = getApplicationContext().openOrCreateDatabase("basketbuddy", MODE_PRIVATE, null);

            //sqLite.execSQL("CREATE TABLE IF NOT EXISTS RESTAURANTS ( RESID INTEGER, RESNAME VARCHAR, ADDRESS VARCHAR,CITY VARCHAR, TYPE VARCHAR, PRICERANGE INTEGER, AVGRATING REAL, OPENTIME TIME, CLOSETIME TIME )");
            sqLite.execSQL("CREATE TABLE IF NOT EXISTS USER_PREF ( CITY_NAME VARCHAR )");
            sqLite.execSQL("CREATE TABLE IF NOT EXISTS CITY_LIST ( CITY_NAME VARCHAR )");
            sqLite.execSQL("CREATE TABLE IF NOT EXISTS PREV_SEARCH ( SEARCH VARCHAR )");
            getCitiesUrl = "http://yourmenu.comuf.com/getCities.php";
            jParser = new JSONParser();
            try {
                JSONObject cities = jParser.getJSONFromUrl(getCitiesUrl);
                // Json format for url is :
                // {"cityNames":[{"city":"bengaluru"},{"city":"Mumbai"}]}
                try {
                    JSONArray jar = cities.getJSONArray("cityNames");
                    Log.d("waiter", "" + jar.length());
                    //sqLite.execSQL("TRUNCATE TABLE CITY_LIST;");
                    sqLite.delete("CITY_LIST",null,null);
                    for (int i = 0; i < jar.length(); i++) {

                        JSONObject j = jar.getJSONObject(i);
                        // each JSONObject is of the form {"city":"bengaluru"}
                        String city = j.getString("city");
                        Log.d("City Name", city);
                        ContentValues val = new ContentValues();
                        val.put( "CITY_NAME", city);
                        sqLite.insert("CITY_LIST",null,val);
                        //sqLite.execSQL("INSERT INTO CITY_LIST VALUES ( '" + city + "' );");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return "perror";
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "error";
            }
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.equals("perror")) {
                moveTaskToBack(true);
                Toast.makeText(getApplicationContext(), "Error!!! Oops..that was humiliating. Could you please try again", Toast.LENGTH_LONG).show();
            } else if (result.equals("error")) {
                moveTaskToBack(true);
                Toast.makeText(getApplicationContext(), "Please Please Check your Internet Connection", Toast.LENGTH_LONG).show();
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Cursor c = sqLite.rawQuery("SELECT CITY_NAME FROM USER_PREF", null);
                        Log.d("arindam", "c count" + c.getCount());
                        if (c.getCount() == 0) {
                            //sqLite.execSQL("INSERT INTO USER_PREF VALUES('NONE')");
                            ContentValues val = new ContentValues();
                            val.put("CITY_NAME","None");
                            sqLite.insert("USER_PREF",null,val);
                        }

                        Cursor d = sqLite.rawQuery("SELECT CITY_NAME FROM USER_PREF", null);
                        Log.d("arindam", "d count" + d.getCount());

                        if (d.moveToFirst()) {
                            Log.d("arindam", "d NONE" + d.getString(0));

                            if (d.getString(0).equals("NONE")) {
                                Intent intent = new Intent(StartScreen.this, CityScreen.class);
                                startActivity(intent);
                            } else {
                                Intent intent = new Intent(StartScreen.this, MainActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }
                        c.close();
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

