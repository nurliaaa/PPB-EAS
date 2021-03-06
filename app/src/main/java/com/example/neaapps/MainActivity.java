package com.example.neaapps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neaapps.Model.Articles;
import com.example.neaapps.Model.Headlines;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

     RecyclerView recyclerView;
     SwipeRefreshLayout swipeRefreshLayout;
     TextView textView;
     EditText etQuery;
     Button btnSearch,btnAboutUs;
     Dialog dialog;
     final String API_KEY = "a85f610b52eb41bcaa0331c0964be615";
     String country = "id";
     Adapter adapter;
     List<Articles>  articles = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);

        etQuery = findViewById(R.id.etQuery);
        btnSearch = findViewById(R.id.btnSearch);
        btnAboutUs = findViewById(R.id.aboutUs);
        dialog = new Dialog(MainActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
         @Override
         public void onRefresh() {
          retrieveJson("",country,API_KEY);
         }
        });
        retrieveJson("",country,API_KEY);

        btnSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
             if (!etQuery.getText().toString().equals("")){
               swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                  @Override
                  public void onRefresh() {
                   retrieveJson(etQuery.getText().toString(),country,API_KEY);
                  }
              });
              retrieveJson(etQuery.getText().toString(),country,API_KEY);

          }else{
           swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
              @Override
              public void onRefresh() {
               retrieveJson("",country,API_KEY);
              }
           });
               retrieveJson("",country,API_KEY);
          }
         }
        });

         btnAboutUs.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            showDialog();
           }
          });

       }

       public void retrieveJson(String query ,String country, String apiKey){
        swipeRefreshLayout.setRefreshing(true);
        Call<Headlines> call;
          if (!etQuery.getText().toString().equals("")){
            call= Api.getInstance().getApi().getSpecificData(query,apiKey);
          }else{
            call= Api.getInstance().getApi().getHeadlines(country,apiKey);
          }

        call.enqueue(new Callback<Headlines>() {
         @Override
          public void onResponse(Call<Headlines> call, Response<Headlines> response) {
            if (response.isSuccessful() && response.body().getArticles() != null){
               swipeRefreshLayout.setRefreshing(false);
               articles.clear();
               articles = response.body().getArticles();
               adapter = new Adapter(MainActivity.this,articles);
               recyclerView.setAdapter(adapter);
            }
         }

         @Override
         public void onFailure(Call<Headlines> call, Throwable t) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
           }
         });
       }

       public void showDialog(){
          Button btnClose;
          dialog.setContentView(R.layout.about_us);
          dialog.show();
          btnClose = dialog.findViewById(R.id.close);

          btnClose.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
              dialog.dismiss();
             }
            });
       }

       public void onClick(View view) {
         textView = findViewById(R.id.tvNewsApp);
         textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent = new Intent(MainActivity.this,MainActivity.class);
             startActivity(intent);
            }
         });
       }

}