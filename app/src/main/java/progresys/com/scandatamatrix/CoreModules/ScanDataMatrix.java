package progresys.com.scandatamatrix.CoreModules;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import progresys.com.scandatamatrix.R;
import progresys.com.scandatamatrix.Utils.CustomDialog;

public class ScanDataMatrix extends AppCompatActivity implements ZXingScannerView.ResultHandler{


    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_scandatamatrix);

        try {
            GetInitialize();
            OpenCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void GetInitialize() {

        ActionBar toolbar = getSupportActionBar();
        toolbar.setTitle("Code Scanner");


    }

    private void OpenCamera() {

        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

        Toast.makeText(this,"Scan the code..",Toast.LENGTH_SHORT).show();

        Constants.changeStatusBarColour(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);

        zXingScannerView.setSoundEffectsEnabled(true);
       // Toast.makeText(this,result.getText(),Toast.LENGTH_SHORT).show();
        CallNextActivity(result.getText());

    }


    public void CallNextActivity(String result)
    {

        Bundle passing_bundle=new Bundle();
        passing_bundle.putString("RESULT", result);
        Constants.globalStartIntent(ScanDataMatrix.this, Details.class, passing_bundle,1);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            recreate();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        CustomDialog customDialog=new CustomDialog(ScanDataMatrix.this);
        customDialog.setLayoutColor(R.color.red_400);
        customDialog.setImage(R.drawable.ic_exit_to_app_black_24dp);
        customDialog.setTitle("Information");
        customDialog.setDescription("Are you sure want to signout?");
        customDialog.setNegativeButtonTitle("No");
        customDialog.setPossitiveButtonTitle("Yes");
        customDialog.setOnPossitiveListener(new CustomDialog.possitiveOnClick() {
            @Override
            public void onPossitivePerformed() {

                Constants.globalStartIntent(ScanDataMatrix.this, Login.class, null, 2);

            }
        });
        customDialog.show();

    }


}//END
