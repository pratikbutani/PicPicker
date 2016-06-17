package com.androidbuts.picpicker.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.androidbuts.picpicker.PhotoPickerActivity;
import com.androidbuts.picpicker.R;
import com.androidbuts.picpicker.adapter.PhotoGridAdapter;
import com.androidbuts.picpicker.adapter.PopupDirectoryListAdapter;
import com.androidbuts.picpicker.entity.Photo;
import com.androidbuts.picpicker.entity.PhotoDirectory;
import com.androidbuts.picpicker.event.OnPhotoClickListener;
import com.androidbuts.picpicker.permission.PermissionsActivity;
import com.androidbuts.picpicker.permission.PermissionsChecker;
import com.androidbuts.picpicker.utils.ImageCaptureManager;
import com.androidbuts.picpicker.utils.MediaStoreHelper;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.androidbuts.picpicker.PhotoPickerActivity.EXTRA_SHOW_GIF;
import static com.androidbuts.picpicker.utils.MediaStoreHelper.INDEX_ALL_PHOTOS;

/**
 * Created by Pratik Butani
 */
public class PhotoPickerFragment extends Fragment {

    private static final String[] PERMISSIONS_CAMERA = new String[]{Manifest.permission.CAMERA};

    /**
     * Permission List
     */
    private static final String[] PERMISSIONS_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * Permission Checker
     */
    PermissionsChecker permissionsChecker;
    private ImageCaptureManager captureManager;
    private PhotoGridAdapter photoGridAdapter;
    private PopupDirectoryListAdapter listAdapter;
    private List<PhotoDirectory> directories;
    private int SCROLL_THRESHOLD = 30;

    /**
     * Views
     */
    StaggeredGridLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        permissionsChecker = new PermissionsChecker(getActivity());

        directories = new ArrayList<>();

        captureManager = new ImageCaptureManager(getActivity());

        Bundle mediaStoreArgs = new Bundle();
        if (getActivity() instanceof PhotoPickerActivity) {
            mediaStoreArgs.putBoolean(EXTRA_SHOW_GIF, ((PhotoPickerActivity) getActivity()).isShowGif());
        }

        if (permissionsChecker.lacksPermissions(PERMISSIONS_STORAGE)) {
            PermissionsActivity.startActivityForResult(getActivity(), 0, PERMISSIONS_STORAGE);
            getActivity().finish();
        } else {

            MediaStoreHelper.getPhotoDirs(getActivity(), mediaStoreArgs,
                    new MediaStoreHelper.PhotosResultCallback() {
                        @Override
                        public void onResultCallback(List<PhotoDirectory> dirs) {
                            directories.clear();
                            directories.addAll(dirs);
                            photoGridAdapter.notifyDataSetChanged();
                            listAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setRetainInstance(true);

        final View rootView = inflater.inflate(R.layout.fragment_photo_picker, container, false);

        photoGridAdapter = new PhotoGridAdapter(getActivity(), directories);
        listAdapter = new PopupDirectoryListAdapter(getActivity(), directories);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_photos);
        layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(photoGridAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final Button btSwitchDirectory = (Button) rootView.findViewById(R.id.button);

        final ListPopupWindow listPopupWindow = new ListPopupWindow(getActivity());
        listPopupWindow.setWidth(ListPopupWindow.MATCH_PARENT);
        listPopupWindow.setAnchorView(btSwitchDirectory);
        listPopupWindow.setAdapter(listAdapter);
        listPopupWindow.setModal(true);
        listPopupWindow.setDropDownGravity(Gravity.BOTTOM);
        listPopupWindow.setAnimationStyle(R.style.Animation_AppCompat_DropDownUp);

        listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPopupWindow.dismiss();

                PhotoDirectory directory = directories.get(position);

                btSwitchDirectory.setText(directory.getName());

                photoGridAdapter.setCurrentDirectoryIndex(position);
                photoGridAdapter.notifyDataSetChanged();
            }
        });

        photoGridAdapter.setOnPhotoClickListener(new OnPhotoClickListener() {
            @Override
            public void onClick(View v, int position, boolean showCamera) {
                final int index = showCamera ? position - 1 : position;

                List<String> photos = photoGridAdapter.getCurrentPhotoPaths();

                int[] screenLocation = new int[2];
                v.getLocationOnScreen(screenLocation);
                ImagePagerFragment imagePagerFragment =
                        ImagePagerFragment.newInstance(photos, index, screenLocation, v.getWidth(),
                                v.getHeight());

                ((PhotoPickerActivity) getActivity()).addImagePagerFragment(imagePagerFragment);
            }
        });

        photoGridAdapter.setOnCameraClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (permissionsChecker.lacksPermissions(PERMISSIONS_CAMERA)) {
                        PermissionsActivity.startActivityForResult(getActivity(), 0, PERMISSIONS_CAMERA);
                    } else {
                        Intent intent = captureManager.dispatchTakePictureIntent();
                        startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                    }
                } catch (IOException e) {
                    Log.e("TAG", "Error : " + e.getLocalizedMessage());
                }
            }
        });

        btSwitchDirectory.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listPopupWindow.isShowing()) {
                    listPopupWindow.dismiss();
                } else if (!getActivity().isFinishing()) {
                    listPopupWindow.setHeight(Math.round(rootView.getHeight() * 0.8f));
                    listPopupWindow.show();
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Log.d(">>> Picker >>>", "dy = " + dy);
                if (Math.abs(dy) > SCROLL_THRESHOLD) {
                    Glide.with(getActivity()).pauseRequests();
                } else {
                    Glide.with(getActivity()).resumeRequests();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(getActivity() != null)
                        Glide.with(getActivity()).resumeRequests();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ImageCaptureManager.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            captureManager.galleryAddPic();
            if (directories.size() > 0) {
                String path = captureManager.getCurrentPhotoPath();
                PhotoDirectory directory = directories.get(INDEX_ALL_PHOTOS);
                directory.getPhotos().add(INDEX_ALL_PHOTOS, new Photo(path.hashCode(), path));
                directory.setCoverPath(path);
                photoGridAdapter.notifyDataSetChanged();
            }
        }
    }

    public PhotoGridAdapter getPhotoGridAdapter() {
        return photoGridAdapter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        captureManager.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        captureManager.onRestoreInstanceState(savedInstanceState);
        super.onViewStateRestored(savedInstanceState);
    }

    public ArrayList<String> getSelectedPhotoPaths() {
        return photoGridAdapter.getSelectedPhotoPaths();
    }
}
