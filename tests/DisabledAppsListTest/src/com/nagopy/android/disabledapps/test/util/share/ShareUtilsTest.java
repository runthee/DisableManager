package com.nagopy.android.disabledapps.test.util.share;

import java.util.ArrayList;

import android.content.Context;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.nagopy.android.disabledapps.R;
import com.nagopy.android.disabledapps.app.MainActivity;
import com.nagopy.android.disabledapps.util.AppStatus;
import com.nagopy.android.disabledapps.util.share.ShareUtils;

public class ShareUtilsTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private ShareUtils mShareUtils;

	public ShareUtilsTest() {
		super(MainActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mShareUtils = new ShareUtils(getActivity());
	}

	public void test_isEmptyにnullでtrueが返る() throws Exception {
		mShareUtils.setList(null);
		assertTrue(mShareUtils.isEmpty());
	}

	public void test_isEmptyに空のリストでtrueが返る() throws Exception {
		mShareUtils.setList(new ArrayList<AppStatus>());
		assertTrue(mShareUtils.isEmpty());
	}

	public void test_isEmptyでアイテムがあればfalseが返る() throws Exception {
		ArrayList<AppStatus> list = new ArrayList<AppStatus>();
		list.add(new AppStatus("label", "packagename", false, false, false));
		mShareUtils.setList(list);
		assertFalse(mShareUtils.isEmpty());
	}

	public void test_createShareString_アプリ名_改行挿入なし() throws Exception {
		改行なしに変更();
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_label);
		String expected = "label_1\nlabel_2\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_アプリ名_改行挿入あり() throws Exception {
		改行ありに変更();
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_label);
		String expected = "label_1\n\nlabel_2\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_パッケージ名_改行挿入なし() throws Exception {
		改行なしに変更();
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_package);
		String expected = "packagename_1\npackagename_2\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_パッケージ名_改行挿入あり() throws Exception {
		改行ありに変更();
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_package);
		String expected = "packagename_1\n\npackagename_2\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_アプリ名とパッケージ名_改行挿入なし() throws Exception {
		改行なしに変更();
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_label_and_package);
		String expected = "label_1\npackagename_1\nlabel_2\npackagename_2\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_アプリ名とパッケージ名_改行挿入あり() throws Exception {
		改行ありに変更();
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_label_and_package);
		String expected = "label_1\npackagename_1\n\nlabel_2\npackagename_2\n\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_カスタム_コメントなし_改行挿入なし() throws Exception {
		改行なしに変更();
		コメントなしカスタムフォーマットの変更("%1$s(%2$s)");
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_customformat);
		String expected = "label_1(packagename_1)\nlabel_2(packagename_2)\n";
		assertEquals(expected, actual);
	}

	public void test_createShareString_カスタム_コメントなし_改行挿入あり() throws Exception {
		改行ありに変更();
		コメントなしカスタムフォーマットの変更("%1$s(%2$s)");
		mShareUtils.setList(createTestList());
		String actual = mShareUtils.createShareString(R.id.menu_share_customformat);
		String expected = "label_1(packagename_1)\n\nlabel_2(packagename_2)\n\n";
		assertEquals(expected, actual);
	}

	// public void test_sendIntent() throws Throwable {
	// ActivityMonitor monitor = new ActivityMonitor(new IntentFilter(Intent.ACTION_SEND,
	// "text/plain"), null,
	// false);
	// getInstrumentation().addMonitor(monitor);
	//
	// runTestOnUiThread(new Runnable() {
	// @Override
	// public void run() {
	// mShareUtils.sendIntent("text", "title");
	// }
	// });
	// getInstrumentation().waitForIdleSync();
	//
	// assertEquals(1, monitor.getHits());
	// getInstrumentation().removeMonitor(monitor);
	// }

	/**
	 * @return テスト用のアプリリストを作成して返す
	 */
	private ArrayList<AppStatus> createTestList() {
		ArrayList<AppStatus> list = new ArrayList<AppStatus>();
		list.add(new AppStatus("label_1", "packagename_1", false, false, false));
		list.add(new AppStatus("label_2", "packagename_2", false, false, false));
		return list;
	}

	private void 改行ありに変更() {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putBoolean(getContext().getString(R.string.pref_key_share_add_linebreak), true).commit());
	}

	private void 改行なしに変更() {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putBoolean(getContext().getString(R.string.pref_key_share_add_linebreak), false).commit());
	}

	@SuppressWarnings("unused")
	private void コメント有カスタムフォーマットの変更(String format) {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putString(getContext().getString(R.string.pref_key_share_customformat_with_comment), format)
				.commit());
	}

	private void コメントなしカスタムフォーマットの変更(String format) {
		assertTrue(PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
				.putString(getContext().getString(R.string.pref_key_share_customformat_without_comment), format)
				.commit());
	}

	private Context getContext() {
		return getActivity().getApplicationContext();
	}
}