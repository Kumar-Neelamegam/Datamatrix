package progresys.com.scandatamatrix.Utils;

import android.widget.EditText;

public class Validations {

    /**
     * To check edittext length is empty
     * @param editText
     * @return
     */
    public static boolean CheckLength(EditText editText)
    {
        return editText.getText().length()!=0 ? true : false;

    }

    /**
     * Get the value of the edittext
     * @param edt
     * @return
     */
    public String GetEdittextValue(EditText edt)
    {

        return edt.getText().length()==0 && edt!=null? "" : edt.getText().toString();
    }



}//End
