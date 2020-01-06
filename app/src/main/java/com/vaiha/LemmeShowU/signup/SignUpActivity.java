package com.vaiha.LemmeShowU.signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.RequestParams;
import com.vaiha.LemmeShowU.R;
import com.vaiha.LemmeShowU.SupportClass;
import com.vaiha.LemmeShowU.Util;
import com.vaiha.LemmeShowU.socketConnection.HomePage;
import com.vaiha.LemmeShowU.utilities.Api;
import com.vaiha.LemmeShowU.utilities.editText.SIEditText;

import org.json.JSONObject;

/**
 * Created by vaiha on 21/11/16.
 */

public class SignUpActivity extends Activity {

    SIEditText nameText, emailText, passwordText;
    String name, email, password;
    Button signup;
    TextView cancel;
    RequestQueue queue;
    Util utility;
    SharedPreferences sharedpreference;
    boolean isInternetPresent;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        nameText = (SIEditText) findViewById(R.id.input_name);
        emailText = (SIEditText) findViewById(R.id.input_email);
        passwordText = (SIEditText) findViewById(R.id.input_password);
        signup = (Button) findViewById(R.id.btn_signup);
        cancel = (TextView) findViewById(R.id.signUpCancel);
        utility = new Util(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameText.getText().toString();
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                if (Util.validate(name, email, password)) {
                    if (isInternetPresent = utility.isConnectingToInternet()) {
                        signUp();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Check Your Internet Connection...!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, WelcomeActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    public void signUp() {
     /*   name = this.nameText.getText().toString();
        email = this.emailText.getText().toString();
        password = this.passwordText.getText().toString();
*/
        final ProgressDialog pDialog;
        pDialog = new ProgressDialog(SignUpActivity.this);
        pDialog.setMessage("loading");
        pDialog.show();
        RequestParams param = new RequestParams();
        param.put("user_name", name);
        param.put("mail", email);
        param.put("password", password);
        String api = Api.signup + param;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(api, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e("Sign-Res", "" + response);
                        pDialog.hide();

                        if (response != null) {

                            try {
                                String status = response.getString("status");

                                if (status.equals("success")) {
                                    //String user_id = response.getString("user_id");
                                    sharedpreference = getApplicationContext().getSharedPreferences(SupportClass.MY_PREFS_NAME, MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreference.edit();
                                    editor.putString("logged", "yes");
                                    //editor.putString("user_id",user_id);
                                    editor.putString("user_name", name);
                                    editor.putString("email", email);
                                    editor.apply();
                                    Toast.makeText(SignUpActivity.this, "Suuccessfully signup..!", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUpActivity.this, HomePage.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed Try Again..!", Toast.LENGTH_SHORT).show();
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
                pDialog.hide();
            }
        });
        jsonObjReq.setShouldCache(false);
        queue = Volley.newRequestQueue(this);
        queue.add(jsonObjReq);
    }

    @Override
    public void onBackPressed() {

        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
