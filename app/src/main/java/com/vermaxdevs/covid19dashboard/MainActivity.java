package com.vermaxdevs.covid19dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior bottomSheetBehavior;
    TextView tConfirmed, tActive, tRecovered, tDeath, lastUpdated,scrollStateName;
    ListView listView;
    ListView slistView;
    CustomListViewAdapter adapter;
    CustomListViewAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tConfirmed = findViewById(R.id.total_confirmed);
        tActive = findViewById(R.id.total_active);
        tRecovered = findViewById(R.id.total_recovered);
        tDeath = findViewById(R.id.total_death);
        lastUpdated = findViewById(R.id.textView2);
        listView = findViewById(R.id.listView);
        slistView = findViewById(R.id.state_listView);
        scrollStateName = findViewById(R.id.scroll_state_name);
        adapter = new CustomListViewAdapter(MainActivity.this, R.layout.custom_listview);
        sAdapter = new CustomListViewAdapter(MainActivity.this, R.layout.custom_listview);
        View btmSheet = findViewById(R.id.btm_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(btmSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        listView.setAdapter(adapter);

        new FetchData().execute();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                TextView temp = view.findViewById(R.id.state_name);
                sAdapter.clear();
                new fetchCities().execute();
                scrollStateName.setText(temp.getText());
                slistView.setAdapter(sAdapter);
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    slistView.setAdapter(null);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    private String timeAgo(String dataDate) {
        String convTime = null;

        String prefix = "Last Updated ";
        String suffix = "Ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            long dateDiff = nowTime.getTime() - pasTime.getTime();

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day  = TimeUnit.MILLISECONDS.toDays(dateDiff);

            if (second < 60) {
                convTime = prefix + second+" Seconds "+suffix;
            } else if (minute < 60) {
                convTime = prefix + minute+" Minutes "+suffix;
            } else if (hour < 24) {
                convTime = prefix + hour+" Hours "+suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = prefix + (day / 30) + " Years " + suffix;
                } else if (day > 30) {
                    convTime = prefix + (day / 360) + " Months " + suffix;
                } else {
                    convTime = prefix + (day / 7) + " Week " + suffix;
                }
            } else if (day < 7) {
                convTime = prefix + day+" Days "+suffix;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Covid Tracker", e.getMessage());
        }

        return convTime;
    }

    class FetchData extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog =new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            sAdapter.clear();

            try {
                JSONArray jsonArray = new JSONObject(s).getJSONArray("statewise");
                tConfirmed.setText(jsonArray.getJSONObject(0).getString("confirmed"));
                tActive.setText(jsonArray.getJSONObject(0).getString("active"));
                tRecovered.setText(jsonArray.getJSONObject(0).getString("recovered"));
                tDeath.setText(jsonArray.getJSONObject(0).getString("deaths"));
                lastUpdated.setText(timeAgo(jsonArray.getJSONObject(0).getString("lastupdatedtime")));

                int count=1;
                while (count < jsonArray.length()) {
                    States states = new States(jsonArray.getJSONObject(count).getString("state"),
                            jsonArray.getJSONObject(count).getString("confirmed"),
                            jsonArray.getJSONObject(count).getString("active"),
                            jsonArray.getJSONObject(count).getString("recovered"));
                    adapter.add(states);
                    count++;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            return new MakeServiceCall().MakeServiceCall("https://api.covid19india.org/data.json",MakeServiceCall.GET,null);
        }
    }

    class fetchCities extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog =new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            try {
                JSONArray jsonArray = new JSONArray(s);
                int j = 0;
                while (j < jsonArray.length()) {
                    if(jsonArray.getJSONObject(j).getString("state").equalsIgnoreCase(scrollStateName.getText().toString())) {
                        break;
                    }
                    j++;
                }
                JSONArray sArray = new JSONArray(jsonArray.getJSONObject(j).getJSONArray("districtData").toString());
                int i=0;
                while (i < sArray.length()) {
                    States cities = new States(sArray.getJSONObject(i).getString("district"),
                            String.valueOf(sArray.getJSONObject(i).getInt("confirmed")),
                            String.valueOf(sArray.getJSONObject(i).getInt("active")),
                            String.valueOf(sArray.getJSONObject(i).getInt("recovered")));
                        sAdapter.add(cities);
                        i++;
                    }
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected String doInBackground(Void... voids) {
            return new MakeServiceCall().MakeServiceCall("https://api.covid19india.org/v2/state_district_wise.json",MakeServiceCall.GET,null);
        }
    }

    public void bottomSheetInstance(String stateName) {
        scrollStateName.setText(stateName);
        new fetchCities().execute();
        slistView.setAdapter(sAdapter);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
    }

    @Override
    public void onBackPressed() {
        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            super.onBackPressed();
        }
        else {
            slistView.setAdapter(null);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
