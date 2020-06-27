package id.teoprayoga.androidfundamentalsubmission2.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.teoprayoga.androidfundamentalsubmission2.R;
import id.teoprayoga.androidfundamentalsubmission2.activities.TabActivity;
import id.teoprayoga.androidfundamentalsubmission2.databinding.FragmentFollowerBinding;
import id.teoprayoga.androidfundamentalsubmission2.databinding.ItemFollowerBinding;
import id.teoprayoga.androidfundamentalsubmission2.models.Item;

public class FollowerFragment extends Fragment {

    FragmentFollowerBinding binding;
    Adapter adapter;
    List<Item> items = new ArrayList<>();
    RequestQueue requestQueue;
    TabActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFollowerBinding.inflate(inflater, container, false);

        activity = (TabActivity) getActivity();
        requestQueue = Volley.newRequestQueue(activity);

        adapter = new Adapter(getActivity(), items);
        binding.rv.setLayoutManager(new LinearLayoutManager(activity));
        binding.rv.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        binding.rv.setAdapter(adapter);

        Bundle bundle = getArguments();
        Item item = new Gson().fromJson(bundle.getString("user"), Item.class);
        setFollower(item);

        return binding.getRoot();
    }

    private void setFollower(Item item){
        String url = "https://api.github.com/users/"+item.getLogin()+"/followers";
        Log.i("logteo", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                items.clear();
                JsonArray jsonArray = new Gson().fromJson(response, JsonArray.class);
                String string = new Gson().toJson(jsonArray);
                List<Item> itemList = new ArrayList<>(Arrays.asList(new Gson().fromJson(string, Item[].class)));
                items.addAll(itemList);
                Log.i("logteo", items.size()+": "+response);
                adapter.reset(items);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        Context context;
        List<Item> items;

        Adapter(Context context, List<Item> items){
            this.context = context;
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemFollowerBinding binding = ItemFollowerBinding.inflate(getLayoutInflater(), parent, false);
            return new ViewHolder(binding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Item item = items.get(position);

            Picasso.get().load(item.getAvatar_url()).placeholder(R.drawable.anim_progress).into(holder.binding.iv);
            holder.binding.tvlogin.setText(item.getLogin());
            holder.binding.tvtype.setText(item.getType());
            holder.binding.tvurl.setText(item.getUrl());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ItemFollowerBinding binding;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = ItemFollowerBinding.bind(itemView);
            }
        }

        public void reset(List<Item> items){

            notifyDataSetChanged();
        }
    }
}