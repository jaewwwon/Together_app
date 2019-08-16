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

public class GroupPhotoAdapter extends RecyclerView.Adapter<GroupPhotoAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    final private String TAG = "GroupPhotoAdapter";
    Context context;
    public ArrayList<GroupPhotoData> listData = new ArrayList<>(); //adapter에 들어갈 list


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
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

    void addItem(int position, GroupPhotoData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(0, data);
    }

    void removeItem(int position) {
        // 외부에서 item을 삭제시킬 함수입니다.
        listData.remove(0);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView groupPhoto; //모임 이미지
        private ImageView utilBtn; //수정/삭제 팝업 버튼

        ItemViewHolder(View itemView) {
            super(itemView);

            groupPhoto = itemView.findViewById(R.id.groupPhoto);
            utilBtn = itemView.findViewById(R.id.utilBtn);
        }

        void onBind(final GroupPhotoData data) {
            Glide.with(itemView.getContext())
                    .load(data.getGroupPhoto())
                    .override(500, 500)
                    .into(groupPhoto);


            //일정 수정/삭제 팝업 버튼을 클릭했을 경우
            utilBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PopupPhotoUtilActivity.class);
                    intent.putExtra("itemPositionOri", getAdapterPosition()); //아이템 위치값
                    intent.putExtra("itemSizeOri", listData.size()); //아이템 크기
                    intent.putExtra("photoIdxOri", data.getPhotoIdx()); //아이템 사진 index
                    v.getContext().startActivity(intent);
                }
            });


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
