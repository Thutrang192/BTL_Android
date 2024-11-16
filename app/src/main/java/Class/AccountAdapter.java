package Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class AccountAdapter extends ArrayAdapter<Account> {
    private Context context;
    private int layout;
    ArrayList<Account> lstAccount;


    public AccountAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Account> objects) {
        super(context, resource, objects);
        this.context = context;
        this.lstAccount = objects;
        this.layout = resource;
    }

    @Override
    public int getCount() {
        return lstAccount.size();
    }

    @Nullable
    @Override
    public Account getItem(int position) {
        return lstAccount.get(position);
    }

}
