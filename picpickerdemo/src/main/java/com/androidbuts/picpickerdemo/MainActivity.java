package com.androidbuts.picpickerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.androidbuts.picpicker.PhotoPickerActivity;
import com.androidbuts.picpicker.utils.PhotoPickerIntent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Requesting by this code
     */
    public final static int REQUEST_CODE = 1;

    /**
     * Layout
     */
    RecyclerView recyclerView;

    /**
     * Adapter for selected photos
     */
    PhotoAdapter photoAdapter;

    /**
     * Selected Photos
     */
    ArrayList<String> selectedPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Setting up with Toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * RecyclerView
         */
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        /**
         * Adapter with blank array
         */
        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        /**
         * RecyclerView LayoutManager
         */
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(photoAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Getting list of selected items
         */
        List<String> photos = null;
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPickerActivity.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            /**
             * Updating selected photos in RecyclerView
             */
            photoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * For Preview of Photo
     *
     * @param intent
     */
    public void previewPhoto(Intent intent) {
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Common clicks for All Buttons
     *
     * @param view
     */
    public void openPicker(View view) {


        switch (view.getId()) {
            case R.id.buttonPickPhoto: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(9);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }

            case R.id.buttonPickPhotoNoCamera: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(7);
                intent.setShowCamera(false);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }

            case R.id.buttonPickOnePhoto: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(1);
                intent.setShowCamera(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }

            case R.id.buttonPickPhotoGif: {
                PhotoPickerIntent intent = new PhotoPickerIntent(MainActivity.this);
                intent.setPhotoCount(4);
                intent.setShowCamera(true);
                intent.setShowGif(true);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            }
        }

    }
}
