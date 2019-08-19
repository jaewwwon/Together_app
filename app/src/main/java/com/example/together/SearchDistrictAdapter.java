package com.example.together;

import android.app.Activity;
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

public class SearchDistrictAdapter extends RecyclerView.Adapter<SearchDistrictAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    final private String TAG = "SearchDistrictAdapter";
    Context context;
    private ArrayList<SearchDistrictData> listData = new ArrayList<>(); //adapter에 들어갈 list
    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;


    public interface OnItemClickListener {
        void onItemClick(View v, int position, SearchDistrictData data);
    }

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_district, parent, false);
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

    void addItem(int position, SearchDistrictData data) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(0, data);
    }

    void removeAllItem() {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.clear();
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView districtName; //읍면동 명
        private TextView districtCounty; //시군구 명
        private TextView districtCity; //시도 명

        ItemViewHolder(View itemView) {
            super(itemView);

            districtName = itemView.findViewById(R.id.districtName);
            districtCounty = itemView.findViewById(R.id.districtCounty);
            districtCity = itemView.findViewById(R.id.districtCity);
        }

        void onBind(final SearchDistrictData data) {
            districtName.setText(data.getDistrictName());
            districtCounty.setText(data.getDistrictCounty());
            districtCity.setText(data.getDistrictCity());

            // 해당 리사이클러뷰 아이템을 클릭했을 경우
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(v.getContext(), CreateGroupInfoActivity.class);
//                    intent.putExtra("districtNameOri", data.getDistrictName());
////                    intent.putExtra("placeAddress", data.getPlaceAddress());
//                    v.getContext().startActivity(intent);

                    //결과 값을 resultIntent 에 담아서 PopupScheduleAddActivity 로 전달하고 현재 Activity는 종료.
//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("districtName", data.getDistrictName());
////                    resultIntent.putExtra("districtCounty", data.getDistrictCounty());
////                    resultIntent.putExtra("districtCity", data.getDistrictCity());
//                    ((Activity) context).setResult(Activity.RESULT_OK, resultIntent);
//                    ((Activity) context).finish();

                    /////////////////////////

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos, data);
                        }
                    }


                }

            });

        }
    }

}
