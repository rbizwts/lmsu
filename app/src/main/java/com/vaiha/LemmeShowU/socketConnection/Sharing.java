package com.vaiha.LemmeShowU.socketConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.RequestParams;
import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.SupportClass;
import com.vaiha.LemmeShowU.Util;
import com.vaiha.LemmeShowU.Waiting;
import com.vaiha.LemmeShowU.utilities.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;


public class Sharing extends ActionBarActivity {
    Button send;
    int id = 0;
    EditText textS;
    ImageView image;
    RequestQueue queue;
    ListView listView;
    ArrayList<String> user_id = new ArrayList<String>();
    ArrayList<String> user_name = new ArrayList<String>();
    ArrayList<String> ip_address = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    String partner_id, cur_user_id;
    Timer t = new Timer();
    boolean isInternetPresent;
    Util util;
    SharedPreferences sharedpreference;
    private TextView tvServerMessage;
    private String Client_Name = "Bobby";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharing);
        final WifiManager myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        listView = (ListView) findViewById(R.id.list);
        util = new Util(getApplicationContext());

        sharedpreference = getApplicationContext().getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
        cur_user_id = sharedpreference.getString("user_id", "");

        getlist();
        adapter = new ArrayAdapter<String>(this,
                R.layout.user_list_layout, android.R.id.text1, user_name);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ip = ip_address.get(position);
                partner_id = user_id.get(position);
                SupportClass.partner_ip = ip;
                if (SupportClass.partner_ip.equals("")) {
                    Toast.makeText(Sharing.this, "Your Friend is Offline", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (SupportClass.getip_timer == null) {
                   /* t.scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run() {
                            if (isInternetPresent = util.isConnectingToInternet()) {
                                getip();
                                //Called each time when 1000 milliseconds (1 second) (the period parameter)
                            }
                        }
                    }, 0, 1000);*/
                }
                //SupportClass.getip_timer = t;
                give_request(partner_id);
                /*Toast.makeText(Sharing.this, ""+ip+"of"+""+user_id.get(position), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Sharing.this, MainActivity.class);
                startActivity(i);
                finish();*/
               /* ClientAsyncTask clientAST = new ClientAsyncTask();

                clientAST.execute(new String[]{
                        ip, SERVER_PORT,
                        Client_Name + " : " + textS.getText().toString()});
                Toast.makeText(Sharing.this, ""+ip+"of"+""+user_id.get(position), Toast.LENGTH_SHORT).show();*/
            }
        });


    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    private void getlist() {
        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(Sharing.this);
        pDialog.setMessage("loading");
        pDialog.show();
        RequestParams param = new RequestParams();
        param.put("user_id", cur_user_id);
        String api = Api.user_list + param;

        JsonArrayRequest req = new JsonArrayRequest(api,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        pDialog.hide();
                        user_id.clear();
                        ip_address.clear();
                        try {

                           // Log.e("Share-res", "" + response);
                            int num = response.length();
                            if (num > 0) {
                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject jsonObject = (JSONObject) response.get(i);
                                    String id = jsonObject.getString("user_id");
                                    String ip = jsonObject.getString("ip_address");
                                    String name = jsonObject.getString("user_name");
                                    user_id.add(i, id);
                                    ip_address.add(i, ip);
                                    user_name.add(i, name);
                                }
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getApplicationContext(), "No Users Online at this Time", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                pDialog.hide();
            }
        });

        req.setShouldCache(false);
        queue = Volley.newRequestQueue(this);
        queue.add(req);
    }

    private void give_request(final String partner_id) {
        RequestParams param = new RequestParams();
        param.put("partner_id", partner_id);
        param.put("user_id", cur_user_id);
        String api = Api.give_request + param;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {

                            try {
                                String status = response.getString("status");
                                if (status.equals("success")) {
                                    HomePage.ip_update_timer.cancel();
                                    Intent i = new Intent(Sharing.this, Waiting.class);
                                    i.putExtra("partner_id", partner_id);
                                    startActivity(i);
                                    finish();
                                }
                            } catch (Exception ex) {
                                Log.e("Exeption", "" + ex);
                            }
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Errrror: ", "" + error.getMessage());
            }
        });
        jsonObjReq.setShouldCache(false);
        queue = Volley.newRequestQueue(this);
        queue.add(jsonObjReq);
    }

}