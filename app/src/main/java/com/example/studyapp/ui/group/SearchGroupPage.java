package com.example.studyapp.ui.group;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.studyapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.studyapp.FirstActivity.USER_ID;
import static com.example.studyapp.FirstActivity.userInfo;

public class SearchGroupPage extends AppCompatActivity {
    private RecyclerView groupRecyclerView;
    private SearchGroupRecyclerAdapter adapter;
    private ArrayList<Group> groupList;
    private String userID;
    private Button makeGroupButton;
    private EditText searchName_ET;
    private String searchName = "";
    private CheckBox university_CB;
    private CheckBox highSchool_CB;
    private CheckBox middleSchool_CB;
    private CheckBox elementarySchool_CB;
    private String university = "대학교";
    private String highSchool = "고등학교";
    private String middleSchool = "중학교";
    private String elementarySchool = "초등학교";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_group_page);

        getSupportActionBar().setTitle("Group");

        userID = userInfo.getString(USER_ID,null);
        groupList = new ArrayList<Group>();
        groupRecyclerView = findViewById(R.id.searchGroupRecyclerView);
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchGroupRecyclerAdapter(this, getApplicationContext(), groupList);
        groupRecyclerView.setAdapter(adapter);
        makeGroupButton = findViewById(R.id.makeGroupButton);
        makeGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchGroupPage.this, MakeGroup.class);
                startActivity(intent);
                finish();
            }
        });

        searchName_ET = findViewById(R.id.searchName_ET);
        searchName_ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchName = s.toString();
                new BackgroundTask().execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        university_CB = findViewById(R.id.university);
        university_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(university_CB.isChecked()) {
                    university = "대학교";
                } else {
                    university = "";
                }
                new BackgroundTask().execute();
            }
        });

        highSchool_CB = findViewById(R.id.highSchool);
        highSchool_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(highSchool_CB.isChecked()) {
                    highSchool = "고등학교";
                } else {
                    highSchool = "";
                }
                new BackgroundTask().execute();
            }
        });

        middleSchool_CB = findViewById(R.id.middleSchool);
        middleSchool_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(middleSchool_CB.isChecked()) {
                    middleSchool = "중학교";
                } else {
                    middleSchool = "";
                }
                new BackgroundTask().execute();
            }
        });

        elementarySchool_CB = findViewById(R.id.elementarySchool);
        elementarySchool_CB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(elementarySchool_CB.isChecked()) {
                    elementarySchool = "초등학교";
                } else {
                    elementarySchool = "";
                }
                new BackgroundTask().execute();
            }
        });

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String link = "https://www.dong0110.com/chatphp/SearchGroup.php?userID=" + userID  + "&searchName=" + searchName + "&university=" + university + "&highSchool=" + highSchool + "&middleSchool=" + middleSchool + "&elementarySchool=" + elementarySchool;
                URL url = new URL(link);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();
                while((temp = bufferedReader.readLine()) != null) {
                    stringBuilder.append(temp + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }

        @Override
        public void onPostExecute(String result) {
            try {
                int size = groupList.size();
                groupList.clear();
                adapter.notifyItemRangeRemoved(0, size);
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                Log.d("응답", ""+jsonArray.length());
                if(jsonArray.length() == 0) return;
                int count = 0;
                String groupName, contents, peopleCount, category, goalTime, master, startDate, memberLimit;
                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count);
                    groupName = object.getString("groupName");
                    contents = object.getString("contents");
                    peopleCount = object.getString("count");
                    category = object.getString("category");
                    goalTime = object.getString("goalTime");
                    master = object.getString("master");
                    startDate = object.getString("startDate");
                    memberLimit = object.getString("peopleLimit");
                    Group group = new Group(groupName, contents, peopleCount, category, goalTime, master, startDate, memberLimit);
                    groupList.add(group);
                    adapter.notifyDataSetChanged();
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}