package com.example.gangaprasadkoturwar.heyywaiter;

/**
 * Created by Gangaprasad.Koturwar on 08-02-2015.
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

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
    ArrayList<Restaurant> productResults = new ArrayList<Restaurant>();
    //Based on the search string, only filtered products will be moved here from productResults
    ArrayList<Restaurant> filteredProductResults = new ArrayList<Restaurant>();

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

        type= Typeface.createFromAsset(activity.getAssets(),"fonts/book.TTF");
        myFragmentView = inflater.inflate(R.layout.fragment_search, container, false);

        search=(SearchView) myFragmentView.findViewById(R.id.searchView1);
        search.setQueryHint("Start typing to search...");

        searchResults = (ListView) myFragmentView.findViewById(R.id.listview_search);
        buttonAudio = (ImageButton) myFragmentView.findViewById(R.id.imageButton1);


        //this part of the code is to handle the situation when user enters any search criteria, how should the
        //application behave?

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
                    myAsyncTask m= (myAsyncTask) new myAsyncTask().execute(newText);
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

        filteredProductResults.clear();
        for (int i = 0; i < productResults.size(); i++)
        {
            pName = productResults.get(i).getRestaurantName().toLowerCase();
            if ( pName.contains(newText.toLowerCase()))
            {
                filteredProductResults.add(productResults.get(i));
            }
        }

    }

    //in this myAsyncTask, we are fetching data from server for the search string entered by user.
    class myAsyncTask extends AsyncTask<String, Void, String>
    {
        JSONParser jParser;
        JSONArray productList;
        String url=new String();
        String textSearch;
        ProgressDialog pd;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productList=new JSONArray();
            jParser = new JSONParser();
            pd= new ProgressDialog(getActivity());
            pd.setCancelable(false);
            pd.setMessage("Searching...");
            pd.getWindow().setGravity(Gravity.CENTER);
            pd.show();
        }

        @Override
        protected String doInBackground(String... sText) {

            url="http://lawgo.in/lawgo/products/user/1/search/"+sText[0];
            String returnResult = getProductList(url);
            this.textSearch = sText[0];
            return returnResult;

        }

        public String getProductList(String url)
        {

            Restaurant tempRestaurant = new Restaurant();
            String matchFound = "N";
            //productResults is an arraylist with all product details for the search criteria
            //productResults.clear();


            try {


                JSONObject json = jParser.getJSONFromUrl(url);

                productList = json.getJSONArray("ProductList");

                //parse date for dateList
                for(int i=0;i<productList.length();i++)
                {
                    tempRestaurant = new Restaurant();

                    JSONObject obj=productList.getJSONObject(i);

                    tempRestaurant.setRestaurantName(obj.getString("RestaurantName"));
                    tempRestaurant.setRestaurantLocation(obj.getString("RestaurantLocation"));
                    tempRestaurant.setRestaurantFoodType(obj.getString("RestaurantFoodType"));
                    tempRestaurant.setRestaurantRating(obj.getInt("RestaurantRating"));

                    //check if this product is already there in productResults, if yes, then don't add it again.
                    matchFound = "N";

                    for (int j=0; j < productResults.size();j++)
                    {

                        if (productResults.get(j).getRestaurantName().equals(tempRestaurant.getRestaurantName()))
                        {
                            matchFound = "Y";
                        }
                    }

                    if (matchFound == "N")
                    {
                        productResults.add(tempRestaurant);
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
                searchResults.setAdapter(new SearchResultsAdapter(getActivity(),filteredProductResults));
                pd.dismiss();
            }
        }

    }
}

class SearchResultsAdapter extends BaseAdapter
{
    private LayoutInflater layoutInflater;

    private ArrayList<Restaurant> productDetails=new ArrayList<Restaurant>();
    int count;
    Typeface type;
    Context context;

    //constructor method
    public SearchResultsAdapter(Context context, ArrayList<Restaurant> product_details) {

        layoutInflater = LayoutInflater.from(context);

        this.productDetails=product_details;
        this.count= product_details.size();
        this.context = context;
        type= Typeface.createFromAsset(context.getAssets(),"fonts/book.TTF");

    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int arg0) {
        return productDetails.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        ViewHolder holder;
        Restaurant tempRestaurant = productDetails.get(position);

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.listtwo_searchresults, null);
            holder = new ViewHolder();
            holder.product_name = (TextView) convertView.findViewById(R.id.product_name);
            holder.product_mrp = (TextView) convertView.findViewById(R.id.product_mrp);
            holder.product_mrpvalue = (TextView) convertView.findViewById(R.id.product_mrpvalue);
            holder.product_bb = (TextView) convertView.findViewById(R.id.product_bb);
            holder.product_bbvalue = (TextView) convertView.findViewById(R.id.product_bbvalue);
            holder.addToCart = (Button) convertView.findViewById(R.id.add_cart);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.product_name.setText(tempRestaurant.getRestaurantName());
        holder.product_name.setTypeface(type);

        holder.product_mrp.setTypeface(type);

        holder.product_mrpvalue.setText(tempRestaurant.getRestaurantLocation());
        holder.product_mrpvalue.setTypeface(type);

        holder.product_bb.setTypeface(type);

        holder.product_bbvalue.setText(tempRestaurant.getRestaurantFoodType());
        holder.product_bbvalue.setTypeface(type);

        return convertView;
    }

    static class ViewHolder
    {
        TextView product_name;
        TextView product_mrp;
        TextView product_mrpvalue;
        TextView product_bb;
        TextView product_bbvalue;
        TextView product_savings;
        TextView product_savingsvalue;
        TextView qty;
        TextView product_value;
        Button addToCart;

    }

}



