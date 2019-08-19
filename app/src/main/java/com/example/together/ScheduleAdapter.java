package com.example.together;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.together.StaticInit.PAGE_GROUP_INDEX;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    Context context;
    private ArrayList<ScheduleData> listData = new ArrayList<>(); //adapter에 들어갈 list
    private List<String> userBookmarkList; // 회원 북마크정보 파싱 결과 담는 배열

    //현재 날짜 가져오기 변수들
//    long mNow;
//    Date mDate;
//    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd");

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
//        holder.placeRating.setRating(4.5f);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(ScheduleData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView groupCategory; //모임 카테고리
        private TextView scheduleDate; //일정 날짜
        private TextView scheduleTitle; //일정 제목
        private TextView scheduleLocation; //일정 장소
        private TextView scheduleMember; //일정참여 멤버 수
        private TextView groupIdx; //모임 index


        ItemViewHolder(View itemView) {
            super(itemView);

            groupCategory = itemView.findViewById(R.id.groupCategory);
            //TODO 임시로 가려놓기
            groupCategory.setVisibility(View.GONE);
            scheduleDate = itemView.findViewById(R.id.scheduleDate);
            scheduleTitle = itemView.findViewById(R.id.scheduleTitle);
            scheduleLocation = itemView.findViewById(R.id.scheduleLocation);
            scheduleMember = itemView.findViewById(R.id.scheduleMember);
            groupIdx = itemView.findViewById(R.id.groupIdx);
        }

        void onBind(final ScheduleData data) {
            groupCategory.setText(data.getGroupCategory());
            scheduleDate.setText(data.getScheduleDate());
            scheduleTitle.setText(data.getScheduleTitle());
            scheduleLocation.setText(data.getScheduleLocation());
            scheduleMember.setText(String.valueOf(data.getScheduleMember()));
            groupIdx.setText(String.valueOf(data.getGroupIdx()));

            // 해당 리사이클러뷰 아이템을 클릭했을 경우
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), GroupScheduleActivity.class);
                    PAGE_GROUP_INDEX = data.getGroupIdx();
//                    intent.putExtra("placeName", data.getScheduleTitle());
                    v.getContext().startActivity(intent);
                }
            });

        }
    }

}
