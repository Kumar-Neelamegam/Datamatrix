package progresys.com.scandatamatrix.CoreModules;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coursebuddy.karthaalabs.com.Utilities.LocalSharedPreference;
import progresys.com.scandatamatrix.NetworkUtils.CallBack;
import progresys.com.scandatamatrix.NetworkUtils.HTTPAsyncTask;
import progresys.com.scandatamatrix.R;
import progresys.com.scandatamatrix.Utils.Validations;

public class Login extends AppCompatActivity {

    @BindView(R.id.imgvw_logo)
    ImageView imgvwLogo;
    @BindView(R.id.edt_username)
    AppCompatEditText edtUsername;
    @BindView(R.id.edt_password)
    AppCompatEditText edtPassword;
    @BindView(R.id.txtvw_signup)
    LinearLayout txtvwSignup;
    @BindView(R.id.sign_up)
    TextView signUp;
    @BindView(R.id.chkbx_rememberme)
    CheckBox remember_me;
    @BindView(R.id.parent_relativelayout)
    RelativeLayout parent_layout;
    @BindView(R.id.txtvw_usernamelabel)
    TextView Label_Username;
    @BindView(R.id.txtvw_passwordlabel)
    TextView Label_Password;

    LocalSharedPreference sharedPreference;

    //***************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_login);

        try {

            GetInitialize();
            Controllisteners();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void Controllisteners() {


    }


    private void GetInitialize() {
        ButterKnife.bind(this);

        sharedPreference = new LocalSharedPreference(Login.this);

        boolean status = sharedPreference.getBoolean(Constants.Preferred_RememberMe_Status);
        if (status) {
            //Constants.globalStartIntent(Login.this, ScanDataMatrix.class, null,1);
            String UName = sharedPreference.getValue(Constants.Preferred_Username);
            String UPassword = sharedPreference.getValue(Constants.Preferred_Password);

            edtUsername.setText(UName);
            edtPassword.setText(UPassword);
            remember_me.setChecked(true);

        }

        YoYo.with(Techniques.BounceInDown)
                .duration(1500)
                .playOn(imgvwLogo);

        Constants.changeStatusBarColour(this);

        Constants.HighlightMandatory(getString(R.string.label_username), Label_Username);
        Constants.HighlightMandatory(getString(R.string.label_password), Label_Password);


    }


    UserData userData;

    @OnClick(R.id.btn_button)
    void onBtnButtonClick() {

        //    Constants.globalStartIntent(Login.this, Details.class, null,1);

        if (Validations.CheckLength(edtUsername) || Validations.CheckLength(edtPassword)) {
            userData = new UserData();
            userData.UName = edtUsername.getText().toString();
            userData.UPassword = edtPassword.getText().toString();

            if (remember_me.isChecked()) {
                sharedPreference.setBoolean(Constants.Preferred_RememberMe_Status, true);
                sharedPreference.setValue(Constants.Preferred_Username, userData.getUName());
                sharedPreference.setValue(Constants.Preferred_Password, userData.getUPassword());
            } else {
                sharedPreference.setBoolean(Constants.Preferred_RememberMe_Status, false);
                sharedPreference.setValue(Constants.Preferred_Username, "");
                sharedPreference.setValue(Constants.Preferred_Password, "");
            }

            if (Constants.CheckNetwork(Login.this)) {
                CheckServerLogin(userData.getUName(), userData.getUPassword());
            } else {
                ShowInternetDialog();
            }
        } else {
            Constants.SnackBar(Login.this, getString(R.string.validation_login), parent_layout, 2);

        }

    }


    private void ShowInternetDialog() {

        final Dialog dialog = new Dialog(Login.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_no_item_internet_image);

        ProgressBar progress_bar;
        LinearLayout lyt_no_connection;
        AppCompatButton bt_retry;

        progress_bar = dialog.findViewById(R.id.progress_bar);
        lyt_no_connection = dialog.findViewById(R.id.lyt_no_connection);
        bt_retry = dialog.findViewById(R.id.bt_retry);

        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);

        bt_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                progress_bar.setVisibility(View.VISIBLE);
                lyt_no_connection.setVisibility(View.GONE);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Constants.CheckNetwork(Login.this)) {
                            dialog.dismiss();
                            CheckServerLogin(userData.getUName(), userData.getUPassword());

                        } else {
                            progress_bar.setVisibility(View.GONE);
                            lyt_no_connection.setVisibility(View.VISIBLE);
                        }
                    }
                }, 1000);
            }
        });

        dialog.show();

    }

    private void CheckServerLogin(String StrUsername, String StrPassword) {

        try {

            String API_LOAD_CLASSES = Constants.APPLICATION_API + "Login";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Username", StrUsername);//StrUsername
            jsonObject.put("Password", StrPassword);//StrPassword
            HTTPAsyncTask asyncTask = new HTTPAsyncTask(Login.this, 0, callback, null, jsonObject, null, "POST");
            asyncTask.execute(API_LOAD_CLASSES);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        Constants.ExitDialog(this);
    }

    BottomSheetDialog dialog;

    final CallBack callback = new CallBack() {
        @Override
        public void onProgress() {

            View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
            dialog = new BottomSheetDialog(Login.this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
            dialog.show();

        }

        @Override
        public void onResult(String result) {
            try {

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();

                }

                JSONObject object = new JSONObject(result);
                String Str_Results = object.optString("Results");
                String Str_Message = object.optString("Message");
                JSONArray jsonArr = new JSONArray(Str_Results);

                sharedPreference.setValue(Constants.Preferred_UserData, result);


                if (jsonArr.length() == 0 || Str_Message.equalsIgnoreCase("Failed")) {

                    //Toast.makeText(Login.this, R.string.label_invalid, Toast.LENGTH_SHORT).show();
                    Constants.SnackBar(Login.this, getString(R.string.label_invalid), parent_layout, 2);
                    edtUsername.setText("");
                    edtPassword.setText("");
                    edtUsername.requestFocus();

                } else {//user valid

                    for (int i = 0; i < jsonArr.length(); i++) {
                        JSONObject jsonObj = jsonArr.getJSONObject(i);
                        sharedPreference.setValue("EMPLOYEE_ID", jsonObj.getString("EmpId"));
                        sharedPreference.setValue("EMPLOYEE_NAME", jsonObj.getString("Name"));
                    }
                    Constants.globalStartIntent(Login.this, ScanDataMatrix.class, null, 1);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onResultExtra(String result, int id) {


        }

        @Override
        public void onCancel() {


        }
    };


    public class UserData {
        String UName, UPassword;

        public UserData() {
        }

        public UserData(String UName, String UPassword) {
            this.UName = UName;
            this.UPassword = UPassword;
        }

        public String getUName() {
            return UName;
        }

        public void setUName(String UName) {
            this.UName = UName;
        }

        public String getUPassword() {
            return UPassword;
        }

        public void setUPassword(String UPassword) {
            this.UPassword = UPassword;
        }
    }

    //***************************************************************************************

}//END

