package com.ventricles.incovid;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class AdapterRecyclerNearby extends RecyclerView.Adapter<AdapterRecyclerNearby.QuickViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<ModelNearbyButton> list;
    ProgressBar pbar;
    PlacesTask placesTask;

    public AdapterRecyclerNearby(Context context, List list) {
        this.context = context;
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        pbar = ((MapsActivity)context).getPbar();
    }

    @NonNull
    @Override
    public QuickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_nearby_btns, parent,false);
        return new QuickViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuickViewHolder holder, int position) {
        final ModelNearbyButton modelNearbyButton = list.get(position);
            if (position==getItemCount()-1) {
                float factor = holder.itemView.getContext().getResources().getDisplayMetrics().density;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (60 * factor), (int) (60 * factor));
                layoutParams.setMargins((int) (20 * factor), 0, (int) (20 * factor), 0);
                holder.imageButton.setLayoutParams(layoutParams);
            }
            holder.imageButton.setImageResource(modelNearbyButton.getDraw_src());
            holder.imageButton.setBackgroundResource(modelNearbyButton.getBackground());
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //((MapsActivity)context).PlaceTypeSearch(modelNearbyButton.getType());
                    InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                    assert inputMethodManager != null;
                    inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
                    ((MapsActivity)context).close_expanded_card();
                    Toast toast = Toast.makeText(context, R.string.searching_within_5km, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP,0, (int) (30*context.getResources().getDisplayMetrics().density));
                    toast.show();
                    pbar.setVisibility(View.VISIBLE);
                    placesTask = new PlacesTask();
                    placesTask.execute(String.valueOf(sbMethod(5000, modelNearbyButton.getType())));



                }
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class QuickViewHolder extends RecyclerView.ViewHolder{
        ImageButton imageButton;

        public QuickViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.btn_nearby);
        }
    }
    public StringBuilder sbMethod(int radius, String type) {
        Location location = ((MapsActivity)context).getCurrentlocation();
        //use your current location here
        double mLatitude = location.getLatitude();
        double mLongitude = location.getLongitude();

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + mLatitude + "," + mLongitude);
        sb.append("&radius=");
        sb.append(String.valueOf(radius));
        sb.append("&types=");
        sb.append(type);
        sb.append("&sensor=true");
        sb.append("&key=").append(context.getResources().getString(R.string.google_api_key));

        Log.d("Map", "api: " + sb.toString());

        return sb;
    }
    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;
        ParserTask parserTask;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }


        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result) {
            parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParserTask
            parserTask.execute(result);
        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception downloading", e.toString());
        } finally {
            assert iStream != null;
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            Place_JSON placeJson = new Place_JSON();

            try {
                jObject = new JSONObject(jsonData[0]);

                places = placeJson.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String, String>> list) {

            Log.d("Map", "list size: " + list.size());
            // Clears all the existing markers;
            ((MapsActivity)context).remove_search_marker_bulk();
            if (list.isEmpty()){
                Toast toast = Toast.makeText(context, "Found Nothing", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP,0, (int) (30*context.getResources().getDisplayMetrics().density));
                toast.show();
                pbar.setVisibility(View.GONE);
            }

            for (int i = 0; i < list.size(); i++) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Getting a place from the places list
                HashMap<String, String> hmPlace = list.get(i);


                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                Log.d("Map", "place: " + name);

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(name + " : " + vicinity);

                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

                // Placing a marker on the touched position
                ((MapsActivity)context).__add_search_marker_bulk(latLng, markerOptions);

            }
            pbar.setVisibility(View.GONE);
        }
    }


    public class Place_JSON {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("results");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            assert jPlaces != null;
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<String, String>();
            String placeName = "-NA-";
            String vicinity = "-NA-";
            String latitude = "";
            String longitude = "";
            String reference = "";

            try {
                // Extracting Place name, if available
                if (!jPlace.isNull("name")) {
                    placeName = jPlace.getString("name");
                }

                // Extracting Place Vicinity, if available
                if (!jPlace.isNull("vicinity")) {
                    vicinity = jPlace.getString("vicinity");
                }

                latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
                longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");
                reference = jPlace.getString("reference");

                place.put("place_name", placeName);
                place.put("vicinity", vicinity);
                place.put("lat", latitude);
                place.put("lng", longitude);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }

}
