package com.kaur0183algonquincollege.doorsopenottawa;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kaur0183algonquincollege.doorsopenottawa.model.Building;
import com.kaur0183algonquincollege.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.List;

/**
 *  In this Class I am creating a menu about dialog Use an Intent to Inflate Detail Activity and pass data of buildings using intent
 *  @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */


public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    // URL to my RESTful API Service hosted on my Bluemix account.
    private static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";


    private ProgressBar pb;
    private List<MyTask> tasks;
    private ListView listview;
    private List<Building> buildingList;
    private static final String ABOUT_DIALOG_TAG = "About";
    private AboutDialogFragment mAboutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAboutDialog = new AboutDialogFragment();

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
        listview=getListView();
        listview.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            mAboutDialog.show(getFragmentManager(), ABOUT_DIALOG_TAG);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building building = buildingList.get(position);
        Intent intent = new Intent( getApplicationContext(), DetailActivity.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.putExtra( "buildingname", building.getName() );
        intent.putExtra( "buildingaddress", building.getAddress());
        intent.putExtra( "buildingdescription", building.getDescription() );

      //  Toast.makeText(MainActivity.this, building.getDescription(), Toast.LENGTH_LONG).show();

        intent.putExtra( "buildingopenhrs", building.getOpenhours());
        startActivity( intent );


    }


    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    protected void updateDisplay() {
        //Use BuildingAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    private class MyTask extends AsyncTask<String, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            buildingList = BuildingJSONParser.parseFeed(content);

            return buildingList;
        }

        @Override
        protected void onPostExecute(List<Building> result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = result;
            updateDisplay();
        }
    }


}
