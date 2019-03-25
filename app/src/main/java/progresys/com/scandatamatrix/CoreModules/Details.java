package progresys.com.scandatamatrix.CoreModules;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import coursebuddy.karthaalabs.com.Utilities.LocalSharedPreference;
import progresys.com.scandatamatrix.NetworkUtils.CallBack;
import progresys.com.scandatamatrix.NetworkUtils.HTTPAsyncTask;
import progresys.com.scandatamatrix.R;
import progresys.com.scandatamatrix.Utils.CustomDialog;

public class Details extends AppCompatActivity {


    @BindView(R.id.txtvw_productname)
    TextView txtvw_Result;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.parent_coordinatorlayout)
    CoordinatorLayout parent_coordinatorlayout;
    @BindView(R.id.layout_dynamicdata)
    LinearLayout dynamicLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_details);


        try {
            GetInitialize();

            Controllisterners();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void Controllisterners() {


        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMail();
            }
        });

    }

    String scanned_result = "";

    private void GetInitialize() {

        ButterKnife.bind(this);
        sharedPreference = new LocalSharedPreference(Details.this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.label_details_title));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        Constants.changeStatusBarColour(this);

        floatingActionButton.setVisibility(View.INVISIBLE);

        if (getIntent().getExtras() != null) {
            Bundle retain_string = getIntent().getExtras();
            scanned_result = retain_string.getString("RESULT");

        }


        LoadDataFromServer();


    }

    private void LoadDataFromServer() {
        if (Constants.CheckNetwork(Details.this)) {
            LoadInformation(scanned_result);

        } else {
            ShowInternetDialog();
        }
    }

    private void ShowInternetDialog() {

        final Dialog dialog = new Dialog(Details.this);
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
                        if (Constants.CheckNetwork(Details.this)) {
                            dialog.dismiss();
                            LoadInformation(scanned_result);

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


    LocalSharedPreference sharedPreference;


    private void LoadInformation(String PassKey) {

        try {

            String API_LOAD_CLASSES = Constants.APPLICATION_API + "getdetails/GetProductDetails";
            JSONObject jsonObject = new JSONObject();
            //jsonObject.put("ProductID", "PSMEMBWSNBxx01");
            jsonObject.put("ProductID", PassKey);
            sharedPreference.setValue("PRODUCT_ID", PassKey);
            HTTPAsyncTask asyncTask = new HTTPAsyncTask(Details.this, 0, callback, null, jsonObject, null, "POST");
            asyncTask.execute(API_LOAD_CLASSES);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void SendMail() {

        Bundle b = new Bundle();
        b.putString("DATA_INFO", str_pass.toString());
        Constants.globalStartIntent2(Details.this, SendMail.class, b);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);

        menu.getItem(0).setIcon(R.drawable.ic_account_circle_black_24dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {

            Constants.globalStartIntent(Details.this, ScanDataMatrix.class, null, 2);

        }
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_refresh) {

            ShowUserinfo();
            return true;
        } else if (id == R.id.action_exit) {

            Constants.ExitDialog(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowUserinfo() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.activity_userprofile);
        TextView EmployeeName, EmployeeDesignation;
        ImageButton exit, close;

        EmployeeName=dialog.findViewById(R.id.employee_name);
        EmployeeDesignation=dialog.findViewById(R.id.employee_destination);

        EmployeeName.setText(sharedPreference.getValue("EMPLOYEE_NAME"));
        EmployeeDesignation.setText(sharedPreference.getValue("EMPLOYEE_ID"));

        exit = dialog.findViewById(R.id.button_exit);
        close = dialog.findViewById(R.id.button_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog customDialog = new CustomDialog(Details.this);
                customDialog.setLayoutColor(R.color.red_400);
                customDialog.setImage(R.drawable.ic_exit_to_app_black_24dp);
                customDialog.setTitle("Information");
                customDialog.setDescription("Are you sure want to signout?");
                customDialog.setNegativeButtonTitle("No");
                customDialog.setPossitiveButtonTitle("Yes");
                customDialog.setOnPossitiveListener(new CustomDialog.possitiveOnClick() {
                    @Override
                    public void onPossitivePerformed() {
                        dialog.dismiss();
                        Constants.globalStartIntent(Details.this, Login.class, null, 2);

                    }
                });
                customDialog.show();

            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();
    }

    BottomSheetDialog dialog;
    StringBuilder str_pass = new StringBuilder();

    final CallBack callback = new CallBack() {
        @Override
        public void onProgress() {

            View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
            dialog = new BottomSheetDialog(Details.this);
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

                Constants.Logger(result, Details.this);

                if (jsonArr.length() == 0 && Str_Message.equalsIgnoreCase("Failed")) {

                    Constants.SnackBar(Details.this, getString(R.string.label_noresult), parent_coordinatorlayout, 2);
                    txtvw_Result.setText(R.string.label_noproduct);

                } else {

                    if (jsonArr.length() > 0) {
                        str_pass.append("Product Details\n");
                        str_pass.append("*******************\n");
                        for (int i = 0; i < jsonArr.length(); i++) {
                            JSONObject jsonObj = jsonArr.getJSONObject(i);

                            for (Iterator<String> it = jsonObj.keys(); it.hasNext(); ) {
                                Object key = it.next();
                                //based on you key types
                                String keyStr = (String) key;
                                Object keyvalue = jsonObj.get(keyStr);

                                //Print key and value
                                //Constants.Logger("key: "+ keyStr + " value: " + keyvalue ,Details.this);

                                //for nested objects iteration if required
                                //if (keyvalue instanceof JSONObject)
                                // {
                                //     Constants.Logger(String.valueOf((JSONObject)keyvalue),Details.this);
                                // }
                                //Constants.Logger(String.valueOf((JSONObject)keyvalue),Details.this);

                                DynamicLayout(keyStr, keyvalue.toString());
                                str_pass.append(keyStr + ":" + Constants.CheckString(keyvalue.toString()) + "\n");
                            }
                        }

                        floatingActionButton.setVisibility(View.VISIBLE);

                    } else {
                        Constants.SnackBar(Details.this, getString(R.string.label_nodata), parent_coordinatorlayout, 2);
                    }


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


    public void DynamicLayout(String Label, String Value) {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row_item_dynamic, null);
        TextView txtvw1, txtvw2;
        txtvw1 = view.findViewById(R.id.txtvw1);
        txtvw2 = view.findViewById(R.id.txtvw2);
        txtvw1.setText(Constants.CheckString(Label));
        txtvw2.setText(Constants.CheckString(Value));
        dynamicLayout.addView(view);
    }

    //**********************************************************************************************

    @Override
    public void onBackPressed() {

        Constants.globalStartIntent(Details.this, ScanDataMatrix.class, null, 2);

    }

    //**********************************************************************************************


}//end
