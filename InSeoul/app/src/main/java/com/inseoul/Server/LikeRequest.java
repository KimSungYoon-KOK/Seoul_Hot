package com.inseoul.Server;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LikeRequest extends StringRequest {
    final static private String URL= "http://ksun1234.cafe24.com/Like.php";
    private Map<String, String> parameters;

    public LikeRequest(String userID,String id  , Response.Listener<String> listener){

        super(Method.POST, URL, listener, null);
        parameters=new HashMap<>();
        parameters.put("ReviewID", userID);
        parameters.put("UserID", id);





    }
    @Override
    public Map<String, String>getParams(){


        return parameters;
    }

}
