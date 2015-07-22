package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.ScreenSche;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;

import java.io.Externalizable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SeatActivity extends Activity {
    private int screenScheId;
    private double price;

    int seat[]=new int[613];

    public CheckBox seat101;
    public CheckBox seat102;
    public CheckBox seat103;
    public CheckBox seat104;
    public CheckBox seat105;
    public CheckBox seat106;
    public CheckBox seat107;
    public CheckBox seat108;
    public CheckBox seat109;
    public CheckBox seat110;
    public CheckBox seat111;

    public CheckBox seat201;
    public CheckBox seat202;
    public CheckBox seat203;
    public CheckBox seat204;
    public CheckBox seat205;
    public CheckBox seat206;
    public CheckBox seat207;
    public CheckBox seat208;
    public CheckBox seat209;
    public CheckBox seat210;
    public CheckBox seat211;

    public CheckBox seat301;
    public CheckBox seat302;
    public CheckBox seat303;
    public CheckBox seat304;
    public CheckBox seat305;
    public CheckBox seat306;
    public CheckBox seat307;

    public CheckBox seat401;
    public CheckBox seat402;
    public CheckBox seat403;
    public CheckBox seat404;
    public CheckBox seat405;
    public CheckBox seat406;
    public CheckBox seat407;
    public CheckBox seat408;
    public CheckBox seat409;

    public CheckBox seat501;
    public CheckBox seat502;
    public CheckBox seat503;
    public CheckBox seat504;
    public CheckBox seat505;
    public CheckBox seat506;
    public CheckBox seat507;
    public CheckBox seat508;
    public CheckBox seat509;

    public CheckBox seat601;
    public CheckBox seat602;
    public CheckBox seat603;
    public CheckBox seat604;
    public CheckBox seat605;
    public CheckBox seat606;
    public CheckBox seat607;
    public CheckBox seat608;
    public CheckBox seat609;
    public CheckBox seat610;
    public CheckBox seat611;
    public ArrayList<CheckBox> mySeats=new ArrayList<CheckBox>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);

        screenScheId=getIntent().getIntExtra("screenScheId", -1);
        price=getIntent().getDoubleExtra("price", -1.00);
        LinearLayout seats=(LinearLayout)findViewById(R.id.seats);
        LinearLayout[] rows=new LinearLayout[6];
        for(int i=0;i< rows.length;i++){
            rows[i]=new LinearLayout(getApplicationContext());
            rows[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        int id=0;
        for(LinearLayout row:rows){

            for(int i=0;i<10;i++){
                id++;
               // CheckBox checkBox=new CheckBox(this,null,R.style.SeatStyle);
                final CheckBox checkBox=(CheckBox)getLayoutInflater().inflate(R.layout.mycheckbox,null);
//                checkBox.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                checkBox.setChecked(false);
//                checkBox.setBackgroundResource(R.drawable.radio);
                checkBox.setId(id);
                row.addView(checkBox);
                checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkBox.isChecked())
                            seatNum++;
                        else
                            seatNum--;
                        setSeat();
                    }
                });
                mySeats.add(checkBox);
            }
        }
        for(int i=rows.length-1;i>=0;i--)
            seats.addView(rows[i]);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        Button Btn1 = (Button) findViewById(R.id.seatbutton);
        Btn1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String s="";
                for(CheckBox checkBox:mySeats){
                    if(checkBox.isChecked()){
                        int id=checkBox.getId();
                        int row;
                        if(id%10==0)
                            row=id/10;
                        else
                            row=id/10+1;
                        int col=id-10*(row-1);
                        s+=row+"-"+col+";";
                    }
                }

                if(s.equals("")){
                    Toast.makeText(getApplicationContext(), "至少选一个座奥", Toast.LENGTH_LONG).show();
                    return;
                }
                s=s.substring(0,s.length()-1);
                Map<String, String> params = new HashMap<String, String>();
                params.put("screenScheId", String.valueOf(screenScheId));
                //todo ???
                params.put("price",String.valueOf(price));
                params.put("seat",s);
                SharedPreferences sharedPreferences=getSharedPreferences("account", Context.MODE_PRIVATE);
                int userId= sharedPreferences.getInt("userId",-1);
                params.put("userId",String.valueOf(userId));
                giveOrder(params);

            }

        });
    }
    private RequestQueue mQueue;
    public void giveOrder(Map<String, String> params){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.GIVE_ORDER_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        parseJson(response);
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
    public void parseJson(JSONObject response){
        try {
            int orderId=response.getInt("orderId");
            Intent intent=new Intent(SeatActivity.this,OrderDetailActivity.class);
            intent.putExtra("orderId",orderId);
            startActivity(intent);
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private int seatNum=0;
    public void setSeat(){
        String result="";
        for(CheckBox checkBox:mySeats){
            if(checkBox.isChecked()){
                int id=checkBox.getId();
                int row;
                if(id%10==0)
                    row=id/10;
                else
                    row=id/10+1;
                int col=id-10*(row-1);
                result+=row+"排"+col+"座 ";
            }
        }


//
//        String result="";
//        for(int i=0;i<612;i++) {
//            if(seat[i]==1) {
//                int col= i%100;
//                int row = i/100;
//                result+=row+"排"+col+"座 ";
//            }
//        }
        TextView tx=(TextView)findViewById(R.id.orderseatposition);
        TextView tx2=(TextView)findViewById(R.id.orderprice);
        double p=getIntent().getDoubleExtra("price",-1.00);
        tx.setText(result);
        tx2.setText(p*seatNum+"元");
    }
    public void orderseat(final CheckBox seatposition ,int a) {
        final int b = a;
        final CheckBox position = seatposition;
        position.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {


                    if (position.isChecked()) {
                        seatNum++;
                        seat[b] = 1;
                    } else {
                        seatNum--;
                        seat[b] = 0;
                    }
                    setSeat();
                }

        });
    }

}
