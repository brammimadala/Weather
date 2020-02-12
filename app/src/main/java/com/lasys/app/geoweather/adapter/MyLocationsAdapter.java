
package com.lasys.app.geoweather.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lasys.app.geoweather.R;
import com.lasys.app.geoweather.activity.AddressDetails;
import com.lasys.app.geoweather.activity.MySelectedPlaces;
import com.lasys.app.geoweather.constants.SharePreference;
import com.lasys.app.geoweather.model.locaddress.AddressModel;
import com.lasys.app.geoweather.sqldb.LocationDetailsDB;

import java.util.ArrayList;

public class MyLocationsAdapter extends RecyclerView.Adapter<MyLocationsAdapter.MyHolder> {
    private Context mcontext;
    private ArrayList<AddressModel> addressModelsList;

    private LocationDetailsDB locationDetailsDB;
    private SQLiteDatabase sqLiteDatabase;

    public MyLocationsAdapter(Context context, ArrayList<AddressModel> list) {
        mcontext = context;
        addressModelsList = list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.selected_places_card_style, parent, false);

        MyHolder my = new MyHolder(v);
        return my;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        final AddressModel addressModel = addressModelsList.get(position);

        holder._sp_address.setText(addressModel.getAddressLine());
        holder._sp_location.setText(addressModel.getLocality());
        holder._sp_state.setText(addressModel.getAdmin() + "-" + "" + addressModel.getPostalCode());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext, AddressDetails.class);
                Bundle bundle = new Bundle();
                bundle.putString("address", addressModel.getAddressLine());
                bundle.putString("city", addressModel.getLocality());
                bundle.putString("state", addressModel.getAdmin());
                bundle.putString("country", addressModel.getCountryName());
                bundle.putString("postalcode", addressModel.getPostalCode());
                bundle.putString("latitude", addressModel.getLatitude());
                bundle.putString("longitude", addressModel.getLongitude());
                intent.putExtras(bundle);
                mcontext.startActivity(intent);
            }
        });

        holder._optionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(mcontext, holder._optionsMenu);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                //handle menu1 click
                                //Toast.makeText(mcontext, "Delete ==> "+addressModel.getSid(), Toast.LENGTH_SHORT).show();
                                deleteAddress(addressModel.getSid(), position);
                                break;
                            case R.id.menu_share:
                                //handle menu2 click
                                shareAddress("Address : " + addressModel.getAddressLine() + ".\n\n" + "City : " + addressModel.getLocality() + ",\n" + "State : " + addressModel.getAdmin() + ",\n" + "Country : " + addressModel.getCountryName() + ",\n" + "Postal Code : " + addressModel.getPostalCode() + ".");
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelsList.size();
    }

    public void deleteAddress(int id, int ArrayListtposition) {
        locationDetailsDB = new LocationDetailsDB(mcontext);
        sqLiteDatabase = locationDetailsDB.getWritableDatabase();

        //Toast.makeText(mcontext, "id == > "+id, Toast.LENGTH_SHORT).show();

        /* String qury = "DELETE FROM "+locationDetailsDB.TABLE_NAME1+" WHERE "+locationDetailsDB.TABLE1_COL1+" = "+"'"+id+"'";;
         //String qury = "select * from "+locationDetailsDB.TABLE_NAME1+" WHERE "+locationDetailsDB.TABLE1_COL1+" = "+"'"+id+"'";
         Cursor c = sqLiteDatabase.rawQuery(qury,null);*/


        String where = locationDetailsDB.TABLE1_COL1 + " = ?";
        String where_args[] = {"" + id};

        int res = sqLiteDatabase.delete(locationDetailsDB.TABLE_NAME1, where, where_args);

        if (res != 0) {
            Toast.makeText(mcontext, "Deleted Successfully ", Toast.LENGTH_SHORT).show();
            sqLiteDatabase.close();
            addressModelsList.remove(ArrayListtposition);
            notifyItemRemoved(ArrayListtposition);
            notifyItemRangeChanged(ArrayListtposition, addressModelsList.size());

        } else {
            Toast.makeText(mcontext, "Unable to Deleted ", Toast.LENGTH_SHORT).show();
        }

    }

    public void shareAddress(String address) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (!address.isEmpty()) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, address);
        }
        sendIntent.setType("text/plain");
        mcontext.startActivity(Intent.createChooser(sendIntent, "Weather"));
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView _sp_address, _sp_location, _sp_state, _optionsMenu;

        public MyHolder(View itemView) {
            super(itemView);
            _sp_address = itemView.findViewById(R.id.sp_address);
            _sp_location = itemView.findViewById(R.id.sp_location);
            _sp_state = itemView.findViewById(R.id.sp_state);
            _optionsMenu = itemView.findViewById(R.id.textViewOptions);

        }

    }
}
