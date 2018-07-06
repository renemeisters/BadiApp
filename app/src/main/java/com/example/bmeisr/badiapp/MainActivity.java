package com.example.bmeisr.badiapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter badiliste;
    /*private final static String AARBERG = "Schwimmbad Aarberg (BE)";
    private final static String ADELBODEN = "Schwimmbad Gruebi Adelboden (BE)";
    private final static String BERN = "Stadtberner Baeder Bern (BE)";*/
    private Button btn_place;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img = (ImageView) findViewById(R.id.badilogo);
         img.setImageResource(R.drawable.badi);
        addBadisToList();
        init();
    }

    private void addBadisToList() {
        ListView badis = (ListView) findViewById(R.id.badiliste);
        badiliste = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        final ArrayList<ArrayList<String>> allBadis = BadiData.allBadis(getApplicationContext());
        for (ArrayList<String> b : allBadis) {
            badiliste.add(b.get(5)+" - "+b.get(1));
        }
        badis.setAdapter(badiliste);
//Definition einer anonymen Klicklistener Klasse
        AdapterView.OnItemClickListener mListClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BadiDetailsActivity.class);
                String selected = parent.getItemAtPosition(position).toString();
                //Kleine Infobox anzeigen
                Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();
                //Intent mit Zusatzinformationen - hier die Badi Nummer
                intent.putExtra("badi", allBadis.get(position).get(0));
                intent.putExtra("name", selected);
                intent.putExtra("latitude",allBadis.get(position).get(10));
                intent.putExtra("longitude",allBadis.get(position).get(11));
                startActivity(intent);
            }
        };
        badis.setOnItemClickListener(mListClickedHandler);
    }

    public void init(){
        btn_place = (Button)findViewById(R.id.btn_position);
        btn_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent toy = new Intent(MainActivity.this,MapsActivity.class);

                startActivity(toy);

            }
        });
    }
}
