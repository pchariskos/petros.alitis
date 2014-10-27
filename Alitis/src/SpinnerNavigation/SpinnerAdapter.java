package SpinnerNavigation;

import java.util.ArrayList;

import petros.alitis.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * The adapter for the spinner drop-down navigation list. It provides the list
 * of the selectable items for the drop-down and the layout to use when drawing
 * each item in the list.
 * 
 * @author petroschariskos
 *
 */
public class SpinnerAdapter extends BaseAdapter {
	
	//private ImageView imgIcon;
    private TextView txtTitle, txtSubtitle;
    private ArrayList<SpinnerNavItem> spinnerNavItem;
    private Context context;
 
    public SpinnerAdapter(Context context,
            ArrayList<SpinnerNavItem> spinnerNavItem) {
        this.spinnerNavItem = spinnerNavItem;
        this.context = context;
    }
 
    @Override
    public int getCount() {
        return spinnerNavItem.size();
    }
 
    @Override
    public Object getItem(int index) {
        return spinnerNavItem.get(index);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

    //The View on the action bar
    @SuppressLint("InflateParams") @Override
    public View getView(int position, View convertView, ViewGroup parent) { 
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_navigation_title, null);
        }
         
        //imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtTitle = (TextView) convertView.findViewById(R.id.txtTitle1);
        txtSubtitle = (TextView) convertView.findViewById(R.id.txtTitle2);
         
        //imgIcon.setImageResource(spinnerNavItem.get(position).getIcon());
        //imgIcon.setVisibility(View.GONE);
        txtTitle.setText(R.string.spinner_title);
        txtSubtitle.setText(spinnerNavItem.get(position).getTitle());
        return convertView;
    }
     
    //The drop down view
    @SuppressLint("InflateParams") @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_navigation_list_item_title, null);
        }
         
        //imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        txtSubtitle = (TextView) convertView.findViewById(R.id.txtTitle);
         
        //imgIcon.setImageResource(spinnerNavItem.get(position).getIcon());        
        txtSubtitle.setText(spinnerNavItem.get(position).getTitle());
        return convertView;
    }
}
