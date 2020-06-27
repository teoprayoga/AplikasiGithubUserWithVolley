package id.teoprayoga.androidfundamentalsubmission2.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import id.teoprayoga.androidfundamentalsubmission2.R;
import id.teoprayoga.androidfundamentalsubmission2.databinding.ActivityTabBinding;
import id.teoprayoga.androidfundamentalsubmission2.fragments.FollowerFragment;
import id.teoprayoga.androidfundamentalsubmission2.fragments.FollowingFragment;
import id.teoprayoga.androidfundamentalsubmission2.models.Item;
import id.teoprayoga.androidfundamentalsubmission2.models.User;

public class TabActivity extends AppCompatActivity {

    ActivityTabBinding binding;
    List<Fragment> fragments = new ArrayList<>();

    RequestQueue requestQueue;
    Item item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTabBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Detail User");

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        item = new Gson().fromJson(intent.getStringExtra("item"), Item.class);
        getUserFromItem(item);

        Bundle bundle = new Bundle();
        bundle.putString("user", new Gson().toJson(item));
        FollowerFragment followerFragment = new FollowerFragment();
        FollowingFragment followingFragment = new FollowingFragment();
        followerFragment.setArguments(bundle);
        followingFragment.setArguments(bundle);

        fragments.add(followerFragment);
        fragments.add(followingFragment);

        binding.tl.setupWithViewPager(binding.vp);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        binding.vp.setAdapter(adapter);
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

    private void getUserFromItem(Item item){
        String url = "https://api.github.com/users/"+item.getLogin();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                User user = new Gson().fromJson(response, User.class);
                Picasso.get().load(user.getAvatar_url()).placeholder(R.drawable.anim_progress).into(binding.iv);
                binding.tvusername.setText(user.getTwitter_username());
                binding.tvname.setText(user.getName());
                binding.tvlocation.setText(user.getLocation());
                binding.tvcompany.setText(user.getCompany());
                binding.tvrepo.setText(String.valueOf(user.getPublic_repos()));

                binding.tl.getTabAt(0).setText("Follower: "+user.getFollowers());
                binding.tl.getTabAt(1).setText("Following: "+user.getFollowing());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

    class PagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragments;
        String[] titles = new String[]{"Following", "Following"};

        public PagerAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment>fragments) {
            super(fm, behavior);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}