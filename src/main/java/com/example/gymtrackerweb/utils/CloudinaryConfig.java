package com.example.gymtrackerweb.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {
    private static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dzed5tvl6",
                "api_key", "139292726386223",
                "api_secret", "VnzSrUDC8bf4v2UiSx6vJMRc5G0"
        ));
    }

    public static Cloudinary getInstance() {
        return cloudinary;
    }
}
