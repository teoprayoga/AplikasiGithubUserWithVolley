package id.teoprayoga.androidfundamentalsubmission2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.teoprayoga.androidfundamentalsubmission2.activities.TabActivity;
import id.teoprayoga.androidfundamentalsubmission2.databinding.ActivityMainBinding;
import id.teoprayoga.androidfundamentalsubmission2.databinding.ItemMainBinding;
import id.teoprayoga.androidfundamentalsubmission2.models.Item;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ActivityMainBinding binding;
    RequestQueue requestQueue;
    List<Item> items = new ArrayList<>();
    MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestQueue = Volley.newRequestQueue(this);

        getSupportActionBar().setTitle("Github User's Search");
        binding.sv.setQueryHint("Username");

        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainAdapter(this, items);
        binding.rv.setAdapter(adapter);
        binding.sv.setOnQueryTextListener(this);
        binding.rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.isettings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        items.clear();
        if(newText.length()>0){
            String url = "https://api.github.com/search/users?q="+newText;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
                    int total_count = jsonObject.get("total_count").getAsInt();
                    boolean incomplete_results = jsonObject.get("incomplete_results").getAsBoolean();
                    JsonArray jsonArray = jsonObject.get("items").getAsJsonArray();
                    String string = new Gson().toJson(jsonArray);
                    List<Item> itemList = new ArrayList<>(Arrays.asList(new Gson().fromJson(string, Item[].class)));
                    items.addAll(itemList);
                    adapter.reset(items);
//                    Log.i("logteo", items.size()+": "+string);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            requestQueue.add(stringRequest);
        }
        return true;
    }

    class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

        Context context;
        List<Item> items;

        public MainAdapter(Context context, List<Item> items){
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemMainBinding binding = ItemMainBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Item item = items.get(position);
            Picasso.get().load(item.getAvatar_url()).placeholder(R.drawable.anim_progress).into(holder.binding.iv);
            holder.binding.tvlogin.setText(item.getLogin());
            holder.binding.tvtype.setText(item.getType());
            holder.binding.tvurl.setText(item.getUrl());

            holder.binding.cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, TabActivity.class);
                    intent.putExtra("item", new Gson().toJson(item));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ItemMainBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemMainBinding.bind(itemView);
            }
        }

        public void reset(List<Item> items){
            notifyDataSetChanged();
        }
    }
}