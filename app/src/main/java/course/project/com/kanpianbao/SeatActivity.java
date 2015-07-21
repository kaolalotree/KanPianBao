package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
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

import java.io.Externalizable;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);
        seat101 = (CheckBox) findViewById(R.id.seat_101);
        seat102 = (CheckBox) findViewById(R.id.seat_102);
        seat103 = (CheckBox) findViewById(R.id.seat_103);
        seat104 = (CheckBox) findViewById(R.id.seat_104);
        seat105 = (CheckBox) findViewById(R.id.seat_105);
        seat106 = (CheckBox) findViewById(R.id.seat_106);
        seat107 = (CheckBox) findViewById(R.id.seat_107);
        seat108 = (CheckBox) findViewById(R.id.seat_108);
        seat109 = (CheckBox) findViewById(R.id.seat_109);
        seat110 = (CheckBox) findViewById(R.id.seat_110);
        seat111 = (CheckBox) findViewById(R.id.seat_111);

        seat201 = (CheckBox) findViewById(R.id.seat_201);
        seat202 = (CheckBox) findViewById(R.id.seat_202);
        seat203 = (CheckBox) findViewById(R.id.seat_203);
        seat204 = (CheckBox) findViewById(R.id.seat_204);
        seat205 = (CheckBox) findViewById(R.id.seat_205);
        seat206 = (CheckBox) findViewById(R.id.seat_206);
        seat207 = (CheckBox) findViewById(R.id.seat_207);
        seat208 = (CheckBox) findViewById(R.id.seat_208);
        seat209 = (CheckBox) findViewById(R.id.seat_209);
        seat210 = (CheckBox) findViewById(R.id.seat_210);
        seat211 = (CheckBox) findViewById(R.id.seat_211);

        seat301 = (CheckBox) findViewById(R.id.seat_301);
        seat302 = (CheckBox) findViewById(R.id.seat_302);
        seat303 = (CheckBox) findViewById(R.id.seat_303);
        seat304 = (CheckBox) findViewById(R.id.seat_304);
        seat305 = (CheckBox) findViewById(R.id.seat_305);
        seat306 = (CheckBox) findViewById(R.id.seat_306);
        seat307 = (CheckBox) findViewById(R.id.seat_307);

        seat401 = (CheckBox) findViewById(R.id.seat_401);
        seat402 = (CheckBox) findViewById(R.id.seat_402);
        seat403 = (CheckBox) findViewById(R.id.seat_403);
        seat404 = (CheckBox) findViewById(R.id.seat_404);
        seat405 = (CheckBox) findViewById(R.id.seat_405);
        seat406 = (CheckBox) findViewById(R.id.seat_406);
        seat407 = (CheckBox) findViewById(R.id.seat_407);
        seat408 = (CheckBox) findViewById(R.id.seat_408);
        seat409 = (CheckBox) findViewById(R.id.seat_409);

        seat501 = (CheckBox) findViewById(R.id.seat_501);
        seat502 = (CheckBox) findViewById(R.id.seat_502);
        seat503 = (CheckBox) findViewById(R.id.seat_503);
        seat504 = (CheckBox) findViewById(R.id.seat_504);
        seat505 = (CheckBox) findViewById(R.id.seat_505);
        seat506 = (CheckBox) findViewById(R.id.seat_506);
        seat507 = (CheckBox) findViewById(R.id.seat_507);
        seat508 = (CheckBox) findViewById(R.id.seat_508);
        seat509 = (CheckBox) findViewById(R.id.seat_509);

        seat601 = (CheckBox) findViewById(R.id.seat_601);
        seat602 = (CheckBox) findViewById(R.id.seat_602);
        seat603 = (CheckBox) findViewById(R.id.seat_603);
        seat604 = (CheckBox) findViewById(R.id.seat_604);
        seat605 = (CheckBox) findViewById(R.id.seat_605);
        seat606 = (CheckBox) findViewById(R.id.seat_606);
        seat607 = (CheckBox) findViewById(R.id.seat_607);
        seat608 = (CheckBox) findViewById(R.id.seat_608);
        seat609 = (CheckBox) findViewById(R.id.seat_609);
        seat610 = (CheckBox) findViewById(R.id.seat_610);
        seat611 = (CheckBox) findViewById(R.id.seat_611);


        orderseat(seat101,101);
        orderseat(seat102,102);
        orderseat(seat103,103);
        orderseat(seat104,104);
        orderseat(seat105,105);
        orderseat(seat106,106);
        orderseat(seat107,107);
        orderseat(seat108,108);
        orderseat(seat109,109);
        orderseat(seat110,110);
        orderseat(seat111,111);

        orderseat(seat201,201);
        orderseat(seat202,202);
        orderseat(seat203,203);
        orderseat(seat204,204);
        orderseat(seat205,205);
        orderseat(seat206,206);
        orderseat(seat207,207);
        orderseat(seat208,208);
        orderseat(seat209,209);
        orderseat(seat210,210);
        orderseat(seat211,211);

        orderseat(seat301,301);
        orderseat(seat302,302);
        orderseat(seat303,303);
        orderseat(seat304,304);
        orderseat(seat305,305);
        orderseat(seat306,306);
        orderseat(seat307,307);

        orderseat(seat401,401);
        orderseat(seat402,402);
        orderseat(seat403,403);
        orderseat(seat404,404);
        orderseat(seat405,405);
        orderseat(seat406,406);
        orderseat(seat407,407);
        orderseat(seat408,408);
        orderseat(seat409,409);

        orderseat(seat501,501);
        orderseat(seat502,502);
        orderseat(seat503,503);
        orderseat(seat504,504);
        orderseat(seat505,505);
        orderseat(seat506,506);
        orderseat(seat507,507);
        orderseat(seat508,508);
        orderseat(seat509,509);

        orderseat(seat601,601);
        orderseat(seat602,602);
        orderseat(seat603,603);
        orderseat(seat604,604);
        orderseat(seat605,605);
        orderseat(seat606,606);
        orderseat(seat607,607);
        orderseat(seat608,608);
        orderseat(seat609,609);
        orderseat(seat610,610);
        orderseat(seat611,611);


        for(int i=0;i<613;i++)
        {
            seat[i] = 0;
        }
        screenScheId=getIntent().getIntExtra("screenScheId",-1);
        price=getIntent().getDoubleExtra("price", -1.00);

        mQueue = Volley.newRequestQueue(getApplicationContext());
        Button Btn1 = (Button) findViewById(R.id.seatbutton);
        Btn1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String s="";
                for(int i=0;i<612;i++) {
                    if(seat[i]==1) {
                        int col= i%100;
                        int row = i/100;
                        s+=row+"-"+col+";";
                    }
                }

                if(s.equals("")){
                    Toast.makeText(getApplicationContext(), "����Ҫѡ��һ����λӴ", Toast.LENGTH_LONG).show();
                    return;
                }
                s=s.substring(0,s.length()-1);
                Map<String, String> params = new HashMap<String, String>();
                params.put("screenScheId", String.valueOf(screenScheId));
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

    public void orderseat(final CheckBox seatposition ,int a) {
        final int b = a;
        final CheckBox position = seatposition;
        position.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position.isChecked()) {
                    seat[b] = 1;
                } else {
                    seat[b] = 0;
                }
            }
        });
    }

}
