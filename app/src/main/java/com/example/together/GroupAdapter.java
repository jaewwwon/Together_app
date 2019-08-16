package com.example.together;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    Context context;
    private static String TAG = "GroupAdapter";
    private ArrayList<GroupData> listData = new ArrayList<>(); //adapter에 들어갈 list


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(int position, GroupData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(0, data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView groupThumbnail; //모임 대표이미지
        private TextView groupCategory; //모임 카테고리
        private TextView groupTitle; //모임 이름
        private TextView groupIntro; //모임 소개
        private TextView groupLocation; //모임 장소
        private TextView groupMember; //모임회원 수
        private TextView groupIdx; //모임 index

        ItemViewHolder(View itemView) {
            super(itemView);

            groupThumbnail = itemView.findViewById(R.id.groupThumbnail);
            groupCategory = itemView.findViewById(R.id.groupCategory);
            groupTitle = itemView.findViewById(R.id.groupTitle);
            groupIntro = itemView.findViewById(R.id.groupIntro);
            groupLocation = itemView.findViewById(R.id.groupLocation);
            groupMember = itemView.findViewById(R.id.groupMember);
            groupIdx = itemView.findViewById(R.id.groupIdx);
        }

        void onBind(final GroupData data) {
            Glide.with(itemView.getContext())
                    .load(data.getGroupThumbnail())
                    .override(500, 300)
                    .into(groupThumbnail);
            groupCategory.setText(data.getGroupCategory());
            groupTitle.setText(data.getGroupTitle());
            groupIntro.setText(Html.fromHtml((data.getGroupIntro())));
            groupLocation.setText(data.getGroupLocation());
            groupMember.setText(String.valueOf(data.getGroupMember()));
            groupIdx.setText(String.valueOf(data.getGroupIdx()));


            // 해당 리사이클러뷰 아이템을 클릭했을 경우
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), GroupInfoActivity.class);
//                    intent.putExtra("groupIdx", data.getGroupIdx()); // 모임 index 전달
                    PAGE_GROUP_INDEX = data.getGroupIdx();
                    v.getContext().startActivity(intent);
                }
            });

        }
    }

}
