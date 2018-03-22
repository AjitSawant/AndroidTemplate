package com.palash.sampleapp.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.palash.sampleapp.R;
import com.palash.sampleapp.api.JsonObjectMapper;
import com.palash.sampleapp.api.WebServiceConsumer;
import com.palash.sampleapp.database.DatabaseAdapter;
import com.palash.sampleapp.database.DatabaseContract;
import com.palash.sampleapp.entiry.ELLogin;
import com.palash.sampleapp.utilities.Constants;
import com.palash.sampleapp.utilities.LocalSettings;
import com.squareup.okhttp.Response;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private DatabaseContract databaseContract;
    private DatabaseAdapter databaseAdapter;
    private DatabaseAdapter.SyncAdapter syncAdapter;
    private DatabaseAdapter.LoginAdapter loginAdapter;

    private EditText login_edt_username;
    private EditText login_edt_password;
    private CheckBox login_cb_remember_me;
    //private RadioButton login_type_employee_rbtn;
    //private RadioButton login_type_doctor_rbtn;
    private Button login_btn_sign_in;

    private ArrayList<ELLogin> listLogin;

    private String Username;
    private String Password;
    private boolean RememberMe;
    //private boolean loginType = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Init();
        InitView();
        CheckPermission();
        //SetUpSynchronization();
    }

    private void CheckPermission() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_CONTACTS}, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Init() {
        try {
            context = this;
            databaseContract = new DatabaseContract(context);
            databaseAdapter = new DatabaseAdapter(databaseContract);
            syncAdapter = databaseAdapter.new SyncAdapter();
            loginAdapter = databaseAdapter.new LoginAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void InitView() {
        try {
            login_edt_username = (EditText) findViewById(R.id.login_edt_username);
            login_edt_password = (EditText) findViewById(R.id.login_edt_password);
            login_cb_remember_me = (CheckBox) findViewById(R.id.login_cb_remember_me);
            //login_type_employee_rbtn = (RadioButton) findViewById(R.id.login_type_employee_rbtn);
            //login_type_doctor_rbtn = (RadioButton) findViewById(R.id.login_type_doctor_rbtn);
            login_btn_sign_in = (Button) findViewById(R.id.login_btn_sign_in);

            login_btn_sign_in.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        ShowLogin();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn_sign_in:
                if (Validate()) {
                    Username = login_edt_username.getText().toString();
                    Password = login_edt_password.getText().toString();
                    RememberMe = login_cb_remember_me.isChecked();
                    //loginType = login_type_employee_rbtn.isChecked();
                    new LoginTask().execute();
                }
                break;
        }
    }

    private boolean Validate() {
        try {
            if (login_edt_username.getText().toString().trim().length() == 0) {
                Toast.makeText(context, "Please enter username.", Toast.LENGTH_SHORT).show();
                login_edt_username.requestFocus();
                return false;
            } else if (login_edt_password.getText().toString().trim().length() == 0) {
                Toast.makeText(context, "Please enter password.", Toast.LENGTH_SHORT).show();
                login_edt_password.requestFocus();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void ShowLogin() {
        try {
            listLogin = loginAdapter.listAll();
            if (listLogin != null && listLogin.size() > 0 && listLogin.get(0).getRememberMe().equals("true")) {
                login_edt_username.setText(listLogin.get(0).getLoginName());
                login_edt_password.setText(listLogin.get(0).getPassword());
                login_cb_remember_me.setChecked(true);
                /*if (listLogin.get(0).getLoginType() != null && (listLogin.get(0).getLoginType().equals("True")
                        || listLogin.get(0).getLoginType().equals("true") || listLogin.get(0).getLoginType().equals("1"))) {
                    login_type_employee_rbtn.setChecked(false);
                    login_type_doctor_rbtn.setChecked(true);
                } else {
                    login_type_doctor_rbtn.setChecked(false);
                    login_type_employee_rbtn.setChecked(true);
                }*/
            } else {
                ClearView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ClearView() {
        try {
            login_edt_username.setText(null);
            login_edt_password.setText(null);
            login_cb_remember_me.setChecked(false);
            //login_type_employee_rbtn.setChecked(true);
            //login_type_doctor_rbtn.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LoginTask extends AsyncTask<Void, Void, String> {
        private JsonObjectMapper objectMapper;
        private WebServiceConsumer serviceConsumer;
        private Response response;
        private int responseCode = 0;
        private ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute() {
            progressDialog = LocalSettings.showDialog(context, "Please wait while log you in...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String responseString = "";
            try {
                /*String mLoginBy = "1";
                if (loginType == true) {
                    mLoginBy = "1";
                } else {
                    mLoginBy = "0";
                }*/
                serviceConsumer = new WebServiceConsumer(context, Username, Password);
                Log.d(Constants.TAG, "Time Start: " + DateFormat.getDateTimeInstance().format(new Date()));
                response = serviceConsumer.GET(Constants.LOGIN_URL);
                Log.d(Constants.TAG, "Time End: " + DateFormat.getDateTimeInstance().format(new Date()));
                if (response != null) {
                    responseString = response.body().string();
                    responseCode = response.code();
                    Log.d(Constants.TAG, "Response code:" + responseCode);
                    Log.d(Constants.TAG, "Response string:" + responseString);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            LocalSettings.hideDialog(progressDialog);
            try {
                if (responseCode == Constants.HTTP_OK_200) {
                    String responseString = LocalSettings.cleanResponseString(result);
                    objectMapper = new JsonObjectMapper();
                    listLogin = objectMapper.map(responseString, ELLogin.class);
                    if (listLogin != null && listLogin.size() > 0) {
                        saveProfile(listLogin.get(0));
                    }
                } else {
                    LocalSettings.alertBox(context, LocalSettings.handleError(responseCode), false);
                }
            } catch (Exception e) {
                LocalSettings.alertBox(context, "Network is unreachable. Please check your network and try again.", false);
            }
            super.onPostExecute(result);
        }
    }

    private void saveProfile(ELLogin elLogin) {
        try {
            if (elLogin != null) {
                RememberMe = login_cb_remember_me.isChecked();
                elLogin.setRememberMe(String.valueOf(RememberMe));
                elLogin.setLoginStatus(Constants.STATUS_LOG_IN);
                loginAdapter.delete();
                loginAdapter.create(elLogin);
                //startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void SetUpSynchronization() {
        try {
            elSync = new ELSync();
            elSync.setDateTime(DateFormat.getDateTimeInstance().format(new Date()));
            elSync.setIsSyncing("0");
            syncAdapter.Add(elSync);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
