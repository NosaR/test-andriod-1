package com.example.test1;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.menu.MenuView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private Context mContext;
    private ArrayList<String> array_username,array_password;
    ProgressDialog progressDialog;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_username,tv_password;
        public CardView cv_main;

        public MyViewHolder(View view){
            super(view);
            cv_main = itemView.findViewById(R.id.cv_main);
            tv_username = itemView.findViewById(R.id.tv_username);
            tv_password = itemView.findViewById(R.id.tv_password);
            progressDialog = new ProgressDialog(mContext);
        }
    }

    public RecyclerViewAdapter(Context mContext, ArrayList<String>array_username, ArrayList<String>array_password){
        super();
        this.mContext = mContext;
        this.array_username = array_username;
        this.array_password = array_password;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.template,parent,false);
        return new RecyclerViewAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        holder.tv_username.setText(array_username.get(position));
        holder.tv_password.setText(array_password.get(position));
        holder.cv_main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent((mContext,Activity_Edit.class);
                i.putExtra("username", array_username.get(position));
                i.putExtra("password", array_password.get(position));
                ((MainActivity)mContext).starActivityForResult(i,2);
            }
        });

        holder.cv_main.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                new AlertDialog.Builder((MainActivity)mContext).setMessage("Ingin Mengahpus Nomer Induk"+array_username.get(position)+"?").setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        progressDialog.setMessage("Menghapus...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        AndroidNetworking.post("https://localhost:7023/api/User1").addBodyParameter("username",""+array_username.get(position)).setPriority(Priority.MEDIUM).build().getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progressDialog.dismiss();
                                try {
                                    boolean status = response.getBoolean("status");
                                    Log.d("status",""+status);
                                    String result = response.getString("result");
                                    if (status){
                                        if (mContext instanceof MainActivity){
                                            ((MainActivity)mContext).scrollRefresh();
                                        }
                                    }else {
                                        Toast.makeText(mContext, ""+result, Toast.LENGTH_SHORT).show();;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                anError.printStackTrace();
                            }
                        });
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount(){
        return array_username.size();
    }

}
