package com.hybris.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hybris.mobile.activity.MainCategoriesActivity;


public class SplashScreenActivity extends Activity
{

	private static int SPLASH_SCREEN_TIMEOUT_IN_SECONDS = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);

		new Handler().postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				Intent intentMain = new Intent(SplashScreenActivity.this, MainCategoriesActivity.class);
				startActivity(intentMain);

				finish();
			}
		}, SPLASH_SCREEN_TIMEOUT_IN_SECONDS * 1000);

	}

}
