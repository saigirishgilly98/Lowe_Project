package com.example.loweproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends ListActivity {
    private List<CountryItem> countryList;

    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<String> listArray = new ArrayList<String>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<String> adapter2;

    String item,product_name;
    String rackorder = "";
    private static final String TAG = MainActivity.class.getName();

    int i,j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillCountryList();

        AutoCompleteTextView editText = findViewById(R.id.actv);
        AutoCompleteCountryAdapter adapter = new AutoCompleteCountryAdapter(this, countryList);
        editText.setAdapter(adapter);
        /** Reference to the button of the layout main.xml */
        Button btn = (Button) findViewById(R.id.btnAdd);

        /** Reference to the delete button of the layout main.xml */
        Button btnDel = (Button) findViewById(R.id.btnDel);

        Button btnDone = (Button) findViewById(R.id.btnDone);

        /** Defining the ArrayAdapter to set items to ListView */
        adapter2= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, listArray);

        /** Defining a click event listener for the button "Add" */
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutoCompleteTextView edit = (AutoCompleteTextView) findViewById(R.id.actv);
                item = edit.getText().toString();
                if(item != "")
                    listArray.add(item);

                //Deleting Same entries

                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(listArray);
                listArray.clear();
                listArray.addAll(hashSet);

                //Alphabetic sorting.
                Collections.sort(listArray);
                edit.setText("");
                adapter2.notifyDataSetChanged();
            }
        };

        /** Defining a click event listener for the button "Delete" */
        View.OnClickListener listenerDel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Getting the checked items from the listview */
                SparseBooleanArray checkedItemPositions = getListView().getCheckedItemPositions();
                int itemCount = getListView().getCount();

                for(int i=itemCount-1; i >= 0; i--){
                    if(checkedItemPositions.get(i)){
                        adapter2.remove(listArray.get(i));
                    }
                }
                checkedItemPositions.clear();
                adapter2.notifyDataSetChanged();
            }
        };

        /** Defining a click event listener for the button "Done" */
        View.OnClickListener listenerDone = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream is = getAssets().open("array.txt");
                    InputStream is2 = getAssets().open("arrayrack.txt");

                    // We guarantee that the available method returns the total
                    // size of the asset...  of course, this does mean that a single
                    // asset can't be more than 2 gigs.
                    int size1 = is.available();
                    int size2 = is2.available();

                    // Read the entire asset into a local byte buffer.
                    byte[] buffer_product = new byte[size1];
                    is.read(buffer_product);
                    is.close();

                    byte[] buffer_rack = new byte[size2];
                    is2.read(buffer_rack);
                    is2.close();

                    // Convert the buffer into a string.
                    String text_product = new String(buffer_product);

                    String lines_product[] = text_product.split("\\r?\\n");

                    String text_rack = new String(buffer_rack);

                    String lines_rack[] = text_rack.split("\\r?\\n");
                    int[] rackList = new int[13];
                    for(i=0;i<rackList.length;i++)
                        rackList[i] = -1;

                    int k = 0;
                    rackorder = "";

                    for (i = 0; i < listArray.size(); i++) {
                        product_name = listArray.get(i);
                        Log.d(MainActivity.TAG,product_name);
                        for (j = 0; j < 3620; j++) {
                            if(j<10)
                                Log.d(MainActivity.TAG,"Product:"+lines_product[j]);
                            if(product_name == lines_product[j]){
                                rackList[k] = Integer.parseInt(lines_rack[j]);
                                Log.d(MainActivity.TAG,"Rack No: "+lines_rack[j]);
                                k++;
                                break;
                            }
                        }
                    }
                    for(i=0;i<rackList.length;i++) {
                        if (rackList[i] != -1) {
                            rackorder = rackorder + rackList[i] + "->";
                        }
                        Log.d(MainActivity.TAG,""+rackList[i]);
                    }
                    rackorder = rackorder + "0";
                    Toast.makeText(MainActivity.this, rackorder, Toast.LENGTH_SHORT).show();
                    for(i=0;i<rackList.length;i++)
                        rackList[i] = -1;

                } catch (IOException e) {
                    // Should never happen!
                    throw new RuntimeException(e);
                }
            }
        };



        /** Setting the event listener for the add button */
        btn.setOnClickListener(listener);

        /** Setting the event listener for the delete button */
        btnDel.setOnClickListener(listenerDel);

        btnDone.setOnClickListener(listenerDone);

        /** Setting the adapter to the ListView */
        setListAdapter(adapter2);
    }

    private void fillCountryList() {
        countryList = new ArrayList<>();
        try {
            InputStream is = getAssets().open("array.txt");

            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String text = new String(buffer);

            String lines[] = text.split("\\r?\\n");
            for(i=0;i<3620;i++)
                countryList.add(new CountryItem(lines[i]));
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}