package com.hungdt.waterplan.view.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.hungdt.waterplan.R;
import com.hungdt.waterplan.model.VipDetail;

import java.util.List;

public class DetailVIPAdapter extends PagerAdapter {

    private List<VipDetail> vipDetails;
    private LayoutInflater layoutInflater;

    public DetailVIPAdapter(Context context, List<VipDetail> vipDetails) {
        layoutInflater = LayoutInflater.from(context);
        this.vipDetails = vipDetails;
    }

    @Override
    public int getCount() {
        return vipDetails.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.item_detail_vip, container, false);
        ImageView imgImagesVD = view.findViewById(R.id.imgImagesVD);
        TextView txtTitle1 = view.findViewById(R.id.txtTitle1);
        TextView txtTitle2 = view.findViewById(R.id.txtTitle2);
        TextView txtDes1 = view.findViewById(R.id.txtDes1);
        TextView txtDes2 = view.findViewById(R.id.txtDes2);
        imgImagesVD.setBackground(layoutInflater.getContext().getDrawable(vipDetails.get(position).getImg()));
        txtTitle1.setText(vipDetails.get(position).getTitile1());
        txtTitle2.setText(vipDetails.get(position).getTitile2());
        txtDes1.setText(vipDetails.get(position).getDes1());
        txtDes2.setText(vipDetails.get(position).getDes2());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
