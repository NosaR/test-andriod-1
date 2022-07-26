package com.example.test1;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class AddActivity extends AppCompatActivity {

    com.rengwuxian.materialedittext.MaterialEditText et_username, et_password;
    String username, password;
    Button btn_submit;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add );

        et_username = findViewById( R.id.et_username );
        et_password = findViewById( R.id.et_password );
        btn_submit = findViewById( R.id.btn_submit );

        progressDialog = new ProgressDialog( this );

        btn_submit.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v){
                progressDialog.setMessage( "Menambahkan Data..." );
                progressDialog.setCancelable( false );
                progressDialog.show();

                username = et_username.getText().toString();
                password = et_password.getText().toString();

                new Handler().postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                },1000 );
            }
        });
    }

    void validasiData(){
        if (username.equals( "" ) || password.equals( "" )){
            progressDialog.dismiss();
            Toast.makeText( AddActivity.this,"Periksa Kembali Data yang Anda Masukkan !", Toast.LENGTH_SHORT ).show();
        }else {
            kirimData();
        }
    }

    void kirimData(){
        AndroidNetworking.post("https://localhost:7023/api/User1").addBodyParameter( "username",""+username ).addBodyParameter( "password",""+password ).setPriority( Priority.MEDIUM ).setTag( "Tambah Data" ).build().getAsJSONObject( new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.dismiss();
                Log.d( "cekTambah",""+response );
                try {
                    Boolean status = response.getBoolean( "status" );
                    String pesan = response.getString( "result" );
                    Toast.makeText( AddActivity.this,""+pesan, Toast.LENGTH_SHORT ).show();
                    Log.d("status",""+status);
                    if (status){
                        new AlertDialog.Builder(AddActivity.this).setMessage( "Berhasil Menambahkan Data!" ).setCancelable( false ).setPositiveButton( "Kembali", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = getIntent();
                                setResult( RESULT_OK, i );
                                AddActivity.this.finish();
                            }
                        } ).show();
                    }else {
                        new AlertDialog.Builder(AddActivity.this).setMessage( "Gagal Menambahkan Data!" ).setPositiveButton( "Kembali", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = getIntent();
                                setResult( RESULT_CANCELED, i );
                                AddActivity.this.finish();
                            }
                        } ).setCancelable( false ).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Log.d("ErrorTambahData",""+anError.getErrorBody());
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate( R.menu.menu_back,menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public  boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.menu_back){
            this.finish();
        }
        return super.onOptionsItemSelected( item );
    }
}