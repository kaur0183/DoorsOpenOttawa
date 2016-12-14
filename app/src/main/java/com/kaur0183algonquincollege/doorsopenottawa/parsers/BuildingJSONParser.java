package com.kaur0183algonquincollege.doorsopenottawa.parsers;

import android.util.Log;

import com.kaur0183algonquincollege.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * In this class the JSON data of building is parsing
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */
public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray planetArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < planetArray.length(); i++) {

                JSONObject obj = planetArray.getJSONObject(i);
                Building building = new Building();

                building.setBuildingID(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));
                building.setImage(obj.getString("image"));
                building.setAddress(obj.getString("address"));
                building.setDescription(obj.getString("description"));
                List<String> temp = new ArrayList<>();
                JSONArray jsonArray = obj.getJSONArray("open_hours");
                if (jsonArray.length() != 0) {
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject json = (JSONObject) jsonArray.get(j);
                        temp.add(json.getString("date"));
                    }
                }
                building.setOpenhours(temp);
                buildingList.add(building);
            }

            return buildingList;
        } catch (JSONException e) {
            Log.e("TAG", "JsonException", e);
            return null;
        }
    }
}
