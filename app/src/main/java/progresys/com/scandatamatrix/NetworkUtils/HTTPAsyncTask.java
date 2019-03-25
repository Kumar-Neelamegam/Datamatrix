package progresys.com.scandatamatrix.NetworkUtils;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class HTTPAsyncTask extends AsyncTask<String, Void, String> {


    private CallBack mCb;
    LinkedHashMap<Object, Object> mData = null;
    LinkedHashMap<Object, Object> mParams = new LinkedHashMap<>();
    String mTypeOfRequest;
    String mStrToBeAppended = "?";
    boolean isPostDataInJSONFormat = false;
    boolean isPutDataInJSONFormat = false;
    boolean isPostDataInJSONArrayFormat = false;
    JSONObject mJSONPostData = null;
    JSONArray mJSONArrayPostData = null;
    Context mContext = null;
    int id;

    //*******************************************************************************************

    public HTTPAsyncTask(Context context, int id, CallBack c, HashMap<Object, Object> data, JSONObject jsonObj,JSONArray jsonArray, String request) {

        this.id=id;
        mContext = context;
        mCb = c;
        mTypeOfRequest = request;
        mJSONPostData = jsonObj;
        mJSONArrayPostData = jsonArray;
        //Log.i("JSONDATA", mJSONPostData.toString());
        if((data != null) && (jsonObj == null)){
            mData = (LinkedHashMap)data;


            //*******************************************************************************************
            //GET
            if(mTypeOfRequest.equalsIgnoreCase("GET")){
                Object key = null;
                Iterator<Object> it = mData.keySet().iterator();
                while(it.hasNext()){
                    key = it.next();
                    mParams.put(key, mData.get(key));
                    Log.d("Data", key.toString() + " " + mData.get(key).toString());
                }
                Iterator<Object>itParams = mParams.keySet().iterator();
                int sizeOfParams = mParams.size();
                int index = 0;
                while(itParams.hasNext()){
                    Object keyParams = itParams.next();
                    index++;
                    if (index == sizeOfParams){
                        mStrToBeAppended+=  keyParams + "=" + mParams.get(keyParams);
                        break;
                    }
                    mStrToBeAppended+=  keyParams + "=" + mParams.get(keyParams)+ "&";
                }
            }


            //*******************************************************************************************
            //POST
            if(mTypeOfRequest.equalsIgnoreCase("POST")){
                Object key = null;
                isPostDataInJSONFormat = false;
                Iterator<Object> it = mData.keySet().iterator();
                while(it.hasNext()){
                    key = it.next();
                    mParams.put(key, mData.get(key));
                }
            }
        }

        //*******************************************************************************************
        //POST with Data in jSON format
        if ((mData == null) && (mJSONPostData != null) && (mTypeOfRequest.equalsIgnoreCase("POST") == true)){
            isPostDataInJSONFormat = true;
        }


         //*******************************************************************************************
        //POST with Data in jSON Array format
        if ((mData == null) && (mJSONArrayPostData != null) && (mTypeOfRequest.equalsIgnoreCase("POST") == true)){
            isPostDataInJSONArrayFormat = true;
        }
         //*******************************************************************************************
        //PUT with Data in jSON Array format
        if ((mData == null) && (mJSONPostData != null) && (mTypeOfRequest.equalsIgnoreCase("PUT") == true)){
            isPutDataInJSONFormat = true;
        }




    }

    @Override
    protected String doInBackground(String... baseUrls) {
        //android.os.Debug.waitForDebugger();
        publishProgress(null);


        switch(mTypeOfRequest)
        {

            case "GET"://*******************************************************************************
                String finalURL = baseUrls[0]+ mStrToBeAppended;
                return HttpUtility.GET(finalURL);


            case "POST"://*******************************************************************************


                if(isPostDataInJSONArrayFormat == true)
                {
                    return HttpUtility.POST(baseUrls[0], mJSONArrayPostData);
                }


                if(isPostDataInJSONFormat == false){
                    return HttpUtility.POST(baseUrls[0],mParams );
                }
                else if(isPostDataInJSONFormat == true){
                    Log.i("JSONDATAPOSTMETHOd","JSON POST method to be called...");
                    return HttpUtility.POST(baseUrls[0], mJSONPostData);
                }


                break;

            case "PUT"://*******************************************************************************
                if(isPutDataInJSONFormat == true){
                    Log.i("JSONDATAPOSTMETHOd","JSON PUT method to be called...");
                    return HttpUtility.PUT(baseUrls[0], mJSONPostData);
                }
                break;


        }


        return null;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {

        if(id==0)
        {
            mCb.onResult(result);
        }else
        {
            mCb.onResultExtra(result,id);
        }


    }

    @Override
    protected void onProgressUpdate(Void...voids ) {
        mCb.onProgress();
    }
}