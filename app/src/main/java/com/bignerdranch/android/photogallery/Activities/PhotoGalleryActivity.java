package com.bignerdranch.android.photogallery.Activities;

import android.support.v4.app.Fragment;

import com.bignerdranch.android.photogallery.Fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
