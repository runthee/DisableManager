package com.nagopy.android.disablemanager.util.sort;

import java.util.Comparator;

import com.nagopy.android.disablemanager.util.AppStatus;
import com.nagopy.android.disablemanager.util.ChangedDateUtils;

/**
 * 変更された日付でソート、それ以外はラベル・パッケージ名でソートする
 */
final class AppComparatorWithDate implements Comparator<AppStatus> {

	/**
	 * インスタンス
	 */
	private static final AppComparatorWithDate instance = new AppComparatorWithDate();

	/**
	 * @see ChangedDateUtils
	 */
	private ChangedDateUtils mDateUtils;

	/**
	 * コンストラクタ
	 */
	private AppComparatorWithDate() {} // CHECKSTYLE IGNORE THIS LINE

	/**
	 * インスタンスを取得する
	 * @param dateUtils
	 *           {@link ChangedDateUtils}
	 * @return インスタンス
	 */
	public static Comparator<AppStatus> getInstance(ChangedDateUtils dateUtils) {
		instance.mDateUtils = dateUtils;
		return instance;
	}

	@Override
	public int compare(final AppStatus obj0, final AppStatus obj1) {
		String pkgName0 = obj0.getPackageName();
		String pkgName1 = obj1.getPackageName();

		long date0 = mDateUtils.get(pkgName0);
		long date1 = mDateUtils.get(pkgName1);
		if (date0 != date1) {
			return (int) (date1 - date0);
		}

		String label0 = obj0.getLabel();
		String label1 = obj1.getLabel();

		int ret = label0.compareToIgnoreCase(label1);
		// ラベルで並び替え、同じラベルがあったらパッケージ名で
		if (ret == 0) {
			ret = pkgName0.compareToIgnoreCase(pkgName1);
		}
		return ret;
	}
}
