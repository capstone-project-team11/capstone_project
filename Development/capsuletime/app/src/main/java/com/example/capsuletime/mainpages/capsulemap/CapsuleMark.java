package com.example.capsuletime.mainpages.capsulemap;

import com.example.capsuletime.CapsuleOneOfAll;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class CapsuleMark implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mNickName;
    private final String mSnippet;
    private final CapsuleOneOfAll mCapsule;

    public CapsuleMark(CapsuleOneOfAll capsule) {
        mCapsule = capsule;
        mPosition = new LatLng(capsule.getLat(), capsule.getLng());
        mTitle = mCapsule.getTitle();
        mSnippet = mCapsule.getText();
        mNickName = mCapsule.getNick_name();
    }

    public String getNickName() {
        return mNickName;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
