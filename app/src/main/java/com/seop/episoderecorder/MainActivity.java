package com.seop.episoderecorder;

import android.content.Context;
import android.media.session.PlaybackState;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private static FirebaseDatabase mFirebaseDatabase;
    private String selectedKey;

    private ListView listView;
    private EditText editText,editText2;
    private CustomAdapter adapter;

    public InputMethodManager imm;


    static{
        mFirebaseDatabase = mFirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button addBtn = (Button) findViewById(R.id.addBtn);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        editText = (EditText) findViewById(R.id.titleText);
        editText2 = (EditText) findViewById(R.id.episodeText);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter();
        listView.setAdapter(adapter);

        // 추가 버튼 클릭시
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                String text2 = editText2.getText().toString();

                if (text.isEmpty()|text2.isEmpty()){
                    Toast.makeText(getApplicationContext(),"값을 입력해주세요.",Toast.LENGTH_LONG).show();
                    return;
                }
                dropDownKeyboard();
                saveMemo();
            }
        });
        displayMemos();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingerItem item = (SingerItem) adapter.getItem(position);
                mFirebaseDatabase.getReference("memos/"+mFirebaseUser.getUid()+"/"+item.getKey())
                        .removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                Toast.makeText(getApplicationContext(),"삭제가 완료되었습니다.",Toast.LENGTH_LONG).show();
                            }
                        });
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initMemo(){
        selectedKey = null;
        editText.setText("");
        editText2.setText("");
    }

    private void dropDownKeyboard(){
        imm.hideSoftInputFromWindow(editText.getWindowToken(),0);
        imm.hideSoftInputFromWindow(editText2.getWindowToken(),0);
    }

    class CustomAdapter extends BaseAdapter{

        ArrayList<SingerItem> items = new ArrayList<SingerItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(SingerItem item){
            items.add(item);
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SingerItemView view = new SingerItemView(getApplicationContext());
            SingerItem item = items.get(position);
            view.setTitle(item.getTitle());
            view.setEpisode(item.getEpisode());
            view.setKey(item.getKey());
            return view;
        }
    }

    private void saveMemo(){
        final SingerItem item = new SingerItem();
        if (editText.getText().toString().isEmpty()){
            return;
        }
        item.setTitle(editText.getText().toString());
        item.setEpisode(editText2.getText().toString());
        mFirebaseDatabase.getReference("memos/"+mFirebaseUser.getUid())
                .push()
                .setValue(item)
                .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(listView,"메모가 저장되었습니다.",Snackbar.LENGTH_LONG).show();
                        initMemo();
                    }
                });
    }

    private void displayMemos(){
        mFirebaseDatabase.getReference("memos/"+mFirebaseUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        SingerItem singerItem = dataSnapshot.getValue(SingerItem.class);
                        singerItem.setKey(dataSnapshot.getKey()); //key 값 지정
                        selectedKey = dataSnapshot.getKey();
                        displayMemoList(singerItem);

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s){
//                        SingerItem singerItem = dataSnapshot.getValue(SingerItem.class);
//                        singerItem.setKey(dataSnapshot.getKey());
//                        for (int i=0;i<adapter.getCount();i++){
//                            SingerItemView singerItemView = (SingerItemView) adapter.getItem(i);
//                            if (singerItem.getKey().equals(singerItemView.getKey())){
//                                singerItemView.setKey(singerItem.getKey());
//                                break;
//                            }
//                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void displayMemoList(SingerItem singerItem) {
        singerItem.setTitle(singerItem.getTitle());
        singerItem.setEpisode(singerItem.getEpisode());
        singerItem.setKey(singerItem.getKey());
        adapter.addItem(singerItem);
        adapter.notifyDataSetChanged();

        View view = new View(getApplication());
        view.setTag(singerItem);

    }

    private void deleteMemo(){

    }

    private void updateMemo(SingerItem singerItem){
        SingerItem item = new SingerItem();
        if (editText.getText().toString().isEmpty()){
            return;
        }
        selectedKey = singerItem.getKey();
        item.setTitle(editText.getText().toString());
        item.setEpisode(editText2.getText().toString());
        mFirebaseDatabase.getReference("memos/"+mFirebaseUser.getUid()+"/"+selectedKey)
                .setValue(item)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(listView,"메모가 수정되었습니다."+selectedKey,Snackbar.LENGTH_LONG).show();
                        initMemo();
                    }
                });
    }


}
