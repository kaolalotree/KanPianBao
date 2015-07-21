package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.Cinema;
import com.kpb.model.Order;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderListActivity extends Activity {
    private List<Order> orderList;
    private ListView listView;
    private OrderAdapter adapter;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        Map<String, String> params = new HashMap<String, String>();
        SharedPreferences sharedPreferences=getSharedPreferences("account", Context.MODE_PRIVATE);
        int userId= sharedPreferences.getInt("userId",-1);
        params.put("userId",String.valueOf(userId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.GET_ORDERS_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        parseJson(response);
                        initView();
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
            JSONArray ja=response.getJSONArray("orders");
            Log.d("HEKK", ja.toString());
            orderList= JSON.parseArray(ja.toString(), Order.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void loadImg(String url,ImageView postView){
        final ImageView imageView=postView;
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        imageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.empty_photo);
            }
        });
        mQueue.add(imageRequest);
    }
    public void initView(){
        adapter = new OrderAdapter(OrderListActivity.this, R.layout.order_item, orderList);
        listView = (ListView) findViewById(R.id.order_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order item = orderList.get(position);
                Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                intent.putExtra("orderId", item.getOrderId());
                startActivity(intent);
            }
        });
    }

    public class OrderAdapter extends ArrayAdapter<Order> {
        private int resourceId;

        public OrderAdapter(Context context, int textViewResourceId,
                            List<Order> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Order item = getItem(position);
            View view;
            ViewHolder holder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.orderImage = (ImageView) view.findViewById(R.id.order_image);
                holder.movieName = (TextView) view.findViewById(R.id.movie_name);
                holder.order_time_seat = (TextView) view.findViewById(R.id.order_time_seat);
                holder.del_btn = (Button) view.findViewById(R.id.del_btn);
                view.setTag(holder);
            }
            else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            //      holder.orderImage.setImageResource(item.getImage_num());

            holder.movieName.setText(item.getFilmName());
            Date date=item.getScreenTime();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String seat=item.getSeat();
            holder.order_time_seat.setText(seat+"  "+sdf.format(date));
            holder.del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    order_del_alert(position);
                }
            });
            return view;
        }
        class ViewHolder {
            ImageView orderImage;
            TextView movieName;
            TextView order_time_seat;
            Button del_btn;
        }
    }

    public void order_del_alert(final int position){
        new AlertDialog.Builder(this).setTitle("取消订单").setMessage("确定取消订单吗").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Order item = orderList.get(position);
                Map<String, String> params = new HashMap<String, String>();
                params.put("orderId", String.valueOf(item.getOrderId()));
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API.CANCEL_ORDER_URL, new JSONObject(params),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("TAG", response.toString());
                                try {
                                    if (response.getBoolean("status")) {
                                        orderList.remove(position);
                                        listView.setAdapter(adapter);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("TAG", error.getMessage(), error);
                            }
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
                mQueue.add(jsonObjectRequest);

            }
        }).show();
    }


}





