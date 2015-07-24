package course.project.com.kanpianbao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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
import com.kpb.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SeatActivity extends Activity {
    private int screenScheId;
    private double price;
    public ArrayList<CheckBox> mySeats=new ArrayList<CheckBox>();
    public void getSeats(int screenScheId){
        Map<String, String> params=new HashMap<String,String>();
        params.put("screenScheId",String.valueOf(screenScheId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.SEAT_CHOOSE_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        parseSeat(response);
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

    public void parseSeat(JSONObject response){
        //todo 可能有当时是空座，但是下单时不是的情况，懒得写了- -
        JSONArray ja= null;
        String seat="";
        try {
            ja = response.getJSONArray("orders");
            List<Order> orders=JSON.parseArray(ja.toString(), Order.class);
            for(Order o:orders){
                seat=seat+o.getSeat()+";";
            }
            initSeat(seat);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public void initSeat(String seat){
        LinearLayout seats=(LinearLayout)findViewById(R.id.seats);
        LinearLayout[] rows=new LinearLayout[6];
        for(int i=0;i< rows.length;i++){
            rows[i]=new LinearLayout(getApplicationContext());
            rows[i].setPadding(30,0,30,0);
            rows[i].setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        int id=0;
        for(LinearLayout row:rows){

            for(int i=0;i<10;i++){
                id++;
                // CheckBox checkBox=new CheckBox(this,null,R.style.SeatStyle);不起作用
                final CheckBox checkBox=(CheckBox)getLayoutInflater().inflate(R.layout.mycheckbox,null);
                //只能在这里设置layout_weight，xml中无效
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f));
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
        if(!seat.equals("")) {
            String[] occupy = seat.split(";");
            for (String o : occupy) {
                String[] row_col = o.split("-");
                int seatNo = (Integer.parseInt(row_col[0]) - 1) * 10 + Integer.parseInt(row_col[1]);
                mySeats.get(seatNo).setClickable(false);
                mySeats.get(seatNo).setBackgroundResource(R.drawable.seat_occupied);
            }
        }
        for(int i=rows.length-1;i>=0;i--)
            seats.addView(rows[i]);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        screenScheId=getIntent().getIntExtra("screenScheId", -1);
        price=getIntent().getDoubleExtra("price", -1.00);
        getSeats(screenScheId);
        Button Btn1 = (Button) findViewById(R.id.seatbutton);
        Btn1.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                String s = "";
                for (CheckBox checkBox : mySeats) {
                    if (checkBox.isChecked()) {
                        int id = checkBox.getId();
                        int row;
                        if (id % 10 == 0)
                            row = id / 10;
                        else
                            row = id / 10 + 1;
                        int col = id - 10 * (row - 1);
                        s += row + "-" + col + ";";
                    }
                }

                if (s.equals("")) {
                    Toast.makeText(getApplicationContext(), "至少选一个座奥", Toast.LENGTH_LONG).show();
                    return;
                }
                s = s.substring(0, s.length() - 1);
                Map<String, String> params = new HashMap<String, String>();
                params.put("screenScheId", String.valueOf(screenScheId));
                params.put("seat", s);
                SharedPreferences sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE);
                int userId = sharedPreferences.getInt("userId", -1);
                params.put("userId", String.valueOf(userId));
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
        TextView tx=(TextView)findViewById(R.id.orderseatposition);
        TextView tx2=(TextView)findViewById(R.id.orderprice);
        double p=getIntent().getDoubleExtra("price",-1.00);
        tx.setText(result);
        tx2.setText(p*seatNum*1.00+"元");
    }

}
