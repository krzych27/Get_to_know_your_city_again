package com.Get_to_know_your_city_again.ui.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

public class MyItemizedOverlay extends ItemizedOverlayWithFocus<OverlayItem> {

    private Context pContext;

    MyItemizedOverlay(Context context, List<OverlayItem> aList) {
        super(context, aList, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                Toast.makeText(context,""+item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                Toast.makeText(context,""+item.getSnippet(),Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }

}
