package com.xelacm.sos.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.xelacm.sos.R;
import com.xelacm.sos.adapters.GpsTracker;
import com.xelacm.sos.adapters.MenuAdapter;
import com.xelacm.sos.adapters.RecyclerItemClickListener;
import com.xelacm.sos.models.MenuModel;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity  {

    RecyclerView recyclerview;
    RequestQueue queue;
    private GpsTracker gpsTracker;
    private FusedLocationProviderClient client;

    String URL = "http://header.safaricombeats.co.ke/dxl/";
    private static final String TAG = "Debug";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview = findViewById(R.id.rec);






        MenuModel[] myListData = new MenuModel[]{
                new MenuModel("Airtime Top up", android.R.drawable.ic_menu_call),
                new MenuModel("Data Calls SMS & Airtime", android.R.drawable.ic_menu_call),
                new MenuModel("Tunukiwa Offers", android.R.drawable.ic_input_add),
                new MenuModel("Ask Zuri", android.R.drawable.ic_dialog_dialer),
                new MenuModel("Send Money", android.R.drawable.ic_dialog_alert),
                new MenuModel("Lipa Na M-PESA", android.R.drawable.ic_dialog_map),
                new MenuModel("Send Money", android.R.drawable.ic_dialog_alert),
                new MenuModel("SOS CALL", android.R.drawable.ic_menu_call),

        };


        MenuAdapter adapter = new MenuAdapter(myListData);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerview.setAdapter(adapter);
        recyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(MainActivity.this, recyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 0) {
                            GetMsisdn();
                            goToUrl("www.google.com");

                        } else {
                            Toast.makeText(MainActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    public String GetMsisdn() {
        final String[] mobileNo = {null};
        getLocation();
        queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               if(!response.isEmpty()) {
                 //  Log.d("##MSISDN", response.toString());
                   JSONObject obj = null;
                   try {
                       obj = new JSONObject(response);
                       Log.d("##MSISDN", obj.toString());
                      String  msisdn = obj.getJSONObject("ServiceResponse").getJSONObject("ResponseBody").getJSONObject("Response").getString("Msisdn");
                       Toast.makeText(MainActivity.this, msisdn, Toast.LENGTH_LONG).show();
                       mobileNo[0] = msisdn;
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }

               }else{
                   Toast.makeText(MainActivity.this, "Error getting MSISDN. Try again", Toast.LENGTH_LONG).show();
               }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });
        queue.add(request);
        return mobileNo[0];
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(MainActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();

            Log.d("########Location","Lat: "+latitude+"   Lng:"+longitude);
        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void goToUrl (String url) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

}