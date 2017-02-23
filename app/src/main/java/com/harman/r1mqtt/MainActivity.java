package com.harman.r1mqtt;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttToken;

import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    MqttAndroidClient mqttAndroidClient;
    MqttConnectOptions mqttConnectOptions;
    String clientId = MqttClient.generateClientId();

    final String serverURI = "tcp://test.mosquitto.org:1883";
    final String publishTopic = "RemoteOperations";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the toolbar
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        serverPrereq();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_Info){
            Log.i(TAG,"Menu Item : " + R.string.action_Info + " Selected");

            Toast.makeText(this,"MQTT Client ID: " + mqttAndroidClient.getClientId() + "/n wwuuwuw new line", Toast.LENGTH_LONG).show();

            // add this info to ?
            mqttConnectOptions.getDebug();

            return true;
        } else if (id == R.id.action_restartClient){
            Log.i(TAG,"Menu Item : " + R.string.action_restartClient + " Selected");

            // Gracefully shutdown client.
            shutdownClient(mqttAndroidClient);

            // go through prereqs and reconnect client.
            serverPrereq();

            return true;
        } else if (id == R.id.action_Mystery) {
            Log.i(TAG, "Menu Item : " + R.string.action_Mystery + " Selected");

            Toast.makeText(this, "Meow Meow Meow Meow", Toast.LENGTH_SHORT).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v;
            int pageNum = getArguments().getInt(ARG_SECTION_NUMBER);

            // This is where we can separate different layouts based on ARG_SECTION NUMBER
            if (pageNum == 1) {
                v = inflater.inflate(R.layout.fragment_remote_operations, container, false);

                final ImageButton unlockButton, lockButton, remoteStart, remoteStop, lightsAndHorns;

                // Map All Buttons
                unlockButton = (ImageButton) v.findViewById(R.id.btnUnlock);
                lockButton = (ImageButton) v.findViewById(R.id.btnLock);
                remoteStart = (ImageButton) v.findViewById(R.id.btnRemoteStart);
                remoteStop = (ImageButton) v.findViewById(R.id.btnRemoteStop);
                lightsAndHorns = (ImageButton) v.findViewById(R.id.btnHornAndLights);

                unlockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG,"Unlock Button Pressed.");
                        ((MainActivity)getContext()).publishMessage("Unlock");
                    }
                });

                lockButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG,"Lock Button Pressed.");
                        ((MainActivity)getContext()).publishMessage("Lock");
                    }
                });

                remoteStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG,"Remote Start Button Pressed.");
                        ((MainActivity)getContext()).publishMessage("RemoteStart");
                    }
                });

                remoteStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG,"Remote Stop Button Pressed.");
                        ((MainActivity)getContext()).publishMessage("RemoteStop");
                    }
                });

                lightsAndHorns.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG,"Lights and Horns Button Pressed.");
                        ((MainActivity)getContext()).publishMessage("LightsAndHorn");
                    }
                });

            } else {
                // Shouldn't ever get here :D

                v = inflater.inflate(null, container, false);

            }

            return v;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 1 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
            }
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Check if client is connected.
        if (mqttAndroidClient!=null){
            shutdownClient(mqttAndroidClient);
        }else{
            // Client wasnt connected.. Do we need to destroy or save state of something ?
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Checks to see if client is somehow still connected.. if so, destroy and create new clean session.
        if (mqttAndroidClient!=null){
            Log.i(TAG,"Client Already Connected..");

            // Shutdown client and init again.
        }else{
            serverPrereq();
        }

    }

    /**
     *  Prechecks to run such as testing for network connectivity / enabling permissions/ etc..
     */
    private void serverPrereq(){
        Log.i(TAG,"serverPrereq");
        // check permissions / other things here?

        // Check if internet connection is established?
        if (!isNetworkAvailable()){

            // Set notification and close application while notifying user to enable internet connection.
            Toast.makeText(this,"Device has no valid internet connection.. Please connect and start application again", Toast.LENGTH_LONG).show();
            this.finishAffinity();
        } else {
            initClient();
        }

    }

    /**
     * Checks to see if valid network connection is available through connection Manager.
     * @return boolean
     */
    private boolean isNetworkAvailable() {
        Log.i(TAG,"isNetworkAvailable");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            Log.i(TAG,"isNetworkAvailable : True");
            return true;
        }

        Log.i(TAG,"isNetworkAvailable : False");
        return false;
    }

    private void initClient(){
        Log.i(TAG,"initClient");

        // Create new MQTT Android Client
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverURI,
                clientId);

        // Create new MQTT Connect Options.
        mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.getConnectionTimeout();

        // Establish connection to the broker.
        clientConnect(mqttAndroidClient,mqttConnectOptions);
    }

    /**
     * Initialize connection to MQTT Server here..
     **/
    private void clientConnect(MqttAndroidClient client,MqttConnectOptions options) {
        Log.i(TAG,"clientConnect");

        try {

            IMqttToken token;

            token = client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.i(TAG,"Successfully connected to " + serverURI);
                    Toast.makeText(getApplicationContext(),"Connected to : " + serverURI, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(TAG,"Failed to connect to " + serverURI);
                }
            });

            Toast.makeText(this,"Connecting to : " + serverURI, Toast.LENGTH_LONG).show();

            token.waitForCompletion(5);

        } catch (MqttException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Terminate the client and stop any ongoing tasks
     **/
    private void shutdownClient(MqttAndroidClient client) {
        Log.i(TAG,"shutdownClient");

        try {
            IMqttToken token = client.disconnect();

            Log.i(TAG,"Shutdown Complete.");
            // Need to wait for success response here?
        } catch (MqttException e){
            Log.e(TAG,"Exception in shutdownClient");
            e.printStackTrace();
        }

    }

    /**
     *  Function to publish to specific topic with certain message
     */
    private void publishMessage(String input){
        Log.i(TAG,"publishMessage");

        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(input.getBytes());
            IMqttDeliveryToken token = mqttAndroidClient.publish(publishTopic,message);
            //token.waitForCompletion();

            Log.i(TAG,"Message Published");


        }catch (MqttException e){
            Log.e(TAG,"Error Publishing " + e.getMessage());
            e.printStackTrace();
        }

    }
}