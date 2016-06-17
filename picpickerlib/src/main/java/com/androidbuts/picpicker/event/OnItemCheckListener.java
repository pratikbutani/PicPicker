package com.androidbuts.picpicker.event;

import com.androidbuts.picpicker.entity.Photo;

/**
 * Created by Pratik Butani
 */
public interface OnItemCheckListener {

    /**
     * @param position
     * @param path
     * @param isCheck
     * @param selectedItemCount
     * @return
     */
    boolean OnItemCheck(int position, Photo path, boolean isCheck, int selectedItemCount);

}
