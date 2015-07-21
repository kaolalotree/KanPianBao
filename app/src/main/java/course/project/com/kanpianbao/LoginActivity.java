package course.project.com.kanpianbao;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        final Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent login_intent = new Intent(LoginActivity.this, MainActivity.class);
//                startActivity(login_intent);
                login();
            }
        });

        Button register=(Button)findViewById((R.id.register_button));
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    public void login(){
        final EditText account=(EditText)findViewById(R.id.account);
        final EditText password=(EditText)findViewById(R.id.password);
        String  phone = account.getText().toString();
        String userPwd=password.getText().toString();



        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("userPwd", userPwd);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.LOGIN_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        dealResponse(response);
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
        mQueue.add(jsonObjectRequest);
    }

    public void dealResponse(JSONObject response){
        try {
            boolean status= response.getBoolean("status");
            if(status){
            //todo store preference
                Intent register_intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(register_intent);
                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}