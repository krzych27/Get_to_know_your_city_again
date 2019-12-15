package com.Get_to_know_your_city_again.ui.map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.Get_to_know_your_city_again.ItemListActivity;
import com.Get_to_know_your_city_again.MapsActivity;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

public class MyItemizedOverlay extends ItemizedOverlayWithFocus<OverlayItem> {

    private Context pContext;

    MyItemizedOverlay(Context context, List<OverlayItem> aList) {
        super(context, aList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                Toast.makeText(context,""+item.getTitle(),Toast.LENGTH_LONG).show();
                return true;
            }
            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {

                String nameItem = item.getTitle();
                Intent intent = new Intent(context,
                        ItemListActivity.class);
                intent.putExtra("name",nameItem);
                context.startActivity(intent);

//                Toast.makeText(context,""+item.getSnippet(),Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

}
