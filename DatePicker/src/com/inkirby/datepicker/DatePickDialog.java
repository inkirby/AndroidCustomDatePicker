package com.inkirby.datepicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

public class DatePickDialog extends DialogFragment {
	View v;
	SimpleDateFormat sdf;
	
	Date currentDate = null;
	CalendarType calendarType;
	Locale locale;
	
	int nextMonthDayMax = 31;
	int prevMonthDayMax = 31;
	int yearOffset = 0;
	
	String[] monthName = null;
	
	boolean monthDecreaseFromDateChange = false;
	
	NoticeDialogListener mListener;
	TextView dialogHead;
	NumberPicker datePicker;
	NumberPicker monthPicker;
	NumberPicker yearPicker;

	public interface NoticeDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog, Date date);
		public void onDialogNegativeClick(DialogFragment dialog);
	}
	
	public DatePickDialog() {
		currentDate = new Date();
	}
	
	public DatePickDialog(Date currentDate) {
		this.currentDate = currentDate;
		this.calendarType = CalendarType.GREGORIAN;
		this.locale = Locale.getDefault();
		
	}
	
	public DatePickDialog(Date currentDate, CalendarType calendarType) {
		this.currentDate = currentDate;
		this.calendarType = calendarType;
		this.locale = Locale.getDefault();
	}
	
	public DatePickDialog(Date currentDate, CalendarType calendarType, Locale locale) {
		this.currentDate = currentDate;
		this.calendarType = calendarType;
		this.locale = locale;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			mListener = (NoticeDialogListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()+" must implement NoticeDialogListener");
		}
	}
	
	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		v = inflater.inflate(R.layout.dialog_layout, null);

		sdf = new SimpleDateFormat("dd MMM yyyy", locale);
		if (calendarType == CalendarType.BUDDHIST) yearOffset = 543;
		
		builder.setView(v);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogPositiveClick(DatePickDialog.this, getDate());
				}
			})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mListener.onDialogNegativeClick(DatePickDialog.this);
				}
			});
		
		dialogHead = (TextView) v.findViewById(R.id.dialogHead);

		datePicker = (NumberPicker) v.findViewById(R.id.firstPicker);
		monthPicker = (NumberPicker) v.findViewById(R.id.secondPicker);
		yearPicker = (NumberPicker) v.findViewById(R.id.thirdPicker);
		
		Calendar today = Calendar.getInstance();
		today.setTime(currentDate);
		
		ArrayList<String> as = new ArrayList<String>();
		SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", locale);
		Calendar c = Calendar.getInstance();
		for (int i = 0; i < 12; i++) {
			c.set(Calendar.MONTH, i);
			as.add(monthFormat.format(c.getTime()));
		}
		monthName = as.toArray(new String[12]);
		
		datePicker.setMinValue(1);
		datePicker.setMaxValue(31);
		datePicker.setValue(today.get(Calendar.DAY_OF_MONTH));
		datePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		datePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				dateChanged(oldVal, newVal);
			}
		});
		
		monthPicker.setMinValue(0);
		monthPicker.setMaxValue(11);
		monthPicker.setValue(today.get(Calendar.MONTH));
		monthPicker.setDisplayedValues(monthName);
		monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				monthChanged(oldVal, newVal);
			}
		});
		
		yearPicker.setMinValue(1900+yearOffset);
		yearPicker.setMaxValue(2050+yearOffset);
		yearPicker.setValue(today.get(Calendar.YEAR)+yearOffset);
		yearPicker.setWrapSelectorWheel(false);
		yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				yearChanged(oldVal, newVal);
			}
		});
		displayHead();
		return builder.create();
	}
	
	public Date getDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, datePicker.getValue());
		calendar.set(Calendar.MONTH, monthPicker.getValue());
		calendar.set(Calendar.YEAR, yearPicker.getValue()-yearOffset);
		currentDate.setTime(calendar.getTimeInMillis());
		return currentDate;
	}
	
	public void displayHead() {
		Date date = getDate();
		String formattedDate = sdf.format(date);
		if (yearOffset != 0) {
			SimpleDateFormat y = new SimpleDateFormat("yyyy");
			String[] splittedDate = formattedDate.split(y.format(date));
			int yearForDisplay = Integer.parseInt(y.format(date))+yearOffset;
			if (formattedDate.indexOf(y.format(date)) >= (formattedDate.length()-y.format(date).length())) {
				formattedDate = splittedDate[0]+yearForDisplay;
			} else if (formattedDate.indexOf(y.format(date)) < 1) {
				formattedDate = yearForDisplay+splittedDate[0];
			} else {
				formattedDate = splittedDate[0]+yearForDisplay+splittedDate[1];
			}
		}
		dialogHead.setText(formattedDate);
	}
	
	public void dateChanged(int oldDate, int newDate) {
		
		int M = monthPicker.getValue();
		if (oldDate == datePicker.getMaxValue() && newDate == 1) {
			if (M < monthPicker.getMaxValue()) {
				monthPicker.setValue(M+1);
				monthChanged(M, M+1);
			} else {
				monthPicker.setValue(0);
				monthChanged(11, 0);
			}
		}
		if (oldDate == 1 && newDate == datePicker.getMaxValue()) {
			monthDecreaseFromDateChange = true;
			if (M > 0) {
				monthPicker.setValue(M-1);
				monthChanged(M, M-1);
			} else {
				monthPicker.setValue(11);
				monthChanged(0, 11);
			}
		}
		displayHead();
	}
	
	public void monthChanged(int oldMonth, int newMonth) {
		if (oldMonth == 11 && newMonth == 0) {
			if (yearPicker.getValue() < yearPicker.getMaxValue())
				yearPicker.setValue(yearPicker.getValue()+1);
		}
		if (oldMonth == 0 && newMonth == 11) {
			if (yearPicker.getValue() > yearPicker.getMinValue())
				yearPicker.setValue(yearPicker.getValue()-1);
		}
		setNewPickerMaxes();
		displayHead();
	}
	
	public void yearChanged(int oldYear, int newYear) {
		setNewPickerMaxes();
		displayHead();
	}
	
	public void setNewPickerMaxes() {
		int d = datePicker.getValue();
		int M = monthPicker.getValue();
		int y = yearPicker.getValue();
		
		int newDayMax = getMonthDayMax(M);
		if (d>newDayMax) {
			datePicker.setValue(newDayMax);
		}
		datePicker.setMaxValue(newDayMax);
		if (monthDecreaseFromDateChange) {
			monthDecreaseFromDateChange = false;
			datePicker.setValue(newDayMax);
		}
		
		if (y == yearPicker.getMinValue() && M < 4) {
			monthPicker.setWrapSelectorWheel(false);
			monthPicker.setMaxValue(monthPicker.getMaxValue());
		} else {
			monthPicker.setWrapSelectorWheel(true);
			monthPicker.setMaxValue(monthPicker.getMaxValue());
		}
		
		if (y == yearPicker.getMinValue() && M == monthPicker.getMinValue()) {
			datePicker.setWrapSelectorWheel(false);
			datePicker.setMaxValue(datePicker.getMaxValue());
		} else {
			datePicker.setWrapSelectorWheel(true);
			datePicker.setMaxValue(datePicker.getMaxValue());
		}

		
		if (y == yearPicker.getMaxValue() && M > 8 && d < 5) {
			monthPicker.setWrapSelectorWheel(false);
			monthPicker.setMaxValue(monthPicker.getMaxValue());
		} else {
			monthPicker.setWrapSelectorWheel(true);
			monthPicker.setMaxValue(monthPicker.getMaxValue());
		}
		
		if (y == yearPicker.getMaxValue() && M == monthPicker.getMaxValue() && d > (datePicker.getMaxValue()-5)) {
			datePicker.setWrapSelectorWheel(false);
			datePicker.setMaxValue(datePicker.getMaxValue());
		} else {
			datePicker.setWrapSelectorWheel(true);
			datePicker.setMaxValue(datePicker.getMaxValue());
		}
	}
	
	public int getMonthDayMax(int month) {
		switch (month) {
		case 0: case 2: case 4: case 6:
		case 7: case 9: case 11:
			return 31;
		case 3: case 5: case 8: case 10:
			return 30;
		case 1:
			GregorianCalendar gc = new GregorianCalendar();
			return gc.isLeapYear(yearPicker.getValue()-yearOffset)?29:28;
		default:
			return 31;
		}
	}
	
	public enum CalendarType {
		GREGORIAN, BUDDHIST
	}
}
