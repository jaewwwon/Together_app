package com.example.together;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    final private String TAG = "GroupInfoAdapter";
    Context context;
    private ArrayList<GroupInfoData> listData = new ArrayList<>(); //adapter에 들어갈 list


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
//        Log.e(TAG + "_onBind", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(int position, GroupInfoData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(0, data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView userProfile; //프로필 이미지
        private TextView userName; //멤버 이름
        private TextView userIntro; //멤버 소개란

        ItemViewHolder(View itemView) {
            super(itemView);

            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            userIntro = itemView.findViewById(R.id.userIntro);
        }

        void onBind(final GroupInfoData data) {
//            userProfile.setImageURI(Uri.parse(data.getUserProfile()));
            Glide.with(itemView.getContext())
                    .load(data.getUserProfile())
                    .override(500, 500)
                    .into(userProfile);
            userName.setText(data.getUserName());
            userIntro.setText(data.getUserIntro());


//            // 해당 리사이클러뷰 아이템을 클릭했을 경우
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), PlaceViewActivity.class);
//                    intent.putExtra("placeName", data.getPlaceName());
//                    intent.putExtra("placeAddress", data.getPlaceAddress());
//                    v.getContext().startActivity(intent);
//                }
//            });

        }
    }

}
