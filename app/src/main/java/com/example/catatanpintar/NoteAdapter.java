package com.example.catatanpintar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Meng-extend RecyclerView.Adapter agar class ini resmi menjadi "Tukang Pos"
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;

    // Konstruktor untuk menerima data list catatan dari MainActivity nanti
    public NoteAdapter(List<Note> noteList) {
        this.noteList = noteList;
    }

    // 1. Memanggil cetakan kartu (item_note.xml)
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    // 2. Menempelkan data dari database ke masing-masing elemen di dalam kartu
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = noteList.get(position);

        holder.tvTitle.setText(currentNote.getTitle());
        holder.tvContent.setText(currentNote.getContent());
        holder.tvTime.setText(currentNote.getTime());

        // MEMBERIKAN AKSI KLIK PADA KARTU
        holder.itemView.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                // Berpindah ke AddNoteActivity sambil membawa "Oleh-oleh" berupa data catatan
                android.content.Intent intent = new android.content.Intent(v.getContext(), AddNoteActivity.class);
                intent.putExtra("NOTE_ID", currentNote.getId());
                intent.putExtra("NOTE_TITLE", currentNote.getTitle());
                intent.putExtra("NOTE_CONTENT", currentNote.getContent());

                v.getContext().startActivity(intent);
            }
        });
    }

    // 3. Menghitung ada berapa total catatan yang harus ditampilkan
    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // Class bantuan untuk menghubungkan ID yang ada di item_note.xml
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Menghubungkan variabel dengan ID yang ada di desain item_note.xml
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvContent = itemView.findViewById(R.id.tv_item_content);
            tvTime = itemView.findViewById(R.id.tv_item_time);
        }
    }
    // Method baru untuk memperbarui daftar saat melakukan pencarian
    public void setFilteredList(List<Note> filteredList) {
        this.noteList = filteredList;
        notifyDataSetChanged(); // Perintah untuk me-refresh layar
    }
}