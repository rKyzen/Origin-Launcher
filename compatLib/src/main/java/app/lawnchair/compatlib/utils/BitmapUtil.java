/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.compatlib.utils;

import android.graphics.Bitmap;
import android.annotation.SuppressLint;
import android.graphics.ColorSpace;
import android.graphics.ParcelableColorSpace;
import android.hardware.HardwareBuffer;
import android.os.Bundle;
import java.util.Objects;

/** Utils for working with Bitmaps. */
public final class BitmapUtil {
    private static final String KEY_BUFFER = "bitmap_util_buffer";
    private static final String KEY_COLOR_SPACE = "bitmap_util_color_space";

    private BitmapUtil() {}

    /**
     * Creates a Bundle that represents the given Bitmap.
     *
     * <p>The Bundle will contain a wrapped version of the Bitmaps HardwareBuffer, so will avoid
     * copies when passing across processes, only pass to processes you trust.
     *
     * <p>Returns a new Bundle rather than modifying an exiting one to avoid key collisions, the
     * returned Bundle should be treated as a standalone object.
     *
     * @param bitmap to convert to bundle
     * @return a Bundle representing the bitmap, should only be parsed by {@link
     *     #bundleToHardwareBitmap(Bundle)}
     */
    @SuppressLint("NewApi")
    public static Bundle hardwareBitmapToBundle(Bitmap bitmap) {
        if (bitmap.getConfig() != Bitmap.Config.HARDWARE) {
            throw new IllegalArgumentException(
                    "Passed bitmap must have hardware config, found: " + bitmap.getConfig());
        }
        // Bitmap assumes SRGB for null color space
        ParcelableColorSpace colorSpace = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            colorSpace = bitmap.getColorSpace() == null
                    ? new ParcelableColorSpace(ColorSpace.get(ColorSpace.Named.SRGB))
                    : new ParcelableColorSpace(bitmap.getColorSpace());
        }
        // For lower API levels, colorSpace remains null; bundle will omit this key.

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BUFFER, bitmap.getHardwareBuffer());
        bundle.putParcelable(KEY_COLOR_SPACE, colorSpace);
        return bundle;
    }

    /**
     * Extracts the Bitmap added to a Bundle with {@link #hardwareBitmapToBundle(Bitmap)} .}
     *
     * <p>This Bitmap contains the HardwareBuffer from the original caller, be careful passing this
     * Bitmap on to any other source.
     *
     * @param bundle containing the bitmap
     * @return a hardware Bitmap
     */
    @SuppressLint("NewApi")
    public static Bitmap bundleToHardwareBitmap(Bundle bundle) {
        HardwareBuffer buffer = bundle.getParcelable(KEY_BUFFER);
        ParcelableColorSpace colorSpace = bundle.getParcelable(KEY_COLOR_SPACE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            return Bitmap.wrapHardwareBuffer(
                    Objects.requireNonNull(buffer),
                    (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && colorSpace != null)
                            ? colorSpace.getColorSpace() : null);
        }
        throw new UnsupportedOperationException("Hardware bitmap not supported on this API level");
    }
}
