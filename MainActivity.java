package com.example.test1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    SwipeRefreshLayout srl_main;
    RecyclerView rv_main;
    ArrayList<String> array_username, array_password;
    ProgressDialog progressDialog;

    RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srl_main = findViewById(R.id.srl_main);
        rv_main = findViewById(R.id.rv_main);
        progressDialog = new ProgressDialog(this);

        rv_main.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv_main.setLayoutManager(layoutManager);

        srl_main.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                scrollRefresh();
                srl_main.setRefreshing(false);
            }
        });
        scrollRefresh();
    }

    public void scrollRefresh(){
        progressDialog.setMessage("Mengambil Data ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                getData();
            }
        }, 1200);
    }

    void initializeArray(){
        array_username = new ArrayList<>();
        array_password = new ArrayList<>();

        array_username.clear();
        array_password.clear();
    }

    public void getData(){
        initializeArray();
        AndroidNetworking.get("https://localhost:7023/api/User1").setTag("Get Data").setPriority(Priority.MEDIUM).build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();

                try {
                    boolean status = response.getBoolean("status");
                    if (status){
                        JSONArray ja = response.getJSONArray("result");
                        Log.d("respon",""+ja);
                        for(int i = 0; i < ja.length(); i++){
                            JSONObject jo = ja.getJSONObject(i);

                            array_username.add(jo.getString("username"));
                            array_password.add(jo.getString("password"));
                        }
                        recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this,array_username,array_password);

                        rv_main.setAdapter(recyclerViewAdapter);
                    }else {
                        Toast.makeText(MainActivity.this,"Gagal Mengambil Data", Toast.LENGTH_SHORT).show();

                        rv_main.setAdapter((recyclerViewAdapter));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menutambah,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.menu_add){
            Intent i = new Intent(MainActivity.this,AddActivity.class);
            startActivityForResult(i,1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                scrollRefresh();
            }else if(resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 2){
            if (resultCode == RESULT_OK){
                scrollRefresh();
            }else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void starActivityForResult(Intent i, int i1) {
    }
}