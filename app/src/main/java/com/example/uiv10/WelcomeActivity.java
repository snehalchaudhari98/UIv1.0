package com.example.uiv10;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uiv10.Services.ApiService;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WelcomeActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;

    //
    public static final String LMTOOL_URL = "http://www.speech.cs.cmu.edu/";
    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_READ_CONTACTS = 100;
    private static final int WRITE_EXTERNAL_STORAGE = 103;
    private Interceptor interceptor = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response response = chain.proceed(chain.request());
//            Toast.makeText(getApplicationContext(), response.header("Location"), Toast.LENGTH_SHORT).show();
            return response;
        }
    };
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_side1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(v -> launchHomeScreen());

        btnNext.setOnClickListener(v -> {
            // checking for last page
            // if last page home screen will be launched
            int current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current);
                switch (current) {
                    case 1:
                        Toast.makeText(WelcomeActivity.this,"Slide 1",Toast.LENGTH_LONG).show();
                        if (ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        ) {
                            MultithreadingDemo thread = new MultithreadingDemo();
                            thread.start();
                        }else{
                            askForPermission();
                        }
                        break;

                    case 2:
                        Toast.makeText(WelcomeActivity.this,"Slide 2",Toast.LENGTH_LONG).show();
                        break;

                    case 3:
                        Toast.makeText(WelcomeActivity.this,"Slide 3",Toast.LENGTH_LONG).show();
                        break;


                }

            } else {
                launchHomeScreen();
            }
        });
    }


    //function ask permission

    private void askForPermission() {

        if (ContextCompat.checkSelfPermission(WelcomeActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.READ_CONTACTS) +
                ContextCompat.checkSelfPermission(WelcomeActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(WelcomeActivity.this,Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(WelcomeActivity.this,Manifest.permission.USE_BIOMETRIC) +
                ContextCompat.checkSelfPermission(WelcomeActivity.this,Manifest.permission.USE_FINGERPRINT) +
                ContextCompat.checkSelfPermission(WelcomeActivity.this,Manifest.permission.RECORD_AUDIO)

                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(WelcomeActivity.this,"Permissions asking mode", Toast.LENGTH_LONG).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.USE_BIOMETRIC,
                                Manifest.permission.USE_FINGERPRINT,
                                Manifest.permission.RECORD_AUDIO},

                        123);
            }
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.USE_BIOMETRIC,
                                Manifest.permission.USE_FINGERPRINT,
                                Manifest.permission.RECORD_AUDIO},
                        123);
            }else{
                Toast.makeText(WelcomeActivity.this,"Already Granted", Toast.LENGTH_LONG).show();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults.length > 0) {
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readContacts = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean writeExternalFile = grantResults[2] == PackageManager.PERMISSION_GRANTED;

                    if(readContacts && readExternalFile)
                    {
                        Toast.makeText(WelcomeActivity.this,"Fetching Contacts..", Toast.LENGTH_LONG).show();

                        MultithreadingDemo demo = new MultithreadingDemo();
                        demo.start();
                        // write your logic here
                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                    new String[]{
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_CONTACTS,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.USE_BIOMETRIC,
                                            Manifest.permission.USE_FINGERPRINT,
                                            Manifest.permission.RECORD_AUDIO},
                                    123);
                        }
                    }

                }
            }
            break;
        }

    }


    //
    class MultithreadingDemo extends Thread
    {
        public void run()
        {
            try
            {
                createFile(getAllContacts());
                getFromWeb();
            }
            catch (Exception e)
            {
                // Throwing an exception
                Log.e(TAG, "run: Error",e );
            }
        }
    }

    private void createFile(List<String> arrayList){
        try {
            final FileOutputStream outputStream = openFileOutput("corpus", Context.MODE_PRIVATE);
            final FileOutputStream gramStream = openFileOutput("names.gram", Context.MODE_PRIVATE);
            gramStream.write(("#JSGF V1.0;\n" +
                    "\n" +
                    "grammar names;\n" +
                    "\n" +
                    "public <names> = ").getBytes());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    for(int i=0;i<arrayList.size(); i++){
                        String name = preProcessString(arrayList.get(i));
                        if(name != null) {
                            outputStream.write(name.concat("\n").getBytes());

                            if (i != arrayList.size() - 1)
                                name += " | ";

                            gramStream.write(name.getBytes());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            gramStream.write(" ; ".getBytes());
            outputStream.close();
            gramStream.close();
//            Toast.makeText(getApplicationContext(),"File Created", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            FileInputStream inputStream = openFileInput("corpus");
//            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//            StringBuilder total = new StringBuilder();
//            String line;
//            while ((line = r.readLine()) != null) {
//                total.append(line);
//            }
//            r.close();
//            inputStream.close();
//            Log.d("File", "File contents: " + total);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private String preProcessString(String input){
        // Replace dash, dot, semicolon with space
        if(input != null) {
            input = input.replaceAll("[-.,]", " ");
            // Remove non characters
            input = input.replaceAll("[^a-zA-Z ]", "");
            input = input.trim();
            // Discard names with less than 1 characters
            if (input.length() > 2) {
                return input.toUpperCase();
            }
            return null;

        }else{
            return null;
        }

    }

    private List<String> getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
//                String id = cur.getString(
//                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                nameList.add(name);
//                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
//                    Cursor pCur = cr.query(
//                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            null,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                            new String[]{id}, null);
////                    while (pCur.moveToNext()) {
////                        String phoneNo = pCur.getString(pCur.getColumnIndex(
////                                ContactsContract.CommonDataKinds.Phone.NUMBER));
////                    }
////                    pCur.close();
//                }
            }
        }
        if (cur != null) {
            cur.close();
        }


        return nameList;
    }

    void getFromWeb(){
        List<ConnectionSpec> specs = new ArrayList<>();
        specs.add(ConnectionSpec.CLEARTEXT);
        specs.add(ConnectionSpec.MODERN_TLS);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addNetworkInterceptor(interceptor)
                .followRedirects(true)
                .connectionSpecs(specs)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl(LMTOOL_URL)
                .client(okHttpClient)
                .build();

        final ApiService apiService = retrofit.create(ApiService.class);

        try{
//            RequestBody filePart = RequestBody.create(MultipartBody.FORM, getBytes(getAssets().open("corpus1.txt")));
            RequestBody filePart = RequestBody.create(MultipartBody.FORM, getBytes(openFileInput("corpus")));
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("corpus", "corpus1.txt", filePart);

            Call<String> stringCall = apiService.getStringResponse("simple", body);
            stringCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        String responseString = response.body();
                        Document doc = Jsoup.parse(responseString);
                        Elements links = doc.select("a[href]");
                        String downloadURLTGZ = links.get(0).attr("href"); //0 is for the .dic link
                        downloadURLTGZ = downloadURLTGZ.substring(0,60);
                        String downloadURLDIC = links.get(4).attr("href"); //4 is for the .dic link
                        String downloadURL = downloadURLTGZ.trim().concat(downloadURLDIC.trim());   //concat the URL's for final URL

                        Toast.makeText(WelcomeActivity.this, "URL:" + downloadURL, Toast.LENGTH_SHORT).show();
                        downloadFile(apiService, downloadURL);
                        responseString = doc.text();
                        Log.d(TAG, "onResponse: "+responseString);
//                    createHashMap(responseString);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "onFailure: " ,t );
                }
            });
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    void downloadFile(ApiService apiService, String url){
        Call<ResponseBody> call = apiService.downloadFileByUrl(url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + "Successfully downloaded");
                    assert response.body() != null;
                    try {
                        byte data[] = new byte[4096];
                        int count;
                        int progress = 0;
                        ResponseBody responseBody = response.body();
                        OutputStream outputStream = openFileOutput("contacts.dic", Context.MODE_PRIVATE);
                        InputStream inputStream = responseBody.byteStream();
                        long fileSize = responseBody.contentLength();

                        while ((count = inputStream.read(data)) != -1) {
                            outputStream.write(data, 0, count);
                            progress += count;
                            Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
//                    downloadZipFileTask.doProgress(pairs);
                            Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //saveToDisk(response.body(), "contacts.dic");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: ",t );
            }
        });
    }
    // ENDD
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}