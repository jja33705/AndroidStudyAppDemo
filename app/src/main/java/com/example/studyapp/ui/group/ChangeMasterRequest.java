package com.example.studyapp.ui.group;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ChangeMasterRequest extends StringRequest {
    final static private String URL = "https://www.dong0110.com/chatphp/ChangeMaster.php";
    private Map<String, String> parameters;

    public ChangeMasterRequest(String groupName, String changeMaster, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("groupName", groupName);
        parameters.put("changeMaster", changeMaster);
    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
