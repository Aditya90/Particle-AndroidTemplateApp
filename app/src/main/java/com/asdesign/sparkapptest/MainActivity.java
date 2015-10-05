package com.asdesign.sparkapptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.content.Intent;

import java.io.IOException;

import io.particle.android.sdk.cloud.SparkCloud;
import io.particle.android.sdk.cloud.SparkCloudException;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

public class MainActivity extends AppCompatActivity {

    // Create a facade for the particle photon
    ParticlePhotonFacade particlePhotonFacade;

    // String for LogCat documentation
    private final static String TAG = "SPARK_TEST_LOG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "Call onCreate()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart(){
        super.onStart();

        // Attempt to make a cloud connection on starting the application
        //particlePhotonFacade.AttemptParticleLogin(findViewById(R.id.mainView),
        //        this, "aditya.sreekumar@gmail.com", "12345");
        Log.i(TAG, "Call onStart()");
    }

    public void buttonLogin_Handler(View v){
        final EditText emailEditText = (EditText)findViewById(R.id.emailInput);
        final EditText passEditText = (EditText)findViewById(R.id.passwordInput);

        attemptLogin(findViewById(R.id.mainView), emailEditText.getText().toString(), passEditText.getText().toString());
    }

    /**
     * @brief This function attempts to login with the user name and password passed in as arguments
     * @param myView specifies the view from which this is being called
     * @param emailInput specifies the user name to try logging in with
     * @param passInput specifies the password to attempt logging in with
     */
    private void attemptLogin(View myView, final String emailInput, final String passInput){

        Async.executeAsync(SparkCloud.get(myView.getContext()), new Async.ApiWork<SparkCloud, Void>() {

            public Void callApi(SparkCloud sparkCloud) throws SparkCloudException, IOException {
                sparkCloud.logIn(emailInput, passInput);
                return null;
            }

            @Override
            public void onSuccess(Void aVoid) {
                Intent loggedInIntent = new Intent(MainActivity.this, LoggedInActivity.class);

                Log.i(TAG, "Call onSuccess()");

                Toaster.l(MainActivity.this, "Logged in");

                // Switch to new screen to list all connected devices
                startActivity(loggedInIntent);

            }

            @Override
            public void onFailure(SparkCloudException e) {
                //Log.e("LOGIN_FAILURE", e);
                Log.i(TAG, "Call onFailure()");
                Log.i(TAG, emailInput);
                Log.i(TAG, passInput);

                Toaster.l(MainActivity.this, "Wrong credentials or no internet connectivity, please try again");
            }
        });
    }
}
