package com.someapp.vishnu.myappwhatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText editText;
    TextView textView;
    String convenienceString = "";

    public void findWeather(View view) {

        DownloadTask task = new DownloadTask();

        try {

            convenienceString = editText.getText().toString();

            String encodedName = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            task.execute("http://openweathermap.org/data/2.5/weather?q=" +
                    encodedName +
                        "&appid=b6907d289e10d714a6e88b30761fae22").get();

            InputMethodManager manager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            editText.setText("");

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        // This method should not touch UI
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection = null;

            try{

                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1) {
                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Weather couldn't be found", Toast.LENGTH_SHORT).show();

                return null;

            }

        }

        // How we can write code after doInBackground, to possibly touch UI
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

//            Log.i("JSON", s);
            try {
                JSONObject jsonObject = new JSONObject(s);

                // Key in getString(...) is case-sensitive...
                // Use a JSON Formatter to read the JSON data more easily (easier for us)
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                String weatherText = "";

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    Log.i("main", jsonPart.getString("main"));
                    Log.i("description", jsonPart.getString("description"));

                    weatherText += jsonPart.getString("main") + ": "
                            + jsonPart.getString("description");
                    weatherText += '\n';

                }

                if(!weatherText.equals("")) {
                    String setString = "In " + convenienceString
                            + " the weather is: " + '\n' + weatherText;
                    textView.setText(setString);
                } else {

                    Toast.makeText(getApplicationContext(),
                            "Weather couldn't be found", Toast.LENGTH_SHORT).show();

                }

            } catch (Exception e) { // More specifically, a JSONException
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Weather couldn't be found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textViewWeather);

    }
}
