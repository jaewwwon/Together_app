package com.example.together;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GroupPhotoSliderAdapter extends PagerAdapter {

    private int[] images = {R.drawable.img_bg_default_small,
            R.drawable.img_profile,
            R.drawable.common_full_open_on_phone};
    private LayoutInflater inflater;
    private Context context;

    public GroupPhotoSliderAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_slider, container, false);
        ImageView imageView = (ImageView)v.findViewById(R.id.slider_img);
//        TextView textView = (TextView)v.findViewById(R.id.textView);
        imageView.setImageResource(images[position]);
//        String text = (position + 1) + "번째 이미지";
//        textView.setText(text);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }
}
