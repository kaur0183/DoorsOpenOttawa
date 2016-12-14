package com.kaur0183algonquincollege.doorsopenottawa;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * In this class we are posting new building to data using request package
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */

public class AddBuilding extends FragmentActivity implements View.OnClickListener {

    private EditText txtName, txtAddress, txtDescription;
    private Button btnPost, btnCancel;
    private String uri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addbuilding);

        txtName = (EditText) findViewById(R.id.buildingName);
        txtAddress = (EditText) findViewById(R.id.buildingAddress);
        txtDescription = (EditText) findViewById(R.id.buildingDescription);
        btnPost = (Button) findViewById(R.id.Submit);
        btnCancel = (Button) findViewById(R.id.Cancel);

        btnPost.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.Submit) {
            RequestPackage pkg = new RequestPackage();
            pkg.setUri(MainActivity.REST_URI);
            pkg.setMethod(HttpMethod.POST);
            pkg.setParam("name", txtName.getText().toString());
            pkg.setParam("address", txtAddress.getText().toString());
            pkg.setParam("description", txtDescription.getText().toString());
            pkg.setParam("image", "Pic.jpg");
            DoTask addTask = new DoTask();
            addTask.execute(pkg);
        } else {
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
        }
    }


    private class DoTask extends AsyncTask<RequestPackage, String, String> {
        @Override
        protected String doInBackground(RequestPackage... requestPackages) {
            String content = HttpManager.getData(requestPackages[0], "kaur0183", "password");

            return content;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                Toast.makeText(getApplicationContext(), "Recorded added successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Record Not added", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
