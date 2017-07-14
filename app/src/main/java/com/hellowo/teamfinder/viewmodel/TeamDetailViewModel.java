package com.hellowo.teamfinder.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.ArrayMap;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hellowo.teamfinder.data.ConnectedUserLiveData;
import com.hellowo.teamfinder.model.Comment;
import com.hellowo.teamfinder.model.Member;
import com.hellowo.teamfinder.model.Team;
import com.hellowo.teamfinder.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hellowo.teamfinder.AppConst.DB_KEY_DT_CREATED;

public class TeamDetailViewModel extends ViewModel {
    public MutableLiveData<Team> team = new MutableLiveData<>();
    public MutableLiveData<List<Member>> members = new MutableLiveData<>();
    public MutableLiveData<Map<String, Integer>> roles = new MutableLiveData<>();
    public MutableLiveData<List<Comment>> comments = new MutableLiveData<>();
    public MutableLiveData<Integer> currentPage = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private String teamId;
    private List<Member> memberList = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
    private Map<String, Integer> roleMap = new HashMap<>();

    public TeamDetailViewModel() {
        currentPage.setValue(0);
    }

    public void initTeam(String teamId) {
        this.teamId = teamId;
        members.setValue(memberList);
        comments.setValue(commentList);
        roles.setValue(roleMap);
        getTeam();
        getComments();
    }

    public void getTeam(){
         loading.setValue(true);
         FirebaseDatabase.getInstance().getReference().child(Team.DB_REF)
                 .child(teamId)
                 .addListenerForSingleValueEvent(
                         new ValueEventListener() {
                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 team.setValue(dataSnapshot.getValue(Team.class));
                                 memberList.clear();
                                 memberList.addAll(team.getValue().getMembers());
                                 members.setValue(memberList);
                                 roleMap.clear();
                                 roleMap.putAll(team.getValue().getRoles());
                                 roles.setValue(roleMap);
                                 loading.setValue(false);
                             }
                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                                 loading.setValue(false);
                             }
                         });
    }

    public void getComments() {
        FirebaseDatabase.getInstance().getReference().child(Comment.DB_REF)
                .child(teamId)
                .orderByChild(DB_KEY_DT_CREATED)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                commentList.clear();
                                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                    Comment comment = postSnapshot.getValue(Comment.class);
                                    commentList.add(0, comment);
                                }
                                comments.setValue(commentList);
                                Log.d("aaa", commentList.size() + "");
                                loading.setValue(false);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
    }

    public void postComment(String message) {
        loading.setValue(true);
        Comment comment = new Comment();
        comment.setDtCreated(System.currentTimeMillis());
        comment.setText(message);
        comment.setUserName(ConnectedUserLiveData.get().getValue().getNickName());
        comment.setUserId(ConnectedUserLiveData.get().getValue().getId());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Comment.DB_REF).child(teamId);
        String key = ref.push().getKey();
        ref.child(key)
                .setValue(comment, (error, databaseReference)->{
                    getComments();

                    FirebaseDatabase.getInstance().getReference().child(Team.DB_REF).child(teamId)
                            .runTransaction(new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Team t = mutableData.getValue(Team.class);
                            if (t == null) {
                                return Transaction.success(mutableData);
                            }

                            t.setCommentCount(t.getCommentCount() + 1);

                            // Set value and report transaction success
                            mutableData.setValue(t);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b,
                                               DataSnapshot dataSnapshot) {}
                    });

                });
    }
}
