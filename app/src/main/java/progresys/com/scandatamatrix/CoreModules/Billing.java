package progresys.com.scandatamatrix.CoreModules;

import android.os.Bundle;

import progresys.com.scandatamatrix.R;
import progresys.com.scandatamatrix.Utils.Application;

public class Billing extends Application {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_billing);

        GetInitialize();
        Controllisteners();

    }

    private void Controllisteners() {
        
    }

    private void GetInitialize() {

    }


}//End
