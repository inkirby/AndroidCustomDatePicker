package com.inkirby.datepicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.inkirby.datepicker.DatePickDialog.CalendarType;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements DatePickDialog.NoticeDialogListener {
	Date currentDate = null;
	SimpleDateFormat sdf;
	TextView dateText;
	TextView calendarType;
	TextView localeType;
	DatePickDialog.CalendarType type;
	ArrayList<Locale> localeList;
	Locale locale;
	DialogFragment dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		localeList = new ArrayList<Locale>();
		localeList.add(Locale.getDefault());
		localeList.add(new Locale("th_TH"));
		locale = localeList.get(0);

		currentDate = new Date();
		sdf = new SimpleDateFormat("dd MMM yyyy");

		dateText = (TextView) findViewById(R.id.dateText);
		dateText.setText("Pick a date");
		dateText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});
		
		type = CalendarType.GREGORIAN;
		calendarType = (TextView) findViewById(R.id.calendarType);
		calendarType.setText(type.toString());
		calendarType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (type == CalendarType.GREGORIAN) {
					type = CalendarType.BUDDHIST;
				} else {
					type = CalendarType.GREGORIAN;
				}
				calendarType.setText(type.toString());
			}
		});
		
		localeType = (TextView) findViewById(R.id.localeType);
		localeType.setText(locale.toString());
		localeType.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				for (int i = 0; i < localeList.size(); i++) {
					if (localeList.get(i).toString().equalsIgnoreCase(localeType.getText().toString())) {
						if (i+1 < localeList.size()) {
							locale = localeList.get(i+1);
						} else {
							locale = localeList.get(0);
						}
						localeType.setText(locale.toString());
						break;
					}
				}
			}
		});
	}
	
	public void showDate() {
		dateText.setText(sdf.format(currentDate));
	}

	public void showDatePickerDialog() {
		dialog = new DatePickDialog(currentDate, type, locale);
		dialog.show(getSupportFragmentManager(), "DatePickDialogFragment");
	}

	public void hideDatePickerDialog() {
		dialog.dismiss();
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, Date date) {
		currentDate = date;
		showDate();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		dateText.setText("Pick a date");
	}
}
