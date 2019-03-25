package progresys.com.scandatamatrix.CoreModules;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import coursebuddy.karthaalabs.com.Utilities.LocalSharedPreference;
import progresys.com.scandatamatrix.NetworkUtils.CallBack;
import progresys.com.scandatamatrix.NetworkUtils.HTTPAsyncTask;
import progresys.com.scandatamatrix.R;
import progresys.com.scandatamatrix.Utils.Application;
import progresys.com.scandatamatrix.Utils.Mail;

/**
 * Created by Muthukumar Neelamegam on 1/20/2019.
 * ScanDataMatrix
 */
public class SendMail extends Application {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edt_quantity) AppCompatEditText edtQunatity;
    @BindView(R.id.parent_sendmail)
    LinearLayout parent;
    @BindView(R.id.txtvw_content)
    TextView Content;

    //***************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_sendmail);

        try {

            GetInitialize();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    String Product_Info="";
    StringBuilder content=new StringBuilder();

    String PRODUCT_ID  ="";
    String EMPLOYEE_ID ="";
    String EMPLOYEE_NAME ="";

    private void GetInitialize() {

        ButterKnife.bind(this);

        sharedPreference = new LocalSharedPreference(SendMail.this);
        PRODUCT_ID = sharedPreference.getValue("PRODUCT_ID");
        EMPLOYEE_ID = sharedPreference.getValue("EMPLOYEE_ID");
        EMPLOYEE_NAME = sharedPreference.getValue("EMPLOYEE_NAME");

        setSupportActionBar(toolbar);
        toolbar.setTitle("Send Email");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        Constants.changeStatusBarColour(this);

        if (getIntent().getExtras() != null) {
            Bundle retain_string = getIntent().getExtras();
            Product_Info = retain_string.getString("DATA_INFO");
        }


        PrepareContent();//Mail Content



    }

    private void PrepareContent() {

        //edtSubject.setText("Reg: Product Enquiry | Progresys ME GmbH");


        Content.setText(Product_Info+"\n");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if(item.getItemId() == R.id.action_done)
        {
            if(edtQunatity.getText().length()>0)
            {
                SendEmail();
            }else
            {
                Constants.SnackBar(SendMail.this, "Enter quantity?", parent, 2);
            }

        }
        else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendEmail() {

        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.execute();

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }


    BottomSheetDialog dialog;
    boolean status=false;
    public class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {

        Mail m;
        Login activity;

        public SendEmailAsyncTask() {
        }

        @Override
        protected void onPreExecute() {
            View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
            dialog = new BottomSheetDialog(SendMail.this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {


            try {

                status = Mail();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
              //  Constants.SnackBar(SendMail.this, "Unexpected error occured.", parent_layout, 2);
              //  Toast.makeText(SendMail.this, "Unexpected error occured.", Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();

            }
            if (status) {

                Toast.makeText(SendMail.this, "Email Sent..", Toast.LENGTH_SHORT).show();
                SendMail.this.finish();

            } else {

                Toast.makeText(SendMail.this, "Email failed to send.. \nTry Later..", Toast.LENGTH_SHORT).show();

            }

        }
    }




    Logger LOGGER = Logger.getAnonymousLogger();
    String SERVER_SMTP      = "smtp.office365.com";
    int SERVER_PORT         = 587;
    String SERVER_USERNAME  = "";
    String SERVER_PASSWORD  = "";
    String FROM = "";
    String TO = "";
    String SUBJECT = "";
    String MESSAGE = "";


    public boolean Mail(){

        try {
            content.append("Hello, \n");
            content.append("This mail is regarding the enquiry/need of the product.\n");
            content.append("Here in below attached the product details for your perusal.\n");
            content.append("\n");
            content.append(Product_Info+"\n");
            content.append("\n");
            content.append("No of quantity needed: "+ edtQunatity.getText());
            content.append("\n");
            content.append("******************");
            content.append("\n");
            content.append("Thanks & Regards\n");
            content.append(EMPLOYEE_NAME);


            FROM = getString(R.string.FROM);
            SERVER_USERNAME  = getString(R.string.SERVER_USERNAME);
            SERVER_PASSWORD  = getString(R.string.SERVER_PASSWORD);
            TO = "support@progresys.com";
            SUBJECT = "Reg: Product Enquiry | Progresys ME GmbH | "+ Constants.GetCurrentDate();
            MESSAGE = content.toString();

            final Session session = Session.getInstance(this.getEmailProperties(), new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SERVER_USERNAME, SERVER_PASSWORD);
                }

            });

            try {
                final Message message = new MimeMessage(session);
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(TO));
                message.setFrom(new InternetAddress(FROM));
                message.setSubject(SUBJECT);
                message.setText(MESSAGE);
                message.setSentDate(new Date());
                Transport.send(message);


                //Push data to server
                SendEnquiryDetails();

                return true;

            } catch (final MessagingException ex) {
                LOGGER.log(Level.WARNING, "Error: " + ex.getMessage(), ex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public Properties getEmailProperties() {
        final Properties config = new Properties();
        config.put("mail.smtp.auth", "true");
        config.put("mail.smtp.starttls.enable", "true");
        config.put("mail.smtp.host", SERVER_SMTP);
        config.put("mail.smtp.port", SERVER_PORT);
        return config;
    }



    LocalSharedPreference sharedPreference;

    private void SendEnquiryDetails() {


        try {

            String API_LOAD_CLASSES = Constants.APPLICATION_API + "PostDetails/postEnquiryDetails";
            JSONObject jsonObject = new JSONObject();

            Constants.Logger(PRODUCT_ID+"/"+EMPLOYEE_ID, SendMail.this);
            jsonObject.put("ProductID", PRODUCT_ID);
            jsonObject.put("UserID", EMPLOYEE_ID);
            HTTPAsyncTask asyncTask = new HTTPAsyncTask(SendMail.this, 0, callback, null, jsonObject, null, "POST");
            asyncTask.execute(API_LOAD_CLASSES);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    final CallBack callback = new CallBack() {
        @Override
        public void onProgress() {

        }

        @Override
        public void onResult(String result) {
            try {

                JSONObject object = new JSONObject(result);
                String Str_Results = object.optString("Results");
                String Str_Message = object.optString("Message");
                JSONArray jsonArr = new JSONArray(Str_Results);

                Constants.Logger(result, SendMail.this);

                if (jsonArr.length() == 0 && Str_Message.equalsIgnoreCase("Failed")) {

                    Constants.SnackBar(SendMail.this, getString(R.string.label_noresult), parent, 2);

                } else {

                    Constants.Logger("Data sent to server..", SendMail.this);

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

}//END
//***************************************************************************************
