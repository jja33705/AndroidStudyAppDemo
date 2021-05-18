package com.example.studyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private EditText idET, passwordET;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        idET = (EditText) findViewById(R.id.idET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idET.getText().toString();
                String userPassword = passwordET.getText().toString();

                if(userID.isEmpty()) {
                    negativeBuilder("아이디를 입력해주세요.");
                    return;
                }
                if(userPassword.isEmpty()) {
                    negativeBuilder("비밀번호를 입력해주세요.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.accumulate("user_id", userID);
                    jsonObject.accumulate("user_password", userPassword);

                    LoginTask loginTask = new LoginTask(jsonObject, "login", "POST");
                    loginTask.execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void negativeBuilder(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(msg)
                .setNegativeButton("확인", null)
                .create()
                .show();
    }

    class LoginTask extends JSONTask {

        public LoginTask(JSONObject jsonObject, String urlPath, String method) {
            super(jsonObject, urlPath, method);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                String resultNum = jsonObject.get("result").toString();

                String userID = idET.getText().toString();
                String userPassword = passwordET.getText().toString();

                if(resultNum.equals("1")) {
                    // Save user info
                    SharedPreferences.Editor autoLogin = FirstActivity.userInfo.edit();
                    autoLogin.putString(FirstActivity.USER_ID, userID);
                    autoLogin.putString(FirstActivity.USER_PASSWORD, userPassword);
                    autoLogin.commit();

                    progressBar.setVisibility(View.GONE);

                    // Next Screen
                    Intent intent = new Intent(LoginActivity.this, UserNameActivity.class);
                    LoginActivity.this.startActivity(intent);
                    finish();
                }
                else {
                    negativeBuilder("회원정보를 확인해주세요.");
                    progressBar.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}