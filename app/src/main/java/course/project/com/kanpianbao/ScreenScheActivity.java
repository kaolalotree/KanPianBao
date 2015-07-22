package course.project.com.kanpianbao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.kpb.model.ScreenSche;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScreenScheActivity extends Activity {

    private List<ScreenSche> screenlist;
    private int filmId;
    private int cinemaId;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_sche);
        filmId=getIntent().getIntExtra("filmId",-1);
        cinemaId=getIntent().getIntExtra("cinemaId", -1);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        Map<String, String> params = new HashMap<String, String>();
        params.put("filmId", String.valueOf(filmId));
        params.put("cinemaId",String.valueOf(cinemaId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.SCREEN_CHOOSE_URL, new JSONObject(params),
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
    public void initView(){
        ScreenAdapter adapter = new ScreenAdapter(ScreenScheActivity.this, R.layout.screenitem, screenlist);
        ListView listView = (ListView) findViewById(R.id.screen_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScreenSche item = screenlist.get(position);
                Intent intent=new Intent(ScreenScheActivity.this,SeatActivity.class);
                intent.putExtra("screenScheId",item.getScreenScheId());
                intent.putExtra("price",item.getPrice());
                startActivity(intent);
                finish();
            }
        });
    }
    public void parseJson(JSONObject response){
        try {
            JSONArray ja=response.getJSONArray("screensches");
            screenlist= JSON.parseArray(ja.toString(), ScreenSche.class);
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
   public final static int[] halls={R.drawable.n1,R.drawable.n2,R.drawable.n3,R.drawable.n4,R.drawable.n5,R.drawable.n6,R.drawable.n7,
            R.drawable.n8,R.drawable.n9,R.drawable.n10,R.drawable.n11,R.drawable.n12};
    public class ScreenAdapter extends ArrayAdapter<ScreenSche> {
        private int resourceId;

        public ScreenAdapter(Context context, int textViewResourceId,
                               List<ScreenSche> objects) {
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
        }
        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            ScreenSche item = getItem(position);
            View view;
            ViewHolder holder;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                holder = new ViewHolder();
                holder.screenImage = (ImageView) view.findViewById(R.id.screen_image);
                holder.screenName = (TextView) view.findViewById(R.id.screen_name);
                holder.screenTime = (TextView) view.findViewById(R.id.screen_time);
                view.setTag(holder);
            }
            else{
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

         //   holder.screenImage.setImageResource(item.getScreen_image_num());
            holder.screenImage.setImageResource(halls[position]);
            holder.screenName.setText(item.getHall()+"号厅");
            Date date=item.getTime();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
            holder.screenTime.setText(sdf.format(date));
            return view;
        }
        class ViewHolder {
            ImageView screenImage;
            TextView screenName;
            TextView screenTime;
        }
    }

}
