package com.example.anku.top10downloads;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ListView listXml;
    private Button btnParse;
    private String mFileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        listXml = (ListView) findViewById(R.id.listXml);
        btnParse = (Button) findViewById(R.id.btnParse);
        DownloadData downloadData = new DownloadData();
        String xmlURL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml";
        downloadData.execute(xmlURL);
    }

    public void parseXml(View view) {
        ParseApplications parseApplications = new ParseApplications(mFileContents);
        parseApplications.process();

        ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                MainActivity.this, R.layout.list_item, parseApplications.getApplications());
        listXml.setAdapter(arrayAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class DownloadData extends AsyncTask<String, Void, String> {
        private final String CLASS_NAME = getClass().getSimpleName();

        @Override
        protected String doInBackground(String... params) {
            mFileContents = downloadXMLData(params[0]);
            if (mFileContents == null)
                Log.d(CLASS_NAME, "Error Downloading data");
            return mFileContents;
        }

        private String downloadXMLData(String urlPath) {
            StringBuilder xmlContent = new StringBuilder();
            try {
                BufferedReader reader = null;
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    int responseCode = connection.getResponseCode();
                    Log.d(CLASS_NAME, "Response code : " + responseCode);
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String temp;
                    while ((temp = reader.readLine()) != null)
                        xmlContent.append(temp);
                    return xmlContent.toString();
                } finally {
                    if (reader != null)
                        reader.close();
                }
            } catch (IOException e) {
                Log.d(CLASS_NAME, "Error reading data : " + e.getMessage());
            } catch (SecurityException e) {
                Log.d(CLASS_NAME, "Security Error : " + e.getMessage());
            }
            return null;
        }
    }

}
