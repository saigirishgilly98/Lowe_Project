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
import java.util.Arrays;
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

                    int[][] distanceMatrix = {
                            {0, 2451, 713, 1018, 1631, 1374, 2408, 213, 2571, 875, 1420, 2145, 1972},
                            {2451, 0, 1745, 1524, 831, 1240, 959, 2596, 403, 1589, 1374, 357, 579},
                            {713, 1745, 0, 355, 920, 803, 1737, 851, 1858, 262, 940, 1453, 1260},
                            {1018, 1524, 355, 0, 700, 862, 1395, 1123, 1584, 466, 1056, 1280, 987},
                            {1631, 831, 920, 700, 0, 663, 1021, 1769, 949, 796, 879, 586, 371},
                            {1374, 1240, 803, 862, 663, 0, 1681, 1551, 1765, 547, 225, 887, 999},
                            {2408, 959, 1737, 1395, 1021, 1681, 0, 2493, 678, 1724, 1891, 1114, 701},
                            {213, 2596, 851, 1123, 1769, 1551, 2493, 0, 2699, 1038, 1605, 2300, 2099},
                            {2571, 403, 1858, 1584, 949, 1765, 678, 2699, 0, 1744, 1645, 653, 600},
                            {875, 1589, 262, 466, 796, 547, 1724, 1038, 1744, 0, 679, 1272, 1162},
                            {1420, 1374, 940, 1056, 879, 225, 1891, 1605, 1645, 679, 0, 1017, 1200},
                            {2145, 357, 1453, 1280, 586, 887, 1114, 2300, 653, 1272, 1017, 0, 504},
                            {1972, 579, 1260, 987, 371, 999, 701, 2099, 600, 1162, 1200, 504, 0} };

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
                    int[] rackList = new int[listArray.size()];
                    for(i=0;i<rackList.length;i++)
                        rackList[i] = -1;

                    int k = 0;
                    rackorder = "";

                    for (i = 0; i < listArray.size(); i++) {
                        product_name = listArray.get(i);
                        Log.d(MainActivity.TAG,product_name);
                        for (j = 0; j < 3620; j++) {
                            if(product_name.equals(lines_product[j])){
                                Log.d(MainActivity.TAG,"Rack No: "+lines_rack[j]);
                                rackList[k] = Integer.parseInt(lines_rack[j]);
                                k++;
                                break;
                            }
                        }
                    }
                    int[] rackorder = new int[k];
                    int t = 0;
                    for(i=0;i<rackList.length;i++) {
                        if (rackList[i] != -1) {
                            rackorder[t] = rackList[i];
                            t++;
                        }
                        Log.d(MainActivity.TAG,""+rackList[i]);
                    }
                    int[] uniquerackorder = new int[t];
                    for(i=0;i<t;i++)
                        uniquerackorder[i] = -1;
                    int l=0,flag=0;
                    for(i = 0; i < t; i++)
                    {
                        for(j=0;j<t;j++)
                        {
                           if(rackorder[i] == uniquerackorder[j]){
                               flag = 1;
                           }
                        }
                        if(flag == 0)
                        {
                            uniquerackorder[l] = rackorder[i];
                            l++;
                        }
                        else{
                            flag = 0;
                        }
                    }
                    int temp;
                    for (i = 0; i < l; i++)
                    {
                        for (j = i + 1; j < l; j++) {
                            if (uniquerackorder[i] > uniquerackorder[j])
                            {
                                temp = uniquerackorder[i];
                                uniquerackorder[i] = uniquerackorder[j];
                                uniquerackorder[j] = temp;
                            }
                        }
                    }
                    int[][] newDistanceMatrix = new int[uniquerackorder.length][uniquerackorder.length];
                    for(i=0;i<l;i++)
                    {
                        for(j=0;j<l;j++)
                        {
                            newDistanceMatrix[i][j] = distanceMatrix[uniquerackorder[i]][uniquerackorder[j]];
                            Log.d(MainActivity.TAG,""+newDistanceMatrix[i][j]);
                        }
                    }
                    //Toast.makeText(MainActivity.this, rackorder, Toast.LENGTH_SHORT).show();


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