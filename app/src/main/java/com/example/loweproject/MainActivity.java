package com.example.loweproject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * This is the Main Activity of the program, where every functionality is implemented.
 */
public class MainActivity extends ListActivity {


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
    int[] sizeList = new int[13];
    private static final String TAG = MainActivity.class.getName();

    int i, j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        /** Reference to the navigate button of the layout activity_main.xml */
        Button btnNav = (Button) findViewById(R.id.btnNav);

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

        View.OnClickListener listenerNav = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //Checking if the list is empty
                    if (listArray.size() == 0)
                        Toast.makeText(MainActivity.this, "List is empty!", Toast.LENGTH_SHORT).show();
                    else {

                        /**
                         * Initializing the distanceMatrix
                         * in which distanceMatrix[i,j] refers to the distance between rack i to rack j
                         */
                        double[][] distanceMatrix = {
                                {0, 4, 4, 7, 7, 10, 10, 10.5, 10.5, 13.5, 13.5, 16.5, 16.5},
                                {4, 0, 0, 11, 11, 14, 14, 6.5, 6.5, 9.5, 9.5, 12.5, 12.5},
                                {4, 0, 0, 11, 11, 14, 14, 6.5, 6.5, 9.5, 9.5, 12.5, 12.5},
                                {7, 11, 11, 0, 0, 11, 11, 9.5, 9.5, 6.5, 6.5, 9.5, 9.5},
                                {7, 11, 11, 0, 0, 11, 11, 9.5, 9.5, 6.5, 6.5, 9.5, 9.5},
                                {10, 14, 14, 11, 11, 0, 0, 12.5, 12.5, 9.5, 9.5, 6.5, 6.5},
                                {10, 14, 14, 11, 11, 0, 0, 12.5, 12.5, 9.5, 9.5, 6.5, 6.5},
                                {10.5, 6.5, 6.5, 9.5, 9.5, 12.5, 12.5, 0, 0, 8, 8, 11, 11},
                                {10.5, 6.5, 6.5, 9.5, 9.5, 12.5, 12.5, 0, 0, 8, 8, 11, 11},
                                {13.5, 9.5, 9.5, 6.5, 6.5, 9.5, 9.5, 8, 8, 0, 0, 8, 8},
                                {13.5, 9.5, 9.5, 6.5, 6.5, 9.5, 9.5, 8, 8, 0, 0, 8, 8},
                                {16.5, 12.5, 12.5, 9.5, 9.5, 6.5, 6.5, 11, 11, 8, 8, 0, 0},
                                {16.5, 12.5, 12.5, 9.5, 9.5, 6.5, 6.5, 11, 11, 8, 8, 0, 0}};

                        /**
                         * opening the files
                         */
                        InputStream is = getAssets().open("optimaluniqueproduct.txt");
                        InputStream is2 = getAssets().open("optimalrack.txt");
                        InputStream is3 = getAssets().open("optimalpath.txt");
                        InputStream is4= getAssets().open("size.txt");

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

                        byte[] buffer_size = new byte[size4];
                        is4.read(buffer_size);
                        is4.close();

                        // Convert the buffer into a string.
                        String text_product = new String(buffer_product);

                        String lines_product[] = text_product.split("\\r?\\n");

                        String text_rack = new String(buffer_rack);

                        String lines_rack[] = text_rack.split("\\r?\\n");

                        String text_path = new String(buffer_path);

                        String lines_path[] = text_path.split("\\r?\\n");

                        String text_size = new String(buffer_size);

                        String lines_size[] = text_size.split("\\r?\\n");


                        //Declaring rackList which is used to store the rack numbers of entered list items
                        int[] rackList = new int[listArray.size()];
                        int[] corresponding_size = new int[12];


                        //Initializing to rackList array to -1
                        for (i = 0; i < rackList.length; i++) {
                            rackList[i] = -1;
                        }


                        int k = 0;

                        ArrayList<String> product_pass = new ArrayList<String>();

                        //Storing all the rack numbers of entered list items
                        for (i = 0; i < listArray.size(); i++) {
                            product_name = listArray.get(i);
                            Log.d(MainActivity.TAG, product_name);
                            for (j = 0; j < 100; j++) {
                                if (product_name.equals(lines_product[j])) {
                                    Log.d(MainActivity.TAG, "Rack No: " + lines_rack[j]);
                                    product_pass.add(product_name);
                                    int racknum = Integer.parseInt(lines_rack[j]);
                                    int sizenum = Integer.parseInt(lines_size[j]);

                                    if(sizeList[racknum]<sizenum && sizeList[racknum]<=3)
                                        sizeList[racknum]=Integer.parseInt(lines_size[j]);

                                    Log.d(MainActivity.TAG, "Size numbers : "+sizeList[racknum]);
                                    rackList[k] = Integer.parseInt(lines_rack[j]);

                                    k++;
                                    break;
                                }
                            }
                        }

                        //Declaring Rackorder to get definite number of rack numbers without impure(-1) values
                        Integer[] rackorder = new Integer[k];
                        int t = 0;
                        int z = 0;
                        for (i = 0; i < rackList.length; i++) {
                            if (rackList[i] != -1) {
                                rackorder[t] = rackList[i];
                                t++;
                            }
                            Log.d(MainActivity.TAG, "" + rackList[i]);
                        }
                        for (i = 0; i < rackorder.length; i++)
                            Log.d(MainActivity.TAG, "rackorder:" + rackorder[i]);

                        int[] pass_rackorder = new int[rackorder.length];

                        for (i = 0; i < pass_rackorder.length; i++)
                            pass_rackorder[i] = rackorder[i];

                        //Create set from array elements
                        LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>(Arrays.asList(rackorder));

                        //Get back the array without duplicates
                        Integer[] uniquerackorder = linkedHashSet.toArray(new Integer[]{});


                        int temp;
                        //Sorting the unique rack order
                        for (i = 0; i < uniquerackorder.length; i++) {
                            for (j = i + 1; j < uniquerackorder.length; j++) {
                                if (uniquerackorder[i] > uniquerackorder[j] ) {
                                    temp = uniquerackorder[i];
                                    uniquerackorder[i] = uniquerackorder[j];
                                    uniquerackorder[j] = temp;
                                }
                            }
                        }

                        //Adding 0 to the beginning and end of the unique rack order
                        Integer[] temp_uniquerackorder = new Integer[uniquerackorder.length + 2];
                        temp_uniquerackorder[0] = 0;
                        for (i = 0; i < uniquerackorder.length; i++) {
                            temp_uniquerackorder[i + 1] = uniquerackorder[i];
                        }
                        temp_uniquerackorder[i + 1] = 0;

                        for (i = 0; i < temp_uniquerackorder.length; i++)
                            Log.d(MainActivity.TAG, "temp uniquerackorder: " + temp_uniquerackorder[i]);

                        /**
                         * Declaring and initializing newDistanceMatrix to only contain the distances between
                         * the rack numbers of entered list items.
                         */
                    /*double[][] newDistanceMatrix = new double[temp_uniquerackorder.length][temp_uniquerackorder.length];
                    for (i = 0; i < temp_uniquerackorder.length; i++) {
                        for (j = 0; j < temp_uniquerackorder.length; j++) {
                            newDistanceMatrix[i][j] = distanceMatrix[temp_uniquerackorder[i]][temp_uniquerackorder[j]];
                        }
                    }

                     */

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

                            if (compareArrays(array_path, temp_uniquerackorder)) {

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


                            //Obtaining the final order in required format and displaying it using customDialog
                            finalOutput += "The Shortest path is : \n" + var_rack_order + "\n----------------------------\n";
                            for (i = 0; i < sortedRackOrder.length; i++)
                                output += "" + sortedRackOrder[i] + "->" + sortedListArray[i] + "\n" + "----------------------------" + "\n";

                            finalOutput += output;
                            Log.d(MainActivity.TAG, "finalOutput: " + finalOutput);
                            //showCustomDialog(finalOutput);
                            Intent i = new Intent(MainActivity.this, Location.class);
                            i.putExtra("path", var_rack_order); //For final output
                            i.putExtra("product", product_pass);//Product Names
                            i.putExtra("rackorder", pass_rackorder); //Rack order
                            i.putExtra("size array", sizeList);
                            startActivity(i);
                        }


                        var_rack_order = "";


                        for (i = 0; i < rackList.length; i++)
                            rackList[i] = -1;
                    }

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


        /** Setting the event listener for the Navigate button */
        btnNav.setOnClickListener(listenerNav);

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

        Button buttonOk = (Button) dialogView.findViewById(R.id.buttonPickUp);

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
            InputStream is = getAssets().open("optimaluniqueproduct.txt");

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
            for (i = 0; i < 100; i++)
                countryList.add(new CountryItem(lines[i]));
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
    }
}