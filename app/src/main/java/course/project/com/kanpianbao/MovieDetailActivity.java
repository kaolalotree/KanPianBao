package course.project.com.kanpianbao;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.Film;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MovieDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent=getIntent();
        final Film film = (Film) intent.getSerializableExtra("film");
        setTitle(film.getFilmName());
        TextView textView=(TextView)findViewById(R.id.movieIntro);
        textView.setText("    "+film.getFilmIntro());
        final  ImageView imageView=(ImageView)findViewById(R.id.moviePic);
        final RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
        ImageRequest imageRequest = new ImageRequest(
                film.getFilmImg(),
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
        Button cmtBtn=(Button)findViewById(R.id.commentBtn);
        cmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailActivity.this, CommentActivity.class);
                intent.putExtra("filmId",film.getFilmId());
                startActivity(intent);
            }
        });


        Button ticketBtn=(Button)findViewById(R.id.ticketBtn);
        ticketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MovieDetailActivity.this,CinemaListActivity.class);
                intent.putExtra("filmId",film.getFilmId());
                startActivity(intent);
            }
        });


        Button favBtn=(Button)findViewById(R.id.favorite);
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<String, String>();
                SharedPreferences sharedPreferences=getSharedPreferences("account", Context.MODE_PRIVATE);
                int userId= sharedPreferences.getInt("userId",-1);
                params.put("userId",String.valueOf(userId));
                params.put("filmId",String.valueOf(film.getFilmId()));
                addFavorite(params,mQueue);
            }
        });
    }

    public void addFavorite(Map<String, String> params,RequestQueue mQueue){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, API.ADD_FAVORITE_URL, new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        try {
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
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
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
