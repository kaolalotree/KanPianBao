package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends Activity {

    NfcAdapter mAdapter;
    private String[][] techList;
    private IntentFilter[] intentFilters;
    private PendingIntent pendingIntent;
    private Tag tag;
    private  boolean hasNFC=false;
    public void initNFC(){
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if ( mAdapter == null) {
            Toast.makeText(getApplicationContext(), "设备不支持NFC", Toast.LENGTH_LONG).show();
            return;
        }
        if ( mAdapter!=null&&! mAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "请在系统设置中启用NFC", Toast.LENGTH_LONG).show();
            return;
        }
        hasNFC=true;
        techList = new String[][] {
                new String[] { android.nfc.tech.NfcV.class.getName() },
                new String[] { android.nfc.tech.NfcA.class.getName() } };

        IntentFilter ndef= new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        ndef.addCategory("*/*");
        intentFilters = new IntentFilter[]{ndef};
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.d("OrderStatus",currentOrder.getStatus());
        if(currentOrder.getStatus().equals("y"))
            Toast.makeText(getApplicationContext(), "该订单已经付过款了奥", Toast.LENGTH_LONG).show();
        else
            PayByNFC();
        return;
    }
    public void onPause() {
        super.onPause();
        if(hasNFC)
            mAdapter.disableForegroundDispatch(this);
    }
    protected void onResume(){

        super.onResume();
        if(hasNFC)
            mAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters,
                techList);
    }
    public void PayByNFC(){
        Map<String, String> params=new HashMap<String,String>();
        params.put("orderId", String.valueOf(orderId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.PAY_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        try {
                            if(response.getBoolean("status")){
                                ImageView status=(ImageView)findViewById(R.id.order_status);
                                currentOrder.setStatus("y");
                                status.setImageResource(R.drawable.payed);
                                Toast.makeText(getApplicationContext(), "付款成功", Toast.LENGTH_LONG).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), "付款失败", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
    private int orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initNFC();
        orderId=getIntent().getIntExtra("orderId",-1);
        Map<String, String> params=new HashMap<String,String>();
        params.put("orderId",String.valueOf(orderId));
        mQueue=Volley.newRequestQueue(getApplicationContext());
        getOrder(params);
        
    }
    private RequestQueue mQueue;
    private Order currentOrder;
    public void getOrder(Map<String, String> params){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.GET_ORDER_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        Order order= JSON.parseObject(response.toString(),Order.class);
                        currentOrder=order;
                        initView(order);
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
    public void initView(Order order){
        TextView order_id =(TextView)findViewById(R.id.order_num);
        TextView film_name=(TextView)findViewById(R.id.order_movie);
        TextView cinema_name=(TextView)findViewById(R.id.order_cinema);
        TextView time =(TextView)findViewById(R.id.order_time);
        TextView hall=(TextView)findViewById(R.id.order_hall);
        TextView seat=(TextView)findViewById(R.id.order_seat);
        ImageView status=(ImageView)findViewById(R.id.order_status);
        order_id.setText(String.valueOf(order.getOrderId()));
        film_name.setText(order.getFilmName());
        cinema_name.setText(order.getCinemaName());
        Date date=order.getScreenTime();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        time.setText(sdf.format(date));
        hall.setText(order.getHall()+"号厅");
        seat.setText(order.getSeat());
        if(order.getStatus().equals("y"))
            status.setImageResource(R.drawable.payed);
        else
            status.setImageResource(R.drawable.unpaid);
    }
}
