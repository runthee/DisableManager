/*
 * Copyright (C) 2015 75py
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
package com.nagopy.android.disablemanager2.judger;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import java.lang.reflect.Method;

/**
 * 「無効化ができるかを判定するためのクラス」の基になるクラス
 */
public abstract class Judge {

    /**
     * アプリケーションのコンテキスト
     */
    private Context mContext;

    /**
     * デバイスポリシーマネージャー<br>
     * 判定に使う
     */
    private DevicePolicyManager mDevicePolicyManager;

    /**
     * {@link android.app.admin.DevicePolicyManager}のpackageHasActiveAdminsを実行するためのメソッド
     */
    private Method methodPackageHasActiveAdmins;

    /**
     * PackageManager
     */
    private PackageManager mPackageManager;

    /**
     * システムのPackageInfo
     */
    private PackageInfo mSystemPackageInfo;

    /**
     * そのアプリが無効化できるものかどうかを判定する
     *
     * @param applicationInfo ApplicationInfoを渡す
     * @return 無効化できるならtrue、そうでなければfalseを返す。
     */
    public abstract boolean isDisablable(ApplicationInfo applicationInfo);

    /**
     * コンストラクタ
     *
     * @param context アプリケーションのコンストラクタ
     */
    protected Judge(Context context) {
        setContext(context);
        mPackageManager = context.getPackageManager();
        mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            methodPackageHasActiveAdmins = mDevicePolicyManager.getClass().getMethod("packageHasActiveAdmins",
                    String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            mSystemPackageInfo = getPackageManager().getPackageInfo("android", PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * {@link android.app.admin.DevicePolicyManager}のpackageHasActiveAdminsメソッドを実行する
     *
     * @param packageName パッケージ名
     * @return packageHasActiveAdminsの結果を返す。<br>
     * エラーがあった場合はfalseを返す。
     */
    protected boolean packageHasActiveAdmins(String packageName) {
        try {
            return (boolean) methodPackageHasActiveAdmins.invoke(mDevicePolicyManager, packageName);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * {@link android.app.admin.DevicePolicyManager}のisThisASystemPackageメソッドと同じ内容.<br>
     * 4.4以下で使用。
     *
     * @param packageInfo 判定したいpackageinfo
     * @return isThisASystemPackageの結果をそのまま返す。<br>
     * エラーがあった場合はfalseを返す。
     */
    protected boolean isThisASystemPackage(PackageInfo packageInfo) {
        return (packageInfo != null && packageInfo.signatures != null && mSystemPackageInfo != null && mSystemPackageInfo.signatures[0]
                .equals(packageInfo.signatures[0]));
    }

    /**
     * 無効化できるかどうかを判定するクラスのオブジェクトを作る
     *
     * @param context アプリケーションのコンテキスト
     * @return 4.2以上、4.0.以上、それ未満で別々のオブジェクトを返す
     */
    public static Judge getInstance(Context context) {
        int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.LOLLIPOP) {
            // 5.0以上
            return new JudgeLollipop(context);
        } else if (version >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // 4.2以上
            return new JudgeJbMr1(context);
        } else if (version >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // 4.0.4以上
            return new JudgeIcs(context);
        } else {
            // 4.0.4未満の場合は、常にfalseを返すようにする
            return new Judge(context) {
                @Override
                public boolean isDisablable(final ApplicationInfo applicationInfo) {
                    return false;
                }
            };
        }
    }

    /**
     * @return パッケージマネージャーを返す
     */
    public PackageManager getPackageManager() {
        return mPackageManager;
    }

    /**
     * @return コンテキスト
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * @param mContext コンテキスト
     */
    private void setContext(Context mContext) {
        this.mContext = mContext;
    }
}
