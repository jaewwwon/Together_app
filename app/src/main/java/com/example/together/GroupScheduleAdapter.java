package com.example.together;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.together.GroupScheduleActivity.diffOfDate;
import static com.example.together.StaticInit.PAGE_GROUP_HOST;
import static com.example.together.StaticInit.loginUserId;

public class GroupScheduleAdapter extends RecyclerView.Adapter<GroupScheduleAdapter.ItemViewHolder> {

    //속성(변수) 초기화
    private Context context;
    private static String TAG = "GroupScheduleAdapter";
    private static String IP_ADDRESS = "13.125.221.240"; //JSON 데이터를 가져올 IP주소
    private String jsonString; // json 데이터 파일
    public ArrayList<SearchScheduleData> listData = new ArrayList<>(); //adapter에 들어갈 list


    public GroupScheduleAdapter(Context context) {
        this.context = context;
    }

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
        private TextView attendCancelButton; //일정 참석 취소버튼
        private TextView attendInfo; //참석 멤버 팝업 버튼

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
            attendCancelButton = itemView.findViewById(R.id.attendCancelButton);
            attendInfo = itemView.findViewById(R.id.attendInfo);
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


            //참석멤버 정보 팝업 버튼을 눌럿을 경우, 참석멤버 정보 팝업 액티비티 호출
            attendInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PopupAttendMemberActivity.class);
                    intent.putExtra("scheduleIdxOri", String.valueOf(data.getScheduleIdx())); //일정 index
                    Log.e(TAG, "전달하는 일정 index 값: " + data.getScheduleIdx());
                    v.getContext().startActivity(intent);
                }
            });

            //모임 참석여부 결과값 가져오기
            String attenCheckResult = null;
            AttendCheckData checkTask = new AttendCheckData();
            // 일정 index, 로그인 이메일 순서
            try {
                attenCheckResult = checkTask.execute("http://" + IP_ADDRESS + "/db/attend_check.php", String.valueOf(data.getScheduleIdx()), loginUserId).get();
                Log.e(TAG, "일정 참석 여부 : " + attenCheckResult);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //참석하지 않았을 경우 참석버튼을 보여주고, 참석 취소 버튼을 숨긴다.
            if(attenCheckResult.equals("0")){
                attendButton.setVisibility(View.VISIBLE);
                attendCancelButton.setVisibility(View.GONE);
            } else {
            //참석한 경우 참석버튼을 숨기고, 참석 취소 버튼을 보여준다.
                attendButton.setVisibility(View.GONE);
                attendCancelButton.setVisibility(View.VISIBLE);
            }


            //일정 참석 버튼을 눌렀을 경우
            attendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    // 제목셋팅
                    alertDialogBuilder.setTitle("알림");

                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("해당 일정에 참석하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("참석하기",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //모임 정보 JSON 파일 가져오기
                                            ScheduleAttendData attendTask = new ScheduleAttendData();
                                            // 모임 index번호, 일정 index, 로그인 이메일 순서
                                            attendTask.execute("http://" + IP_ADDRESS + "/group/schedule_attend.php",
                                                    String.valueOf(data.getGroupIdx()),
                                                    String.valueOf(data.getScheduleIdx()),
                                                    loginUserId);

                                            Intent intent = new Intent(v.getContext(), GroupScheduleActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            v.getContext().startActivity(intent);
                                            // 다이얼로그를 종료한다
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("닫기",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 다이얼로그를 종료한다
                                            dialog.cancel();
                                        }
                                    });

                    // 다이얼로그 생성
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // 다이얼로그 보여주기
                    alertDialog.show();
                }
            });

            //일정 참석 취소버튼을 눌렀을 경우
            attendCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());

                    // 제목셋팅
                    alertDialogBuilder.setTitle("알림");

                    // AlertDialog 셋팅
                    alertDialogBuilder
                            .setMessage("해당 일정 참석을 취소하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("참석취소",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //모임 정보 JSON 파일 가져오기
                                            ScheduleAttendCancelData attendCancelTask = new ScheduleAttendCancelData();
                                            // 일정 index, 로그인 이메일 순서
                                            attendCancelTask.execute("http://" + IP_ADDRESS + "/group/schedule_cancel.php",
                                                    String.valueOf(data.getScheduleIdx()),
                                                    loginUserId);

                                            Intent intent = new Intent(v.getContext(), GroupScheduleActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            v.getContext().startActivity(intent);
                                            // 다이얼로그를 종료한다
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("닫기",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 다이얼로그를 종료한다
                                            dialog.cancel();
                                        }
                                    });

                    // 다이얼로그 생성
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // 다이얼로그 보여주기
                    alertDialog.show();
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

    //일정 참석 여부값 가져오기
    private class AttendCheckData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {
                jsonString = result;
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String scIdx = params[1];
            String memberEmail = params[2];

            // 일정 index, 로그인 이메일 순서
            String postParameters = "scIdx=" + scIdx + "&memberEmail=" + memberEmail;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    //일정 참석 정보 보내기
    private class ScheduleAttendData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {
                jsonString = result;
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String groupIdx = params[1];
            String scIdx = params[2];
            String memberEmail = params[3];

            // 모임 index번호, 일정 index, 로그인 이메일 순서
            String postParameters = "groupIdx=" + groupIdx + "&scIdx=" + scIdx + "&memberEmail=" + memberEmail;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    //일정 참석취소 정보 보내기
    private class ScheduleAttendCancelData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(context,
                    "Please Wait", null, true, true);
        }


        // 에러가 있는 경우 에러메시지를 보여주고 아니면 JSON을 파싱하여 화면에 보여주는 scheduleResult 메소드를 호출합니다.
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
//            Log.e(TAG, "response - " + result);

            if (result == null) {
                Log.e(TAG, errorString);
            } else {
                jsonString = result;
            }
        }

        // doInBackground 메소드에서 서버에 있는 PHP 파일을 실행시키고, 응답을 저장하고, 스트링으로 변환하여 리턴합니다.
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String scIdx = params[1];
            String memberEmail = params[2];

            // 일정 index, 로그인 이메일 순서
            String postParameters = "scIdx=" + scIdx + "&memberEmail=" + memberEmail;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
//                Log.e(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }


}
