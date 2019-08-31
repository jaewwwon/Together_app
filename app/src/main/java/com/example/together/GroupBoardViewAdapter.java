package com.example.together;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.together.StaticInit.loginUserId;

public class GroupBoardViewAdapter extends RecyclerView.Adapter<GroupBoardViewAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    Context context;
    private static String TAG = "GroupBoardViewAdapter";
    public ArrayList<GroupBoardViewData> listData = new ArrayList<>(); //adapter에 들어갈 list

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));
//        Log.e(TAG+"_댓글 : ", String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(GroupBoardViewData data) {
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

        private ImageView userProfile; //댓글 작성자 프로필
        private TextView userName; //댓글 작성자 이름
        private TextView commentDate; //댓글 작성일
        private TextView commentContent; //댓글 내용
        private ImageView utilBtn; //댓글 컨트롤(수정, 삭제, 답글) 버튼

        ItemViewHolder(View itemView) {
            super(itemView);

            userProfile = itemView.findViewById(R.id.userProfile);
            userName = itemView.findViewById(R.id.userName);
            commentDate = itemView.findViewById(R.id.commentDate);
            commentContent = itemView.findViewById(R.id.commentContent);
            utilBtn = itemView.findViewById(R.id.utilBtn);
        }

        void onBind(final GroupBoardViewData data) {
//            userProfile.setText(data.getUserProfile());
            Glide.with(itemView.getContext())
                    .load(data.getUserProfile())
                    .override(500, 500)
                    .into(userProfile);
            userName.setText(data.getUserName());
            commentDate.setText(data.getCommentDate());
            commentContent.setText(String.valueOf(data.getCommentContent()));

            //로그인한 아메일과 작성자 이메일이 다를 경우, 수정버튼을 숨긴다.
            if(!data.getUserEmail().equals(loginUserId)){
                utilBtn.setVisibility(View.GONE);
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

        }
    }


}
