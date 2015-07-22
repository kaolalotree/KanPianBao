package course.project.com.kanpianbao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kpb.model.API;
import com.kpb.model.Film;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyScrollView extends ScrollView implements View.OnTouchListener {


    public static final int PAGE_SIZE = 15;


    private int page;

    private int columnWidth;


    private int firstColumnHeight;


    private int secondColumnHeight;


 //   private int thirdColumnHeight;


    private boolean loadOnce;


    private ImageLoader imageLoader;


    private LinearLayout firstColumn;


    private LinearLayout secondColumn;


    private LinearLayout thirdColumn;


    private static Set<LoadImageTask> taskCollection;


    private static View scrollLayout;


    private static int scrollViewHeight;


    private static int lastScrollY = -1;


    private List<ImageView> imageViewList = new ArrayList<ImageView>();
    private List<TextView>  introViewList =new ArrayList<TextView>();


    private static Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            MyScrollView myScrollView = (MyScrollView) msg.obj;
            int scrollY = myScrollView.getScrollY();

            if (scrollY == lastScrollY) {

                if (scrollViewHeight + scrollY >= scrollLayout.getHeight()
                        && taskCollection.isEmpty()) {
                    myScrollView.loadMoreImages();
                }
                myScrollView.checkVisibility();
            } else {
                lastScrollY = scrollY;
                Message message = new Message();
                message.obj = myScrollView;

                handler.sendMessageDelayed(message, 5);
            }
        };

    };


    private  RequestQueue mQueue;
    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(API.GET_RECOMMENDMOVIES_URL,
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("TAG", response.toString());
                        setMovieList(response);
                        loadMoreImages();
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                });

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(50 * 1000, 1, 1.0f));
        mQueue.add(jsonArrayRequest);
        imageLoader = ImageLoader.getInstance();
        taskCollection = new HashSet<LoadImageTask>();
        setOnTouchListener(this);
    }
    public List<Film> recommendMovies;
    public void setMovieList(JSONArray response){
        recommendMovies= JSON.parseArray(response.toString(), Film.class);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            scrollViewHeight = getHeight();
            scrollLayout = getChildAt(0);
            firstColumn = (LinearLayout) findViewById(R.id.first_column);
            secondColumn = (LinearLayout) findViewById(R.id.second_column);
    //        thirdColumn = (LinearLayout) findViewById(R.id.third_column);
            columnWidth = firstColumn.getWidth();
            loadOnce = true;
     //       loadMoreImages();
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            Message message = new Message();
            message.obj = this;
            handler.sendMessageDelayed(message, 5);
        }
        return false;
    }


    public void loadMoreImages() {
        ArrayList<String> imageUrls=new ArrayList<String>();
        for(Film film:recommendMovies)
            imageUrls.add(film.getFilmImg());
        if (hasSDCard()) {
            int startIndex = page * PAGE_SIZE;
            int endIndex = page * PAGE_SIZE + PAGE_SIZE;
            if (startIndex < imageUrls.size()) {
                Toast.makeText(getContext(), "少女祈祷中...", Toast.LENGTH_SHORT)
                        .show();
                if (endIndex > imageUrls.size()) {
                    endIndex = imageUrls.size();
                }
                for (int i = startIndex; i < endIndex; i++) {
                    LoadImageTask task = new LoadImageTask();
                    taskCollection.add(task);
                    task.execute(imageUrls.get(i));
                }
                page++;
            } else {
                Toast.makeText(getContext(), "没有了奥", Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            Toast.makeText(getContext(), "系统没有SD卡！", Toast.LENGTH_SHORT).show();
        }
    }


    public void checkVisibility() {
        for (int i = 0; i < imageViewList.size(); i++) {
            ImageView imageView = imageViewList.get(i);
            int borderTop = (Integer) imageView.getTag(R.string.border_top);
            int borderBottom = (Integer) imageView
                    .getTag(R.string.border_bottom);
            if (borderBottom > getScrollY()
                    && borderTop < getScrollY() + scrollViewHeight) {
                String imageUrl = (String) imageView.getTag(R.string.image_url);
                Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    LoadImageTask task = new LoadImageTask(imageView);
                    task.execute(imageUrl);
                }
            } else {
                imageView.setImageResource(R.drawable.empty_photo);
            }
        }
    }


    private boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }


    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {


        private String mImageUrl;


        private ImageView mImageView;

        public LoadImageTask() {
        }


        public LoadImageTask(ImageView imageView) {
            mImageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            mImageUrl = params[0];
            Bitmap imageBitmap = imageLoader
                    .getBitmapFromMemoryCache(mImageUrl);
            if (imageBitmap == null) {
                imageBitmap = loadImage(mImageUrl);
            }
            return imageBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                double ratio = bitmap.getWidth() / (columnWidth * 1.0);
                int scaledHeight = (int) (bitmap.getHeight() / ratio);
                addImage(bitmap, columnWidth, scaledHeight);
            }
            taskCollection.remove(this);
        }


        private Bitmap loadImage(String imageUrl) {
            File imageFile = new File(getImagePath(imageUrl));
            if (!imageFile.exists()) {
                downloadImage(imageUrl);
            }
            if (imageUrl != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), columnWidth);
                if (bitmap != null) {
                    imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                    return bitmap;
                }
            }
            return null;
        }


        private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    imageWidth, imageHeight);
            if (mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            } else {
                final ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(params);
                imageView.setImageBitmap(bitmap);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(5, 5, 5, 5);
                imageView.setTag(R.string.image_url, mImageUrl);
                String s= (String) imageView.getTag(R.string.image_url);

                imageView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), MovieDetailActivity.class);
                        Film selectMovie=null;
                        for(Film film:recommendMovies){
                            if(film.getFilmImg().equals((String) imageView.getTag(R.string.image_url))) {
                                selectMovie=film;
                            }
                        }
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("film",selectMovie);
                        intent.putExtras(bundle);
                        getContext().startActivity(intent);
                    }
                });

                final TextView tx=new TextView(getContext());
                String name=null;
                for(Film m:recommendMovies){
                    if(m.getFilmImg().equals(s)) {
                        name = m.getFilmName();
                    }
                }
                tx.setText(name);
                tx.setGravity(Gravity.CENTER);
                introViewList.add(tx);

                LinearLayout theColumn=  findColumnToAdd(imageView, imageHeight+tx.getHeight());
                theColumn.addView(imageView);
                theColumn.addView(tx);
                imageViewList.add(imageView);



            }
        }


        private LinearLayout findColumnToAdd(ImageView imageView,
                                             int imageHeight) {
            if(firstColumnHeight<=secondColumnHeight){
                imageView.setTag(R.string.border_top, firstColumnHeight);
                firstColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, firstColumnHeight);
                return firstColumn;
            }
            else{
                imageView.setTag(R.string.border_top, secondColumnHeight);
                secondColumnHeight += imageHeight;
                imageView.setTag(R.string.border_bottom, secondColumnHeight);
                return secondColumn;
            }
        }


        private void downloadImage(String imageUrl) {
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            File imageFile = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                con.setDoInput(true);
                con.setDoOutput(true);
                bis = new BufferedInputStream(con.getInputStream());
                imageFile = new File(getImagePath(imageUrl));
                fos = new FileOutputStream(imageFile);
                bos = new BufferedOutputStream(fos);
                byte[] b = new byte[1024];
                int length;
                while ((length = bis.read(b)) != -1) {
                    bos.write(b, 0, length);
                    bos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                    if (con != null) {
                        con.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (imageFile != null) {
                Bitmap bitmap = ImageLoader.decodeSampledBitmapFromResource(
                        imageFile.getPath(), columnWidth);
                if (bitmap != null) {
                    imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
                }
            }
        }


        private String getImagePath(String imageUrl) {
            int lastSlashIndex = imageUrl.lastIndexOf("/");
            String imageName = imageUrl.substring(lastSlashIndex + 1);
            String imageDir = Environment.getExternalStorageDirectory()
                    .getPath() + "/PhotoWallFalls/";
            File file = new File(imageDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            String imagePath = imageDir + imageName;
            return imagePath;
        }
    }

}