package cn.hxc.imgrecognition;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;
import java.util.Map;

public class PreferencesService {
    private Context context;
    public PreferencesService(Context context) {
        // TODO Auto-generated constructor stub
        this.context=context;
    }
    public void save(String index,String smsContent) {
        SharedPreferences preferences=context.getSharedPreferences("set", context.MODE_PRIVATE);
         Editor editor= preferences.edit();
         editor.putString(index,smsContent);
         editor.commit();         
    }
  
    public Map<String,String> getPreferences(String index){
        Map<String,String>params=new HashMap<String, String>();
        SharedPreferences prefers=context.getSharedPreferences("set", context.MODE_PRIVATE);
       // prefers.getString("content", "");
        params.put(index, prefers.getString(index, ""));
        return params;
    }
    
   
}
