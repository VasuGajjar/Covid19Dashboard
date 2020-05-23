package com.vermaxdevs.covid19dashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    TextView tConfiremed, tActive, tRecovered, tDeath;
    ListView listView;
    CustomListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tConfiremed = findViewById(R.id.total_confirmed);
        tActive = findViewById(R.id.total_active);
        tRecovered = findViewById(R.id.total_recovered);
        tDeath = findViewById(R.id.total_death);
        listView = findViewById(R.id.listView);
        adapter = new CustomListViewAdapter(MainActivity.this, R.layout.custom_listview);

        new FetchData().execute();

        listView.setAdapter(adapter);
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
            try {
                JSONArray jsonArray = new JSONObject(s).getJSONArray("statewise");
                tConfiremed.setText(jsonArray.getJSONObject(0).getString("confirmed"));
                tActive.setText(jsonArray.getJSONObject(0).getString("active"));
                tRecovered.setText(jsonArray.getJSONObject(0).getString("recovered"));
                tDeath.setText(jsonArray.getJSONObject(0).getString("deaths"));

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
}
