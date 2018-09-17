package se.opiflex.opiflexindustrialmonitoring;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.view.View.inflate;
import static java.lang.String.valueOf;

public class lvAdapter extends BaseAdapter {

    private Context mContext;
    private List<Error> mErrorList;

    public lvAdapter(Context mContext, List<Error> mErrorList) {
        this.mContext = mContext;
        this.mErrorList = mErrorList;
    }

    @Override
    public int getCount() {
        return mErrorList.size ();
    }

    @Override
    public Object getItem(int position) {
        return mErrorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflate(mContext, R.layout.errorlist_layout , null);

        TextView RobotId = (TextView)view.findViewById(R.id.RobotID);
        TextView ErrorMessage = (TextView)view.findViewById(R.id.ErrorMessage);
        TextView TimeStamp = (TextView)view.findViewById(R.id.TimeStamp);

        // set text for textview
        RobotId.setText(valueOf(mErrorList.get(position).getRobotId()));
        ErrorMessage.setText(mErrorList.get(position).getErrorMessage());

        DateFormat outputFormat = SimpleDateFormat.getDateTimeInstance();
        TimeStamp.setText(outputFormat.format(new Date(mErrorList.get(position).getTime()*1000)));
        //save
        view.setTag(mErrorList.get(position).getId());
        return view;


    }
}
