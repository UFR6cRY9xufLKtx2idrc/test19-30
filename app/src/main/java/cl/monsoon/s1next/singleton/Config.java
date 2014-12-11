package cl.monsoon.s1next.singleton;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cl.monsoon.s1next.MyApplication;
import cl.monsoon.s1next.R;
import cl.monsoon.s1next.fragment.SettingsFragment;

public enum Config {
    INSTANCE;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LIGHT_THEME, DARK_THEME})
    public @interface Theme {

    }

    public static final int LIGHT_THEME = R.style.LightTheme;

    /**
     * Night mode.
     */
    public static final int DARK_THEME = R.style.DarkTheme;

    /**
     * Take care of Menu ~ open in browser ~
     * when these numbers are not default.
     */
    public static final int THREADS_PER_PAGE = 50;
    public static final int POSTS_PER_PAGE = 30;

    private volatile int currentTheme;
    private volatile int colorAccent;
    private volatile boolean wifi;
    private volatile DownloadStrategy avatarsDownloadStrategy;
    private volatile DownloadStrategy imagesDownloadStrategy;

    @Theme
    public static int getCurrentTheme() {
        return INSTANCE.currentTheme;
    }

    public static void setCurrentTheme(@Theme int theme) {
        INSTANCE.currentTheme = theme;

        // get theme's accent color
        TypedArray typedArray =
                MyApplication.getContext()
                        .obtainStyledAttributes(theme, new int[]{R.attr.colorAccent});
        INSTANCE.colorAccent = typedArray.getColor(0, -1);

        if (INSTANCE.colorAccent == -1) {
            throw new IllegalStateException("Theme accent color can't be -1.");
        }
    }

    @ColorRes
    public static int getColorAccent() {
        return INSTANCE.colorAccent;
    }

    public static void setWifi(boolean wifi) {
        INSTANCE.wifi = wifi;
    }

    public static DownloadStrategy getAvatarsDownloadStrategy() {
        return INSTANCE.avatarsDownloadStrategy;
    }

    public static void setAvatarsDownloadStrategy(SharedPreferences sharedPreferences) {
        String value =
                sharedPreferences.getString(
                        SettingsFragment.KEY_PREF_DOWNLOAD_AVATARS,
                        MyApplication.getContext()
                                .getString(R.string.pref_download_avatars_default_value));

        INSTANCE.avatarsDownloadStrategy = DownloadStrategy.fromString(value);
    }

    public static boolean isAvatarsDownload() {
        return isDownload(INSTANCE.avatarsDownloadStrategy);
    }

    public static DownloadStrategy getImagesDownloadStrategy() {
        return INSTANCE.imagesDownloadStrategy;
    }

    public static void setImagesDownloadStrategy(SharedPreferences sharedPreferences) {
        String value =
                sharedPreferences.getString(
                        SettingsFragment.KEY_PREF_DOWNLOAD_IMAGES,
                        MyApplication.getContext().
                                getString(R.string.pref_download_images_default_value));

        INSTANCE.imagesDownloadStrategy = DownloadStrategy.fromString(value);
    }

    public static boolean isImagesDownload() {
        return isDownload(INSTANCE.imagesDownloadStrategy);
    }

    private static boolean isDownload(DownloadStrategy downloadStrategy) {
        return
                downloadStrategy ==
                        DownloadStrategy.WIFI
                        && INSTANCE.wifi
                        || downloadStrategy == DownloadStrategy.ALWAYS;
    }

    public static enum DownloadStrategy {
        NOT, WIFI, ALWAYS;

        private static final DownloadStrategy[] values = DownloadStrategy.values();

        public static DownloadStrategy fromString(String value) {
            return values[Integer.parseInt(value)];
        }
    }
}
