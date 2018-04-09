package com.developerhaoz.ijkplayerdemo.media;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.developerhaoz.ijkplayerdemo.R;

/**
 * @author Haoz
 * @date 2018/4/9.
 */
public class TableLayoutBinder {
    private Context mContext;
    public ViewGroup mTableView;
    public TableLayout mTableLayout;

    public TableLayoutBinder(Context context){
        this(context, R.layout.table_media_info);
    }

    public TableLayoutBinder(Context context, int layoutResourceId){
        mContext = context;
        mTableView = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutResourceId, null);
        mTableLayout = (TableLayout) mTableView.findViewById(R.id.table);
    }

    public TableLayoutBinder(Context context, TableLayout tableLayout){
        mContext = context;
        mTableView = tableLayout;
        mTableLayout = tableLayout;
    }

    public View appendRow1(String name, String value){
        return appendRow(R.layout.table_media_info_row1, name, value);
    }

    public View appendRow1(int nameId, String value){
        return appendRow1(mContext.getString(nameId), value);
    }

    public View appendRow2(String name, String value){
        return appendRow(R.layout.table_media_info_row2, name, value);
    }

    public View appendRow2(int nameId, String value){
        return appendRow2(mContext.getString(nameId), value);
    }

    public View appendSection(String name){
        return appendRow(R.layout.table_media_info_section, name, null);
    }

    public View appendSection(int nameId) {
        return appendSection(mContext.getString(nameId));
    }

    public View appendRow(int layoutId, String name, String value){
        ViewGroup rowView = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutId, mTableLayout, false);
        setNameValueText(rowView, name, value);
        mTableLayout.addView(rowView);
        return rowView;
    }

    public ViewHolder obtainViewHolder(View rowView){
        ViewHolder viewHolder = (ViewHolder) rowView.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
            viewHolder.mTvName = (TextView) rowView.findViewById(R.id.name);
            viewHolder.mTvValue = (TextView) rowView.findViewById(R.id.value);
            rowView.setTag(viewHolder);
        }
        return viewHolder;
    }

    public void setNameValueText(View rowView, String name, String value){
        ViewHolder viewHolder = obtainViewHolder(rowView);
        viewHolder.setName(name);
        viewHolder.setValue(value);
    }

    public void setValueText(View rowView, String value){
        ViewHolder viewHolder = obtainViewHolder(rowView);
        viewHolder.setValue(value);
    }

    public ViewGroup buildLayout() {
        return mTableView;
    }

    public AlertDialog.Builder buildAlertDialogBuilder() {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(mContext);
        dlgBuilder.setView(buildLayout());
        return dlgBuilder;
    }

    private static class ViewHolder {
        public TextView mTvName;
        public TextView mTvValue;

        public void setName(String name){
            if(mTvName != null){
                mTvName.setText(name);
            }
        }

        public void setValue(String value){
            if(mTvValue != null){
                mTvValue.setText(value);
            }
        }
    }
}



























