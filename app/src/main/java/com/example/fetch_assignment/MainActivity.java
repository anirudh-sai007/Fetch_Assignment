package com.example.fetch_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList=new ArrayList<>();

    private static final String url_json="https://fetch-hiring.s3.amazonaws.com/hiring.json";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= findViewById(R.id.view);
        adapter= new CustomAdapter(this, arrayList);
        listView.setAdapter(adapter);
        datapull();
    }

    private void datapull() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL(url_json);
                    HttpURLConnection httpURLConnection= (HttpURLConnection) url.openConnection();
                    httpURLConnection.connect();

                    InputStream inputStream=httpURLConnection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb=new StringBuilder();
                    String row;
                    while((row =reader.readLine()) !=null)
                    {
                        sb.append(row);
                    }

                    reader.close();
                    httpURLConnection.disconnect();

                    //sort by listid

                    JSONArray jsonArray=new JSONArray(sb.toString());
                    List<ItemsList> listItems=new ArrayList<>();
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        int id=jsonObject.getInt("id");
                        int listid=jsonObject.getInt("listId");
                        String name= jsonObject.isNull("name") ? null : jsonObject.getString("name");
                        if(name==null || name.trim().isEmpty())
                        {
                            continue;
                        }
                        listItems.add(new ItemsList(id,listid,name));

                    }

                    //group by list id and name

                    Collections.sort(listItems, new Comparator<ItemsList>() {
                        @Override
                        public int compare(ItemsList itemsList1, ItemsList itemsList2) {
                            int x= Integer.compare(itemsList1.getListid(),itemsList2.getListid());
                            if(x==0)
                                return itemsList1.getName().compareTo(itemsList2.getName());

                            return x;
                        }
                    });

                    ArrayList<String> arrayList1=new ArrayList<>();
                    int pos=-1;
                    for(ItemsList i: listItems)
                    {
                       if(i.getListid()!= pos)
                       {
                           pos= i.getListid();
                           arrayList1.add("List ID: "+pos);

                       }
                       arrayList1.add(" "+i.getName());
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayList.clear();
                            arrayList.addAll(arrayList1);
                            adapter.notifyDataSetChanged();

                        }
                    });

                }
                catch (Exception e)
                {
//                    Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).start();
    }
    // I want to make the list id text bold
    public static class CustomAdapter extends ArrayAdapter<String>
    {
        public CustomAdapter(MainActivity activity, ArrayList<String> stringArrayList)
        {
            super(activity, android.R.layout.simple_list_item_1, stringArrayList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view=super.getView(position, convertView, parent);
            TextView textView= view.findViewById(android.R.id.text1);
            String boldtext= getItem(position);
            if(boldtext!=null && boldtext.startsWith("List ID: "))
            {
                SpannableString spannableString=new SpannableString(boldtext);
                spannableString.setSpan(new StyleSpan(Typeface.BOLD),0,textView.length(),spannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.DKGRAY),0,textView.length(),spannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);

            }
            else
            {
                textView.setText(boldtext);
            }
            return view;
        }
    }
}