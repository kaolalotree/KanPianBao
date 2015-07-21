package course.project.com.kanpianbao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import com.kpb.model.Comment;
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


public class CommentActivity extends Activity {
    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mQueue = Volley.newRequestQueue(getApplicationContext());
        Button btn =(Button)findViewById(R.id.cmtBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmtDialog();
            }
        });
        getComments();
    }
    public void initView(){
        ListView listView=(ListView)findViewById(R.id.cmtListView);
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,getData(),R.layout.comment_item,new String[]{"user_head","user_name","user_cmt","cmt_time"},new int[]{R.id.user_head,R.id.user_name,R.id.user_cmt,R.id.cmt_time});
        listView.setAdapter(simpleAdapter);
    }
    private List<Comment> cmtList;
    public void getComments(){
        Map<String, String> params = new HashMap<String, String>();
        int filmId=getIntent().getIntExtra("filmId",-1);
        params.put("filmId", String.valueOf(filmId));
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.GET_COMMENTS_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        try {
                            JSONArray ja=response.getJSONArray("comments");
                            cmtList= JSON.parseArray(ja.toString(), Comment.class);
                            initView();
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
    private List<HashMap<String, Object>> getData() {

        // 新建一个集合类，用于存放多条数据
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = null;
        for (int i = 0; i <cmtList.size(); i++) {
            Comment c=cmtList.get(i);
            map = new HashMap<String, Object>();
            map.put("user_head",R.drawable.user_head);
            map.put("user_name", c.getUserName());
            map.put("user_cmt", c.getComment());
            Date date= c.getTime();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            map.put("cmt_time", sdf.format(date));
            list.add(map);
        }
        return list;
    }
    public void cmtDialog(){
        final EditText inputServer = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入评论").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                .setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String cmt=inputServer.getText().toString();
                giveComment(cmt);
            }
        });
        builder.show();
    }

    public void giveComment(String cmt){
        Map<String, String> params = new HashMap<String, String>();
        int filmId=getIntent().getIntExtra("filmId",-1);
        params.put("filmId", String.valueOf(filmId));
        //todo use share
        params.put("userId","1");
        params.put("comment",cmt);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.GIVE_COMMENT_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        try {
                             boolean status= response.getBoolean("status");
                             if(status){
                                 Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();
                                 getComments();
                             }
                            else
                                 Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_LONG).show();


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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
