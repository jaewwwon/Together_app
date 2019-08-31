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
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.together.GroupScheduleActivity.diffOfDate;
import static com.example.together.StaticInit.PAGE_GROUP_HOST;
import static com.example.together.StaticInit.loginUserId;

public class GroupScheduleAdapter extends RecyclerView.Adapter<GroupScheduleAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    Context context;
    private static String TAG = "GroupScheduleAdapter";
    public ArrayList<SearchScheduleData> listData = new ArrayList<>(); //adapter에 들어갈 list

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
//        Log.e(TAG, "아이템 번호: " + position);
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

//    void addItem(int position, SearchScheduleData data) {
//        // 외부에서 item을 추가시킬 함수입니다.
//        listData.add(0, data);
//    }

    void firstAddItem(int position, SearchScheduleData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(0, data);
    }

    void addItem(SearchScheduleData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(data);
    }

    void removeItem(int position) {
        // 외부에서 item을 삭제시킬 함수입니다.
        listData.remove(0);
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView calendarIcon; //일정 캘린더 아이콘
        private TextView scheduleWeek; //일정 날짜 요일
        private TextView scheduleCount; //일정 날짜 카운트
        private TextView scheduleDate; //일정 날짜
        private TextView scheduleTitle; //일정 제목
        private TextView scheduleContent; //일정 내용
        private TextView scheduleLocation; //일정 장소
        private TextView scheduleMember; //일정참여 멤버 수
        private TextView scheduleIdx; //일정 index
        private TextView groupIdx; //모임 index
        private ImageView utilBtn; //일정 수정/삭제 팝업 버튼
        private TextView attendButton; //일정 참석 버튼

        ItemViewHolder(View itemView) {
            super(itemView);

            calendarIcon = itemView.findViewById(R.id.calendarIcon);
            scheduleWeek = itemView.findViewById(R.id.scheduleWeek);
            scheduleCount = itemView.findViewById(R.id.scheduleCount);
            scheduleDate = itemView.findViewById(R.id.scheduleDate);
            scheduleTitle = itemView.findViewById(R.id.scheduleTitle);
            scheduleContent = itemView.findViewById(R.id.scheduleContent);
            scheduleLocation = itemView.findViewById(R.id.scheduleLocation);
            scheduleMember = itemView.findViewById(R.id.scheduleMember);
            scheduleIdx = itemView.findViewById(R.id.scheduleIdx);
            groupIdx = itemView.findViewById(R.id.groupIdx);
            utilBtn = itemView.findViewById(R.id.utilBtn);
            attendButton = itemView.findViewById(R.id.attendButton);
        }

        void onBind(final SearchScheduleData data) {
            scheduleWeek.setText(data.getScheduleWeek());
            scheduleCount.setText(data.getScheduleCount());
            scheduleDate.setText(data.getScheduleDate());
            scheduleTitle.setText(data.getScheduleTitle());
            scheduleContent.setText(data.getScheduleContent());
            scheduleLocation.setText(data.getScheduleLocation());
            scheduleMember.setText(String.valueOf(data.getScheduleMember()));
            scheduleIdx.setText(String.valueOf(data.getScheduleIdx()));
            groupIdx.setText(String.valueOf(data.getGroupIdx()));

            if (diffOfDate(data.getScheduleDate()) < 0) {
                calendarIcon.setImageResource(R.drawable.img_calendar_end);
            } else {
                calendarIcon.setImageResource(R.drawable.img_calendar);
//                Log.e(TAG, String.valueOf(diffOfDate(data.getScheduleDate())));
            }


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

            //일정 참석 버튼을 눌렀을 경우
            attendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //로그인한 이메일과 모임장의 이메일이 다를 경우에는 일정 수정/삭제 버튼을 숨긴다.
            if (!PAGE_GROUP_HOST.equals(loginUserId)) {
                utilBtn.setVisibility(View.GONE);
            } else {
                //일정 수정/삭제 팝업 버튼을 클릭했을 경우
                utilBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), PopupScheduleUtilActivity.class);
                        intent.putExtra("itemPositionOri", getAdapterPosition()); //아이템 위치값
                        intent.putExtra("itemSizeOri", listData.size()); //아이템 크기
                        intent.putExtra("scheduleIdxOri", data.getScheduleIdx()); //아이템 일정 index
                        intent.putExtra("scheduleTitleOri", data.getScheduleTitle()); //아이템 일정 제목
                        intent.putExtra("scheduleContentOri", data.getScheduleContent()); //아이템 일정 내용
                        intent.putExtra("scheduleDateOri", data.getScheduleDate()); //아이템 일정 날짜
                        intent.putExtra("scheduleLocationOri", data.getScheduleLocation()); //아이템 일정 장소
                        v.getContext().startActivity(intent);
                    }
                });
            }

        }
    }


}
