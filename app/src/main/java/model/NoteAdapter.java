package model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.LayoutChiTietGhiChu;
import com.example.myapplication.LayoutCongViec;
import com.example.myapplication.LayoutGhiChu;
import com.example.myapplication.R;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

import Interface.iClickItemNote;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private List<Note> lstNote;
    private List<Note> originList;
    private iClickItemNote iClickItemNote;

    public NoteAdapter(List<Note> lst, iClickItemNote listener) {
        this.lstNote = lst; // danh sach loc
        this.originList = lst; // danh sach goc
        this.iClickItemNote = listener;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        Note note = lstNote.get(position);
        String title = note.getTitle();
        String content = note.getContent();
        String date = note.getDate();
        String theme = note.getTheme();
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(title);
        holder.tvContent.setText(content);
        holder.tvDate.setText(date);
        holder.layout_item_note.setCardBackgroundColor(Color.parseColor(theme));

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // lay vi tri hien tai cua item note
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(context)
                            .setTitle("Xóa ghi chú")
                            .setMessage("Bạn có chắc chắn muốn xóa ghi chú này?")
                            .setPositiveButton("Xóa", ((dialog, which) -> {
                                Note deletNote = lstNote.get(currentPosition);
                                String noteID = deletNote.getId();

                                // xoa ghi chu tu firebase
                                iClickItemNote.deleteData(noteID);

                                // xoa ghi chu ra khoi lstNote va originList
                                lstNote.remove(currentPosition);
                                //originList.remove(currentPosition);

                                // cap nhat giao dien
                                notifyItemRemoved(currentPosition);

                            }))
                            .setNegativeButton("Hủy", null)
                            .show();
                }
            }
        });
        
        holder.layout_item_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iClickItemNote.onClickItemNote(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lstNote.size();
    }



    public class ViewHolder extends  RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvContent;
        ImageView ivDelete;
        CardView layout_item_note;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            layout_item_note = itemView.findViewById(R.id.layout_item_note);
        }
    }

    public void filter(String query) {
        if (query.isEmpty()) {
            // hien thi toan bo danh sach neu khong co tu khoa
            lstNote = originList;
        } else {
            List<Note> lstSearch = new ArrayList<>();
            for (Note note : originList) {
                if (note.getTitle().toLowerCase().contains(query.toLowerCase())
                || note.getContent().toLowerCase().contains(query.toLowerCase())) {
                    lstSearch.add(note);
                }
            }
            lstNote = lstSearch;
        }

        // cap nhat giao dien
        notifyDataSetChanged();
    }

    public void updateListSearch(ArrayList<Note> notes) {
        this.originList = notes;
    }


}
