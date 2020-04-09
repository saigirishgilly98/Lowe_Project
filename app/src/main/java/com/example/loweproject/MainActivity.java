package com.example.loweproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


/**
 * This is the Main Activity of the program, where every functionality is implemented.
 */
public class MainActivity extends ListActivity {

    SharedPreferences sp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private List<CountryItem> countryList;

    /**
     * Items entered by the user is stored in this ArrayList variable
     */
    ArrayList<String> listArray = new ArrayList<String>();

    /**
     * Declaring an ArrayAdapter to set items to ListView
     */
    ArrayAdapter<String> adapter2;

    /**
     * Declaring item to set the user entered item  in textview
     * Declaring product_name to set each list item entered recursively
     * Declaring var_rack_order to set the path of racks
     */
    String item, product_name;
    String var_rack_order = "";
    private static final String TAG = MainActivity.class.getName();

    int i, j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SharedPreferences for Logout
                sp = getSharedPreferences("login",
                MODE_PRIVATE);

        /**
         * Function called to fill all the product names in autoCompleteTextView
         */
        fillCountryList();

        /**
         * Referencing AutoCompleteTextView by using id and setting AutoCompleteCountryAdapter
         */
        AutoCompleteTextView editText = findViewById(R.id.actv);
        AutoCompleteCountryAdapter adapter = new AutoCompleteCountryAdapter(this, countryList);
        editText.setAdapter(adapter);
        /** Reference to the button of the layout activity_main.xml */
        Button btn = (Button) findViewById(R.id.btnAdd);

        /** Reference to the delete button of the layout activity_main.xml */
        Button btnDel = (Button) findViewById(R.id.btnDel);

        /** Reference to the done button of the layout activity_main.xml */
        final Button btnDone = (Button) findViewById(R.id.btnDone);

        /** Reference to the LogOut button of the layout activity_main.xml */
        Button btnLogOut = (Button) findViewById(R.id.btnLogOut);

        /** Defining the ArrayAdapter to set items to ListView */
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, listArray);

        /** Defining a click event listener for the button "Add" */
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adding every valid entered element into the listArray
                AutoCompleteTextView edit = (AutoCompleteTextView) findViewById(R.id.actv);
                item = edit.getText().toString();
                int var_toast = 0;
                for (CountryItem country : countryList) {
                    if (country.getCountryName().equals(item)) {
                        listArray.add(item);
                        var_toast = 0;
                        break;
                    } else {
                        var_toast = 1;
                    }
                }
                if (var_toast == 1) {
                    Toast.makeText(MainActivity.this, "Enter Valid Option!", Toast.LENGTH_SHORT).show();
                }


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
                int item_flag = 0;

                if (itemCount == 0) {
                    Toast.makeText(MainActivity.this, "List is Empty!", Toast.LENGTH_SHORT).show();
                    item_flag = 1;
                }
                //Delete all the selected items
                for (int i = itemCount - 1; i >= 0; i--) {
                    if (checkedItemPositions.get(i)) {
                        adapter2.remove(listArray.get(i));
                        item_flag = 1;
                    }
                }
                if (item_flag == 0) {
                    Toast.makeText(MainActivity.this, "Select Items to delete!", Toast.LENGTH_SHORT).show();
                } else {
                    item_flag = 0;
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

                    //Checking if the list is empty
                    if (listArray.size() == 0)
                        Toast.makeText(MainActivity.this, "List is empty!", Toast.LENGTH_SHORT).show();

                    /**
                     * Initializing the distanceMatrix
                     * in which distanceMatrix[i,j] refers to the distance between rack i to rack j
                     */
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
                            {1972, 579, 1260, 987, 371, 999, 701, 2099, 600, 1162, 1200, 504, 0}};

                    /**
                     * opening the files
                     */
                    InputStream is = getAssets().open("array.txt");
                    InputStream is2 = getAssets().open("arrayrack.txt");
                    InputStream is3 = getAssets().open("path.txt");
                    InputStream is4 = getAssets().open("recommendation.txt");

                    // We guarantee that the available method returns the total
                    // size of the asset...  of course, this does mean that a single
                    // asset can't be more than 2 gigs.
                    int size1 = is.available();
                    int size2 = is2.available();
                    int size3 = is3.available();
                    int size4 = is4.available();

                    // Read the entire asset into a local byte buffer.
                    byte[] buffer_product = new byte[size1];
                    is.read(buffer_product);
                    is.close();

                    byte[] buffer_rack = new byte[size2];
                    is2.read(buffer_rack);
                    is2.close();

                    byte[] buffer_path = new byte[size3];
                    is3.read(buffer_path);
                    is3.close();

                    byte[] buffer_recommendation = new byte[size4];
                    is4.read(buffer_recommendation);
                    is4.close();

                    // Convert the buffer into a string.
                    String text_product = new String(buffer_product);

                    String lines_product[] = text_product.split("\\r?\\n");

                    String text_rack = new String(buffer_rack);

                    String lines_rack[] = text_rack.split("\\r?\\n");

                    String text_path = new String(buffer_path);

                    String lines_path[] = text_path.split("\\r?\\n");

                    String text_recommendation = new String(buffer_recommendation);

                    String lines_recommendation[] = text_recommendation.split("\\r?\\n");

                    //Declaring rackList which is used to store the rack numbers of entered list items
                    int[] rackList = new int[listArray.size()];
                    int[] recommendationIndex = new int[listArray.size()];

                    //Initializing to rackList array to -1
                    for (i = 0; i < rackList.length; i++) {
                        rackList[i] = -1;
                        recommendationIndex[i] = -1;
                    }


                    int k = 0;

                    //Storing all the rack numbers of entered list items
                    for (i = 0; i < listArray.size(); i++) {
                        product_name = listArray.get(i);
                        Log.d(MainActivity.TAG, product_name);
                        for (j = 0; j < 3620; j++) {
                            if (product_name.equals(lines_product[j])) {
                                Log.d(MainActivity.TAG, "Rack No: " + lines_rack[j]);
                                rackList[k] = Integer.parseInt(lines_rack[j]);
                                recommendationIndex[k] = Integer.parseInt(lines_recommendation[j]);
                                k++;
                                break;
                            }
                        }
                    }
                    //Declaring Rackorder to get definite number of rack numbers without impure(-1) values
                    Integer[] rackorder = new Integer[k];
                    Integer[] recommendationIndexPure = new Integer[k];
                    int t = 0;
                    int z = 0;
                    for (i = 0; i < rackList.length; i++) {
                        if (rackList[i] != -1) {
                            rackorder[t] = rackList[i];
                            t++;
                        }
                        if (recommendationIndex[i] != -1) {
                            recommendationIndexPure[z] = recommendationIndex[i];
                            z++;
                        }
                        Log.d(MainActivity.TAG, "" + rackList[i]);
                    }
                    for (i = 0; i < rackorder.length; i++)
                        Log.d(MainActivity.TAG, "rackorder:" + rackorder[i]);

                    //Create set from array elements
                    LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>(Arrays.asList(rackorder));
                    LinkedHashSet<Integer> linkedHashSet1 = new LinkedHashSet<>(Arrays.asList(recommendationIndexPure));

                    //Get back the array without duplicates
                    Integer[] uniquerackorder = linkedHashSet.toArray(new Integer[]{});
                    Integer[] uniquerecommendation = linkedHashSet1.toArray(new Integer[]{});


                    int[] recommendorRackOrder = new int[uniquerecommendation.length];

                    /**
                     * Obtaining recommendation rack order for all the products entered in the list
                     */
                    k = 0;
                    for (i = 0; i < uniquerecommendation.length; i++) {
                        product_name = lines_product[uniquerecommendation[i]];
                        for (j = 0; j < 3620; j++) {
                            if (product_name.equals(lines_product[j])) {
                                Log.d(MainActivity.TAG, "Rack No: " + lines_rack[j]);
                                recommendorRackOrder[k] = Integer.parseInt(lines_rack[j]);
                                k++;
                                break;
                            }
                        }
                    }

                    int temp;
                    //Sorting the unique rack order
                    for (i = 0; i < uniquerackorder.length; i++) {
                        for (j = i + 1; j < uniquerackorder.length; j++) {
                            if (uniquerackorder[i] > uniquerackorder[j]) {
                                temp = uniquerackorder[i];
                                uniquerackorder[i] = uniquerackorder[j];
                                uniquerackorder[j] = temp;
                            }
                        }
                    }
                    /**
                     * Declaring and initializing newDistanceMatrix to only contain the distances between
                     * the rack numbers of entered list items.
                     */
                    int[][] newDistanceMatrix = new int[uniquerackorder.length][uniquerackorder.length];
                    for (i = 0; i < uniquerackorder.length; i++) {
                        for (j = 0; j < uniquerackorder.length; j++) {
                            newDistanceMatrix[i][j] = distanceMatrix[uniquerackorder[i]][uniquerackorder[j]];
                        }
                    }

                    /**
                     * Getting the shortest path by comparing the obtained rack numbers with the
                     * same rack numbers in any order present in paths.txt file
                     */
                    int rackflag = 0;
                    for (i = 0; i < (lines_path.length); i++) {
                        String[] tokens = lines_path[i].split(" ");
                        Integer[] array_path = new Integer[tokens.length];
                        int y = 0;
                        for (String token : tokens) {
                            array_path[y++] = Integer.parseInt(token);
                        }
                        if (compareArrays(array_path, uniquerackorder)) {
                            for (i = 0; i < ((array_path.length) - 1); i++) {
                                var_rack_order += "" + array_path[i] + "->";
                            }
                            var_rack_order += array_path[i];
                            rackflag = 1;
                            break;
                        }
                    }
                    /**
                     * If shortest rack order found then sort rack order and corresponding product name in
                     * ascending order according to rack no
                     */
                    if (rackflag == 1) {
                        rackflag = 0;
                        String output = "";
                        String finalOutput = "";
                        Integer[] sortedRackOrder = new Integer[rackorder.length];
                        String[] sortedListArray = new String[listArray.size()];
                        for (i = 0; i < rackorder.length; i++) {
                            sortedRackOrder[i] = rackorder[i];
                            sortedListArray[i] = listArray.get(i);
                        }
                        int temp1;
                        String temp2;
                        for (i = 0; i < sortedRackOrder.length; i++) {
                            for (j = i + 1; j < sortedRackOrder.length; j++) {
                                if (sortedRackOrder[i] > sortedRackOrder[j]) {
                                    temp1 = sortedRackOrder[i];
                                    sortedRackOrder[i] = sortedRackOrder[j];
                                    sortedRackOrder[j] = temp1;
                                    temp2 = sortedListArray[i];
                                    sortedListArray[i] = sortedListArray[j];
                                    sortedListArray[j] = temp2;
                                }
                            }
                        }

                        /**
                         * Sorting recommendation order according to the corresponding rack orders
                         */
                        int temp3;
                        for (i = 0; i < uniquerecommendation.length; i++) {
                            for (j = i + 1; j < uniquerecommendation.length; j++) {
                                if (recommendorRackOrder[i] > recommendorRackOrder[j]) {
                                    temp1 = recommendorRackOrder[i];
                                    recommendorRackOrder[i] = recommendorRackOrder[j];
                                    recommendorRackOrder[j] = temp1;
                                    temp3 = uniquerecommendation[i];
                                    uniquerecommendation[i] = uniquerecommendation[j];
                                    uniquerecommendation[j] = temp3;
                                }
                            }
                        }

                        //Obtaining the final order in required format and displaying it using customDialog
                        finalOutput += "The Shortest path is : \n" + var_rack_order + "\n----------------------------\n";
                        for (i = 0; i < sortedRackOrder.length; i++)
                            output += "" + sortedRackOrder[i] + "->" + sortedListArray[i] + "\n" + "----------------------------" + "\n";
                        output += "\n----Recommendation----" + "\n\n";
                        for (i = 0; i < uniquerecommendation.length; i++)
                            output += "" + recommendorRackOrder[i] + "->" + lines_product[uniquerecommendation[i]] + "\n" + "----------------------------" + "\n";

                        finalOutput += output;
                        showCustomDialog(finalOutput);
                    }


                    var_rack_order = "";


                    for (i = 0; i < rackList.length; i++)
                        rackList[i] = -1;

                } catch (IOException e) {
                    // Should never happen!
                    throw new RuntimeException(e);
                }
            }
        };

        /** Defining a click event listener for the button "Log Out" */
        View.OnClickListener listenerLogOut = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mAuth.signOut();
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });

                Intent intent8 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent8);
                sp.edit().putBoolean("logged", false).apply();

            }
        };


        /** Setting the event listener for the add button */
        btn.setOnClickListener(listener);

        /** Setting the event listener for the delete button */
        btnDel.setOnClickListener(listenerDel);

        /** Setting the event listener for the done button */
        btnDone.setOnClickListener(listenerDone);

        /** Setting the event listener for the log out button */
        btnLogOut.setOnClickListener(listenerLogOut);

        /** Setting the adapter to the ListView */
        setListAdapter(adapter2);
    }

    //Compares to arrays if they contain same elements in any order
    public static boolean compareArrays(Integer[] arr1, Integer[] arr2) {
        HashSet<Integer> set1 = new HashSet<Integer>(Arrays.asList(arr1));
        HashSet<Integer> set2 = new HashSet<Integer>(Arrays.asList(arr2));
        return set1.equals(set2);
    }

    //To display custom dialog
    private void showCustomDialog(String var_rack_order) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);


        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);

        Button buttonOk = (Button) dialogView.findViewById(R.id.buttonOk);

        TextView txtPath = (TextView) dialogView.findViewById(R.id.txtPath);
        txtPath.setText(var_rack_order);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        View.OnClickListener listenerOk = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        };


        buttonOk.setOnClickListener(listenerOk);

    }


    /***
     * Function to fill all the product names in autoCompleteTextView
     */
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

            //Spliting each line by using new line regex expression
            String lines[] = text.split("\\r?\\n");
            //Adding each item to the list
            for (i = 0; i < 3620; i++)
                countryList.add(new CountryItem(lines[i]));
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}