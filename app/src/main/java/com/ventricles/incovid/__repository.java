package com.ventricles.incovid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class __repository {

    private String DB_NAME = "Database";
    private ___database _db;
    private FirebaseFirestore db;
    private Context context;
    private String JSON_STRING;
    private String JSON_STRING_toll;
    private String JSON_STRING_COVID;
    private JSONArray redzoneJsonArray;
    private JSONArray tollfreenoJsonArray;
    private Map<String, Integer> mapCases;
    BufferedReader reader=null;
    HttpURLConnection conn = null;
    List<ModelDetailedPlace> placeList;

    public __repository(Context context) {
        _db = Room.databaseBuilder(context, ___database.class,DB_NAME).build();
        this.context = context;
    }

    void destroyDatabase(){
       // _db.close();
       // _db=null;
    }
    public void insert_into_redzone(int ID, String PlaceName, double Lat, double Lon, double Radius){
        Model_red_zone red_zone = new Model_red_zone();
        red_zone.setID(ID);
        red_zone.setLat(Lat);
        red_zone.setLon(Lon);
        red_zone.setPlaceName(PlaceName);
        red_zone.setRadius(Radius);
        insert_into_redzone(red_zone);
    }
    @SuppressLint("StaticFieldLeak")
    public void insert_into_redzone(final Model_red_zone red_zone){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                _db.dao_red_zone().insert(red_zone);
                return null;
            }
        }.execute();
    }


    public void insert_into_cases(int ID, String districtName, int activeCase, double Lat, double Lon){
        modelCasesDistrict casesDistrict = new modelCasesDistrict();
        casesDistrict.setID(ID);
        casesDistrict.setLat(Lat);
        casesDistrict.setLon(Lon);
        casesDistrict.setDistrict(districtName);
        casesDistrict.setActiveCase(activeCase);
        insert_into_cases(casesDistrict);
    }
    @SuppressLint("StaticFieldLeak")
    public void insert_into_cases(final modelCasesDistrict casesDistrict){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                if(_db.dao_cases_districts()!=null)
                _db.dao_cases_districts().Insert(casesDistrict);
                return null;
            }
        }.execute();
    }

    public void insert_into_toll_free_no(String call_name, String call_no){
        Model_toll_free_no model_toll_free_no = new Model_toll_free_no();
        model_toll_free_no.setCall_name(call_name);
        model_toll_free_no.setCall_no(call_no);
        insert_into_toll_free_no(model_toll_free_no);
    }
    @SuppressLint("StaticFieldLeak")
    public void insert_into_toll_free_no(final Model_toll_free_no model_toll_free_no){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                _db.dao_toll_free_no().insert(model_toll_free_no);
                return null;
            }
        }.execute();
    }
    public void insert_into_covid_info(int activeCase, int deathCase){
        modelCovidInfo modelCovidInfo = new modelCovidInfo();
        modelCovidInfo.setTotalCase(activeCase);
        modelCovidInfo.setDeathCase(deathCase);
        insert_into_covid_info(modelCovidInfo);
    }
    @SuppressLint("StaticFieldLeak")
    public void insert_into_covid_info(final modelCovidInfo modelCovidInfo){
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                _db.dao_covidInfo().insert(modelCovidInfo);
                return null;
            }
        }.execute();
    }

    public void insert_into_history( String PlaceName, double Lat, double Lon, String Address){
        SharedPreferences preference = context.getSharedPreferences("order",Context.MODE_PRIVATE);
        int creation = preference.getInt("creation",0);
        ModelDetailedPlace history = new ModelDetailedPlace();
        history.setPlaceAddress(Address);
        history.setLat(Lat);
        history.setLon(Lon);
        history.setPlacename(PlaceName);
        history.setCreation(creation);
        insert_into_history(history);


    }



    @SuppressLint("StaticFieldLeak")
    public void insert_into_history(final ModelDetailedPlace modelDetailedPlace){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                SharedPreferences preference = context.getSharedPreferences("order",Context.MODE_PRIVATE);
                int orderID = preference.getInt("orderID",0);
                int creation = preference.getInt("creation",0);
                List<ModelDetailedPlace> list = _db.daoModelDetailedPlace().getStaticLatLonHistory(modelDetailedPlace.lat, modelDetailedPlace.lon);
                if (list.size()>0){
                 ModelDetailedPlace modelPlace = list.get(0);
                 modelPlace.setCreation(creation);
                 modelPlace.setPlacename(modelDetailedPlace.placename);
                 modelPlace.setPlacename(modelDetailedPlace.placeAddress);
                 _db.daoModelDetailedPlace().update(modelPlace);
                }else {
                    if (orderID < 7) {
                        modelDetailedPlace.setID(orderID);
                        orderID++;
                        preference.edit().putInt("orderID", orderID).apply();
                    } else {
                        orderID = 0;
                        modelDetailedPlace.setID(orderID);
                        orderID++;
                        preference.edit().putInt("orderID", orderID).apply();
                    }
                    _db.daoModelDetailedPlace().insert(modelDetailedPlace);
                }
                preference.edit().putInt("creation", creation + 1).apply();
                return null;
            }
        }.execute();
    }
    @SuppressLint("StaticFieldLeak")
    public void update_history(final ModelDetailedPlace modelDetailedPlace){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                _db.daoModelDetailedPlace().update(modelDetailedPlace);
                return null;
            }
        }.execute();
    }
    public void update_redzone(int ID, String PlaceName, double Lat, double Lon, double Radius){
        Model_red_zone red_zone = new Model_red_zone();
        red_zone.setID(ID);
        red_zone.setLat(Lat);
        red_zone.setLon(Lon);
        red_zone.setPlaceName(PlaceName);
        red_zone.setRadius(Radius);
        update_redzone(red_zone);
    }
    @SuppressLint("StaticFieldLeak")
    public void update_redzone(final Model_red_zone red_zone){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                _db.dao_red_zone().update(red_zone);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void delete_redzone(final Model_red_zone model_red_zone){


            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    _db.dao_red_zone().delete(model_red_zone);
                    return null;
                }
            }.execute();
        }


    @SuppressLint("StaticFieldLeak")
    public void delete_toll_free(final Model_toll_free_no model_toll_free_no){


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                _db.dao_toll_free_no().delete(model_toll_free_no);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void delete_cases_district(final modelCasesDistrict modelCasesDistrict){


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                _db.dao_cases_districts().Delete(modelCasesDistrict);
                return null;
            }
        }.execute();
    }

    public LiveData<List<Model_red_zone>> getRedzone(){
        return _db.dao_red_zone().getAllRedzone();
    }
    public LiveData<Model_red_zone> getForIDRedzone(int ID){
        return _db.dao_red_zone().getForIDRedzone(ID);
    }
    public LiveData<Model_red_zone> getForAllRedzone(){
        return _db.dao_red_zone().getForAllRedzone();
    }
    public LiveData<List<Model_red_zone>> getRedzoneNearby(double lat, double lon){
        return _db.dao_red_zone().getRedzoneNearby(lat, lon);
    }

    public List<Model_red_zone> check_if_in_redzone(double lat, double lon){
        return _db.dao_red_zone().getStaticSingleRedzoneInRange(lat,lon,3);
    }
    public LiveData<List<Model_red_zone>> getRedzoneRanged(double lat, double lon, double distance){
        return _db.dao_red_zone().getRedzoneNearbyRange(lat, lon, distance);
    }

    public LiveData<List<ModelDetailedPlace>> getAllHistory(){
        return _db.daoModelDetailedPlace().getAllHistory();
    }

    public LiveData<List<Model_toll_free_no>> gettollfreeno(){
        return _db.dao_toll_free_no().getAllTollfree();
    }

    public LiveData<List<modelCovidInfo>> getCovidInfo(){
        return _db.dao_covidInfo().getCovidInfo();
    }

    public LiveData<List<modelCasesDistrict>> getCasesDistrictInDistance(double lat, double lon, double distance){
        return _db.dao_cases_districts().getInDistancedActiveCases(distance, lat, lon);
    }

    public LiveData<List<modelCasesDistrict>> getCasesDistrict(){
        return _db.dao_cases_districts().getActiveCases();
    }



    public void getzones(){
        if(mapCases==null){
            getcases();
        }
        new HTTPGetJsonFile().execute("https://data.covid19india.org/zones.json");
    }

    public void getcases(){
        new HTTPGetCases().execute("https://data.covid19india.org/state_district_wise.json");
    }

    public void download_toll_free(){
        JSON_STRING_toll = "";
        new HTTPGetJsonFileToll().execute("https://api.rootnet.in/covid19-in/contacts");
    }

    @SuppressLint("StaticFieldLeak")
    private void fetch_db_redzone(String JSON_STRING_){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                Log.e("testing","fetch_db");
                Log.e("error", strings[0]);
                JSONObject redzoneJsonReader = null;
                String districtListString ="{\"Nicobars\":{\"lat\":10.3871439,\"lon\":84.13529679999999},\"North and Middle Andaman\":{\"lat\":12.651883199999999,\"lon\":92.8976254},\"South Andaman\":{\"lat\":11.8311681,\"lon\":92.6586401},\"Anantapur\":{\"lat\":14.6818877,\"lon\":77.6005911},\"Chittoor\":{\"lat\":13.217176,\"lon\":79.1003289},\"East Godavari\":{\"lat\":17.32125,\"lon\":82.0407137},\"Guntur\":{\"lat\":16.3066525,\"lon\":80.4365402},\"Kurnool\":{\"lat\":15.8281257,\"lon\":78.0372792},\"Prakasam\":{\"lat\":15.348463,\"lon\":79.560344},\"S.P.S. Nellore\":{\"lat\":14.258084799999999,\"lon\":79.9192702},\"Srikakulam\":{\"lat\":18.294916500000003,\"lon\":83.89380179999999},\"Visakhapatnam\":{\"lat\":17.6868159,\"lon\":83.2184815},\"Vizianagaram\":{\"lat\":18.1066576,\"lon\":83.39555059999999},\"West Godavari\":{\"lat\":16.9174181,\"lon\":81.3399414},\"Y.S.R. Kadapa\":{\"lat\":14.5767233,\"lon\":78.8382644},\"Changlang\":{\"lat\":27.7421763,\"lon\":96.642433},\"East Kameng\":{\"lat\":27.423126099999998,\"lon\":93.0175712},\"East Siang\":{\"lat\":28.1097051,\"lon\":95.1432068},\"Kamle\":{\"lat\":23.5407115,\"lon\":84.73939159999999},\"Lohit\":{\"lat\":27.903965099999997,\"lon\":96.17391160000001},\"Lower Dibang Valley\":{\"lat\":28.1428728,\"lon\":95.8431018},\"Lower Subansiri\":{\"lat\":27.6169349,\"lon\":93.83915569999999},\"Namsai\":{\"lat\":27.6692115,\"lon\":95.8644018},\"Pakke Kessang\":{\"lat\":27.145853,\"lon\":93.24626099999999},\"Papum Pare\":{\"lat\":27.171901899999998,\"lon\":93.7029002},\"Siang\":{\"lat\":28.1097051,\"lon\":95.1432068},\"Tawang\":{\"lat\":27.586057399999998,\"lon\":91.8594062},\"Tirap\":{\"lat\":26.9942703,\"lon\":95.5407299},\"Upper Dibang Valley\":{\"lat\":28.812876700000004,\"lon\":96.15269850000001},\"Upper Siang\":{\"lat\":28.7588781,\"lon\":95.22666749999999},\"Upper Subansiri\":{\"lat\":27.786801999999998,\"lon\":94.33596349999999},\"West Kameng\":{\"lat\":27.342763299999998,\"lon\":92.30244599999999},\"West Siang\":{\"lat\":28.147254000000004,\"lon\":94.7484518},\"Baksa\":{\"lat\":26.693544799999998,\"lon\":91.5983959},\"Barpeta\":{\"lat\":26.3215985,\"lon\":90.9820668},\"Biswanath\":{\"lat\":26.726663199999997,\"lon\":93.1478514},\"Bongaigaon\":{\"lat\":26.5030072,\"lon\":90.5535724},\"Cachar\":{\"lat\":24.7821253,\"lon\":92.8577105},\"Chirang\":{\"lat\":26.6539048,\"lon\":90.6393702},\"Darrang\":{\"lat\":26.4522876,\"lon\":92.02731899999999},\"Dhemaji\":{\"lat\":27.481110599999997,\"lon\":94.55728470000001},\"Dhubri\":{\"lat\":26.020698199999998,\"lon\":89.9743463},\"Dibrugarh\":{\"lat\":27.472832699999998,\"lon\":94.9119621},\"Dima Hasao\":{\"lat\":25.3478004,\"lon\":93.0175712},\"Goalpara\":{\"lat\":26.0875755,\"lon\":90.563609},\"Golaghat\":{\"lat\":26.5238515,\"lon\":93.96233699999999},\"Hailakandi\":{\"lat\":24.6811018,\"lon\":92.5638055},\"Hojai\":{\"lat\":26.0016601,\"lon\":92.847737},\"Jorhat\":{\"lat\":26.750920699999998,\"lon\":94.2036696},\"Kamrup\":{\"lat\":26.3160819,\"lon\":91.5983959},\"Kamrup Metropolitan\":{\"lat\":26.0794316,\"lon\":91.63721500000001},\"Karbi Anglong\":{\"lat\":25.8456764,\"lon\":93.43775839999999},\"Karimganj\":{\"lat\":24.8649128,\"lon\":92.3591531},\"Kokrajhar\":{\"lat\":26.4014362,\"lon\":90.266699},\"Lakhimpur\":{\"lat\":27.946239499999997,\"lon\":80.7787163},\"Majuli\":{\"lat\":27.0016172,\"lon\":94.2242981},\"Morigaon\":{\"lat\":26.2528853,\"lon\":92.33695499999999},\"Nagaon\":{\"lat\":26.348008999999998,\"lon\":92.6838111},\"Nalbari\":{\"lat\":26.444618499999997,\"lon\":91.4410527},\"Sivasagar\":{\"lat\":26.9826098,\"lon\":94.6424521},\"Sonitpur\":{\"lat\":26.673885100000003,\"lon\":92.8577105},\"Tinsukia\":{\"lat\":27.488553900000003,\"lon\":95.355758},\"Udalguri\":{\"lat\":26.6897159,\"lon\":91.9099238},\"West Karbi Anglong\":{\"lat\":25.8456764,\"lon\":93.43775839999999},\"Araria\":{\"lat\":26.1324689,\"lon\":87.4528067},\"Arwal\":{\"lat\":25.1643264,\"lon\":84.6688348},\"Aurangabad\":{\"lat\":19.8761653,\"lon\":75.3433139},\"Begusarai\":{\"lat\":25.4181638,\"lon\":86.1271542},\"Bhagalpur\":{\"lat\":25.242452999999998,\"lon\":86.9842256},\"Bhojpur\":{\"lat\":25.466155,\"lon\":84.5222189},\"Buxar\":{\"lat\":25.564710299999998,\"lon\":83.9777482},\"Darbhanga\":{\"lat\":26.1542045,\"lon\":85.8918454},\"East Champaran\":{\"lat\":26.6098139,\"lon\":84.8567932},\"Gaya\":{\"lat\":24.7913957,\"lon\":85.0002336},\"Gopalganj\":{\"lat\":26.483158399999997,\"lon\":84.43655},\"Jamui\":{\"lat\":24.9195147,\"lon\":86.224718},\"Jehanabad\":{\"lat\":25.2132649,\"lon\":84.9853322},\"Kaimur\":{\"lat\":25.0545504,\"lon\":83.67739279999999},\"Katihar\":{\"lat\":25.5540648,\"lon\":87.5591073},\"Khagaria\":{\"lat\":25.50452,\"lon\":86.47014159999999},\"Kishanganj\":{\"lat\":26.0982167,\"lon\":87.9450379},\"Lakhisarai\":{\"lat\":25.157145399999997,\"lon\":86.0951592},\"Madhepura\":{\"lat\":25.9239677,\"lon\":86.7946387},\"Madhubani\":{\"lat\":26.3482938,\"lon\":86.0711661},\"Munger\":{\"lat\":25.370799299999998,\"lon\":86.47339029999999},\"Muzaffarpur\":{\"lat\":26.119660699999997,\"lon\":85.390982},\"Nalanda\":{\"lat\":25.1240603,\"lon\":85.45947489999999},\"Nawada\":{\"lat\":24.8866859,\"lon\":85.54345719999999},\"Patna\":{\"lat\":25.594094700000003,\"lon\":85.1375645},\"Purnia\":{\"lat\":25.777139100000003,\"lon\":87.4752551},\"Rohtas\":{\"lat\":25.0685878,\"lon\":84.01674229999999},\"Saharsa\":{\"lat\":25.8773651,\"lon\":86.5927887},\"Samastipur\":{\"lat\":25.8560271,\"lon\":85.7868233},\"Saran\":{\"lat\":25.8559698,\"lon\":84.8567932},\"Sheikhpura\":{\"lat\":25.141693699999998,\"lon\":85.86289149999999},\"Sheohar\":{\"lat\":26.514587199999998,\"lon\":85.29423129999999},\"Sitamarhi\":{\"lat\":26.5886976,\"lon\":85.5012971},\"Siwan\":{\"lat\":26.2243204,\"lon\":84.3599953},\"Supaul\":{\"lat\":26.123371799999997,\"lon\":86.6045134},\"Vaishali\":{\"lat\":25.6838206,\"lon\":85.35496499999999},\"West Champaran\":{\"lat\":27.1543104,\"lon\":84.3542049},\"Chandigarh\":{\"lat\":30.7333148,\"lon\":76.7794179},\"Balod\":{\"lat\":20.7270655,\"lon\":81.2055798},\"Baloda Bazar\":{\"lat\":21.6569173,\"lon\":82.15919629999999},\"Balrampur\":{\"lat\":27.4307473,\"lon\":82.1805203},\"Bametara\":{\"lat\":21.7140253,\"lon\":81.5356149},\"Bastar\":{\"lat\":19.1071317,\"lon\":81.9534815},\"Bijapur\":{\"lat\":16.830170799999998,\"lon\":75.710031},\"Bilaspur\":{\"lat\":22.079654500000004,\"lon\":82.1409152},\"Dakshin Bastar Dantewada\":{\"lat\":18.8456296,\"lon\":81.38393260000001},\"Dhamtari\":{\"lat\":20.701499899999998,\"lon\":81.55415789999999},\"Durg\":{\"lat\":21.1904494,\"lon\":81.2849169},\"Gariaband\":{\"lat\":20.6347897,\"lon\":82.0614974},\"Janjgir Champa\":{\"lat\":21.970552899999998,\"lon\":82.4752757},\"Jashpur\":{\"lat\":22.875933099999997,\"lon\":84.1381159},\"Kabeerdham\":{\"lat\":22.0990944,\"lon\":81.2518833},\"Kondagaon\":{\"lat\":19.595851,\"lon\":81.6637765},\"Korba\":{\"lat\":22.3594501,\"lon\":82.75005949999999},\"Koriya\":{\"lat\":23.3875499,\"lon\":82.38857829999999},\"Mahasamund\":{\"lat\":21.112406699999998,\"lon\":82.09596200000001},\"Mungeli\":{\"lat\":22.068541900000003,\"lon\":81.68568080000001},\"Narayanpur\":{\"lat\":19.7195568,\"lon\":81.2471973},\"Raigarh\":{\"lat\":21.897400299999997,\"lon\":83.39496319999999},\"Raipur\":{\"lat\":21.2513844,\"lon\":81.62964130000002},\"Rajnandgaon\":{\"lat\":21.0972123,\"lon\":81.03375009999999},\"Sukma\":{\"lat\":18.390911799999998,\"lon\":81.6588003},\"Surajpur\":{\"lat\":23.213601099999998,\"lon\":82.8679549},\"Surguja\":{\"lat\":22.9494079,\"lon\":83.1649001},\"Uttar Bastar Kanker\":{\"lat\":20.2641635,\"lon\":81.4980353},\"Gaurela Pendra Marwahi\":{\"lat\":23.00621,\"lon\":82.0541902},\"Central Delhi\":{\"lat\":28.664342700000002,\"lon\":77.2166836},\"East Delhi\":{\"lat\":28.6279559,\"lon\":77.29562729999999},\"New Delhi\":{\"lat\":28.6139391,\"lon\":77.2090212},\"North Delhi\":{\"lat\":28.7886037,\"lon\":77.1411602},\"North East Delhi\":{\"lat\":28.718369300000003,\"lon\":77.2580268},\"North West Delhi\":{\"lat\":28.718621099999996,\"lon\":77.0685134},\"Shahdara\":{\"lat\":28.689353499999996,\"lon\":77.2919352},\"South Delhi\":{\"lat\":28.4816551,\"lon\":77.18728569999999},\"South East Delhi\":{\"lat\":28.563029099999998,\"lon\":77.261088},\"South West Delhi\":{\"lat\":28.5928929,\"lon\":77.03461639999999},\"West Delhi\":{\"lat\":28.666343299999998,\"lon\":77.067959},\"Dadra and Nagar Haveli\":{\"lat\":20.180867199999998,\"lon\":73.0169135},\"Daman\":{\"lat\":20.397373599999998,\"lon\":72.8327991},\"Diu\":{\"lat\":20.714409399999997,\"lon\":70.9873719},\"North Goa\":{\"lat\":15.5163112,\"lon\":73.98300290000002},\"South Goa\":{\"lat\":15.11766,\"lon\":74.12399599999999},\"Ahmedabad\":{\"lat\":23.022505,\"lon\":72.5713621},\"Amreli\":{\"lat\":21.6015242,\"lon\":71.2203555},\"Aravalli\":{\"lat\":25.223497500000004,\"lon\":73.7477857},\"Banaskantha\":{\"lat\":24.3454739,\"lon\":71.7622481},\"Bharuch\":{\"lat\":21.7051358,\"lon\":72.9958748},\"Bhavnagar\":{\"lat\":21.7644725,\"lon\":72.15193040000001},\"Botad\":{\"lat\":22.172250899999998,\"lon\":71.663622},\"Chhota Udaipur\":{\"lat\":22.308494099999997,\"lon\":74.0119993},\"Dahod\":{\"lat\":22.8344992,\"lon\":74.26061849999999},\"Devbhumi Dwarka\":{\"lat\":22.1232327,\"lon\":69.3831079},\"Gandhinagar\":{\"lat\":23.2156354,\"lon\":72.63694149999999},\"Gir Somnath\":{\"lat\":21.0119385,\"lon\":70.7168469},\"Jamnagar\":{\"lat\":22.470701899999998,\"lon\":70.05773},\"Junagadh\":{\"lat\":21.5222203,\"lon\":70.4579436},\"Kheda\":{\"lat\":22.750650999999998,\"lon\":72.68466579999999},\"Kutch\":{\"lat\":23.7337326,\"lon\":69.8597406},\"Mahisagar\":{\"lat\":23.1711262,\"lon\":73.55941279999999},\"Mehsana\":{\"lat\":23.5879607,\"lon\":72.36932519999999},\"Morbi\":{\"lat\":22.825187399999997,\"lon\":70.84908089999999},\"Narmada\":{\"lat\":22.4965494,\"lon\":77.04947279999999},\"Navsari\":{\"lat\":20.9467019,\"lon\":72.95203479999999},\"Panchmahal\":{\"lat\":22.8011177,\"lon\":73.55941279999999},\"Patan\":{\"lat\":23.8500156,\"lon\":72.1210274},\"Porbandar\":{\"lat\":21.6416979,\"lon\":69.62930589999999},\"Rajkot\":{\"lat\":22.3038945,\"lon\":70.80215989999999},\"Sabarkantha\":{\"lat\":23.8476704,\"lon\":72.99329689999999},\"Surat\":{\"lat\":21.170240099999997,\"lon\":72.83106070000001},\"Surendranagar\":{\"lat\":22.7738938,\"lon\":71.6673352},\"Vadodara\":{\"lat\":22.3071588,\"lon\":73.1812187},\"Valsad\":{\"lat\":20.5992349,\"lon\":72.9342451},\"Chamba\":{\"lat\":32.5533633,\"lon\":76.1258083},\"Hamirpur\":{\"lat\":31.6861745,\"lon\":76.52130919999999},\"Kangra\":{\"lat\":32.099803099999995,\"lon\":76.2691006},\"Kinnaur\":{\"lat\":31.650957599999995,\"lon\":78.4751945},\"Kullu\":{\"lat\":31.959204999999997,\"lon\":77.1089377},\"Lahaul and Spiti\":{\"lat\":32.6192107,\"lon\":77.3783789},\"Shimla\":{\"lat\":31.104814500000003,\"lon\":77.17340329999999},\"Sirmaur\":{\"lat\":30.562845499999998,\"lon\":77.4701972},\"Solan\":{\"lat\":30.908424499999995,\"lon\":77.09990309999999},\"Una\":{\"lat\":31.468464899999997,\"lon\":76.2708152},\"Ambala\":{\"lat\":30.375201099999998,\"lon\":76.782122},\"Bhiwani\":{\"lat\":28.7974684,\"lon\":76.1322058},\"Charkhi Dadri\":{\"lat\":28.5920617,\"lon\":76.2652909},\"Faridabad\":{\"lat\":28.4089123,\"lon\":77.3177894},\"Fatehabad\":{\"lat\":29.513181399999997,\"lon\":75.4509532},\"Gurugram\":{\"lat\":28.4594965,\"lon\":77.0266383},\"Hisar\":{\"lat\":29.1491875,\"lon\":75.7216527},\"Italians\":{\"lat\":41.704967499999995,\"lon\":12.685046199999999},\"Jhajjar\":{\"lat\":28.6054875,\"lon\":76.6537749},\"Jind\":{\"lat\":29.361293599999996,\"lon\":76.3637285},\"Kaithal\":{\"lat\":29.804275800000003,\"lon\":76.4039016},\"Karnal\":{\"lat\":29.6856929,\"lon\":76.9904825},\"Kurukshetra\":{\"lat\":29.969512100000003,\"lon\":76.878282},\"Mahendragarh\":{\"lat\":28.2734201,\"lon\":76.14013489999999},\"Nuh\":{\"lat\":1.2937277999999999,\"lon\":103.78317559999999},\"Palwal\":{\"lat\":28.1472852,\"lon\":77.3259878},\"Panchkula\":{\"lat\":30.6942091,\"lon\":76.860565},\"Rewari\":{\"lat\":28.1919738,\"lon\":76.6190774},\"Rohtak\":{\"lat\":28.8955152,\"lon\":76.606611},\"Sirsa\":{\"lat\":29.532073099999998,\"lon\":75.03177339999999},\"Sonipat\":{\"lat\":28.993082299999994,\"lon\":77.0150735},\"Yamunanagar\":{\"lat\":30.1290485,\"lon\":77.2673901},\"Bokaro\":{\"lat\":23.669295599999998,\"lon\":86.15111200000001},\"Chatra\":{\"lat\":24.206544599999997,\"lon\":84.871802},\"Deoghar\":{\"lat\":24.485179,\"lon\":86.694785},\"Dhanbad\":{\"lat\":23.7956531,\"lon\":86.43038589999999},\"Dumka\":{\"lat\":24.2684794,\"lon\":87.24880879999999},\"East Singhbhum\":{\"lat\":22.486675599999998,\"lon\":86.49965460000001},\"Garhwa\":{\"lat\":24.154898199999998,\"lon\":83.7995617},\"Giridih\":{\"lat\":24.19135,\"lon\":86.2996368},\"Godda\":{\"lat\":24.825521499999997,\"lon\":87.2135177},\"Gumla\":{\"lat\":23.0441295,\"lon\":84.53794549999999},\"Hazaribagh\":{\"lat\":23.9924669,\"lon\":85.3636758},\"Jamtara\":{\"lat\":23.9505496,\"lon\":86.81701319999999},\"Khunti\":{\"lat\":23.079759499999998,\"lon\":85.2774207},\"Koderma\":{\"lat\":24.4676805,\"lon\":85.59336449999999},\"Latehar\":{\"lat\":23.7463215,\"lon\":84.5091102},\"Lohardaga\":{\"lat\":23.433750399999997,\"lon\":84.6479124},\"Pakur\":{\"lat\":24.6336908,\"lon\":87.8500644},\"Ramgarh\":{\"lat\":23.652367800000004,\"lon\":85.56121},\"Ranchi\":{\"lat\":23.344099699999997,\"lon\":85.309562},\"Sahibganj\":{\"lat\":25.2381216,\"lon\":87.64535920000002},\"Saraikela-Kharsawan\":{\"lat\":22.856126099999997,\"lon\":86.0121573},\"Simdega\":{\"lat\":22.6151138,\"lon\":84.4959985},\"West Singhbhum\":{\"lat\":22.3650858,\"lon\":85.4375574},\"Anantnag\":{\"lat\":33.7311255,\"lon\":75.14870069999999},\"Bandipora\":{\"lat\":34.5052269,\"lon\":74.6868815},\"Baramulla\":{\"lat\":34.1595145,\"lon\":74.35874729999999},\"Budgam\":{\"lat\":33.9348549,\"lon\":74.64004320000001},\"Doda\":{\"lat\":33.145749699999996,\"lon\":75.5480491},\"Ganderbal\":{\"lat\":34.2164955,\"lon\":74.7719431},\"Jammu\":{\"lat\":33.778175,\"lon\":76.57617139999999},\"Kathua\":{\"lat\":32.3863082,\"lon\":75.5173465},\"Kishtwar\":{\"lat\":33.311590599999995,\"lon\":75.76621949999999},\"Kulgam\":{\"lat\":33.644990799999995,\"lon\":75.018031},\"Kupwara\":{\"lat\":34.5261786,\"lon\":74.2546136},\"Mirpur\":{\"lat\":33.1479849,\"lon\":73.7536695},\"Muzaffarabad\":{\"lat\":34.3551036,\"lon\":73.4769458},\"Pulwama\":{\"lat\":33.871611699999995,\"lon\":74.89456919999999},\"Rajouri\":{\"lat\":33.3716143,\"lon\":74.315191},\"Ramban\":{\"lat\":33.2463875,\"lon\":75.1938909},\"Reasi\":{\"lat\":33.0803564,\"lon\":74.83644129999999},\"Shopiyan\":{\"lat\":33.7593643,\"lon\":74.8039205},\"Srinagar\":{\"lat\":34.0836708,\"lon\":74.7972825},\"Udhampur\":{\"lat\":32.915984699999996,\"lon\":75.1416173},\"Bagalkote\":{\"lat\":16.1691096,\"lon\":75.6615029},\"Ballari\":{\"lat\":15.1393932,\"lon\":76.9214428},\"Belagavi\":{\"lat\":15.849695299999999,\"lon\":74.4976741},\"Bengaluru Rural\":{\"lat\":13.2846993,\"lon\":77.6077865},\"Bengaluru Urban\":{\"lat\":12.9700247,\"lon\":77.6536125},\"Bidar\":{\"lat\":17.9103939,\"lon\":77.51990789999999},\"Chamarajanagara\":{\"lat\":11.9261471,\"lon\":76.9437312},\"Chikkaballapura\":{\"lat\":13.4354985,\"lon\":77.7315344},\"Chikkamagaluru\":{\"lat\":13.316144099999999,\"lon\":75.7720439},\"Chitradurga\":{\"lat\":14.2250932,\"lon\":76.3980464},\"Dakshina Kannada\":{\"lat\":12.8437814,\"lon\":75.2479061},\"Davanagere\":{\"lat\":14.4644085,\"lon\":75.921758},\"Dharwad\":{\"lat\":15.458923599999999,\"lon\":75.007808},\"Gadag\":{\"lat\":15.431540599999998,\"lon\":75.63551489999999},\"Hassan\":{\"lat\":13.0033234,\"lon\":76.1003894},\"Haveri\":{\"lat\":14.7950698,\"lon\":75.39906739999999},\"Kalaburagi\":{\"lat\":17.329731,\"lon\":76.8342957},\"Kodagu\":{\"lat\":12.3374942,\"lon\":75.8069082},\"Kolar\":{\"lat\":13.136214299999999,\"lon\":78.12909859999999},\"Koppal\":{\"lat\":15.350465199999999,\"lon\":76.1567298},\"Mandya\":{\"lat\":12.5218157,\"lon\":76.89514880000002},\"Mysuru\":{\"lat\":12.295810399999999,\"lon\":76.6393805},\"Raichur\":{\"lat\":16.216018,\"lon\":77.3565608},\"Ramanagara\":{\"lat\":12.720861399999999,\"lon\":77.27989629999999},\"Shivamogga\":{\"lat\":13.9299299,\"lon\":75.568101},\"Tumakuru\":{\"lat\":13.3378762,\"lon\":77.117325},\"Udupi\":{\"lat\":13.3408807,\"lon\":74.7421427},\"Uttara Kannada\":{\"lat\":14.793706499999999,\"lon\":74.6868815},\"Vijayapura\":{\"lat\":16.830170799999998,\"lon\":75.710031},\"Yadgir\":{\"lat\":16.7625516,\"lon\":77.1442251},\"Alappuzha\":{\"lat\":9.498066699999999,\"lon\":76.3388484},\"Ernakulam\":{\"lat\":9.9816358,\"lon\":76.2998842},\"Idukki\":{\"lat\":9.9188973,\"lon\":77.10249019999999},\"Kannur\":{\"lat\":11.8744775,\"lon\":75.37036619999999},\"Kasaragod\":{\"lat\":12.4995966,\"lon\":74.9869276},\"Kollam\":{\"lat\":8.8932118,\"lon\":76.6141396},\"Kottayam\":{\"lat\":9.591566799999999,\"lon\":76.5221531},\"Kozhikode\":{\"lat\":11.2587531,\"lon\":75.78041},\"Malappuram\":{\"lat\":11.0509762,\"lon\":76.0710967},\"Palakkad\":{\"lat\":10.7867303,\"lon\":76.6547932},\"Pathanamthitta\":{\"lat\":9.2647582,\"lon\":76.78704139999999},\"Thiruvananthapuram\":{\"lat\":8.5241391,\"lon\":76.9366376},\"Thrissur\":{\"lat\":10.527641599999999,\"lon\":76.2144349},\"Wayanad\":{\"lat\":11.6853575,\"lon\":76.1319953},\"Kargil\":{\"lat\":34.5538522,\"lon\":76.1348944},\"Leh\":{\"lat\":34.1525864,\"lon\":77.57705349999999},\"Lakshadweep\":{\"lat\":10.3280265,\"lon\":72.78463359999999},\"Ahmednagar\":{\"lat\":19.0948287,\"lon\":74.74797889999999},\"Akola\":{\"lat\":20.7002159,\"lon\":77.0081678},\"Amravati\":{\"lat\":20.9319821,\"lon\":77.7523039},\"Beed\":{\"lat\":18.990088,\"lon\":75.7531324},\"Bhandara\":{\"lat\":21.177657999999997,\"lon\":79.6570127},\"Buldhana\":{\"lat\":20.5292147,\"lon\":76.1841701},\"Chandrapur\":{\"lat\":19.9615398,\"lon\":79.2961468},\"Dhule\":{\"lat\":20.9042201,\"lon\":74.7748979},\"Gadchiroli\":{\"lat\":20.184870999999998,\"lon\":79.9947956},\"Gondia\":{\"lat\":21.454947699999998,\"lon\":80.19607119999999},\"Hingoli\":{\"lat\":19.7173703,\"lon\":77.1493722},\"Jalgaon\":{\"lat\":21.0076578,\"lon\":75.5626039},\"Jalna\":{\"lat\":19.834665899999997,\"lon\":75.88163449999999},\"Kolhapur\":{\"lat\":16.7049873,\"lon\":74.24325270000001},\"Latur\":{\"lat\":18.4087934,\"lon\":76.5603828},\"Mumbai\":{\"lat\":19.0759837,\"lon\":72.8776559},\"Mumbai Suburban\":{\"lat\":19.1538231,\"lon\":72.8751786},\"Nagpur\":{\"lat\":21.1458004,\"lon\":79.0881546},\"Nanded\":{\"lat\":19.138251399999998,\"lon\":77.3209555},\"Nandurbar\":{\"lat\":21.746854799999998,\"lon\":74.12399599999999},\"Nashik\":{\"lat\":19.9974533,\"lon\":73.78980229999999},\"Osmanabad\":{\"lat\":18.206963599999998,\"lon\":76.17837390000001},\"Palghar\":{\"lat\":19.6967136,\"lon\":72.769885},\"Parbhani\":{\"lat\":19.2608384,\"lon\":76.774776},\"Pune\":{\"lat\":18.520430299999997,\"lon\":73.8567437},\"Raigad\":{\"lat\":18.515751899999998,\"lon\":73.1821623},\"Ratnagiri\":{\"lat\":16.990215,\"lon\":73.31202329999999},\"Sangli\":{\"lat\":16.8523973,\"lon\":74.5814773},\"Satara\":{\"lat\":17.6804639,\"lon\":74.018261},\"Sindhudurg\":{\"lat\":16.349219299999998,\"lon\":73.55941279999999},\"Solapur\":{\"lat\":17.6599188,\"lon\":75.9063906},\"Thane\":{\"lat\":19.2183307,\"lon\":72.9780897},\"Wardha\":{\"lat\":20.745319,\"lon\":78.60219459999999},\"Washim\":{\"lat\":20.111912300000004,\"lon\":77.1312586},\"Yavatmal\":{\"lat\":20.3899385,\"lon\":78.1306846},\"East Garo Hills\":{\"lat\":25.567169200000002,\"lon\":90.52578229999999},\"East Jaintia Hills\":{\"lat\":25.310076799999997,\"lon\":92.49999179999999},\"East Khasi Hills\":{\"lat\":25.3681768,\"lon\":91.7538817},\"North Garo Hills\":{\"lat\":25.8986758,\"lon\":90.4879916},\"Ribhoi\":{\"lat\":25.8431574,\"lon\":91.985621},\"South Garo Hills\":{\"lat\":25.330096899999997,\"lon\":90.563609},\"South West Khasi Hills\":{\"lat\":25.3258908,\"lon\":91.2506002},\"West Garo Hills\":{\"lat\":25.5679372,\"lon\":90.2244662},\"West Jaintia Hills\":{\"lat\":25.5021272,\"lon\":92.341887},\"West Khasi Hills\":{\"lat\":25.5624625,\"lon\":91.28910359999999},\"Bishnupur\":{\"lat\":23.067179,\"lon\":87.32146809999999},\"Chandel\":{\"lat\":24.3262003,\"lon\":94.0006003},\"Churachandpur\":{\"lat\":24.2993576,\"lon\":93.2583626},\"Imphal West\":{\"lat\":24.782783700000003,\"lon\":93.88589549999999},\"Jiribam\":{\"lat\":24.786434699999997,\"lon\":93.1538899},\"Kakching\":{\"lat\":24.496869,\"lon\":93.98305289999999},\"Kamjong\":{\"lat\":24.8570444,\"lon\":94.5134629},\"Kangpokpi\":{\"lat\":25.1519197,\"lon\":93.969963},\"Noney\":{\"lat\":24.8546947,\"lon\":93.6167146},\"Pherzawl\":{\"lat\":24.263711999999998,\"lon\":93.1892637},\"Senapati\":{\"lat\":25.2677276,\"lon\":94.0210189},\"Tamenglong\":{\"lat\":24.987934199999998,\"lon\":93.49529199999999},\"Tengnoupal\":{\"lat\":24.383792,\"lon\":94.1481775},\"Thoubal\":{\"lat\":24.5435506,\"lon\":93.9674371},\"Ukhrul\":{\"lat\":24.9320611,\"lon\":94.479976},\"Agar Malwa\":{\"lat\":23.7137337,\"lon\":76.0094637},\"Alirajpur\":{\"lat\":22.3403431,\"lon\":74.4994517},\"Anuppur\":{\"lat\":23.1136554,\"lon\":81.69762899999999},\"Ashoknagar\":{\"lat\":24.5775148,\"lon\":77.7318495},\"Balaghat\":{\"lat\":21.812876,\"lon\":80.18382930000001},\"Barwani\":{\"lat\":22.0363157,\"lon\":74.903339},\"Betul\":{\"lat\":21.901160100000002,\"lon\":77.8960201},\"Bhind\":{\"lat\":26.563776800000003,\"lon\":78.78609159999999},\"Bhopal\":{\"lat\":23.2599333,\"lon\":77.412615},\"Burhanpur\":{\"lat\":21.3145021,\"lon\":76.2180095},\"Chhatarpur\":{\"lat\":24.9167821,\"lon\":79.5910058},\"Chhindwara\":{\"lat\":22.057437,\"lon\":78.9381729},\"Damoh\":{\"lat\":23.832302199999997,\"lon\":79.4386591},\"Datia\":{\"lat\":25.6653168,\"lon\":78.4609182},\"Dewas\":{\"lat\":22.9675929,\"lon\":76.0534454},\"Dhar\":{\"lat\":22.6012922,\"lon\":75.3024655},\"Dindori\":{\"lat\":22.8457457,\"lon\":81.0754657},\"Guna\":{\"lat\":24.632364799999998,\"lon\":77.3001762},\"Gwalior\":{\"lat\":26.218287099999998,\"lon\":78.18283079999999},\"Harda\":{\"lat\":22.3466702,\"lon\":77.0889583},\"Hoshangabad\":{\"lat\":22.7518961,\"lon\":77.7288655},\"Indore\":{\"lat\":22.7195687,\"lon\":75.8577258},\"Jabalpur\":{\"lat\":23.181466999999998,\"lon\":79.9864071},\"Jhabua\":{\"lat\":22.769665999999997,\"lon\":74.5920921},\"Katni\":{\"lat\":23.8343441,\"lon\":80.38938139999999},\"Khandwa\":{\"lat\":21.8314037,\"lon\":76.3497612},\"Khargone\":{\"lat\":21.833524399999998,\"lon\":75.61498929999999},\"Mandla\":{\"lat\":22.5979218,\"lon\":80.3713855},\"Mandsaur\":{\"lat\":24.073435600000003,\"lon\":75.0679018},\"Morena\":{\"lat\":26.494717200000004,\"lon\":77.9940222},\"Narsinghpur\":{\"lat\":22.947317899999998,\"lon\":79.1923266},\"Neemuch\":{\"lat\":24.473766299999998,\"lon\":74.8726497},\"Niwari\":{\"lat\":25.3697238,\"lon\":78.7986181},\"Panna\":{\"lat\":24.718031099999997,\"lon\":80.1819268},\"Raisen\":{\"lat\":23.3301074,\"lon\":77.78428509999999},\"Rajgarh\":{\"lat\":24.007881899999997,\"lon\":76.7278803},\"Ratlam\":{\"lat\":23.3315103,\"lon\":75.0366677},\"Rewa\":{\"lat\":24.5362477,\"lon\":81.30369460000001},\"Sagar\":{\"lat\":23.838805,\"lon\":78.7378068},\"Satna\":{\"lat\":24.6005075,\"lon\":80.8322428},\"Sehore\":{\"lat\":23.2032399,\"lon\":77.0844044},\"Seoni\":{\"lat\":22.086869099999998,\"lon\":79.5434841},\"Shahdol\":{\"lat\":23.300231999999998,\"lon\":81.3568619},\"Shajapur\":{\"lat\":23.427293799999998,\"lon\":76.2729839},\"Sheopur\":{\"lat\":25.672819999999998,\"lon\":76.6961305},\"Shivpuri\":{\"lat\":25.4320478,\"lon\":77.66438149999999},\"Sidhi\":{\"lat\":24.3956038,\"lon\":81.88252790000001},\"Singrauli\":{\"lat\":24.1992101,\"lon\":82.66454689999999},\"Tikamgarh\":{\"lat\":24.745614699999997,\"lon\":78.8320779},\"Ujjain\":{\"lat\":23.1764665,\"lon\":75.7885163},\"Umaria\":{\"lat\":23.601374699999997,\"lon\":81.0754657},\"Vidisha\":{\"lat\":23.5235719,\"lon\":77.81397179999999},\"Aizawl\":{\"lat\":23.730717499999997,\"lon\":92.71731059999999},\"Champhai\":{\"lat\":23.456570799999998,\"lon\":93.32819289999999},\"Hnahthial\":{\"lat\":22.9653238,\"lon\":92.930081},\"Khawzawl\":{\"lat\":23.5344974,\"lon\":93.1829868},\"Kolasib\":{\"lat\":24.224565,\"lon\":92.67602509999999},\"Lawngtlai\":{\"lat\":22.5284445,\"lon\":92.89263419999999},\"Lunglei\":{\"lat\":22.8670691,\"lon\":92.7655358},\"Mamit\":{\"lat\":23.6473811,\"lon\":92.539603},\"Saiha\":{\"lat\":22.489734,\"lon\":92.979269},\"Saitual\":{\"lat\":23.9704247,\"lon\":92.57583989999999},\"Serchhip\":{\"lat\":23.3416578,\"lon\":92.8502302},\"Dimapur\":{\"lat\":25.909140599999997,\"lon\":93.72656049999999},\"Kiphire\":{\"lat\":25.8187605,\"lon\":94.8520636},\"Kohima\":{\"lat\":25.6751129,\"lon\":94.10859980000001},\"Longleng\":{\"lat\":26.4901992,\"lon\":94.81966399999999},\"Mokokchung\":{\"lat\":26.3220358,\"lon\":94.5134629},\"Phek\":{\"lat\":25.6634215,\"lon\":94.47032019999999},\"Tuensang\":{\"lat\":26.235742199999997,\"lon\":94.8131863},\"Wokha\":{\"lat\":26.0910351,\"lon\":94.25897379999999},\"Zunheboto\":{\"lat\":26.009283099999998,\"lon\":94.5237707},\"Angul\":{\"lat\":20.8444033,\"lon\":85.1510818},\"Balangir\":{\"lat\":20.7011108,\"lon\":83.4846069},\"Balasore\":{\"lat\":21.4933578,\"lon\":86.9134794},\"Bargarh\":{\"lat\":21.3470154,\"lon\":83.6320212},\"Bhadrak\":{\"lat\":21.0573616,\"lon\":86.4962996},\"Boudh\":{\"lat\":20.660744700000002,\"lon\":84.14351359999999},\"Cuttack\":{\"lat\":20.462521,\"lon\":85.8829895},\"Deogarh\":{\"lat\":21.5383136,\"lon\":84.7289442},\"Dhenkanal\":{\"lat\":20.650475300000004,\"lon\":85.5981223},\"Gajapati\":{\"lat\":19.191222099999997,\"lon\":84.1857115},\"Ganjam\":{\"lat\":19.5859712,\"lon\":84.68974949999999},\"Jagatsinghpur\":{\"lat\":20.2548998,\"lon\":86.1706221},\"Jajpur\":{\"lat\":20.8341019,\"lon\":86.3326058},\"Jharsuguda\":{\"lat\":21.8554375,\"lon\":84.0061661},\"Kalahandi\":{\"lat\":19.9137363,\"lon\":83.1649001},\"Kandhamal\":{\"lat\":20.1342042,\"lon\":84.01674229999999},\"Kendrapara\":{\"lat\":20.4969108,\"lon\":86.4288534},\"Kendujhar\":{\"lat\":21.628933,\"lon\":85.5816847},\"Khordha\":{\"lat\":20.130141,\"lon\":85.47880649999999},\"Koraput\":{\"lat\":18.813487000000002,\"lon\":82.71233269999999},\"Malkangiri\":{\"lat\":18.1640803,\"lon\":81.9534815},\"Mayurbhanj\":{\"lat\":22.0086978,\"lon\":86.41873079999999},\"Nabarangapur\":{\"lat\":19.2281434,\"lon\":82.54698979999999},\"Nayagarh\":{\"lat\":20.123133199999998,\"lon\":85.1038426},\"Nuapada\":{\"lat\":20.8060184,\"lon\":82.5361393},\"Puri\":{\"lat\":19.8134554,\"lon\":85.8312359},\"Rayagada\":{\"lat\":19.171208999999998,\"lon\":83.4163226},\"Sambalpur\":{\"lat\":21.466871599999997,\"lon\":83.9811665},\"Subarnapur\":{\"lat\":20.9338113,\"lon\":83.804868},\"Sundargarh\":{\"lat\":22.1240025,\"lon\":84.043175},\"Amritsar\":{\"lat\":31.6339793,\"lon\":74.8722642},\"Barnala\":{\"lat\":30.381944599999997,\"lon\":75.5467979},\"Bathinda\":{\"lat\":30.210994000000003,\"lon\":74.9454745},\"Faridkot\":{\"lat\":30.6769462,\"lon\":74.7583351},\"Fatehgarh Sahib\":{\"lat\":30.643534499999998,\"lon\":76.39703},\"Fazilka\":{\"lat\":30.403647799999998,\"lon\":74.0279621},\"Ferozepur\":{\"lat\":30.933134799999998,\"lon\":74.6224755},\"Gurdaspur\":{\"lat\":32.0413917,\"lon\":75.403086},\"Hoshiarpur\":{\"lat\":31.514317799999997,\"lon\":75.911483},\"Jalandhar\":{\"lat\":31.3260152,\"lon\":75.57618289999999},\"Kapurthala\":{\"lat\":31.3722571,\"lon\":75.4017654},\"Ludhiana\":{\"lat\":30.900965,\"lon\":75.8572758},\"Mansa\":{\"lat\":29.999506900000004,\"lon\":75.3936808},\"Moga\":{\"lat\":30.8230114,\"lon\":75.17344709999999},\"Pathankot\":{\"lat\":32.2733352,\"lon\":75.6522066},\"Patiala\":{\"lat\":30.339780899999997,\"lon\":76.3868797},\"Rupnagar\":{\"lat\":30.9661003,\"lon\":76.5230961},\"S.A.S. Nagar\":{\"lat\":30.649648599999995,\"lon\":76.7567368},\"Sangrur\":{\"lat\":30.2457963,\"lon\":75.8420716},\"Shahid Bhagat Singh Nagar\":{\"lat\":31.091295399999996,\"lon\":76.0391909},\"Sri Muktsar Sahib\":{\"lat\":30.476177299999996,\"lon\":74.5121599},\"Tarn Taran\":{\"lat\":31.4538668,\"lon\":74.9267599},\"Karaikal\":{\"lat\":10.9254398,\"lon\":79.8380056},\"Mahe\":{\"lat\":11.7002703,\"lon\":75.5424843},\"Puducherry\":{\"lat\":11.9415915,\"lon\":79.8083133},\"Yanam\":{\"lat\":16.7271912,\"lon\":82.21757749999999},\"Ajmer\":{\"lat\":26.4498954,\"lon\":74.6399163},\"Alwar\":{\"lat\":27.5529907,\"lon\":76.6345735},\"Banswara\":{\"lat\":23.546139399999998,\"lon\":74.4349761},\"Barmer\":{\"lat\":25.752146699999997,\"lon\":71.3966865},\"Bharatpur\":{\"lat\":27.2151863,\"lon\":77.5029996},\"Bhilwara\":{\"lat\":25.3407388,\"lon\":74.63131829999999},\"Bikaner\":{\"lat\":28.022934799999998,\"lon\":73.3119159},\"BSF Camp\":{\"lat\":26.135577899999998,\"lon\":88.01281499999999},\"Bundi\":{\"lat\":25.4325869,\"lon\":75.6482726},\"Chittorgarh\":{\"lat\":24.8829177,\"lon\":74.6229699},\"Churu\":{\"lat\":28.2925364,\"lon\":74.9707262},\"Dausa\":{\"lat\":26.899695299999998,\"lon\":76.332411},\"Dholpur\":{\"lat\":26.696552099999998,\"lon\":77.8907576},\"Dungarpur\":{\"lat\":23.841668,\"lon\":73.7146623},\"Ganganagar\":{\"lat\":29.9093759,\"lon\":73.87998050000002},\"Hanumangarh\":{\"lat\":29.5815012,\"lon\":74.32941989999999},\"Jaipur\":{\"lat\":26.9124336,\"lon\":75.7872709},\"Jaisalmer\":{\"lat\":26.915748699999998,\"lon\":70.9083443},\"Jalore\":{\"lat\":25.3444799,\"lon\":72.6253763},\"Jhalawar\":{\"lat\":24.5973494,\"lon\":76.1609838},\"Jhunjhunu\":{\"lat\":28.1317038,\"lon\":75.4022233},\"Jodhpur\":{\"lat\":26.2389469,\"lon\":73.02430939999999},\"Karauli\":{\"lat\":26.488322999999998,\"lon\":77.01614359999999},\"Kota\":{\"lat\":25.213815600000004,\"lon\":75.8647527},\"Nagaur\":{\"lat\":27.1983368,\"lon\":73.7493272},\"Pratapgarh\":{\"lat\":25.8973038,\"lon\":81.9452981},\"Rajsamand\":{\"lat\":25.0583257,\"lon\":73.88600339999999},\"Sawai Madhopur\":{\"lat\":26.012373,\"lon\":76.3560109},\"Sikar\":{\"lat\":27.609391199999997,\"lon\":75.1397935},\"Sirohi\":{\"lat\":24.8851548,\"lon\":72.8574558},\"Tonk\":{\"lat\":26.1659063,\"lon\":75.7962852},\"Udaipur\":{\"lat\":24.585445,\"lon\":73.712479},\"East Sikkim\":{\"lat\":27.308363699999997,\"lon\":88.6723578},\"North Sikkim\":{\"lat\":27.8236356,\"lon\":88.55653099999999},\"South Sikkim\":{\"lat\":27.285840000000004,\"lon\":88.3945669},\"West Sikkim\":{\"lat\":27.303198599999998,\"lon\":88.2071598},\"Adilabad\":{\"lat\":19.6640624,\"lon\":78.5320107},\"Bhadradri Kothagudem\":{\"lat\":14.7430452,\"lon\":74.6758388},\"Hyderabad\":{\"lat\":17.385044,\"lon\":78.486671},\"Jagtial\":{\"lat\":18.7894881,\"lon\":78.91204590000001},\"Jangaon\":{\"lat\":17.722650899999998,\"lon\":79.1517868},\"Jayashankar Bhupalapally\":{\"lat\":18.4292622,\"lon\":79.8634855},\"Kamareddy\":{\"lat\":18.320483199999998,\"lon\":78.3369523},\"Karimnagar\":{\"lat\":18.4385553,\"lon\":79.1288412},\"Khammam\":{\"lat\":17.2472528,\"lon\":80.1514447},\"Mahabubabad\":{\"lat\":17.602625,\"lon\":80.00361989999999},\"Mahabubnagar\":{\"lat\":16.748837899999998,\"lon\":78.00351719999999},\"Mancherial\":{\"lat\":18.8713826,\"lon\":79.4443099},\"Medak\":{\"lat\":18.0529357,\"lon\":78.261853},\"Medchal Malkajgiri\":{\"lat\":17.4139017,\"lon\":78.5855965},\"Mulugu\":{\"lat\":17.415377,\"lon\":78.443567},\"Nagarkurnool\":{\"lat\":16.4939417,\"lon\":78.31024889999999},\"Nalgonda\":{\"lat\":17.057466299999998,\"lon\":79.26841689999999},\"Narayanpet\":{\"lat\":16.744511199999998,\"lon\":77.4960078},\"Nirmal\":{\"lat\":19.0964117,\"lon\":78.342975},\"Nizamabad\":{\"lat\":18.672504699999998,\"lon\":78.09408669999999},\"Peddapalli\":{\"lat\":18.6151461,\"lon\":79.3826826},\"Ranga Reddy\":{\"lat\":17.389137899999998,\"lon\":77.8367282},\"Sangareddy\":{\"lat\":17.6140077,\"lon\":78.08156269999999},\"Siddipet\":{\"lat\":18.1017996,\"lon\":78.8519601},\"Suryapet\":{\"lat\":17.1313756,\"lon\":79.6336242},\"Vikarabad\":{\"lat\":17.336455500000003,\"lon\":77.9048268},\"Wanaparthy\":{\"lat\":16.3623118,\"lon\":78.0621824},\"Warangal Urban\":{\"lat\":17.9744395,\"lon\":79.9192702},\"Ariyalur\":{\"lat\":11.1400585,\"lon\":79.0786281},\"Chengalpattu\":{\"lat\":12.6819372,\"lon\":79.98884129999999},\"Chennai\":{\"lat\":13.082680199999999,\"lon\":80.2707184},\"Coimbatore\":{\"lat\":11.016844500000001,\"lon\":76.9558321},\"Cuddalore\":{\"lat\":11.748041899999999,\"lon\":79.7713687},\"Dharmapuri\":{\"lat\":12.1210997,\"lon\":78.1582143},\"Dindigul\":{\"lat\":10.3623794,\"lon\":77.9694579},\"Erode\":{\"lat\":11.3410364,\"lon\":77.7171642},\"Kallakurichi\":{\"lat\":11.7383735,\"lon\":78.9638899},\"Kancheepuram\":{\"lat\":12.818456,\"lon\":79.6946586},\"Kanyakumari\":{\"lat\":8.0883064,\"lon\":77.5384507},\"Karur\":{\"lat\":10.960077799999999,\"lon\":78.07660360000001},\"Krishnagiri\":{\"lat\":12.5265661,\"lon\":78.2149575},\"Madurai\":{\"lat\":9.9252007,\"lon\":78.1197754},\"Nagapattinam\":{\"lat\":10.767231299999999,\"lon\":79.8448512},\"Namakkal\":{\"lat\":11.2188958,\"lon\":78.1673575},\"Nilgiris\":{\"lat\":11.491604299999999,\"lon\":76.7336521},\"Perambalur\":{\"lat\":11.2342104,\"lon\":78.8806852},\"Pudukkottai\":{\"lat\":10.3832867,\"lon\":78.800129},\"Ramanathapuram\":{\"lat\":9.3639356,\"lon\":78.8394819},\"Ranipet\":{\"lat\":12.948740299999999,\"lon\":79.318978},\"Salem\":{\"lat\":42.51954,\"lon\":-70.8967155},\"Sivaganga\":{\"lat\":9.8432999,\"lon\":78.48087749999999},\"Tenkasi\":{\"lat\":8.959351999999999,\"lon\":77.316109},\"Thanjavur\":{\"lat\":10.7869994,\"lon\":79.13782739999999},\"Theni\":{\"lat\":9.932983199999999,\"lon\":77.4701972},\"Thiruvallur\":{\"lat\":13.2544335,\"lon\":80.0087746},\"Thiruvarur\":{\"lat\":10.7661312,\"lon\":79.6343691},\"Thoothukkudi\":{\"lat\":8.764166099999999,\"lon\":78.1348361},\"Tiruchirappalli\":{\"lat\":10.7904833,\"lon\":78.7046725},\"Tirunelveli\":{\"lat\":8.713912600000002,\"lon\":77.7566523},\"Tirupathur\":{\"lat\":12.4950124,\"lon\":78.56784069999999},\"Tiruppur\":{\"lat\":11.1085242,\"lon\":77.3410656},\"Tiruvannamalai\":{\"lat\":12.2252841,\"lon\":79.07469569999999},\"Vellore\":{\"lat\":12.916516699999999,\"lon\":79.13249859999999},\"Viluppuram\":{\"lat\":11.9401378,\"lon\":79.4861449},\"Virudhunagar\":{\"lat\":9.5680116,\"lon\":77.96244349999999},\"Dhalai\":{\"lat\":23.846698200000002,\"lon\":91.9099238},\"Gomati\":{\"lat\":26.692042999999998,\"lon\":81.6005086},\"Khowai\":{\"lat\":24.0671796,\"lon\":91.60567180000001},\"North Tripura\":{\"lat\":24.079713899999998,\"lon\":92.2630393},\"Sipahijala\":{\"lat\":23.6148176,\"lon\":91.3276422},\"South Tripura\":{\"lat\":23.2317493,\"lon\":91.55961169999999},\"Unokoti\":{\"lat\":24.3169807,\"lon\":92.0668989},\"West Tripura\":{\"lat\":23.899682,\"lon\":91.4048249},\"Agra\":{\"lat\":27.1766701,\"lon\":78.00807449999999},\"Aligarh\":{\"lat\":27.8973944,\"lon\":78.0880129},\"Ambedkar Nagar\":{\"lat\":26.4683952,\"lon\":82.6915429},\"Amethi\":{\"lat\":26.1540538,\"lon\":81.814238},\"Amroha\":{\"lat\":28.905177799999997,\"lon\":78.46731799999999},\"Auraiya\":{\"lat\":26.4605377,\"lon\":79.5112528},\"Ayodhya\":{\"lat\":26.7921605,\"lon\":82.1997954},\"Azamgarh\":{\"lat\":26.0739138,\"lon\":83.18594949999999},\"Baghpat\":{\"lat\":28.9427827,\"lon\":77.22760699999999},\"Bahraich\":{\"lat\":27.5705152,\"lon\":81.59766719999999},\"Ballia\":{\"lat\":28.2006882,\"lon\":79.3651914},\"Banda\":{\"lat\":25.4520223,\"lon\":80.54384499999999},\"Barabanki\":{\"lat\":26.9268042,\"lon\":81.1833809},\"Bareilly\":{\"lat\":28.3670355,\"lon\":79.4304381},\"Basti\":{\"lat\":26.8139844,\"lon\":82.7629893},\"Bhadohi\":{\"lat\":25.3804531,\"lon\":82.56769810000002},\"Bijnor\":{\"lat\":29.373167299999995,\"lon\":78.1350904},\"Budaun\":{\"lat\":28.0311101,\"lon\":79.1271229},\"Bulandshahr\":{\"lat\":28.406963,\"lon\":77.8498292},\"Chandauli\":{\"lat\":25.2604696,\"lon\":83.264538},\"Chitrakoot\":{\"lat\":25.178815399999998,\"lon\":80.86545339999999},\"Deoria\":{\"lat\":26.5024286,\"lon\":83.7791283},\"Etah\":{\"lat\":27.5172789,\"lon\":78.792953},\"Etawah\":{\"lat\":26.782910299999998,\"lon\":79.027659},\"Farrukhabad\":{\"lat\":27.3825853,\"lon\":79.5840195},\"Fatehpur\":{\"lat\":25.849980799999997,\"lon\":80.89865019999999},\"Firozabad\":{\"lat\":27.1591961,\"lon\":78.3957331},\"Gautam Buddha Nagar\":{\"lat\":28.338333,\"lon\":77.6077865},\"Ghaziabad\":{\"lat\":28.669156500000003,\"lon\":77.45375779999999},\"Ghazipur\":{\"lat\":25.587790100000003,\"lon\":83.5783078},\"Gonda\":{\"lat\":27.1339874,\"lon\":81.96189790000001},\"Gorakhpur\":{\"lat\":26.760554499999998,\"lon\":83.3731675},\"Hapur\":{\"lat\":28.730579799999994,\"lon\":77.7758825},\"Hardoi\":{\"lat\":27.396507099999997,\"lon\":80.1250479},\"Hathras\":{\"lat\":27.5980718,\"lon\":78.0492265},\"Jalaun\":{\"lat\":26.1458649,\"lon\":79.3296565},\"Jaunpur\":{\"lat\":25.7464145,\"lon\":82.68370329999999},\"Jhansi\":{\"lat\":25.448425699999998,\"lon\":78.5684594},\"Kannauj\":{\"lat\":27.0514156,\"lon\":79.9136731},\"Kanpur Dehat\":{\"lat\":26.526707,\"lon\":79.8296743},\"Kanpur Nagar\":{\"lat\":26.414824499999998,\"lon\":80.23213129999999},\"Kasganj\":{\"lat\":27.808529,\"lon\":78.64610979999999},\"Kaushambi\":{\"lat\":25.361054000000003,\"lon\":81.40317069999999},\"Kushinagar\":{\"lat\":26.7398787,\"lon\":83.8869698},\"Lakhimpur Kheri\":{\"lat\":27.946239499999997,\"lon\":80.7787163},\"Lalitpur\":{\"lat\":24.691170699999997,\"lon\":78.4138183},\"Lucknow\":{\"lat\":26.8466937,\"lon\":80.94616599999999},\"Maharajganj\":{\"lat\":27.1177463,\"lon\":83.5070203},\"Mahoba\":{\"lat\":25.2920964,\"lon\":79.8724168},\"Mainpuri\":{\"lat\":27.2281937,\"lon\":79.02516589999999},\"Mathura\":{\"lat\":27.4924134,\"lon\":77.673673},\"Meerut\":{\"lat\":28.9844618,\"lon\":77.7064137},\"Moradabad\":{\"lat\":28.8386481,\"lon\":78.7733286},\"Muzaffarnagar\":{\"lat\":29.4726817,\"lon\":77.7085091},\"Pilibhit\":{\"lat\":28.62494,\"lon\":79.8075272},\"Prayagraj\":{\"lat\":25.4358011,\"lon\":81.846311},\"Rae Bareli\":{\"lat\":26.214480599999998,\"lon\":81.25281389999999},\"Rampur\":{\"lat\":28.798299,\"lon\":79.02202869999999},\"Saharanpur\":{\"lat\":29.9680035,\"lon\":77.55520659999999},\"Sambhal\":{\"lat\":28.5903614,\"lon\":78.5717631},\"Sant Kabir Nagar\":{\"lat\":26.7671755,\"lon\":83.03613759999999},\"Shahjahanpur\":{\"lat\":27.8753399,\"lon\":79.9147268},\"Shamli\":{\"lat\":29.4501986,\"lon\":77.3172046},\"Shrawasti\":{\"lat\":27.503542699999997,\"lon\":82.036461},\"Siddharthnagar\":{\"lat\":27.2990659,\"lon\":83.0927827},\"Sitapur\":{\"lat\":27.5680156,\"lon\":80.6789519},\"Sonbhadra\":{\"lat\":24.685000499999997,\"lon\":83.0683519},\"Sultanpur\":{\"lat\":26.2585371,\"lon\":82.06598579999999},\"Unnao\":{\"lat\":26.5393449,\"lon\":80.4878195},\"Varanasi\":{\"lat\":25.317645199999998,\"lon\":82.9739144},\"Almora\":{\"lat\":29.589240699999998,\"lon\":79.646666},\"Bageshwar\":{\"lat\":29.8403606,\"lon\":79.769426},\"Chamoli\":{\"lat\":30.293743900000003,\"lon\":79.560344},\"Champawat\":{\"lat\":29.336097399999996,\"lon\":80.0910275},\"Dehradun\":{\"lat\":30.316494499999997,\"lon\":78.03219179999999},\"Haridwar\":{\"lat\":29.9456906,\"lon\":78.16424780000001},\"Nainital\":{\"lat\":29.391920199999998,\"lon\":79.4542033},\"Pauri Garhwal\":{\"lat\":29.868768199999998,\"lon\":78.8382644},\"Pithoragarh\":{\"lat\":29.5828604,\"lon\":80.2181884},\"Rudraprayag\":{\"lat\":30.284414100000003,\"lon\":78.9811407},\"Tehri Garhwal\":{\"lat\":30.3011858,\"lon\":78.5660852},\"Udham Singh Nagar\":{\"lat\":28.960965299999998,\"lon\":79.5153773},\"Uttarkashi\":{\"lat\":30.726830699999997,\"lon\":78.4354042},\"Alipurduar\":{\"lat\":26.4922164,\"lon\":89.5319627},\"Bankura\":{\"lat\":23.2312686,\"lon\":87.07838749999999},\"Birbhum\":{\"lat\":23.8401675,\"lon\":87.6186379},\"Cooch Behar\":{\"lat\":26.3452397,\"lon\":89.4482079},\"Dakshin Dinajpur\":{\"lat\":25.3715308,\"lon\":88.55653099999999},\"Darjeeling\":{\"lat\":27.0410218,\"lon\":88.2662745},\"Hooghly\":{\"lat\":22.901158799999997,\"lon\":88.3898552},\"Howrah\":{\"lat\":22.5957689,\"lon\":88.26363940000002},\"Jalpaiguri\":{\"lat\":26.521457899999998,\"lon\":88.7195567},\"Jhargram\":{\"lat\":22.4549909,\"lon\":86.9974385},\"Kalimpong\":{\"lat\":27.0593562,\"lon\":88.46945350000001},\"Kolkata\":{\"lat\":22.572646,\"lon\":88.36389500000001},\"Malda\":{\"lat\":25.1785773,\"lon\":88.24611829999999},\"Murshidabad\":{\"lat\":24.175903899999998,\"lon\":88.2801785},\"North 24 Parganas\":{\"lat\":22.616809900000003,\"lon\":88.402895},\"Paschim Medinipur\":{\"lat\":22.4080376,\"lon\":87.38107269999999},\"Purba Bardhaman\":{\"lat\":23.2390023,\"lon\":87.86945970000001},\"Purba Medinipur\":{\"lat\":21.937287899999998,\"lon\":87.77633329999999},\"Purulia\":{\"lat\":23.332202600000002,\"lon\":86.3616405},\"South 24 Parganas\":{\"lat\":22.1352378,\"lon\":88.4016041},\"Uttar Dinajpur\":{\"lat\":25.9810393,\"lon\":88.050979}}";
                try {
                    JSONObject districtLatLonJson = new JSONObject(districtListString);
                    redzoneJsonReader = new JSONObject(strings[0]);
                    redzoneJsonArray = redzoneJsonReader.getJSONArray("zones");


                    for (int posJson = 0; posJson < redzoneJsonArray.length(); posJson++) {

                        JSONObject groupRedzone = redzoneJsonArray.getJSONObject(posJson);
                        if(groupRedzone.optString("zone").equals("Red")&&districtLatLonJson.has(groupRedzone.getString("district"))) {
                            int range = 2000;
                            groupRedzone.getString("district");
                            if(mapCases!=null&&groupRedzone.has("district")) {
                                range = (int) (Math.sqrt(mapCases.get(groupRedzone.getString("district")))*1000);
                                if(range>200000){
                                    range = 200000;
                                }
                            }
                            insert_into_redzone(
                                    posJson,
                                    groupRedzone.getString("district"),
                                    districtLatLonJson.getJSONObject(groupRedzone.getString("district")).getDouble("lat"),
                                    districtLatLonJson.getJSONObject(groupRedzone.getString("district")).getDouble("lon"),
                                    range
                            );
                            Log.e("cases in", String.valueOf(mapCases.get(groupRedzone.getString("district"))));
                        }
                    }
                    if(_db.dao_red_zone()!=null) {
                        List<Model_red_zone> model_red_zones = _db.dao_red_zone().getStaticRedzone();
                        if (model_red_zones.size() > redzoneJsonArray.length())
                            for (int i = redzoneJsonArray.length(); i < model_red_zones.size(); i++) {
                                Model_red_zone model_red_zone = new Model_red_zone();
                                model_red_zone.setID(model_red_zones.get(i).getID());
                                model_red_zone.setLat(model_red_zones.get(i).getLat());
                                model_red_zone.setLon(model_red_zones.get(i).getLon());
                                model_red_zone.setRadius(model_red_zones.get(i).getRadius());
                                model_red_zone.setPlaceName(model_red_zones.get(i).getPlaceName());
                                delete_redzone(model_red_zone);
                            }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON",e.getLocalizedMessage());
                }
                return null;
            }
        }.execute(JSON_STRING_);

    }

    @SuppressLint("StaticFieldLeak")
    private void fetch_db_cases(String JSON_STRING__){
        new AsyncTask<String,String,String>(){

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.e("json active case", "done");
                getzones();
            }

            @Override
            protected String doInBackground(String... strings) {
                Log.e("testing","fetch_db");
                JSONObject redzoneJsonReader = null;
                String districtListString ="{\"Nicobars\":{\"lat\":10.3871439,\"lon\":84.13529679999999},\"North and Middle Andaman\":{\"lat\":12.651883199999999,\"lon\":92.8976254},\"South Andaman\":{\"lat\":11.8311681,\"lon\":92.6586401},\"Anantapur\":{\"lat\":14.6818877,\"lon\":77.6005911},\"Chittoor\":{\"lat\":13.217176,\"lon\":79.1003289},\"East Godavari\":{\"lat\":17.32125,\"lon\":82.0407137},\"Guntur\":{\"lat\":16.3066525,\"lon\":80.4365402},\"Kurnool\":{\"lat\":15.8281257,\"lon\":78.0372792},\"Prakasam\":{\"lat\":15.348463,\"lon\":79.560344},\"S.P.S. Nellore\":{\"lat\":14.258084799999999,\"lon\":79.9192702},\"Srikakulam\":{\"lat\":18.294916500000003,\"lon\":83.89380179999999},\"Visakhapatnam\":{\"lat\":17.6868159,\"lon\":83.2184815},\"Vizianagaram\":{\"lat\":18.1066576,\"lon\":83.39555059999999},\"West Godavari\":{\"lat\":16.9174181,\"lon\":81.3399414},\"Y.S.R. Kadapa\":{\"lat\":14.5767233,\"lon\":78.8382644},\"Changlang\":{\"lat\":27.7421763,\"lon\":96.642433},\"East Kameng\":{\"lat\":27.423126099999998,\"lon\":93.0175712},\"East Siang\":{\"lat\":28.1097051,\"lon\":95.1432068},\"Kamle\":{\"lat\":23.5407115,\"lon\":84.73939159999999},\"Lohit\":{\"lat\":27.903965099999997,\"lon\":96.17391160000001},\"Lower Dibang Valley\":{\"lat\":28.1428728,\"lon\":95.8431018},\"Lower Subansiri\":{\"lat\":27.6169349,\"lon\":93.83915569999999},\"Namsai\":{\"lat\":27.6692115,\"lon\":95.8644018},\"Pakke Kessang\":{\"lat\":27.145853,\"lon\":93.24626099999999},\"Papum Pare\":{\"lat\":27.171901899999998,\"lon\":93.7029002},\"Siang\":{\"lat\":28.1097051,\"lon\":95.1432068},\"Tawang\":{\"lat\":27.586057399999998,\"lon\":91.8594062},\"Tirap\":{\"lat\":26.9942703,\"lon\":95.5407299},\"Upper Dibang Valley\":{\"lat\":28.812876700000004,\"lon\":96.15269850000001},\"Upper Siang\":{\"lat\":28.7588781,\"lon\":95.22666749999999},\"Upper Subansiri\":{\"lat\":27.786801999999998,\"lon\":94.33596349999999},\"West Kameng\":{\"lat\":27.342763299999998,\"lon\":92.30244599999999},\"West Siang\":{\"lat\":28.147254000000004,\"lon\":94.7484518},\"Baksa\":{\"lat\":26.693544799999998,\"lon\":91.5983959},\"Barpeta\":{\"lat\":26.3215985,\"lon\":90.9820668},\"Biswanath\":{\"lat\":26.726663199999997,\"lon\":93.1478514},\"Bongaigaon\":{\"lat\":26.5030072,\"lon\":90.5535724},\"Cachar\":{\"lat\":24.7821253,\"lon\":92.8577105},\"Chirang\":{\"lat\":26.6539048,\"lon\":90.6393702},\"Darrang\":{\"lat\":26.4522876,\"lon\":92.02731899999999},\"Dhemaji\":{\"lat\":27.481110599999997,\"lon\":94.55728470000001},\"Dhubri\":{\"lat\":26.020698199999998,\"lon\":89.9743463},\"Dibrugarh\":{\"lat\":27.472832699999998,\"lon\":94.9119621},\"Dima Hasao\":{\"lat\":25.3478004,\"lon\":93.0175712},\"Goalpara\":{\"lat\":26.0875755,\"lon\":90.563609},\"Golaghat\":{\"lat\":26.5238515,\"lon\":93.96233699999999},\"Hailakandi\":{\"lat\":24.6811018,\"lon\":92.5638055},\"Hojai\":{\"lat\":26.0016601,\"lon\":92.847737},\"Jorhat\":{\"lat\":26.750920699999998,\"lon\":94.2036696},\"Kamrup\":{\"lat\":26.3160819,\"lon\":91.5983959},\"Kamrup Metropolitan\":{\"lat\":26.0794316,\"lon\":91.63721500000001},\"Karbi Anglong\":{\"lat\":25.8456764,\"lon\":93.43775839999999},\"Karimganj\":{\"lat\":24.8649128,\"lon\":92.3591531},\"Kokrajhar\":{\"lat\":26.4014362,\"lon\":90.266699},\"Lakhimpur\":{\"lat\":27.946239499999997,\"lon\":80.7787163},\"Majuli\":{\"lat\":27.0016172,\"lon\":94.2242981},\"Morigaon\":{\"lat\":26.2528853,\"lon\":92.33695499999999},\"Nagaon\":{\"lat\":26.348008999999998,\"lon\":92.6838111},\"Nalbari\":{\"lat\":26.444618499999997,\"lon\":91.4410527},\"Sivasagar\":{\"lat\":26.9826098,\"lon\":94.6424521},\"Sonitpur\":{\"lat\":26.673885100000003,\"lon\":92.8577105},\"Tinsukia\":{\"lat\":27.488553900000003,\"lon\":95.355758},\"Udalguri\":{\"lat\":26.6897159,\"lon\":91.9099238},\"West Karbi Anglong\":{\"lat\":25.8456764,\"lon\":93.43775839999999},\"Araria\":{\"lat\":26.1324689,\"lon\":87.4528067},\"Arwal\":{\"lat\":25.1643264,\"lon\":84.6688348},\"Aurangabad\":{\"lat\":19.8761653,\"lon\":75.3433139},\"Begusarai\":{\"lat\":25.4181638,\"lon\":86.1271542},\"Bhagalpur\":{\"lat\":25.242452999999998,\"lon\":86.9842256},\"Bhojpur\":{\"lat\":25.466155,\"lon\":84.5222189},\"Buxar\":{\"lat\":25.564710299999998,\"lon\":83.9777482},\"Darbhanga\":{\"lat\":26.1542045,\"lon\":85.8918454},\"East Champaran\":{\"lat\":26.6098139,\"lon\":84.8567932},\"Gaya\":{\"lat\":24.7913957,\"lon\":85.0002336},\"Gopalganj\":{\"lat\":26.483158399999997,\"lon\":84.43655},\"Jamui\":{\"lat\":24.9195147,\"lon\":86.224718},\"Jehanabad\":{\"lat\":25.2132649,\"lon\":84.9853322},\"Kaimur\":{\"lat\":25.0545504,\"lon\":83.67739279999999},\"Katihar\":{\"lat\":25.5540648,\"lon\":87.5591073},\"Khagaria\":{\"lat\":25.50452,\"lon\":86.47014159999999},\"Kishanganj\":{\"lat\":26.0982167,\"lon\":87.9450379},\"Lakhisarai\":{\"lat\":25.157145399999997,\"lon\":86.0951592},\"Madhepura\":{\"lat\":25.9239677,\"lon\":86.7946387},\"Madhubani\":{\"lat\":26.3482938,\"lon\":86.0711661},\"Munger\":{\"lat\":25.370799299999998,\"lon\":86.47339029999999},\"Muzaffarpur\":{\"lat\":26.119660699999997,\"lon\":85.390982},\"Nalanda\":{\"lat\":25.1240603,\"lon\":85.45947489999999},\"Nawada\":{\"lat\":24.8866859,\"lon\":85.54345719999999},\"Patna\":{\"lat\":25.594094700000003,\"lon\":85.1375645},\"Purnia\":{\"lat\":25.777139100000003,\"lon\":87.4752551},\"Rohtas\":{\"lat\":25.0685878,\"lon\":84.01674229999999},\"Saharsa\":{\"lat\":25.8773651,\"lon\":86.5927887},\"Samastipur\":{\"lat\":25.8560271,\"lon\":85.7868233},\"Saran\":{\"lat\":25.8559698,\"lon\":84.8567932},\"Sheikhpura\":{\"lat\":25.141693699999998,\"lon\":85.86289149999999},\"Sheohar\":{\"lat\":26.514587199999998,\"lon\":85.29423129999999},\"Sitamarhi\":{\"lat\":26.5886976,\"lon\":85.5012971},\"Siwan\":{\"lat\":26.2243204,\"lon\":84.3599953},\"Supaul\":{\"lat\":26.123371799999997,\"lon\":86.6045134},\"Vaishali\":{\"lat\":25.6838206,\"lon\":85.35496499999999},\"West Champaran\":{\"lat\":27.1543104,\"lon\":84.3542049},\"Chandigarh\":{\"lat\":30.7333148,\"lon\":76.7794179},\"Balod\":{\"lat\":20.7270655,\"lon\":81.2055798},\"Baloda Bazar\":{\"lat\":21.6569173,\"lon\":82.15919629999999},\"Balrampur\":{\"lat\":27.4307473,\"lon\":82.1805203},\"Bametara\":{\"lat\":21.7140253,\"lon\":81.5356149},\"Bastar\":{\"lat\":19.1071317,\"lon\":81.9534815},\"Bijapur\":{\"lat\":16.830170799999998,\"lon\":75.710031},\"Bilaspur\":{\"lat\":22.079654500000004,\"lon\":82.1409152},\"Dakshin Bastar Dantewada\":{\"lat\":18.8456296,\"lon\":81.38393260000001},\"Dhamtari\":{\"lat\":20.701499899999998,\"lon\":81.55415789999999},\"Durg\":{\"lat\":21.1904494,\"lon\":81.2849169},\"Gariaband\":{\"lat\":20.6347897,\"lon\":82.0614974},\"Janjgir Champa\":{\"lat\":21.970552899999998,\"lon\":82.4752757},\"Jashpur\":{\"lat\":22.875933099999997,\"lon\":84.1381159},\"Kabeerdham\":{\"lat\":22.0990944,\"lon\":81.2518833},\"Kondagaon\":{\"lat\":19.595851,\"lon\":81.6637765},\"Korba\":{\"lat\":22.3594501,\"lon\":82.75005949999999},\"Koriya\":{\"lat\":23.3875499,\"lon\":82.38857829999999},\"Mahasamund\":{\"lat\":21.112406699999998,\"lon\":82.09596200000001},\"Mungeli\":{\"lat\":22.068541900000003,\"lon\":81.68568080000001},\"Narayanpur\":{\"lat\":19.7195568,\"lon\":81.2471973},\"Raigarh\":{\"lat\":21.897400299999997,\"lon\":83.39496319999999},\"Raipur\":{\"lat\":21.2513844,\"lon\":81.62964130000002},\"Rajnandgaon\":{\"lat\":21.0972123,\"lon\":81.03375009999999},\"Sukma\":{\"lat\":18.390911799999998,\"lon\":81.6588003},\"Surajpur\":{\"lat\":23.213601099999998,\"lon\":82.8679549},\"Surguja\":{\"lat\":22.9494079,\"lon\":83.1649001},\"Uttar Bastar Kanker\":{\"lat\":20.2641635,\"lon\":81.4980353},\"Gaurela Pendra Marwahi\":{\"lat\":23.00621,\"lon\":82.0541902},\"Central Delhi\":{\"lat\":28.664342700000002,\"lon\":77.2166836},\"East Delhi\":{\"lat\":28.6279559,\"lon\":77.29562729999999},\"New Delhi\":{\"lat\":28.6139391,\"lon\":77.2090212},\"North Delhi\":{\"lat\":28.7886037,\"lon\":77.1411602},\"North East Delhi\":{\"lat\":28.718369300000003,\"lon\":77.2580268},\"North West Delhi\":{\"lat\":28.718621099999996,\"lon\":77.0685134},\"Shahdara\":{\"lat\":28.689353499999996,\"lon\":77.2919352},\"South Delhi\":{\"lat\":28.4816551,\"lon\":77.18728569999999},\"South East Delhi\":{\"lat\":28.563029099999998,\"lon\":77.261088},\"South West Delhi\":{\"lat\":28.5928929,\"lon\":77.03461639999999},\"West Delhi\":{\"lat\":28.666343299999998,\"lon\":77.067959},\"Dadra and Nagar Haveli\":{\"lat\":20.180867199999998,\"lon\":73.0169135},\"Daman\":{\"lat\":20.397373599999998,\"lon\":72.8327991},\"Diu\":{\"lat\":20.714409399999997,\"lon\":70.9873719},\"North Goa\":{\"lat\":15.5163112,\"lon\":73.98300290000002},\"South Goa\":{\"lat\":15.11766,\"lon\":74.12399599999999},\"Ahmedabad\":{\"lat\":23.022505,\"lon\":72.5713621},\"Amreli\":{\"lat\":21.6015242,\"lon\":71.2203555},\"Aravalli\":{\"lat\":25.223497500000004,\"lon\":73.7477857},\"Banaskantha\":{\"lat\":24.3454739,\"lon\":71.7622481},\"Bharuch\":{\"lat\":21.7051358,\"lon\":72.9958748},\"Bhavnagar\":{\"lat\":21.7644725,\"lon\":72.15193040000001},\"Botad\":{\"lat\":22.172250899999998,\"lon\":71.663622},\"Chhota Udaipur\":{\"lat\":22.308494099999997,\"lon\":74.0119993},\"Dahod\":{\"lat\":22.8344992,\"lon\":74.26061849999999},\"Devbhumi Dwarka\":{\"lat\":22.1232327,\"lon\":69.3831079},\"Gandhinagar\":{\"lat\":23.2156354,\"lon\":72.63694149999999},\"Gir Somnath\":{\"lat\":21.0119385,\"lon\":70.7168469},\"Jamnagar\":{\"lat\":22.470701899999998,\"lon\":70.05773},\"Junagadh\":{\"lat\":21.5222203,\"lon\":70.4579436},\"Kheda\":{\"lat\":22.750650999999998,\"lon\":72.68466579999999},\"Kutch\":{\"lat\":23.7337326,\"lon\":69.8597406},\"Mahisagar\":{\"lat\":23.1711262,\"lon\":73.55941279999999},\"Mehsana\":{\"lat\":23.5879607,\"lon\":72.36932519999999},\"Morbi\":{\"lat\":22.825187399999997,\"lon\":70.84908089999999},\"Narmada\":{\"lat\":22.4965494,\"lon\":77.04947279999999},\"Navsari\":{\"lat\":20.9467019,\"lon\":72.95203479999999},\"Panchmahal\":{\"lat\":22.8011177,\"lon\":73.55941279999999},\"Patan\":{\"lat\":23.8500156,\"lon\":72.1210274},\"Porbandar\":{\"lat\":21.6416979,\"lon\":69.62930589999999},\"Rajkot\":{\"lat\":22.3038945,\"lon\":70.80215989999999},\"Sabarkantha\":{\"lat\":23.8476704,\"lon\":72.99329689999999},\"Surat\":{\"lat\":21.170240099999997,\"lon\":72.83106070000001},\"Surendranagar\":{\"lat\":22.7738938,\"lon\":71.6673352},\"Vadodara\":{\"lat\":22.3071588,\"lon\":73.1812187},\"Valsad\":{\"lat\":20.5992349,\"lon\":72.9342451},\"Chamba\":{\"lat\":32.5533633,\"lon\":76.1258083},\"Hamirpur\":{\"lat\":31.6861745,\"lon\":76.52130919999999},\"Kangra\":{\"lat\":32.099803099999995,\"lon\":76.2691006},\"Kinnaur\":{\"lat\":31.650957599999995,\"lon\":78.4751945},\"Kullu\":{\"lat\":31.959204999999997,\"lon\":77.1089377},\"Lahaul and Spiti\":{\"lat\":32.6192107,\"lon\":77.3783789},\"Shimla\":{\"lat\":31.104814500000003,\"lon\":77.17340329999999},\"Sirmaur\":{\"lat\":30.562845499999998,\"lon\":77.4701972},\"Solan\":{\"lat\":30.908424499999995,\"lon\":77.09990309999999},\"Una\":{\"lat\":31.468464899999997,\"lon\":76.2708152},\"Ambala\":{\"lat\":30.375201099999998,\"lon\":76.782122},\"Bhiwani\":{\"lat\":28.7974684,\"lon\":76.1322058},\"Charkhi Dadri\":{\"lat\":28.5920617,\"lon\":76.2652909},\"Faridabad\":{\"lat\":28.4089123,\"lon\":77.3177894},\"Fatehabad\":{\"lat\":29.513181399999997,\"lon\":75.4509532},\"Gurugram\":{\"lat\":28.4594965,\"lon\":77.0266383},\"Hisar\":{\"lat\":29.1491875,\"lon\":75.7216527},\"Italians\":{\"lat\":41.704967499999995,\"lon\":12.685046199999999},\"Jhajjar\":{\"lat\":28.6054875,\"lon\":76.6537749},\"Jind\":{\"lat\":29.361293599999996,\"lon\":76.3637285},\"Kaithal\":{\"lat\":29.804275800000003,\"lon\":76.4039016},\"Karnal\":{\"lat\":29.6856929,\"lon\":76.9904825},\"Kurukshetra\":{\"lat\":29.969512100000003,\"lon\":76.878282},\"Mahendragarh\":{\"lat\":28.2734201,\"lon\":76.14013489999999},\"Nuh\":{\"lat\":1.2937277999999999,\"lon\":103.78317559999999},\"Palwal\":{\"lat\":28.1472852,\"lon\":77.3259878},\"Panchkula\":{\"lat\":30.6942091,\"lon\":76.860565},\"Rewari\":{\"lat\":28.1919738,\"lon\":76.6190774},\"Rohtak\":{\"lat\":28.8955152,\"lon\":76.606611},\"Sirsa\":{\"lat\":29.532073099999998,\"lon\":75.03177339999999},\"Sonipat\":{\"lat\":28.993082299999994,\"lon\":77.0150735},\"Yamunanagar\":{\"lat\":30.1290485,\"lon\":77.2673901},\"Bokaro\":{\"lat\":23.669295599999998,\"lon\":86.15111200000001},\"Chatra\":{\"lat\":24.206544599999997,\"lon\":84.871802},\"Deoghar\":{\"lat\":24.485179,\"lon\":86.694785},\"Dhanbad\":{\"lat\":23.7956531,\"lon\":86.43038589999999},\"Dumka\":{\"lat\":24.2684794,\"lon\":87.24880879999999},\"East Singhbhum\":{\"lat\":22.486675599999998,\"lon\":86.49965460000001},\"Garhwa\":{\"lat\":24.154898199999998,\"lon\":83.7995617},\"Giridih\":{\"lat\":24.19135,\"lon\":86.2996368},\"Godda\":{\"lat\":24.825521499999997,\"lon\":87.2135177},\"Gumla\":{\"lat\":23.0441295,\"lon\":84.53794549999999},\"Hazaribagh\":{\"lat\":23.9924669,\"lon\":85.3636758},\"Jamtara\":{\"lat\":23.9505496,\"lon\":86.81701319999999},\"Khunti\":{\"lat\":23.079759499999998,\"lon\":85.2774207},\"Koderma\":{\"lat\":24.4676805,\"lon\":85.59336449999999},\"Latehar\":{\"lat\":23.7463215,\"lon\":84.5091102},\"Lohardaga\":{\"lat\":23.433750399999997,\"lon\":84.6479124},\"Pakur\":{\"lat\":24.6336908,\"lon\":87.8500644},\"Ramgarh\":{\"lat\":23.652367800000004,\"lon\":85.56121},\"Ranchi\":{\"lat\":23.344099699999997,\"lon\":85.309562},\"Sahibganj\":{\"lat\":25.2381216,\"lon\":87.64535920000002},\"Saraikela-Kharsawan\":{\"lat\":22.856126099999997,\"lon\":86.0121573},\"Simdega\":{\"lat\":22.6151138,\"lon\":84.4959985},\"West Singhbhum\":{\"lat\":22.3650858,\"lon\":85.4375574},\"Anantnag\":{\"lat\":33.7311255,\"lon\":75.14870069999999},\"Bandipora\":{\"lat\":34.5052269,\"lon\":74.6868815},\"Baramulla\":{\"lat\":34.1595145,\"lon\":74.35874729999999},\"Budgam\":{\"lat\":33.9348549,\"lon\":74.64004320000001},\"Doda\":{\"lat\":33.145749699999996,\"lon\":75.5480491},\"Ganderbal\":{\"lat\":34.2164955,\"lon\":74.7719431},\"Jammu\":{\"lat\":33.778175,\"lon\":76.57617139999999},\"Kathua\":{\"lat\":32.3863082,\"lon\":75.5173465},\"Kishtwar\":{\"lat\":33.311590599999995,\"lon\":75.76621949999999},\"Kulgam\":{\"lat\":33.644990799999995,\"lon\":75.018031},\"Kupwara\":{\"lat\":34.5261786,\"lon\":74.2546136},\"Mirpur\":{\"lat\":33.1479849,\"lon\":73.7536695},\"Muzaffarabad\":{\"lat\":34.3551036,\"lon\":73.4769458},\"Pulwama\":{\"lat\":33.871611699999995,\"lon\":74.89456919999999},\"Rajouri\":{\"lat\":33.3716143,\"lon\":74.315191},\"Ramban\":{\"lat\":33.2463875,\"lon\":75.1938909},\"Reasi\":{\"lat\":33.0803564,\"lon\":74.83644129999999},\"Shopiyan\":{\"lat\":33.7593643,\"lon\":74.8039205},\"Srinagar\":{\"lat\":34.0836708,\"lon\":74.7972825},\"Udhampur\":{\"lat\":32.915984699999996,\"lon\":75.1416173},\"Bagalkote\":{\"lat\":16.1691096,\"lon\":75.6615029},\"Ballari\":{\"lat\":15.1393932,\"lon\":76.9214428},\"Belagavi\":{\"lat\":15.849695299999999,\"lon\":74.4976741},\"Bengaluru Rural\":{\"lat\":13.2846993,\"lon\":77.6077865},\"Bengaluru Urban\":{\"lat\":12.9700247,\"lon\":77.6536125},\"Bidar\":{\"lat\":17.9103939,\"lon\":77.51990789999999},\"Chamarajanagara\":{\"lat\":11.9261471,\"lon\":76.9437312},\"Chikkaballapura\":{\"lat\":13.4354985,\"lon\":77.7315344},\"Chikkamagaluru\":{\"lat\":13.316144099999999,\"lon\":75.7720439},\"Chitradurga\":{\"lat\":14.2250932,\"lon\":76.3980464},\"Dakshina Kannada\":{\"lat\":12.8437814,\"lon\":75.2479061},\"Davanagere\":{\"lat\":14.4644085,\"lon\":75.921758},\"Dharwad\":{\"lat\":15.458923599999999,\"lon\":75.007808},\"Gadag\":{\"lat\":15.431540599999998,\"lon\":75.63551489999999},\"Hassan\":{\"lat\":13.0033234,\"lon\":76.1003894},\"Haveri\":{\"lat\":14.7950698,\"lon\":75.39906739999999},\"Kalaburagi\":{\"lat\":17.329731,\"lon\":76.8342957},\"Kodagu\":{\"lat\":12.3374942,\"lon\":75.8069082},\"Kolar\":{\"lat\":13.136214299999999,\"lon\":78.12909859999999},\"Koppal\":{\"lat\":15.350465199999999,\"lon\":76.1567298},\"Mandya\":{\"lat\":12.5218157,\"lon\":76.89514880000002},\"Mysuru\":{\"lat\":12.295810399999999,\"lon\":76.6393805},\"Raichur\":{\"lat\":16.216018,\"lon\":77.3565608},\"Ramanagara\":{\"lat\":12.720861399999999,\"lon\":77.27989629999999},\"Shivamogga\":{\"lat\":13.9299299,\"lon\":75.568101},\"Tumakuru\":{\"lat\":13.3378762,\"lon\":77.117325},\"Udupi\":{\"lat\":13.3408807,\"lon\":74.7421427},\"Uttara Kannada\":{\"lat\":14.793706499999999,\"lon\":74.6868815},\"Vijayapura\":{\"lat\":16.830170799999998,\"lon\":75.710031},\"Yadgir\":{\"lat\":16.7625516,\"lon\":77.1442251},\"Alappuzha\":{\"lat\":9.498066699999999,\"lon\":76.3388484},\"Ernakulam\":{\"lat\":9.9816358,\"lon\":76.2998842},\"Idukki\":{\"lat\":9.9188973,\"lon\":77.10249019999999},\"Kannur\":{\"lat\":11.8744775,\"lon\":75.37036619999999},\"Kasaragod\":{\"lat\":12.4995966,\"lon\":74.9869276},\"Kollam\":{\"lat\":8.8932118,\"lon\":76.6141396},\"Kottayam\":{\"lat\":9.591566799999999,\"lon\":76.5221531},\"Kozhikode\":{\"lat\":11.2587531,\"lon\":75.78041},\"Malappuram\":{\"lat\":11.0509762,\"lon\":76.0710967},\"Palakkad\":{\"lat\":10.7867303,\"lon\":76.6547932},\"Pathanamthitta\":{\"lat\":9.2647582,\"lon\":76.78704139999999},\"Thiruvananthapuram\":{\"lat\":8.5241391,\"lon\":76.9366376},\"Thrissur\":{\"lat\":10.527641599999999,\"lon\":76.2144349},\"Wayanad\":{\"lat\":11.6853575,\"lon\":76.1319953},\"Kargil\":{\"lat\":34.5538522,\"lon\":76.1348944},\"Leh\":{\"lat\":34.1525864,\"lon\":77.57705349999999},\"Lakshadweep\":{\"lat\":10.3280265,\"lon\":72.78463359999999},\"Ahmednagar\":{\"lat\":19.0948287,\"lon\":74.74797889999999},\"Akola\":{\"lat\":20.7002159,\"lon\":77.0081678},\"Amravati\":{\"lat\":20.9319821,\"lon\":77.7523039},\"Beed\":{\"lat\":18.990088,\"lon\":75.7531324},\"Bhandara\":{\"lat\":21.177657999999997,\"lon\":79.6570127},\"Buldhana\":{\"lat\":20.5292147,\"lon\":76.1841701},\"Chandrapur\":{\"lat\":19.9615398,\"lon\":79.2961468},\"Dhule\":{\"lat\":20.9042201,\"lon\":74.7748979},\"Gadchiroli\":{\"lat\":20.184870999999998,\"lon\":79.9947956},\"Gondia\":{\"lat\":21.454947699999998,\"lon\":80.19607119999999},\"Hingoli\":{\"lat\":19.7173703,\"lon\":77.1493722},\"Jalgaon\":{\"lat\":21.0076578,\"lon\":75.5626039},\"Jalna\":{\"lat\":19.834665899999997,\"lon\":75.88163449999999},\"Kolhapur\":{\"lat\":16.7049873,\"lon\":74.24325270000001},\"Latur\":{\"lat\":18.4087934,\"lon\":76.5603828},\"Mumbai\":{\"lat\":19.0759837,\"lon\":72.8776559},\"Mumbai Suburban\":{\"lat\":19.1538231,\"lon\":72.8751786},\"Nagpur\":{\"lat\":21.1458004,\"lon\":79.0881546},\"Nanded\":{\"lat\":19.138251399999998,\"lon\":77.3209555},\"Nandurbar\":{\"lat\":21.746854799999998,\"lon\":74.12399599999999},\"Nashik\":{\"lat\":19.9974533,\"lon\":73.78980229999999},\"Osmanabad\":{\"lat\":18.206963599999998,\"lon\":76.17837390000001},\"Palghar\":{\"lat\":19.6967136,\"lon\":72.769885},\"Parbhani\":{\"lat\":19.2608384,\"lon\":76.774776},\"Pune\":{\"lat\":18.520430299999997,\"lon\":73.8567437},\"Raigad\":{\"lat\":18.515751899999998,\"lon\":73.1821623},\"Ratnagiri\":{\"lat\":16.990215,\"lon\":73.31202329999999},\"Sangli\":{\"lat\":16.8523973,\"lon\":74.5814773},\"Satara\":{\"lat\":17.6804639,\"lon\":74.018261},\"Sindhudurg\":{\"lat\":16.349219299999998,\"lon\":73.55941279999999},\"Solapur\":{\"lat\":17.6599188,\"lon\":75.9063906},\"Thane\":{\"lat\":19.2183307,\"lon\":72.9780897},\"Wardha\":{\"lat\":20.745319,\"lon\":78.60219459999999},\"Washim\":{\"lat\":20.111912300000004,\"lon\":77.1312586},\"Yavatmal\":{\"lat\":20.3899385,\"lon\":78.1306846},\"East Garo Hills\":{\"lat\":25.567169200000002,\"lon\":90.52578229999999},\"East Jaintia Hills\":{\"lat\":25.310076799999997,\"lon\":92.49999179999999},\"East Khasi Hills\":{\"lat\":25.3681768,\"lon\":91.7538817},\"North Garo Hills\":{\"lat\":25.8986758,\"lon\":90.4879916},\"Ribhoi\":{\"lat\":25.8431574,\"lon\":91.985621},\"South Garo Hills\":{\"lat\":25.330096899999997,\"lon\":90.563609},\"South West Khasi Hills\":{\"lat\":25.3258908,\"lon\":91.2506002},\"West Garo Hills\":{\"lat\":25.5679372,\"lon\":90.2244662},\"West Jaintia Hills\":{\"lat\":25.5021272,\"lon\":92.341887},\"West Khasi Hills\":{\"lat\":25.5624625,\"lon\":91.28910359999999},\"Bishnupur\":{\"lat\":23.067179,\"lon\":87.32146809999999},\"Chandel\":{\"lat\":24.3262003,\"lon\":94.0006003},\"Churachandpur\":{\"lat\":24.2993576,\"lon\":93.2583626},\"Imphal West\":{\"lat\":24.782783700000003,\"lon\":93.88589549999999},\"Jiribam\":{\"lat\":24.786434699999997,\"lon\":93.1538899},\"Kakching\":{\"lat\":24.496869,\"lon\":93.98305289999999},\"Kamjong\":{\"lat\":24.8570444,\"lon\":94.5134629},\"Kangpokpi\":{\"lat\":25.1519197,\"lon\":93.969963},\"Noney\":{\"lat\":24.8546947,\"lon\":93.6167146},\"Pherzawl\":{\"lat\":24.263711999999998,\"lon\":93.1892637},\"Senapati\":{\"lat\":25.2677276,\"lon\":94.0210189},\"Tamenglong\":{\"lat\":24.987934199999998,\"lon\":93.49529199999999},\"Tengnoupal\":{\"lat\":24.383792,\"lon\":94.1481775},\"Thoubal\":{\"lat\":24.5435506,\"lon\":93.9674371},\"Ukhrul\":{\"lat\":24.9320611,\"lon\":94.479976},\"Agar Malwa\":{\"lat\":23.7137337,\"lon\":76.0094637},\"Alirajpur\":{\"lat\":22.3403431,\"lon\":74.4994517},\"Anuppur\":{\"lat\":23.1136554,\"lon\":81.69762899999999},\"Ashoknagar\":{\"lat\":24.5775148,\"lon\":77.7318495},\"Balaghat\":{\"lat\":21.812876,\"lon\":80.18382930000001},\"Barwani\":{\"lat\":22.0363157,\"lon\":74.903339},\"Betul\":{\"lat\":21.901160100000002,\"lon\":77.8960201},\"Bhind\":{\"lat\":26.563776800000003,\"lon\":78.78609159999999},\"Bhopal\":{\"lat\":23.2599333,\"lon\":77.412615},\"Burhanpur\":{\"lat\":21.3145021,\"lon\":76.2180095},\"Chhatarpur\":{\"lat\":24.9167821,\"lon\":79.5910058},\"Chhindwara\":{\"lat\":22.057437,\"lon\":78.9381729},\"Damoh\":{\"lat\":23.832302199999997,\"lon\":79.4386591},\"Datia\":{\"lat\":25.6653168,\"lon\":78.4609182},\"Dewas\":{\"lat\":22.9675929,\"lon\":76.0534454},\"Dhar\":{\"lat\":22.6012922,\"lon\":75.3024655},\"Dindori\":{\"lat\":22.8457457,\"lon\":81.0754657},\"Guna\":{\"lat\":24.632364799999998,\"lon\":77.3001762},\"Gwalior\":{\"lat\":26.218287099999998,\"lon\":78.18283079999999},\"Harda\":{\"lat\":22.3466702,\"lon\":77.0889583},\"Hoshangabad\":{\"lat\":22.7518961,\"lon\":77.7288655},\"Indore\":{\"lat\":22.7195687,\"lon\":75.8577258},\"Jabalpur\":{\"lat\":23.181466999999998,\"lon\":79.9864071},\"Jhabua\":{\"lat\":22.769665999999997,\"lon\":74.5920921},\"Katni\":{\"lat\":23.8343441,\"lon\":80.38938139999999},\"Khandwa\":{\"lat\":21.8314037,\"lon\":76.3497612},\"Khargone\":{\"lat\":21.833524399999998,\"lon\":75.61498929999999},\"Mandla\":{\"lat\":22.5979218,\"lon\":80.3713855},\"Mandsaur\":{\"lat\":24.073435600000003,\"lon\":75.0679018},\"Morena\":{\"lat\":26.494717200000004,\"lon\":77.9940222},\"Narsinghpur\":{\"lat\":22.947317899999998,\"lon\":79.1923266},\"Neemuch\":{\"lat\":24.473766299999998,\"lon\":74.8726497},\"Niwari\":{\"lat\":25.3697238,\"lon\":78.7986181},\"Panna\":{\"lat\":24.718031099999997,\"lon\":80.1819268},\"Raisen\":{\"lat\":23.3301074,\"lon\":77.78428509999999},\"Rajgarh\":{\"lat\":24.007881899999997,\"lon\":76.7278803},\"Ratlam\":{\"lat\":23.3315103,\"lon\":75.0366677},\"Rewa\":{\"lat\":24.5362477,\"lon\":81.30369460000001},\"Sagar\":{\"lat\":23.838805,\"lon\":78.7378068},\"Satna\":{\"lat\":24.6005075,\"lon\":80.8322428},\"Sehore\":{\"lat\":23.2032399,\"lon\":77.0844044},\"Seoni\":{\"lat\":22.086869099999998,\"lon\":79.5434841},\"Shahdol\":{\"lat\":23.300231999999998,\"lon\":81.3568619},\"Shajapur\":{\"lat\":23.427293799999998,\"lon\":76.2729839},\"Sheopur\":{\"lat\":25.672819999999998,\"lon\":76.6961305},\"Shivpuri\":{\"lat\":25.4320478,\"lon\":77.66438149999999},\"Sidhi\":{\"lat\":24.3956038,\"lon\":81.88252790000001},\"Singrauli\":{\"lat\":24.1992101,\"lon\":82.66454689999999},\"Tikamgarh\":{\"lat\":24.745614699999997,\"lon\":78.8320779},\"Ujjain\":{\"lat\":23.1764665,\"lon\":75.7885163},\"Umaria\":{\"lat\":23.601374699999997,\"lon\":81.0754657},\"Vidisha\":{\"lat\":23.5235719,\"lon\":77.81397179999999},\"Aizawl\":{\"lat\":23.730717499999997,\"lon\":92.71731059999999},\"Champhai\":{\"lat\":23.456570799999998,\"lon\":93.32819289999999},\"Hnahthial\":{\"lat\":22.9653238,\"lon\":92.930081},\"Khawzawl\":{\"lat\":23.5344974,\"lon\":93.1829868},\"Kolasib\":{\"lat\":24.224565,\"lon\":92.67602509999999},\"Lawngtlai\":{\"lat\":22.5284445,\"lon\":92.89263419999999},\"Lunglei\":{\"lat\":22.8670691,\"lon\":92.7655358},\"Mamit\":{\"lat\":23.6473811,\"lon\":92.539603},\"Saiha\":{\"lat\":22.489734,\"lon\":92.979269},\"Saitual\":{\"lat\":23.9704247,\"lon\":92.57583989999999},\"Serchhip\":{\"lat\":23.3416578,\"lon\":92.8502302},\"Dimapur\":{\"lat\":25.909140599999997,\"lon\":93.72656049999999},\"Kiphire\":{\"lat\":25.8187605,\"lon\":94.8520636},\"Kohima\":{\"lat\":25.6751129,\"lon\":94.10859980000001},\"Longleng\":{\"lat\":26.4901992,\"lon\":94.81966399999999},\"Mokokchung\":{\"lat\":26.3220358,\"lon\":94.5134629},\"Phek\":{\"lat\":25.6634215,\"lon\":94.47032019999999},\"Tuensang\":{\"lat\":26.235742199999997,\"lon\":94.8131863},\"Wokha\":{\"lat\":26.0910351,\"lon\":94.25897379999999},\"Zunheboto\":{\"lat\":26.009283099999998,\"lon\":94.5237707},\"Angul\":{\"lat\":20.8444033,\"lon\":85.1510818},\"Balangir\":{\"lat\":20.7011108,\"lon\":83.4846069},\"Balasore\":{\"lat\":21.4933578,\"lon\":86.9134794},\"Bargarh\":{\"lat\":21.3470154,\"lon\":83.6320212},\"Bhadrak\":{\"lat\":21.0573616,\"lon\":86.4962996},\"Boudh\":{\"lat\":20.660744700000002,\"lon\":84.14351359999999},\"Cuttack\":{\"lat\":20.462521,\"lon\":85.8829895},\"Deogarh\":{\"lat\":21.5383136,\"lon\":84.7289442},\"Dhenkanal\":{\"lat\":20.650475300000004,\"lon\":85.5981223},\"Gajapati\":{\"lat\":19.191222099999997,\"lon\":84.1857115},\"Ganjam\":{\"lat\":19.5859712,\"lon\":84.68974949999999},\"Jagatsinghpur\":{\"lat\":20.2548998,\"lon\":86.1706221},\"Jajpur\":{\"lat\":20.8341019,\"lon\":86.3326058},\"Jharsuguda\":{\"lat\":21.8554375,\"lon\":84.0061661},\"Kalahandi\":{\"lat\":19.9137363,\"lon\":83.1649001},\"Kandhamal\":{\"lat\":20.1342042,\"lon\":84.01674229999999},\"Kendrapara\":{\"lat\":20.4969108,\"lon\":86.4288534},\"Kendujhar\":{\"lat\":21.628933,\"lon\":85.5816847},\"Khordha\":{\"lat\":20.130141,\"lon\":85.47880649999999},\"Koraput\":{\"lat\":18.813487000000002,\"lon\":82.71233269999999},\"Malkangiri\":{\"lat\":18.1640803,\"lon\":81.9534815},\"Mayurbhanj\":{\"lat\":22.0086978,\"lon\":86.41873079999999},\"Nabarangapur\":{\"lat\":19.2281434,\"lon\":82.54698979999999},\"Nayagarh\":{\"lat\":20.123133199999998,\"lon\":85.1038426},\"Nuapada\":{\"lat\":20.8060184,\"lon\":82.5361393},\"Puri\":{\"lat\":19.8134554,\"lon\":85.8312359},\"Rayagada\":{\"lat\":19.171208999999998,\"lon\":83.4163226},\"Sambalpur\":{\"lat\":21.466871599999997,\"lon\":83.9811665},\"Subarnapur\":{\"lat\":20.9338113,\"lon\":83.804868},\"Sundargarh\":{\"lat\":22.1240025,\"lon\":84.043175},\"Amritsar\":{\"lat\":31.6339793,\"lon\":74.8722642},\"Barnala\":{\"lat\":30.381944599999997,\"lon\":75.5467979},\"Bathinda\":{\"lat\":30.210994000000003,\"lon\":74.9454745},\"Faridkot\":{\"lat\":30.6769462,\"lon\":74.7583351},\"Fatehgarh Sahib\":{\"lat\":30.643534499999998,\"lon\":76.39703},\"Fazilka\":{\"lat\":30.403647799999998,\"lon\":74.0279621},\"Ferozepur\":{\"lat\":30.933134799999998,\"lon\":74.6224755},\"Gurdaspur\":{\"lat\":32.0413917,\"lon\":75.403086},\"Hoshiarpur\":{\"lat\":31.514317799999997,\"lon\":75.911483},\"Jalandhar\":{\"lat\":31.3260152,\"lon\":75.57618289999999},\"Kapurthala\":{\"lat\":31.3722571,\"lon\":75.4017654},\"Ludhiana\":{\"lat\":30.900965,\"lon\":75.8572758},\"Mansa\":{\"lat\":29.999506900000004,\"lon\":75.3936808},\"Moga\":{\"lat\":30.8230114,\"lon\":75.17344709999999},\"Pathankot\":{\"lat\":32.2733352,\"lon\":75.6522066},\"Patiala\":{\"lat\":30.339780899999997,\"lon\":76.3868797},\"Rupnagar\":{\"lat\":30.9661003,\"lon\":76.5230961},\"S.A.S. Nagar\":{\"lat\":30.649648599999995,\"lon\":76.7567368},\"Sangrur\":{\"lat\":30.2457963,\"lon\":75.8420716},\"Shahid Bhagat Singh Nagar\":{\"lat\":31.091295399999996,\"lon\":76.0391909},\"Sri Muktsar Sahib\":{\"lat\":30.476177299999996,\"lon\":74.5121599},\"Tarn Taran\":{\"lat\":31.4538668,\"lon\":74.9267599},\"Karaikal\":{\"lat\":10.9254398,\"lon\":79.8380056},\"Mahe\":{\"lat\":11.7002703,\"lon\":75.5424843},\"Puducherry\":{\"lat\":11.9415915,\"lon\":79.8083133},\"Yanam\":{\"lat\":16.7271912,\"lon\":82.21757749999999},\"Ajmer\":{\"lat\":26.4498954,\"lon\":74.6399163},\"Alwar\":{\"lat\":27.5529907,\"lon\":76.6345735},\"Banswara\":{\"lat\":23.546139399999998,\"lon\":74.4349761},\"Barmer\":{\"lat\":25.752146699999997,\"lon\":71.3966865},\"Bharatpur\":{\"lat\":27.2151863,\"lon\":77.5029996},\"Bhilwara\":{\"lat\":25.3407388,\"lon\":74.63131829999999},\"Bikaner\":{\"lat\":28.022934799999998,\"lon\":73.3119159},\"BSF Camp\":{\"lat\":26.135577899999998,\"lon\":88.01281499999999},\"Bundi\":{\"lat\":25.4325869,\"lon\":75.6482726},\"Chittorgarh\":{\"lat\":24.8829177,\"lon\":74.6229699},\"Churu\":{\"lat\":28.2925364,\"lon\":74.9707262},\"Dausa\":{\"lat\":26.899695299999998,\"lon\":76.332411},\"Dholpur\":{\"lat\":26.696552099999998,\"lon\":77.8907576},\"Dungarpur\":{\"lat\":23.841668,\"lon\":73.7146623},\"Ganganagar\":{\"lat\":29.9093759,\"lon\":73.87998050000002},\"Hanumangarh\":{\"lat\":29.5815012,\"lon\":74.32941989999999},\"Jaipur\":{\"lat\":26.9124336,\"lon\":75.7872709},\"Jaisalmer\":{\"lat\":26.915748699999998,\"lon\":70.9083443},\"Jalore\":{\"lat\":25.3444799,\"lon\":72.6253763},\"Jhalawar\":{\"lat\":24.5973494,\"lon\":76.1609838},\"Jhunjhunu\":{\"lat\":28.1317038,\"lon\":75.4022233},\"Jodhpur\":{\"lat\":26.2389469,\"lon\":73.02430939999999},\"Karauli\":{\"lat\":26.488322999999998,\"lon\":77.01614359999999},\"Kota\":{\"lat\":25.213815600000004,\"lon\":75.8647527},\"Nagaur\":{\"lat\":27.1983368,\"lon\":73.7493272},\"Pratapgarh\":{\"lat\":25.8973038,\"lon\":81.9452981},\"Rajsamand\":{\"lat\":25.0583257,\"lon\":73.88600339999999},\"Sawai Madhopur\":{\"lat\":26.012373,\"lon\":76.3560109},\"Sikar\":{\"lat\":27.609391199999997,\"lon\":75.1397935},\"Sirohi\":{\"lat\":24.8851548,\"lon\":72.8574558},\"Tonk\":{\"lat\":26.1659063,\"lon\":75.7962852},\"Udaipur\":{\"lat\":24.585445,\"lon\":73.712479},\"East Sikkim\":{\"lat\":27.308363699999997,\"lon\":88.6723578},\"North Sikkim\":{\"lat\":27.8236356,\"lon\":88.55653099999999},\"South Sikkim\":{\"lat\":27.285840000000004,\"lon\":88.3945669},\"West Sikkim\":{\"lat\":27.303198599999998,\"lon\":88.2071598},\"Adilabad\":{\"lat\":19.6640624,\"lon\":78.5320107},\"Bhadradri Kothagudem\":{\"lat\":14.7430452,\"lon\":74.6758388},\"Hyderabad\":{\"lat\":17.385044,\"lon\":78.486671},\"Jagtial\":{\"lat\":18.7894881,\"lon\":78.91204590000001},\"Jangaon\":{\"lat\":17.722650899999998,\"lon\":79.1517868},\"Jayashankar Bhupalapally\":{\"lat\":18.4292622,\"lon\":79.8634855},\"Kamareddy\":{\"lat\":18.320483199999998,\"lon\":78.3369523},\"Karimnagar\":{\"lat\":18.4385553,\"lon\":79.1288412},\"Khammam\":{\"lat\":17.2472528,\"lon\":80.1514447},\"Mahabubabad\":{\"lat\":17.602625,\"lon\":80.00361989999999},\"Mahabubnagar\":{\"lat\":16.748837899999998,\"lon\":78.00351719999999},\"Mancherial\":{\"lat\":18.8713826,\"lon\":79.4443099},\"Medak\":{\"lat\":18.0529357,\"lon\":78.261853},\"Medchal Malkajgiri\":{\"lat\":17.4139017,\"lon\":78.5855965},\"Mulugu\":{\"lat\":17.415377,\"lon\":78.443567},\"Nagarkurnool\":{\"lat\":16.4939417,\"lon\":78.31024889999999},\"Nalgonda\":{\"lat\":17.057466299999998,\"lon\":79.26841689999999},\"Narayanpet\":{\"lat\":16.744511199999998,\"lon\":77.4960078},\"Nirmal\":{\"lat\":19.0964117,\"lon\":78.342975},\"Nizamabad\":{\"lat\":18.672504699999998,\"lon\":78.09408669999999},\"Peddapalli\":{\"lat\":18.6151461,\"lon\":79.3826826},\"Ranga Reddy\":{\"lat\":17.389137899999998,\"lon\":77.8367282},\"Sangareddy\":{\"lat\":17.6140077,\"lon\":78.08156269999999},\"Siddipet\":{\"lat\":18.1017996,\"lon\":78.8519601},\"Suryapet\":{\"lat\":17.1313756,\"lon\":79.6336242},\"Vikarabad\":{\"lat\":17.336455500000003,\"lon\":77.9048268},\"Wanaparthy\":{\"lat\":16.3623118,\"lon\":78.0621824},\"Warangal Urban\":{\"lat\":17.9744395,\"lon\":79.9192702},\"Ariyalur\":{\"lat\":11.1400585,\"lon\":79.0786281},\"Chengalpattu\":{\"lat\":12.6819372,\"lon\":79.98884129999999},\"Chennai\":{\"lat\":13.082680199999999,\"lon\":80.2707184},\"Coimbatore\":{\"lat\":11.016844500000001,\"lon\":76.9558321},\"Cuddalore\":{\"lat\":11.748041899999999,\"lon\":79.7713687},\"Dharmapuri\":{\"lat\":12.1210997,\"lon\":78.1582143},\"Dindigul\":{\"lat\":10.3623794,\"lon\":77.9694579},\"Erode\":{\"lat\":11.3410364,\"lon\":77.7171642},\"Kallakurichi\":{\"lat\":11.7383735,\"lon\":78.9638899},\"Kancheepuram\":{\"lat\":12.818456,\"lon\":79.6946586},\"Kanyakumari\":{\"lat\":8.0883064,\"lon\":77.5384507},\"Karur\":{\"lat\":10.960077799999999,\"lon\":78.07660360000001},\"Krishnagiri\":{\"lat\":12.5265661,\"lon\":78.2149575},\"Madurai\":{\"lat\":9.9252007,\"lon\":78.1197754},\"Nagapattinam\":{\"lat\":10.767231299999999,\"lon\":79.8448512},\"Namakkal\":{\"lat\":11.2188958,\"lon\":78.1673575},\"Nilgiris\":{\"lat\":11.491604299999999,\"lon\":76.7336521},\"Perambalur\":{\"lat\":11.2342104,\"lon\":78.8806852},\"Pudukkottai\":{\"lat\":10.3832867,\"lon\":78.800129},\"Ramanathapuram\":{\"lat\":9.3639356,\"lon\":78.8394819},\"Ranipet\":{\"lat\":12.948740299999999,\"lon\":79.318978},\"Salem\":{\"lat\":42.51954,\"lon\":-70.8967155},\"Sivaganga\":{\"lat\":9.8432999,\"lon\":78.48087749999999},\"Tenkasi\":{\"lat\":8.959351999999999,\"lon\":77.316109},\"Thanjavur\":{\"lat\":10.7869994,\"lon\":79.13782739999999},\"Theni\":{\"lat\":9.932983199999999,\"lon\":77.4701972},\"Thiruvallur\":{\"lat\":13.2544335,\"lon\":80.0087746},\"Thiruvarur\":{\"lat\":10.7661312,\"lon\":79.6343691},\"Thoothukkudi\":{\"lat\":8.764166099999999,\"lon\":78.1348361},\"Tiruchirappalli\":{\"lat\":10.7904833,\"lon\":78.7046725},\"Tirunelveli\":{\"lat\":8.713912600000002,\"lon\":77.7566523},\"Tirupathur\":{\"lat\":12.4950124,\"lon\":78.56784069999999},\"Tiruppur\":{\"lat\":11.1085242,\"lon\":77.3410656},\"Tiruvannamalai\":{\"lat\":12.2252841,\"lon\":79.07469569999999},\"Vellore\":{\"lat\":12.916516699999999,\"lon\":79.13249859999999},\"Viluppuram\":{\"lat\":11.9401378,\"lon\":79.4861449},\"Virudhunagar\":{\"lat\":9.5680116,\"lon\":77.96244349999999},\"Dhalai\":{\"lat\":23.846698200000002,\"lon\":91.9099238},\"Gomati\":{\"lat\":26.692042999999998,\"lon\":81.6005086},\"Khowai\":{\"lat\":24.0671796,\"lon\":91.60567180000001},\"North Tripura\":{\"lat\":24.079713899999998,\"lon\":92.2630393},\"Sipahijala\":{\"lat\":23.6148176,\"lon\":91.3276422},\"South Tripura\":{\"lat\":23.2317493,\"lon\":91.55961169999999},\"Unokoti\":{\"lat\":24.3169807,\"lon\":92.0668989},\"West Tripura\":{\"lat\":23.899682,\"lon\":91.4048249},\"Agra\":{\"lat\":27.1766701,\"lon\":78.00807449999999},\"Aligarh\":{\"lat\":27.8973944,\"lon\":78.0880129},\"Ambedkar Nagar\":{\"lat\":26.4683952,\"lon\":82.6915429},\"Amethi\":{\"lat\":26.1540538,\"lon\":81.814238},\"Amroha\":{\"lat\":28.905177799999997,\"lon\":78.46731799999999},\"Auraiya\":{\"lat\":26.4605377,\"lon\":79.5112528},\"Ayodhya\":{\"lat\":26.7921605,\"lon\":82.1997954},\"Azamgarh\":{\"lat\":26.0739138,\"lon\":83.18594949999999},\"Baghpat\":{\"lat\":28.9427827,\"lon\":77.22760699999999},\"Bahraich\":{\"lat\":27.5705152,\"lon\":81.59766719999999},\"Ballia\":{\"lat\":28.2006882,\"lon\":79.3651914},\"Banda\":{\"lat\":25.4520223,\"lon\":80.54384499999999},\"Barabanki\":{\"lat\":26.9268042,\"lon\":81.1833809},\"Bareilly\":{\"lat\":28.3670355,\"lon\":79.4304381},\"Basti\":{\"lat\":26.8139844,\"lon\":82.7629893},\"Bhadohi\":{\"lat\":25.3804531,\"lon\":82.56769810000002},\"Bijnor\":{\"lat\":29.373167299999995,\"lon\":78.1350904},\"Budaun\":{\"lat\":28.0311101,\"lon\":79.1271229},\"Bulandshahr\":{\"lat\":28.406963,\"lon\":77.8498292},\"Chandauli\":{\"lat\":25.2604696,\"lon\":83.264538},\"Chitrakoot\":{\"lat\":25.178815399999998,\"lon\":80.86545339999999},\"Deoria\":{\"lat\":26.5024286,\"lon\":83.7791283},\"Etah\":{\"lat\":27.5172789,\"lon\":78.792953},\"Etawah\":{\"lat\":26.782910299999998,\"lon\":79.027659},\"Farrukhabad\":{\"lat\":27.3825853,\"lon\":79.5840195},\"Fatehpur\":{\"lat\":25.849980799999997,\"lon\":80.89865019999999},\"Firozabad\":{\"lat\":27.1591961,\"lon\":78.3957331},\"Gautam Buddha Nagar\":{\"lat\":28.338333,\"lon\":77.6077865},\"Ghaziabad\":{\"lat\":28.669156500000003,\"lon\":77.45375779999999},\"Ghazipur\":{\"lat\":25.587790100000003,\"lon\":83.5783078},\"Gonda\":{\"lat\":27.1339874,\"lon\":81.96189790000001},\"Gorakhpur\":{\"lat\":26.760554499999998,\"lon\":83.3731675},\"Hapur\":{\"lat\":28.730579799999994,\"lon\":77.7758825},\"Hardoi\":{\"lat\":27.396507099999997,\"lon\":80.1250479},\"Hathras\":{\"lat\":27.5980718,\"lon\":78.0492265},\"Jalaun\":{\"lat\":26.1458649,\"lon\":79.3296565},\"Jaunpur\":{\"lat\":25.7464145,\"lon\":82.68370329999999},\"Jhansi\":{\"lat\":25.448425699999998,\"lon\":78.5684594},\"Kannauj\":{\"lat\":27.0514156,\"lon\":79.9136731},\"Kanpur Dehat\":{\"lat\":26.526707,\"lon\":79.8296743},\"Kanpur Nagar\":{\"lat\":26.414824499999998,\"lon\":80.23213129999999},\"Kasganj\":{\"lat\":27.808529,\"lon\":78.64610979999999},\"Kaushambi\":{\"lat\":25.361054000000003,\"lon\":81.40317069999999},\"Kushinagar\":{\"lat\":26.7398787,\"lon\":83.8869698},\"Lakhimpur Kheri\":{\"lat\":27.946239499999997,\"lon\":80.7787163},\"Lalitpur\":{\"lat\":24.691170699999997,\"lon\":78.4138183},\"Lucknow\":{\"lat\":26.8466937,\"lon\":80.94616599999999},\"Maharajganj\":{\"lat\":27.1177463,\"lon\":83.5070203},\"Mahoba\":{\"lat\":25.2920964,\"lon\":79.8724168},\"Mainpuri\":{\"lat\":27.2281937,\"lon\":79.02516589999999},\"Mathura\":{\"lat\":27.4924134,\"lon\":77.673673},\"Meerut\":{\"lat\":28.9844618,\"lon\":77.7064137},\"Moradabad\":{\"lat\":28.8386481,\"lon\":78.7733286},\"Muzaffarnagar\":{\"lat\":29.4726817,\"lon\":77.7085091},\"Pilibhit\":{\"lat\":28.62494,\"lon\":79.8075272},\"Prayagraj\":{\"lat\":25.4358011,\"lon\":81.846311},\"Rae Bareli\":{\"lat\":26.214480599999998,\"lon\":81.25281389999999},\"Rampur\":{\"lat\":28.798299,\"lon\":79.02202869999999},\"Saharanpur\":{\"lat\":29.9680035,\"lon\":77.55520659999999},\"Sambhal\":{\"lat\":28.5903614,\"lon\":78.5717631},\"Sant Kabir Nagar\":{\"lat\":26.7671755,\"lon\":83.03613759999999},\"Shahjahanpur\":{\"lat\":27.8753399,\"lon\":79.9147268},\"Shamli\":{\"lat\":29.4501986,\"lon\":77.3172046},\"Shrawasti\":{\"lat\":27.503542699999997,\"lon\":82.036461},\"Siddharthnagar\":{\"lat\":27.2990659,\"lon\":83.0927827},\"Sitapur\":{\"lat\":27.5680156,\"lon\":80.6789519},\"Sonbhadra\":{\"lat\":24.685000499999997,\"lon\":83.0683519},\"Sultanpur\":{\"lat\":26.2585371,\"lon\":82.06598579999999},\"Unnao\":{\"lat\":26.5393449,\"lon\":80.4878195},\"Varanasi\":{\"lat\":25.317645199999998,\"lon\":82.9739144},\"Almora\":{\"lat\":29.589240699999998,\"lon\":79.646666},\"Bageshwar\":{\"lat\":29.8403606,\"lon\":79.769426},\"Chamoli\":{\"lat\":30.293743900000003,\"lon\":79.560344},\"Champawat\":{\"lat\":29.336097399999996,\"lon\":80.0910275},\"Dehradun\":{\"lat\":30.316494499999997,\"lon\":78.03219179999999},\"Haridwar\":{\"lat\":29.9456906,\"lon\":78.16424780000001},\"Nainital\":{\"lat\":29.391920199999998,\"lon\":79.4542033},\"Pauri Garhwal\":{\"lat\":29.868768199999998,\"lon\":78.8382644},\"Pithoragarh\":{\"lat\":29.5828604,\"lon\":80.2181884},\"Rudraprayag\":{\"lat\":30.284414100000003,\"lon\":78.9811407},\"Tehri Garhwal\":{\"lat\":30.3011858,\"lon\":78.5660852},\"Udham Singh Nagar\":{\"lat\":28.960965299999998,\"lon\":79.5153773},\"Uttarkashi\":{\"lat\":30.726830699999997,\"lon\":78.4354042},\"Alipurduar\":{\"lat\":26.4922164,\"lon\":89.5319627},\"Bankura\":{\"lat\":23.2312686,\"lon\":87.07838749999999},\"Birbhum\":{\"lat\":23.8401675,\"lon\":87.6186379},\"Cooch Behar\":{\"lat\":26.3452397,\"lon\":89.4482079},\"Dakshin Dinajpur\":{\"lat\":25.3715308,\"lon\":88.55653099999999},\"Darjeeling\":{\"lat\":27.0410218,\"lon\":88.2662745},\"Hooghly\":{\"lat\":22.901158799999997,\"lon\":88.3898552},\"Howrah\":{\"lat\":22.5957689,\"lon\":88.26363940000002},\"Jalpaiguri\":{\"lat\":26.521457899999998,\"lon\":88.7195567},\"Jhargram\":{\"lat\":22.4549909,\"lon\":86.9974385},\"Kalimpong\":{\"lat\":27.0593562,\"lon\":88.46945350000001},\"Kolkata\":{\"lat\":22.572646,\"lon\":88.36389500000001},\"Malda\":{\"lat\":25.1785773,\"lon\":88.24611829999999},\"Murshidabad\":{\"lat\":24.175903899999998,\"lon\":88.2801785},\"North 24 Parganas\":{\"lat\":22.616809900000003,\"lon\":88.402895},\"Paschim Medinipur\":{\"lat\":22.4080376,\"lon\":87.38107269999999},\"Purba Bardhaman\":{\"lat\":23.2390023,\"lon\":87.86945970000001},\"Purba Medinipur\":{\"lat\":21.937287899999998,\"lon\":87.77633329999999},\"Purulia\":{\"lat\":23.332202600000002,\"lon\":86.3616405},\"South 24 Parganas\":{\"lat\":22.1352378,\"lon\":88.4016041},\"Uttar Dinajpur\":{\"lat\":25.9810393,\"lon\":88.050979}}";
                try {
                    JSONObject districtLatLonJson = new JSONObject(districtListString);
                    redzoneJsonReader = new JSONObject(strings[0]);
                    redzoneJsonArray = redzoneJsonReader.names();
                    mapCases = new HashMap<>();
                    Log.e("json", String.valueOf(redzoneJsonReader.names().length()));
                    //Log.e("json", redzoneJsonReader.names().toString());
                    assert redzoneJsonArray != null;
                    for (int posJson = 1; posJson < redzoneJsonArray.length(); posJson++) {
                        String groupRedzone = redzoneJsonArray.getString(posJson);
                        JSONObject districtData = redzoneJsonReader.getJSONObject(groupRedzone).getJSONObject("districtData");
                        if (districtData.names() != null) {
                            Log.e("district", districtData.names().toString());
                            for(int i=0; i<districtData.length();i++) {
                                String districtName = districtData.names().getString(i);
                                JSONObject mainData = districtData.optJSONObject(districtName);
                                assert mainData != null;
                                if(districtLatLonJson.has(districtName)) {
                                    insert_into_cases(
                                            posJson * 10000 + i,
                                            districtName,
                                            mainData.getInt("active"),
                                            districtLatLonJson.getJSONObject(districtName).getDouble("lat"),
                                            districtLatLonJson.getJSONObject(districtName).getDouble("lon")
                                    );
                                    mapCases.put(districtName, mainData.getInt("active"));
                                }


                            }
                        }
                        List<modelCasesDistrict> modelCasesDistricts = _db.dao_cases_districts().getStaticCases();
                        if (modelCasesDistricts.size() > redzoneJsonArray.length())
                            for (int i = redzoneJsonArray.length(); i < modelCasesDistricts.size(); i++) {
                                modelCasesDistrict modelCases = new modelCasesDistrict();
                                modelCases.setID(modelCasesDistricts.get(i).getID());
                                modelCases.setLat(modelCasesDistricts.get(i).getLat());
                                modelCases.setLon(modelCasesDistricts.get(i).getLon());
                                modelCases.setActiveCase(modelCasesDistricts.get(i).getActiveCase());
                                modelCases.setDistrict(modelCasesDistricts.get(i).getDistrict());
                                delete_cases_district(modelCases);
                            }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON",e.getLocalizedMessage());
                }
                return strings[0];
            }
        }.execute(JSON_STRING__);
    }

    class modeltemo{
        String name;
        double lat;
        double lon;
        modeltemo(String name, double lat, double lon){
            this.name=name;
            this.lat = lat;
            this.lon = lon;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void fetch_db_toll_free_no(){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                Log.e("testing","fetch_db");
                JSONObject tolllfreenoJsonReader = null;

                try {
                    tolllfreenoJsonReader = new JSONObject(JSON_STRING_toll);
                    JSONObject data_toll_free = tolllfreenoJsonReader.getJSONObject("data");
                    JSONObject contact_toll_free = data_toll_free.getJSONObject("contacts");
                    tollfreenoJsonArray = contact_toll_free.getJSONArray("regional");

                    for (int posJson = 0; posJson < tollfreenoJsonArray.length(); posJson++) {
                        JSONObject groupTollFreeNo = tollfreenoJsonArray.getJSONObject(posJson);
                        insert_into_toll_free_no(
                                groupTollFreeNo.getString("loc"),
                                groupTollFreeNo.getString("number")
                        );
                    }
                    if(_db!=null&& _db.dao_toll_free_no().getStaticTollFreeNo()!=null) {
                        List<Model_toll_free_no> model_toll_free_nos = _db.dao_toll_free_no().getStaticTollFreeNo();
                        if (model_toll_free_nos.size() > tollfreenoJsonArray.length())
                            for (int i = tollfreenoJsonArray.length(); i < model_toll_free_nos.size(); i++) {
                                Model_toll_free_no model_toll_free_no = new Model_toll_free_no();
                                model_toll_free_no.setCall_name(model_toll_free_nos.get(i).getCall_name());
                                model_toll_free_no.setCall_no(model_toll_free_nos.get(i).getCall_no());
                                delete_toll_free(model_toll_free_no);
                            }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON",e.getLocalizedMessage());
                }
                return null;
            }
        }.execute(JSON_STRING_toll);

    }
    @SuppressLint("StaticFieldLeak")
    private void fetch_db_covid_info(){
        new AsyncTask<String,String,String>(){
            @Override
            protected String doInBackground(String... strings) {
                Log.e("testing","fetch_db_covid_info");
                JSONObject covidInfoJsonReader = null;

                try {
                    covidInfoJsonReader = new JSONObject(JSON_STRING_COVID);
                    JSONObject data_covid_info = covidInfoJsonReader.getJSONObject("data");
                    JSONArray summary_covid_info = data_covid_info.getJSONArray("unofficial-summary");

                    JSONObject extractedCovidinfo = summary_covid_info.getJSONObject(0);
                    insert_into_covid_info(
                            extractedCovidinfo.getInt("active"),
                            extractedCovidinfo.getInt("deaths")
                    );

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON",e.getLocalizedMessage());
                }
                return null;
            }
        }.execute(JSON_STRING_toll);

    }



    class HTTPGetJsonFile extends AsyncTask<String, String,String> {

        int LengthOfFile;
        int count;
        String JSON_STRING_;


        @Override
        protected void onPreExecute() {
            //fragmentTest.progressBar.setVisibility(View.VISIBLE);
            Log.e("testing","http");
            JSON_STRING_ = "";
        }

        @Override
        protected void onPostExecute(String s) {


            //Toast.makeText(mContext, JSON_STRING, Toast.LENGTH_LONG).show();
            //fragmentTest.progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(JSON_STRING_)) {
                Log.e("testing","http_done");
                    fetch_db_redzone(JSON_STRING_);

            }
            else {
                Toast.makeText(context, "Couldn't load new file", Toast.LENGTH_SHORT).show();
                Log.e("testing","http_fail");
            }



        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // fragmentTest.progressBar.setIndeterminate(false);

            //fragmentTest.updateProgress(10);
        }

        @Override
        protected String doInBackground(String... strings) {
            reader=null;
            conn = null;
            try {

                //Prepare the url and connectin
                URL u = new URL(strings[0]);
                conn = (HttpURLConnection) u.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                LengthOfFile = conn.getContentLength();
                StringBuilder buffer = new StringBuilder();
                String line="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                    count = LengthOfFile - buffer.length();
                    publishProgress(String.valueOf(count));
                }

                JSON_STRING_ = buffer.toString();

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return strings[0];

        }
    }

    class HTTPGetCases extends AsyncTask<String, String,String> {

        int LengthOfFile;
        int count;
        String JSON_STRING__;


        @Override
        protected void onPreExecute() {
            //fragmentTest.progressBar.setVisibility(View.VISIBLE);
            Log.e("testing","httpcases");
            JSON_STRING__ = "";
        }

        @Override
        protected void onPostExecute(String s) {


            //Toast.makeText(mContext, JSON_STRING, Toast.LENGTH_LONG).show();
            //fragmentTest.progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(JSON_STRING__)) {
                Log.e("testing","http_done_cases");
                fetch_db_cases(JSON_STRING__);

            }
            else {
                Toast.makeText(context, "Couldn't load new file", Toast.LENGTH_SHORT).show();
                Log.e("testing","http_fail_cases");
            }



        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // fragmentTest.progressBar.setIndeterminate(false);

            //fragmentTest.updateProgress(10);
        }

        @Override
        protected String doInBackground(String... strings) {
            reader=null;
            conn = null;
            try {

                //Prepare the url and connectin
                URL u = new URL(strings[0]);
                conn = (HttpURLConnection) u.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                LengthOfFile = conn.getContentLength();
                StringBuilder buffer = new StringBuilder();
                String line="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                    count = LengthOfFile - buffer.length();
                    publishProgress(String.valueOf(count));
                }

                JSON_STRING__ = buffer.toString();

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return strings[0];

        }
    }


    class HTTPGetJsonFileToll extends AsyncTask<String, String,String> {

        int LengthOfFile;
        int count;


        @Override
        protected void onPreExecute() {
            //fragmentTest.progressBar.setVisibility(View.VISIBLE);
            Log.e("testing","httptoll");
            JSON_STRING_toll = "";
        }

        @Override
        protected void onPostExecute(String s) {


            //Toast.makeText(mContext, JSON_STRING, Toast.LENGTH_LONG).show();
            //fragmentTest.progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(JSON_STRING_toll)) {
                Log.e("testing","http_done_toll");
                fetch_db_toll_free_no();

            }
            else {
                Toast.makeText(context, "Couldn't load new file", Toast.LENGTH_SHORT).show();
                Log.e("testing","http_fail_toll");
            }



        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // fragmentTest.progressBar.setIndeterminate(false);

            //fragmentTest.updateProgress(10);
        }

        @Override
        protected String doInBackground(String... strings) {
            reader=null;
            conn = null;
            try {

                //Prepare the url and connectin
                URL u = new URL(strings[0]);
                conn = (HttpURLConnection) u.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                LengthOfFile = conn.getContentLength();
                StringBuilder buffer = new StringBuilder();
                String line="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                    count = LengthOfFile - buffer.length();
                    publishProgress(String.valueOf(count));
                }

                JSON_STRING_toll = buffer.toString();

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return strings[0];

        }
    }

    public void covidInfo(){
        new HTTPGetJsonFileCovidInfo().execute("https://api.rootnet.in/covid19-in/stats/latest");
    }

    class HTTPGetJsonFileCovidInfo extends AsyncTask<String, String,String> {

        int LengthOfFile;
        int count;


        @Override
        protected void onPreExecute() {
            //fragmentTest.progressBar.setVisibility(View.VISIBLE);
            Log.e("testing","httpcovidinfo");
            JSON_STRING_COVID = "";
        }

        @Override
        protected void onPostExecute(String s) {


            //Toast.makeText(mContext, JSON_STRING, Toast.LENGTH_LONG).show();
            //fragmentTest.progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(JSON_STRING_COVID)) {
                Log.e("testing","http_done_covidinfo");
                //fetch_db_toll_free_no();
                fetch_db_covid_info();

            }
            else {
                Toast.makeText(context, "Couldn't load new file", Toast.LENGTH_SHORT).show();
                Log.e("testing","http_fail_covidinfo");
            }



        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            // fragmentTest.progressBar.setIndeterminate(false);

            //fragmentTest.updateProgress(10);
        }

        @Override
        protected String doInBackground(String... strings) {
            reader=null;
            conn = null;
            try {

                //Prepare the url and connectin
                URL u = new URL(strings[0]);
                conn = (HttpURLConnection) u.openConnection();
                conn.connect();
                InputStream stream = conn.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                LengthOfFile = conn.getContentLength();
                StringBuilder buffer = new StringBuilder();
                String line="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                    count = LengthOfFile - buffer.length();
                    publishProgress(String.valueOf(count));
                }

                JSON_STRING_COVID = buffer.toString();

                return buffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return strings[0];

        }
    }




}
