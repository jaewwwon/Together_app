package com.example.together;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupBoardAdapter extends RecyclerView.Adapter<GroupBoardAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    Context context;
    private static String TAG = "GroupBoardAdapter";
    public ArrayList<GroupBoardData> listData = new ArrayList<>(); //adapter에 들어갈 list

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board_list, parent, false);
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

    void addItem(int position, GroupBoardData data) {
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

        private TextView boardTitle; //게시글 제목
        private TextView boardUser; //게시글 작성자
        private TextView boardDate; //게시글 작성일
        private TextView boardView; //게시글 조회수
        private TextView boardIdx; //게시글 index
        private TextView boardComment; //게시글 댓글수

        ItemViewHolder(View itemView) {
            super(itemView);

            boardTitle = itemView.findViewById(R.id.boardTitle);
            boardUser = itemView.findViewById(R.id.boardUser);
            boardDate = itemView.findViewById(R.id.boardDate);
            boardView = itemView.findViewById(R.id.boardView);
            boardIdx = itemView.findViewById(R.id.boardIdx);
            boardComment = itemView.findViewById(R.id.boardComment);
        }

        void onBind(final GroupBoardData data) {
            boardTitle.setText(data.getBoardTitle());
            boardUser.setText(data.getBoardUser());
            boardDate.setText(data.getBoardDate());
            boardView.setText(String.valueOf(data.getBoardView()));
            boardIdx.setText(String.valueOf(data.getBoardIdx()));
            boardComment.setText(String.valueOf(data.getBoardComment()));


            // 해당 리사이클러뷰 아이템을 클릭했을 경우
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), GroupBoardViewActivity.class);
                    intent.putExtra("itemPositionOri", getAdapterPosition()); //아이템 위치값
                    intent.putExtra("itemSizeOri", listData.size()); //아이템 크기
                    intent.putExtra("boardIdxOri", data.getBoardIdx()); //해당 게시글의 index
                    v.getContext().startActivity(intent);
                }
            });

        }
    }


}
