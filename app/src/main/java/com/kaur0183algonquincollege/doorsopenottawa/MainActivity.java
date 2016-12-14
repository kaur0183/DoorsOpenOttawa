package com.kaur0183algonquincollege.doorsopenottawa;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.kaur0183algonquincollege.doorsopenottawa.model.Building;
import com.kaur0183algonquincollege.doorsopenottawa.parsers.BuildingJSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * In this Class I am creating a menu about dialog Use an Intent to Inflate Detail Activity and pass data of buildings using intent.
 * Refreshing the data using swipe refresh layout
 * searching the building
 * sorting the buildings by name
 * Adding new building
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */


public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    // URL to my RESTful API Service hosted on my Bluemix account.
    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";


    private ProgressBar pb;
    private List<MyTask> tasks;
    private ListView listview;
    private List<Building> buildingList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Adapter mAdapter;
    private static final String ABOUT_DIALOG_TAG = "About";
    private AboutDialogFragment mAboutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAboutDialog = new AboutDialogFragment();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);


        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();
        if (isOnline()) {
            requestData(REST_URI);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }
        listview = getListView();
        listview.setOnItemClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MyTask task = new MyTask();
                try {
                    RequestPackage pkg = new RequestPackage();
                    pkg.setUri("https://doors-open-ottawa-hurdleg.mybluemix.net/buildings");
                    task.execute(pkg);

                } catch (Exception e) {

                    e.printStackTrace();
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 20);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            mAboutDialog.show(getFragmentManager(), ABOUT_DIALOG_TAG);
            return true;
        }

        if (item.isCheckable()) {
            // leave if the list is null
            if (buildingList == null) {
                return true;
            }
        }

        switch (item.getItemId()) {
            case R.id.action_sort_name_asc:
                Collections.sort(buildingList, new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        Log.i("Buildings", "Sorting Buildings by name (a-z)");
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                break;
            case R.id.add_building:
                Intent intent = new Intent(getApplicationContext(), AddBuilding.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.action_sort_name_dsc:
                Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Building>() {
                    @Override
                    public int compare(Building lhs, Building rhs) {
                        Log.i("Buildings", "Sorting Buildings by name (z-a)");
                        return lhs.getName().compareTo(rhs.getName());
                    }
                }));
                break;

            default:
                return false;
        }
        // remember which sort option the user picked
        ((BuildingAdapter) getListView().getAdapter()).notifyDataSetChanged();
        item.setChecked(true);
        // re-fresh the list to show the sort order
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building building = buildingList.get(position);
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("buildingname", building.getName());
        intent.putExtra("buildingaddress", building.getAddress());
        intent.putExtra("buildingdescription", building.getDescription());

        //  Toast.makeText(MainActivity.this, building.getDescription(), Toast.LENGTH_LONG).show();

        intent.putStringArrayListExtra("buildingopenhrs", (ArrayList<String>) building.getOpenhours());
        startActivity(intent);


    }

    private void requestData(String uri) {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod(HttpMethod.GET);
        getPackage.setUri(uri);
        MyTask task = new MyTask();
        task.execute(getPackage);
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

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {

        ((BuildingAdapter) getListAdapter()).getFilter().filter(s);
        return true;
    }


    private class MyTask extends AsyncTask<RequestPackage, String, List<Building>> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Building> doInBackground(RequestPackage... params) {

            String content = HttpManager.getData(params[0], "kaur0183", "password");
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
