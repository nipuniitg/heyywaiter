package com.example.gangaprasadkoturwar.heyywaiter;

/**
 * Created by Gangaprasad.Koturwar on 08-02-2015.
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;


public class Search extends Fragment
{
    View myFragmentView;
    SearchView search;
    ImageButton buttonBarcode;
    ImageButton buttonAudio;
    Typeface type;
    ListView searchResults;
    String found = "N";


    //This arraylist will have data as pulled from server. This will keep cumulating.
    ArrayList<Restaurant> restaurantResults = new ArrayList<Restaurant>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<Restaurant> filteredRestaurantResults = new ArrayList<Restaurant>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        //get the context of the HomeScreen Activity
        final HomeScreen activity = (HomeScreen) getActivity();

        //define a typeface for formatting text fields and listview.

        //type= Typeface.createFromAsset(activity.getAssets(),"fonts/book.TTF");
        myFragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        search=(SearchView) myFragmentView.findViewById(R.id.searchView1);
        search.setQueryHint("Start typing to search...");

        searchResults = (ListView) myFragmentView.findViewById(R.id.listview_search);
        buttonAudio = (ImageButton) myFragmentView.findViewById(R.id.imageButton1);


        //@Nipun, can we get the SearchableActivity called from this function itself?

        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                //Toast.makeText(activity, String.valueOf(hasFocus),Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnQueryTextListener(new OnQueryTextListener()
        {

            // Write the query builder inside these function
            /*@Nipun,
                This would be traditional query submission.
            */
            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.length() > 3)
                {

                    searchResults.setVisibility(myFragmentView.VISIBLE);
                    getRestaurants gR= (getRestaurants) new getRestaurants().execute(newText);
                }
                else
                {

                    searchResults.setVisibility(myFragmentView.INVISIBLE);
                }



                return false;
            }

        });
        return myFragmentView;
    }

    //this filters products from productResults and copies to filteredProductResults based on search text

    public void filterProductArray(String newText)
    {

        String pName;

        filteredRestaurantResults.clear();
        for (int i = 0; i < restaurantResults.size(); i++)
        {
            pName = restaurantResults.get(i).getRestaurantName().toLowerCase();
            if ( pName.contains(newText.toLowerCase()))
            {
                filteredRestaurantResults.add(restaurantResults.get(i));
            }
        }

    }

    //in this getRestaurants AsyncTask, we are fetching list of Restaurants from server for the search string entered by user.
    class getRestaurants extends AsyncTask<String, Void, String>
    {
        JSONParser jParser;
        JSONArray restaurantList;
        String url=new String();
        String textSearch;
        ProgressDialog pd;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            restaurantList=new JSONArray();
            jParser = new JSONParser();
            pd= new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }


        @Override
        protected String doInBackground(String... sText) {
            /*@Nipun,
                Here we cab add the asynchronous query to server to fetch the results as user keeps on typing the term
                Can we get our queries to respond to these?
                i.e. Where clause in sql queries would be like 'WHERE RestaurantName = *[text]*' i.e substring.
                It would be cooler this way
                I am hardcoding bengaluru and bheema in this as per the example you have provided in searchableActivity
            */
            String getRestaurantsUrl = "http://yourmenu.comuf.com/getRestaurants.php?resname=bheemas&defaultCity=bengaluru";
            //JSONObject restaurants = jParser.getJSONFromUrl(getRestaurantsUrl);
            //url="http://lawgo.in/lawgo/products/user/1/search/"+sText[0];
            String returnResult = getRestaurantList(getRestaurantsUrl);
            this.textSearch = sText[0];
            return returnResult;

        }

        public String getRestaurantList(String url)
        {

            Restaurant tempRestaurant = new Restaurant();
            String matchFound = "N";
            //productResults is an arraylist with all product details for the search criteria
            //productResults.clear();


            try {


                JSONObject json = jParser.getJSONFromUrl(url);
                restaurantList = json.getJSONArray("Restaurants");
                for(int i=0;i<restaurantList.length();i++)
                {
                    /*@Nipun,
                        Create temporary Restaurant class and feed the details (We wont add this directly yet as we need to check for the
                        duplicates)
                        Please change the aliases as per the json query that you have generated.
                    */
                    tempRestaurant = new Restaurant();
                    JSONObject obj=restaurantList.getJSONObject(i);
                    tempRestaurant.setRestaurantName(obj.getString("resname"));
                    tempRestaurant.setRestaurantLocation(obj.getString("address"));
                    tempRestaurant.setRestaurantFoodType(obj.getString("type"));
<<<<<<< HEAD
                    tempRestaurant.setRestaurantRating(obj.getDouble("avgrating"));
=======
                    String rating = obj.getString("avgrating");
                    tempRestaurant.setRestaurantRating(Double.parseDouble(rating));
>>>>>>> origin/master

                    //check if this restaurant is already there in restaurantResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j=0; j < restaurantResults.size();j++)
                    {
                        if (restaurantResults.get(j).equals(tempRestaurant))
                        {
                            matchFound = "Y";
                        }
                    }

                    if (matchFound == "N")
                    {
                        restaurantResults.add(tempRestaurant);
                    }

                }

                return ("OK");

            } catch (Exception e) {
                e.printStackTrace();
                return ("Exception Caught");
            }
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if(result.equalsIgnoreCase("Exception Caught"))
            {
                Toast.makeText(getActivity(), "Unable to connect to server,please try later", Toast.LENGTH_LONG).show();

                pd.dismiss();
            }
            else
            {


                //calling this method to filter the search results from productResults and move them to
                //filteredProductResults
                filterProductArray(textSearch);
                searchResults.setAdapter(new SearchResultsAdapter(getActivity(),filteredRestaurantResults));
                pd.dismiss();
            }
        }

    }
}

class SearchResultsAdapter extends BaseAdapter
{
    private LayoutInflater layoutInflater;

    private ArrayList<Restaurant> restaurantDetails=new ArrayList<Restaurant>();
    int count;
 //   Typeface type;
    Context context;

    //constructor method
    public SearchResultsAdapter(Context context, ArrayList<Restaurant> restaurant_details) {

        layoutInflater = LayoutInflater.from(context);

        this.restaurantDetails=restaurant_details;
        this.count= restaurant_details.size();
        this.context = context;
//      type= Typeface.createFromAsset(context.getAssets(),"fonts/book.TTF");

    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int arg0) {
        return restaurantDetails.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder;
        Restaurant tempRestaurant = restaurantDetails.get(position);

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.listtwo_searchresults, null);
            holder = new ViewHolder();
            holder.restaurant_name = (TextView) convertView.findViewById(R.id.restaurant_name);
            holder.restaurant_location = (TextView) convertView.findViewById(R.id.restaurant_location);
            holder.restaurant_food_type = (TextView) convertView.findViewById(R.id.restaurant_food_type);
            holder.restaurant_price_range = (TextView) convertView.findViewById(R.id.restaurant_price_range);
            holder.restaurant_menu = (Button) convertView.findViewById(R.id.restaurant_menu);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.restaurant_name.setText(tempRestaurant.getRestaurantName());
        holder.restaurant_location.setText(tempRestaurant.getRestaurantLocation());
        holder.restaurant_food_type.setText(tempRestaurant.getRestaurantFoodType());
        holder.restaurant_price_range.setText(tempRestaurant.getRestaurantPriceRange());


        return convertView;
    }

    static class ViewHolder
    {
        TextView restaurant_name;
        TextView restaurant_price_range;
        TextView restaurant_food_type;
        TextView restaurant_location;
        Button restaurant_menu;

    }

}



