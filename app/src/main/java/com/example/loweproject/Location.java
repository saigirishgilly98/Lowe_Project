package com.example.loweproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Location extends AppCompatActivity {

    private static final String TAG = Location.class.getName();

    String finalOutput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final ImageView rack = findViewById(R.id.rack);
        final ImageView map = findViewById(R.id.map);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnClose = findViewById(R.id.btnClose);

        final int[][][] imageArray = {{{0}, {0, 3}, {0, 3}, {1, 17, 7}, {1, 17, 7}, {1, 19, 21, 13}, {1, 19, 21, 13}, {0, 3, 26, 5}, {0, 3, 26, 5}, {0, 3, 32, 29, 11}, {0, 3, 32, 29, 10}, {0, 3, 32, 30, 34, 15}, {0, 3, 32, 30, 34, 15}},
                {{16, 0}, {3}, {4}, {16, 1, 17, 7}, {16, 1, 17, 7}, {16, 1, 19, 21, 13}, {16, 1, 19, 21, 13}, {3, 26, 5}, {3, 26, 5}, {3, 32, 29, 11}, {3, 32, 29, 10}, {3, 32, 30, 34, 15}, {3, 32, 30, 34, 15}},
                {{16, 0}, {3}, {4}, {16, 1, 17, 7}, {16, 1, 17, 7}, {16, 1, 19, 21, 13}, {16, 1, 19, 21, 13}, {3, 26, 5}, {3, 26, 5}, {3, 32, 29, 11}, {3, 32, 29, 10}, {3, 32, 30, 34, 15}, {3, 32, 30, 34, 15}},
                {{18, 20, 0}, {18, 20, 0, 3}, {18, 20, 0, 3}, {7}, {7}, {18, 19, 21, 13}, {18, 19, 21, 13}, {7, 31, 26, 5}, {7, 31, 26, 5}, {7, 29, 11}, {7, 29, 10}, {7, 30, 34, 15}, {7, 30, 34, 15}},
                {{18, 20, 0}, {18, 20, 0, 3}, {18, 20, 0, 3}, {7}, {7}, {18, 19, 21, 13}, {18, 19, 21, 13}, {7, 31, 26, 5}, {7, 31, 26, 5}, {7, 29, 11}, {7, 29, 10}, {7, 30, 34, 15}, {7, 30, 34, 15}},
                {{24, 22, 20, 0}, {24, 22, 20, 0, 3}, {24, 22, 20, 0, 3}, {24, 22, 17, 7}, {24, 22, 17, 7}, {13}, {13}, {13, 36, 31, 26, 5}, {13, 36, 31, 26, 5}, {13, 36, 29, 11}, {13, 36, 29, 10}, {13, 34, 15}, {13, 34, 15}},
                {{24, 22, 20, 0}, {24, 22, 20, 0, 3}, {24, 22, 20, 0, 3}, {24, 22, 17, 7}, {24, 22, 17, 7}, {13}, {13}, {13, 36, 31, 26, 5}, {13, 36, 31, 26, 5}, {13, 36, 29, 11}, {13, 36, 29, 10}, {13, 34, 15}, {13, 34, 15}},
                {{27, 25, 16, 0}, {27, 25, 16}, {27, 25, 16}, {27, 32, 28, 18}, {27, 32, 28, 18}, {27, 32, 30, 33, 24}, {27, 32, 30, 33, 24}, {5}, {5}, {27, 32, 29, 11}, {27, 32, 29, 10}, {27, 32, 30, 34, 15}, {27, 32, 30, 34, 15}},
                {{27, 25, 16, 0}, {27, 25, 16}, {27, 25, 16}, {27, 32, 28, 18}, {27, 32, 28, 18}, {27, 32, 30, 33, 24}, {27, 32, 30, 33, 24}, {5}, {5}, {27, 32, 29, 11}, {27, 32, 29, 10}, {27, 32, 30, 34, 15}, {27, 32, 30, 34, 15}},
                {{23, 28, 18, 20, 0}, {23, 31, 25, 16}, {23, 31, 25, 16}, {23, 28, 18}, {23, 28, 18}, {23, 30, 33, 24}, {23, 30, 33, 24}, {23, 31, 26, 5}, {23, 31, 26, 5}, {11}, {10}, {23, 30, 34, 15}, {23, 30, 34, 15}},
                {{23, 28, 18, 20, 0}, {23, 31, 25, 16}, {23, 31, 25, 16}, {23, 28, 18}, {23, 28, 18}, {23, 30, 33, 24}, {23, 30, 33, 24}, {23, 31, 26, 5}, {23, 31, 26, 5}, {11}, {10}, {23, 30, 34, 15}, {23, 30, 34, 15}},
                {{35, 33, 24, 22, 20, 0}, {35, 36, 31, 25, 16}, {35, 36, 31, 25, 16}, {35, 36, 28, 18}, {35, 36, 28, 18}, {35, 33, 24}, {35, 33, 24}, {35, 36, 31, 26, 5}, {35, 36, 31, 26, 5}, {35, 36, 29, 11}, {35, 36, 29, 10}, {15}, {15}},
                {{35, 33, 24, 22, 20, 0}, {35, 36, 31, 25, 16}, {35, 36, 31, 25, 16}, {35, 36, 28, 18}, {35, 36, 28, 18}, {35, 33, 24}, {35, 33, 24}, {35, 36, 31, 26, 5}, {35, 36, 31, 26, 5}, {35, 36, 29, 11}, {35, 36, 29, 10}, {15}, {15}}
        };

        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        final int[] sizeList = intent.getIntArrayExtra("size array");
        final ArrayList<String> product = intent.getStringArrayListExtra("product");
        final int[] rackorder = intent.getIntArrayExtra("rackorder");
        int i, size = 0;
        for (i = 0; i < product.size(); i++)
            Log.d(Location.TAG, "Product in Location : " + product.get(i));
        for (i = 0; i < rackorder.length; i++)
            Log.d(Location.TAG, "Rack Order in Location : " + rackorder[i]);
        final int[] array_product_rack = new int[product.size()];
        for (i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if (Character.isDigit(c)) {
                if ((i + 1) != path.length()
                        && Character.isDigit(path.charAt(i + 1))) {
                    size++;

                    Log.d(Location.TAG, "Digits " + path.charAt(i) + path.charAt(i + 1));
                    i++;
                } else {
                    size++;
                    Log.d(Location.TAG, "Digits " + c);
                }
            }
        }
        Log.d(Location.TAG, "Path variable " + path);


        final int[] arr_path_new = new int[size];
        final int[] arr_path = new int[size];
        final ArrayList<Integer> arr_product = new ArrayList<Integer>();
        int k = 0;
        int cnt = 0, z = 0;
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(path);
        while (m.find()) {
            arr_path_new[k++] = Integer.parseInt(m.group());
            if (arr_path_new[k - 1] == 0)
                cnt++;
            if (cnt == 2)
                break;
        }

        for (i = 0; i < arr_path_new.length; i++) {
            Log.d(Location.TAG, "Arr_path_new has " + arr_path_new[i] + "\n");

        }


        try {
            InputStream is = getAssets().open("optimalrack.txt");

            int size1 = is.available();

            byte[] buffer_rack = new byte[size1];
            is.read(buffer_rack);
            is.close();


            // Convert the buffer into a string.
            String text_rack = new String(buffer_rack);

            String lines_rack[] = text_rack.split("\\r?\\n");
            int c = 0, p1 = 1, p2 = arr_path_new.length - 2;
            arr_path[0] = 0;
            arr_path[arr_path_new.length - 1] = 0;
            for (i = 0; i < arr_path_new.length - 1; i++) {
                if (arr_path_new[i] == 0) {
                    c++;
                    if (c == 2)
                        break;
                    continue;
                }
                if (sizeList[arr_path_new[i]] == 1) {
                    int x = arr_path_new[i];
                    arr_path[p1++] = arr_path_new[i];
                }
                if (sizeList[arr_path_new[i]] == 3) {
                    Log.d(Location.TAG, "Altered " + i);
                    int x = arr_path_new[i];
                    arr_path[p2--] = arr_path_new[i];
                }

            }

            for (i = 0; i < arr_path_new.length; i++) {
                if (sizeList[arr_path_new[i]] == 2) {
                    arr_path[p1++] = arr_path_new[i];
                }


            }
            for (i = 0; i < arr_path_new.length; i++) {
                Log.d(Location.TAG, "Final ARR_PATH " + arr_path[i]);
            }


            View.OnClickListener listenerClose = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Location.this, MainActivity.class);
                    startActivity(intent);
                }
            };

            final int imagePath[] = {R.drawable.path_0, R.drawable.path_1, R.drawable.path_2, R.drawable.path_3, R.drawable.path_4, R.drawable.path_5, R.drawable.path_6, R.drawable.path_7,
                    R.drawable.path_8, R.drawable.path_9, R.drawable.path_10, R.drawable.path_11, R.drawable.path_12, R.drawable.path_13, R.drawable.path_14, R.drawable.path_15, R.drawable.path_16,
                    R.drawable.path_17, R.drawable.path_18, R.drawable.path_19, R.drawable.path_20, R.drawable.path_21, R.drawable.path_22, R.drawable.path_23, R.drawable.path_24, R.drawable.path_25,
                    R.drawable.path_26, R.drawable.path_27, R.drawable.path_28, R.drawable.path_29, R.drawable.path_30, R.drawable.path_31, R.drawable.path_32, R.drawable.path_33, R.drawable.path_34,
                    R.drawable.path_35, R.drawable.path_36};

            final int imageMap[] = {R.drawable.map_0, R.drawable.map_1, R.drawable.map_2, R.drawable.map_3, R.drawable.map_4, R.drawable.map_5, R.drawable.map_6, R.drawable.map_7, R.drawable.map_8,
                    R.drawable.map_9, R.drawable.map_10, R.drawable.map_11};

            int j;
            final ArrayList<Integer> listPath = new ArrayList<Integer>();
            final ArrayList<Integer> listPathTemp = new ArrayList<Integer>();
            for (i = 1; i < arr_path.length; i++) {
                int[] images = imageArray[arr_path[i - 1]][arr_path[i]];
                for (j = 0; j < images.length; j++) {
                    listPathTemp.add(images[j]);
                }
            }

            for (j = 0; j < listPathTemp.size(); j++) {
                Log.d(Location.TAG, "List Path Temp : " + listPathTemp.get(j) + "\n");
            }

            int temp = listPathTemp.get(0);
            listPath.add(listPathTemp.get(0));
            for (j = 1; j < listPathTemp.size(); j++) {
                if (temp != listPathTemp.get(j)) {
                    listPath.add(listPathTemp.get(j));
                }
                temp = listPathTemp.get(j);
            }

            for (j = 0; j < listPath.size(); j++) {
                Log.d(Location.TAG, "List Path : " + listPath.get(j) + "\n");
            }

            final int[] count = {0};

            final int[] v_12 = {0};
            final int[] v_34 = {0};
            final int[] v_56 = {0};
            final int[] v_78 = {0};
            final int[] v_910 = {0};
            final int[] v_1112 = {0};

            View.OnClickListener listenerNext = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rack.setImageResource(imagePath[listPath.get(count[0])]);
                    map.setImageResource(imageMap[0]);
                    switch (listPath.get(count[0])) {
                        case 0:
                        case 1:
                            map.setImageResource(imageMap[0]);
                            break;
                        case 3:
                        case 4:
                        case 16:
                            map.setImageResource(imageMap[1]);
                            break;
                        case 17:
                        case 19:
                        case 20:
                            map.setImageResource(imageMap[2]);
                            break;
                        case 7:
                        case 8:
                        case 9:
                        case 18:
                            map.setImageResource(imageMap[3]);
                            break;
                        case 21:
                        case 22:
                            map.setImageResource(imageMap[4]);
                            break;
                        case 13:
                        case 14:
                        case 24:
                            map.setImageResource(imageMap[5]);
                            break;
                        case 25:
                        case 26:
                        case 32:
                            map.setImageResource(imageMap[6]);
                            break;
                        case 5:
                        case 27:
                            map.setImageResource(imageMap[7]);
                            break;
                        case 28:
                        case 29:
                        case 30:
                        case 31:
                            map.setImageResource(imageMap[8]);
                            break;
                        case 10:
                        case 11:
                        case 23:
                            map.setImageResource(imageMap[9]);
                            break;
                        case 33:
                        case 34:
                        case 36:
                            map.setImageResource(imageMap[10]);
                            break;
                        case 15:
                        case 35:
                            map.setImageResource(imageMap[11]);
                            break;
                        default:
                            map.setImageResource(imageMap[0]);

                    }

                    String output = "";

                    switch (listPath.get(count[0])) {
                        case 3:
                        case 4:
                        case 16:
                            if (v_12[0] == 0) {
                                for (int i = 0; i < rackorder.length; i++) {
                                    if (rackorder[i] == 1 || rackorder[i] == 2) {
                                        output += rackorder[i] + " -> " + product.get(i) + "\n" + "----------------------------" + "\n";
                                    }
                                }
                                if (output != "")
                                    showCustomDialog(output);
                                output = "";
                                v_12[0] = 1;
                            }
                            break;
                        case 7:
                        case 8:
                        case 9:
                        case 18:
                            if (v_34[0] == 0) {
                                for (int i = 0; i < rackorder.length; i++) {
                                    if (rackorder[i] == 3 || rackorder[i] == 4) {
                                        output += rackorder[i] + " -> " + product.get(i) + "\n" + "----------------------------" + "\n";
                                    }
                                }
                                if (output != "")
                                    showCustomDialog(output);
                                output = "";
                                v_34[0] = 1;
                            }
                            break;
                        case 13:
                        case 14:
                        case 24:
                            if (v_56[0] == 0) {
                                for (int i = 0; i < rackorder.length; i++) {
                                    if (rackorder[i] == 5 || rackorder[i] == 6) {
                                        output += rackorder[i] + " -> " + product.get(i) + "\n" + "----------------------------" + "\n";
                                    }
                                }
                                if (output != "")
                                    showCustomDialog(output);
                                output = "";
                                v_56[0] = 1;
                            }
                            break;
                        case 5:
                        case 27:
                            if (v_78[0] == 0) {
                                for (int i = 0; i < rackorder.length; i++) {
                                    if (rackorder[i] == 7 || rackorder[i] == 8) {
                                        output += rackorder[i] + " -> " + product.get(i) + "\n" + "----------------------------" + "\n";
                                    }
                                }
                                if (output != "")
                                    showCustomDialog(output);
                                output = "";
                                v_78[0] = 1;
                            }
                            break;
                        case 10:
                        case 11:
                        case 23:
                            if (v_910[0] == 0) {
                                for (int i = 0; i < rackorder.length; i++) {
                                    if (rackorder[i] == 9 || rackorder[i] == 10) {
                                        output += rackorder[i] + " -> " + product.get(i) + "\n" + "----------------------------" + "\n";
                                    }
                                }
                                if (output != "")
                                    showCustomDialog(output);
                                output = "";
                                v_910[0] = 1;
                            }
                            break;
                        case 15:
                        case 35:
                            if (v_1112[0] == 0) {
                                for (int i = 0; i < rackorder.length; i++) {
                                    if (rackorder[i] == 11 || rackorder[i] == 12) {
                                        output += rackorder[i] + " -> " + product.get(i) + "\n" + "----------------------------" + "\n";
                                    }
                                }
                                if (output != "")
                                    showCustomDialog(output);
                                output = "";
                                v_1112[0] = 1;
                            }
                            break;
                    }


                    if (count[0] < listPath.size() - 1) {
                        count[0]++;
                    } else {
                        String result = "";
                        showCustomDialog(result);
                        //Toast.makeText(Location.this, "Shopping Trip Completed", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            View.OnClickListener listenerPrev = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count[0] > 0) {
                        count[0]--;
                        rack.setImageResource(imagePath[listPath.get(count[0])]);
                        switch (listPath.get(count[0])) {
                            case 0:
                            case 1:
                                map.setImageResource(imageMap[0]);
                                break;
                            case 3:
                            case 4:
                            case 16:
                                map.setImageResource(imageMap[1]);
                                break;
                            case 17:
                            case 19:
                            case 20:
                                map.setImageResource(imageMap[2]);
                                break;
                            case 7:
                            case 8:
                            case 9:
                            case 18:
                                map.setImageResource(imageMap[3]);
                                break;
                            case 21:
                            case 22:
                                map.setImageResource(imageMap[4]);
                                break;
                            case 13:
                            case 14:
                            case 24:
                                map.setImageResource(imageMap[5]);
                                break;
                            case 25:
                            case 26:
                            case 32:
                                map.setImageResource(imageMap[6]);
                                break;
                            case 5:
                            case 27:
                                map.setImageResource(imageMap[7]);
                                break;
                            case 28:
                            case 29:
                            case 30:
                            case 31:
                                map.setImageResource(imageMap[8]);
                                break;
                            case 10:
                            case 11:
                            case 23:
                                map.setImageResource(imageMap[9]);
                                break;
                            case 33:
                            case 34:
                            case 36:
                                map.setImageResource(imageMap[10]);
                                break;
                            case 15:
                            case 35:
                                map.setImageResource(imageMap[11]);
                                break;
                            default:
                                map.setImageResource(imageMap[0]);

                        }

                    } else {
                        Toast.makeText(Location.this, "Start Reached", Toast.LENGTH_SHORT).show();
                    }

                }
            };


            /** Setting the event listener for the Prev button */
            btnPrev.setOnClickListener(listenerPrev);

            /** Setting the event listener for the Next button */
            btnNext.setOnClickListener(listenerNext);

            /** Setting the event listener for the Close button */
            btnClose.setOnClickListener(listenerClose);

        } catch (ArrayIndexOutOfBoundsException | IOException e) {
            e.printStackTrace();
        }

    }

    //To display custom dialog
    private void showCustomDialog(String var_rack_order) {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.my_dialog, viewGroup, false);

        if (var_rack_order == "") {
            TextView txtShoppingTripCompleted = (TextView) dialogView.findViewById(R.id.txtShoppingTripCompleted);
            txtShoppingTripCompleted.setText("Shopping Trip Completed");

            Button buttonPickUp = (Button) dialogView.findViewById(R.id.buttonPickUp);
            buttonPickUp.setText("Done");

        }
        Button buttonOk = (Button) dialogView.findViewById(R.id.buttonPickUp);


        TextView txtPath = (TextView) dialogView.findViewById(R.id.txtPath);
        txtPath.setText(var_rack_order);

        TextView txtTrolleyItems = (TextView) dialogView.findViewById(R.id.txtTrolleyItems);
        if (finalOutput != "") {
            txtTrolleyItems.setText(finalOutput);
        } else {
            txtTrolleyItems.setText("Trolley is Empty!!");
        }

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

        finalOutput += var_rack_order;

    }
}
