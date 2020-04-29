package com.hungdt.waterplant.view.adater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hungdt.waterplant.R;
import com.hungdt.waterplant.model.VipDetail;

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
        TextView txtTitle = view.findViewById(R.id.txtTitle);
        TextView txtDes = view.findViewById(R.id.txtDes);
        imgImagesVD.setBackground(layoutInflater.getContext().getDrawable(vipDetails.get(position).getImg()));
        txtTitle.setText(vipDetails.get(position).getTitle());
        txtDes.setText(vipDetails.get(position).getDes());
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
