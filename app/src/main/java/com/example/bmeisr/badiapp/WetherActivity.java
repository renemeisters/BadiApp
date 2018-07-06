package com.example.bmeisr.badiapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WetherActivity extends AppCompatActivity {


    private TextView txt;
    private ProgressDialog mDialog;
    private static String TAG = "WeatherInfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wether);
        Intent intent = getIntent();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Aussentemperatur");

        }
        //Hier holen wir die Zusatzinformationen des Inents
        String ort = intent.getStringExtra("ort"); //Badi Nummer
        txt = (TextView)findViewById(R.id.txt_place);
        ort = ort.split(" -")[0];
        txt.setText(ort);


        //Evtl. ist der Dialog nicht sichtbar, weil die Daten schnell geladen sind
        //aber hier ziegen wir dem Benutzer den Ladedialog an.
        mDialog = ProgressDialog.show(this, "Lade Wetter Wetter-Infos", "Bitte warten...");
        //Danach wollen wir die Badidaten von der Webseite wiewarm.ch holen und verarbeiten:
        getWeatherTemp("http://api.openweathermap.org/data/2.5/weather?q="+ort+"&units=metric&appid=5e9af9fd3560ebf5a20be698a59c0051");
        //Dies ist ein Test
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void getWeatherTemp(String url) {
        //Den ArrayAdapter wollen wir später verwenden um die Temperaturen zu speichern
        //angezeigt sollen sie im Format der simple_list_item_1 werden (einem Standard Android Element)
        final ArrayAdapter temps = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //Android verlangt, dass die Datenverarbeitung von den GUI Prozessen getrennt wird.
        //Darum starten wir hier einen asynchronen Task (quasi einen Hintergrundprozess).
        new AsyncTask<String, String, String>() {
            //Der AsyncTask verlangt die implementation der Methode doInBackground.
            //Nachdem doInBackground ausgeführt wurde, startet automatisch die Methode onPostExecute
            //mit den Daten die man in der Metohde doInBackground mit return zurückgegeben hat (hier msg).
            @Override
            protected String doInBackground(String[] weather) {
                //In der variable msg soll die Antwort der Seite openweather.org gespeichert werden.
                String msg = "";
                try {
                    URL url = new URL(weather[0]);
                    //Hier bauen wir die Verbindung auf:
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //Lesen des Antwortcodes der Webseite:
                    int code = conn.getResponseCode();
                    //Nun können wir den Lade Dialog wieder ausblenden (die Daten sind ja gelesen)
                    mDialog.dismiss();
                    //Hier lesen wir die Nachricht der Webseite openweather.orh für den Ort XY:

                    msg = IOUtils.toString(conn.getInputStream());
                    //und Loggen den Statuscode in der Konsole:
                    Log.i(TAG, Integer.toString(code));
                } catch (Exception e) {
                    Log.v(TAG, e.toString());
                    error();
                }
                return msg;
            }public void onPostExecute(String result) {
                //In result werden zurückgelieferten Daten der Methode doInBackground (return msg;) übergeben.
                //Hier ist also unser Resultat der Seite z.B. http://www.wiewarm.ch/api/v1/bad.json/55
                //In einem Browser IE, Chrome usw. sieht man schön das Resulat als JSON formatiert.
                //JSON Daten können wir aber nicht direkt ausgeben, also müssen wir sie umformatieren.
                try {

                    //Zum Verarbeiten bauen wir die Methode parseBadiTemp und speichern das Resulat in einer Liste.
                    List<String> weatherInfos = parseBadiTemp(result);
                    //Jetzt müssen wir nur noch alle Elemente der Liste badidetails hinzufügen.
                    //Dazu holen wir die ListView weatherdetails vom GUI
                    ListView weatherdetails = (ListView) findViewById(R.id.wetterdetails);
                    //und befüllen unser ArrayAdapter den wir am Anfang definiert haben (braucht es zum befüllen
                    //  eines ListViews)
                    temps.addAll(weatherInfos);
                    //Mit folgender Zeile fügen wir den befüllten ArrayAdapter der ListView hinzu:
                    weatherdetails.setAdapter(temps);
                } catch (JSONException e) {
                    Log.v(TAG, e.toString());
                    error();
                }
            }private List parseBadiTemp(String jonString)throws JSONException{
                {
                    //Wie bereits erwähnt können JSON Daten nicht direkt einem ListView übergeben werden.
                    //Darum parsen ("lesen") wir die JSON Daten und bauen eine ArrayListe, die kompatibel
                    //mit unserem ListView ist.
                    ArrayList<String> resultList = new ArrayList<String>();
                    JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                    JSONObject main = jsonObj.getJSONObject("main");
                    JSONObject wind = jsonObj.getJSONObject("wind");

                    //Das ist unser Pointer um aus den JSON Daten alle Datensätze herauszulesen

                    //Hier holen wir Element für Element aus dem JSON Stream:
                    //Was wo drin steckt, definiert die API der Datenquelle.
                    //Für wiewarm.ch muss man es wie folgt machen:


                        String name = main.getString("temp");
                        //und unter temp ist die Temperatur angegeben
                        String temp = main.getString("temp_max");
                    String min = main.getString("temp_min");
                    String hum = main.getString("humidity");
                    String speed =wind.getString("speed");
                        //Sobald wir die Daten haben, fügen wir sie unserer Liste hinzu:
                        resultList.add("Aktuelle Temperatur: "+name + " Grad Celsius");
                        resultList.add("Höchsttemperatur: " + temp + " Grad Celsius");
                        resultList.add("Tiefsttemperatur: " + min + " Grad Celsius" );
                    resultList.add("Luftfeuchtigkeit: " + hum + " %" );
                    resultList.add("Windgeschwindigkeit: " + speed + " km/h" );

                    return resultList;
                }
            }

        }.execute(url);


    }
    public void error(){
        AlertDialog alertDialog = new AlertDialog.Builder(WetherActivity.this).create();
        alertDialog.setTitle("Alert Dialog");
        alertDialog.setMessage("Es konnte keine Internetverbindung hergestellt werden\nBitte überprüfen Sie Ihre Internetverbindung");
        //alertDialog.setIcon(R.drawable.welcome);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WetherActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        alertDialog.show();
}

}
