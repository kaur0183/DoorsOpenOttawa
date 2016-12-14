package com.kaur0183algonquincollege.doorsopenottawa;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaur0183algonquincollege.doorsopenottawa.model.Building;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * In this class we are displaying the name , address and showing the image
 *
 * @author Prabhjot kaur (kaur0183@algonquinlive.com)
 */

public class BuildingAdapter extends ArrayAdapter<Building> implements Filterable {
    private Context context;
    private List<Building> buildingList;

    private LruCache<Integer, Bitmap> imageCache;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.buildingList = objects;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize);
    }

    @Override
    public int getCount() {
        return buildingList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display building name in the TextView widget
        Building building = buildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(building.getName());

        //Display building address in the TextView widget
        TextView ad = (TextView) view.findViewById(R.id.address);
        ad.setText(building.getAddress());

        Bitmap bitmap = imageCache.get(building.getBuildingID());
        if (bitmap != null) {
            Log.i("BUILDINGS", building.getName() + "\tbitmap in cache");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(building.getBitmap());
        } else {
            Log.i("Building", building.getName() + "\tfetching bitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }

        return view;

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    ArrayList<Building> tempList = new ArrayList<Building>();

                    // search content in friend list
                    for (Building user : buildingList) {
                        if (user.getName().toLowerCase().startsWith(constraint.toString().toLowerCase().toString().toLowerCase())) {
                            tempList.add(user);
                        }
                    }

                    filterResults.count = tempList.size();
                    filterResults.values = tempList;
                } else {
                    filterResults.count = buildingList.size();
                    filterResults.values = buildingList;
                }

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                buildingList = (ArrayList<Building>) results.values;
                notifyDataSetChanged();

            }
        };
    }


    private class BuildingAndView {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView> {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params) {

            BuildingAndView container = params[0];
            Building building = container.building;

            try {
                String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                building.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (Exception e) {
                System.err.println("IMAGE: " + building.getName());
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(BuildingAndView result) {
            try {
                ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
                image.setImageBitmap(result.bitmap);
//            result.building.setBitmap(result.bitmap);
                imageCache.put(result.building.getBuildingID(), result.bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
