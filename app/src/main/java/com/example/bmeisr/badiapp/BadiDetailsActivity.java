package com.example.bmeisr.badiapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class BadiDetailsActivity extends AppCompatActivity {


    private static String TAG = "BadiInfo";
    private String badiId;
    private String name;
    private ProgressDialog mDialog;
    private Button btn_wether;
    private String lat;
    private String lon;
    private Button btn_map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badi_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        //Hier holen wir die Zusatzinformationen des Inents
        badiId = intent.getStringExtra("badi"); //Badi Nummer
        name = intent.getStringExtra("name"); //Badi Name
        lat = intent.getStringExtra("latitude");
        lon = intent.getStringExtra("longitude");

        getSupportActionBar().setTitle("Beckentemperatur");
        //Jetzt holen wir den TextView
        TextView text = (TextView) findViewById(R.id.badiinfos);
        //und setzen setzen als Text den Namen der Badi
        text.setText(name);
        //Evtl. ist der Dialog nicht sichtbar, weil die Daten schnell geladen sind
        //aber hier ziegen wir dem Benutzer den Ladedialog an.
        mDialog = ProgressDialog.show(this, "Lade Badi-Infos", "Bitte warten...");
        //Danach wollen wir die Badidaten von der Webseite wiewarm.ch holen und verarbeiten:
        getBadiTemp("http://www.wiewarm.ch/api/v1/bad.json/" + badiId);
        init();
        initMap();

    }

    // Erhält die JswnDateien aus der URL und füllt diese in die Liste
    private void getBadiTemp(String url) {
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
            protected String doInBackground(String[] badi) {
                //In der variable msg soll die Antwort der Seite wiewarm.ch gespeichert werden.
                String msg = "";
                try {
                    URL url = new URL(badi[0]);
                    //Hier bauen wir die Verbindung auf:
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //Lesen des Antwortcodes der Webseite:
                    int code = conn.getResponseCode();
                    //Nun können wir den Lade Dialog wieder ausblenden (die Daten sind ja gelesen)
                    mDialog.dismiss();
                    //Hier lesen wir die Nachricht der Webseite wiewarm.ch für Badi XY:

                    msg = IOUtils.toString(conn.getInputStream());
                    //und Loggen den Statuscode in der Konsole:
                    Log.i(TAG, Integer.toString(code));
                } catch (Exception e) {
                    Log.v(TAG, e.toString());
                }
                return msg;
            }public void onPostExecute(String result) {
                //In result werden zurückgelieferten Daten der Methode doInBackground (return msg;) übergeben.
                //Hier ist also unser Resultat der Seite z.B. http://www.wiewarm.ch/api/v1/bad.json/55
                //In einem Browser IE, Chrome usw. sieht man schön das Resulat als JSON formatiert.
                //JSON Daten können wir aber nicht direkt ausgeben, also müssen wir sie umformatieren.
                try {
                    //Zum Verarbeiten bauen wir die Methode parseBadiTemp und speichern das Resulat in einer Liste.
                    List<String> badiInfos = parseBadiTemp(result);
                    //Jetzt müssen wir nur noch alle Elemente der Liste badidetails hinzufügen.
                    //Dazu holen wir die ListView badidetails vom GUI
                    ListView badidetails = (ListView) findViewById(R.id.badidetails);
                    //und befüllen unser ArrayAdapter den wir am Anfang definiert haben (braucht es zum befüllen
                  //  eines ListViews)
                    temps.addAll(badiInfos);
                    //Mit folgender Zeile fügen wir den befüllten ArrayAdapter der ListView hinzu:
                    badidetails.setAdapter(temps);
                } catch (JSONException e) {
                    error();
                    Log.v(TAG, e.toString());
                }
            }private List parseBadiTemp(String jonString)throws JSONException{
                {
                    //Wie bereits erwähnt können JSON Daten nicht direkt einem ListView übergeben werden.
                    //Darum parsen ("lesen") wir die JSON Daten und bauen eine ArrayListe, die kompatibel
                    //mit unserem ListView ist.
                    ArrayList<String> resultList = new ArrayList<String>();
                    JSONObject jsonObj = jsonObj = new JSONObject(jonString);
                    JSONObject becken = jsonObj.getJSONObject("becken");
                    //Das ist unser Pointer um aus den JSON Daten alle Datensätze herauszulesen
                    Iterator keys = becken.keys();
                    //Hier holen wir Element für Element aus dem JSON Stream:
                    //Was wo drin steckt, definiert die API der Datenquelle.
                    //Für wiewarm.ch muss man es wie folgt machen:
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        JSONObject subObj = becken.getJSONObject(key);
                        //Wenn man die Antwort der Webseite anschaut, steckt im Element "beckenname",
                        //der Name des Schwimmbeckens
                        String name = subObj.getString("beckenname");
                        //und unter temp ist die Temperatur angegeben
                        String temp = subObj.getString("temp");
                        //Sobald wir die Daten haben, fügen wir sie unserer Liste hinzu:
                        resultList.add(name + ": " + temp + " Grad Celsius");
                    }
                    return resultList;
                }
            }

        }.execute(url);

        }

    // Knopf um zum WeatherActivity zu gelangen
        public void init(){
            btn_wether = (Button)findViewById(R.id.btn_wether);
            btn_wether.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent toy = new Intent(BadiDetailsActivity.this,WetherActivity.class);
                    toy.putExtra("ort", name );

                    startActivity(toy);

                }
            });

        }

        // Knopf um Zur Karte zu gelangen
        public void initMap(){
            btn_map = (Button)findViewById(R.id.btn_map);
            btn_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent toy = new Intent(BadiDetailsActivity.this,MapsActivity.class);
                    toy.putExtra("isMyLocation","true");
                    toy.putExtra("latitude",lat);
                    toy.putExtra("longitude",lon);
                    startActivity(toy);

                }
            });


        }

        // ErrorMeödung wenn kein Internet vorhanden ist.
    public void error(){
        AlertDialog alertDialog = new AlertDialog.Builder(BadiDetailsActivity.this).create();
        alertDialog.setTitle("Alert Dialog");
        alertDialog.setMessage("Es konnte keine Internetverbindung hergestellt werden\nBitte überprüfen Sie Ihre Internetverbindung");
        //alertDialog.setIcon(R.drawable.welcome);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Dur wurdest zurückgeleitet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BadiDetailsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        alertDialog.show();
    }


}




