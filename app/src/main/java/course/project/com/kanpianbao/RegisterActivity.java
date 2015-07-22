package course.project.com.kanpianbao;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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


public class RegisterActivity extends Activity {
    private RequestQueue mQueue;
    private static String[] year = { "2015", "2016", "2017", "2018", "2019" };
    private static String[] month = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "n10", "n11", "12" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mQueue = Volley.newRequestQueue(getApplicationContext());

        Spinner spin_year = (Spinner) findViewById(R.id.spin_year);
        ArrayAdapter<String> adapter_year = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, year);
        adapter_year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_year.setAdapter(adapter_year);
        spin_year.setVisibility(View.VISIBLE);

        Spinner spin_month = (Spinner) findViewById(R.id.spin_month);
        ArrayAdapter<String> adapter_month = new ArrayAdapter<String>(RegisterActivity.this, android.R.layout.simple_spinner_item, month);
        adapter_month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_month.setAdapter(adapter_month);
        spin_month.setVisibility(View.VISIBLE);

        Button res = (Button) findViewById(R.id.res_button);
        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }
    public void register(){
        final EditText account=(EditText)findViewById(R.id.account);
        final EditText password=(EditText)findViewById(R.id.password);
        final EditText bankNum=(EditText)findViewById(R.id.bank_num);
        String  phone = account.getText().toString();
        String userPwd=password.getText().toString();
        String accountNum=bankNum.getText().toString();


        Map<String, String> params = new HashMap<String, String>();
        params.put("phone", phone);
        params.put("userPwd", userPwd);
        params.put("accountNum", accountNum);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.REGISTER_URL, new JSONObject(params),
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
    public void dealResponse(JSONObject response) {
        try {
            boolean status= response.getBoolean("status");
            if(status){
                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                Intent res_intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(res_intent);
            }
            else
                Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

