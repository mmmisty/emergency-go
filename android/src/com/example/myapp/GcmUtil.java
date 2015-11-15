package com.example.myapp;

import android.content.Context;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class GcmUtil {
    private Context _ctx;
    private GoogleCloudMessaging _gcm;
    private String _projectId;

    public GcmUtil(Context ctx, String projectId) {
        _ctx = ctx;
        _projectId = projectId;
        _gcm = GoogleCloudMessaging.getInstance(_ctx);
    }

    public String Register() throws IOException {
        return _gcm.register(_projectId);
    }
}
