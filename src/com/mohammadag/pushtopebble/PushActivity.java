package com.mohammadag.pushtopebble;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;

public class PushActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
			String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
			if (sharedText != null) {
				sendPebble(R.string.app_name, sharedText);
			}
		} else if ("com.mohammadag.pushtopebble.SEND".equals(getIntent().getAction())) {
			String title = null;
			if (getIntent().hasExtra("title"))
				title = getIntent().getStringExtra("title");

			String text = getIntent().getStringExtra("body");

			String defaultTitle = getString(R.string.app_name);
			sendPebble((title == null || TextUtils.isEmpty(title))
					? defaultTitle : title, text);
		}

		finish();
	}

	private void showPebbleNotConnected() {
		Toast.makeText(this, R.string.pebble_not_connected_message,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void sendPebble(int resId, String body) {
		sendPebble(getString(resId), body);
	}

	public void sendPebble(String title, String body) {
		if (!PebbleKit.isWatchConnected(this)) {
			showPebbleNotConnected();
			return;
		}

		final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

		final Map<String, String> data = new HashMap<String, String>();
		data.put("title", title);
		data.put("body", body);

		final JSONObject jsonData = new JSONObject(data);
		final String notificationData = new JSONArray().put(jsonData).toString();
		i.putExtra("messageType", "PEBBLE_ALERT");
		i.putExtra("sender", title);
		i.putExtra("notificationData", notificationData);

		sendBroadcast(i);
	}

}
