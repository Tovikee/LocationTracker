package com.example.locationtracker;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.locationtracker.Init.Common;
import com.example.locationtracker.Interface.IFirebaseLoadDone;
import com.example.locationtracker.Models.User;
import com.example.locationtracker.ViewHolder.FriendRequestViewHolder;
import com.example.locationtracker.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, FriendRequestViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone iFirebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        searchBar = (MaterialSearchBar)findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> suggest = new ArrayList<>();
                for(String search:suggestList)
                {
                    if(search.toLowerCase().contains(searchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    if(adapter != null){
                        recycler_all_user.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                StartSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_all_user = (RecyclerView)findViewById(R.id.recycler_all_people);
        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this,((LinearLayoutManager) layoutManager).getOrientation()));

        iFirebaseLoadDone = this;

        loadFriendRequestList();
        loadSearchData();
    }

    private void StartSearch(String search_value) {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST)
                .orderByChild("name")
                .startAt(search_value);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull final User model) {
                holder.txt_user_email.setText(model.getEmail());
                holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, false);
                        addToAcceptList(model);
                        addUserToFriendContact(model);
                    }
                });

                holder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, true);
                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_friend_request,viewGroup,false);
                return new FriendRequestViewHolder(itemView);
            }
        };

        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);

    }

    private void loadFriendRequestList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, FriendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestViewHolder holder, int position, @NonNull final User model) {
                holder.txt_user_email.setText(model.getEmail());
                holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, false);
                        addToAcceptList(model);
                        addUserToFriendContact(model);
                    }
                });

                holder.btn_decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteFriendRequest(model, true);
                    }
                });
            }

            @NonNull
            @Override
            public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_friend_request,viewGroup,false);
                return new FriendRequestViewHolder(itemView);
            }
        };

        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

    private void addUserToFriendContact(User model) {
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(model.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(model.getUid()).setValue(Common.loggedUser);
    }

    private void addToAcceptList(User model) {
        DatabaseReference acceptList = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.ACCEPT_LIST);

        acceptList.child(model.getUid()).setValue(model);
    }

    private void deleteFriendRequest(User model, final boolean show) {
        DatabaseReference friendRequest = FirebaseDatabase.getInstance()
                .getReference(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);

        friendRequest.child(model.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(show){
                            Toast.makeText(FriendRequestActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        if(adapter != null)
            adapter.stopListening();
        if(searchAdapter != null)
            searchAdapter.stopListening();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null)
            adapter.startListening();
        if(searchAdapter != null)
            searchAdapter.startListening();
    }

    private void loadSearchData() {

        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFORMATION)
                .child(Common.loggedUser.getUid())
                .child(Common.FRIEND_REQUEST);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapShot:dataSnapshot.getChildren())
                {
                    User user = userSnapShot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                iFirebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iFirebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });

    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lastEmail) {
        searchBar.setLastSuggestions(lastEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
