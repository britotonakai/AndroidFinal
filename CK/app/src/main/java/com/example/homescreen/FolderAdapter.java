package com.example.homescreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderRecyclerView> {
    Context context;
    NoteAdapter.OnPopupMenuItemClickListener mListener;
    List<Folder> listFolder = new ArrayList<>();

    AllFolderScreen allFolderScreen = new AllFolderScreen();


    public FolderAdapter(Context context){
        this.context = context;
    }

    public FolderAdapter(NoteAdapter.OnPopupMenuItemClickListener listener, List<Folder> folderItem){
        this.mListener = listener;
        this.listFolder = folderItem;
    }

    public void setFolderData(List<Folder> listFolder){
        this.listFolder = listFolder;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FolderRecyclerView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_list, parent, false);
        return new FolderRecyclerView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderRecyclerView holder, int position) {
        Folder folder = listFolder.get(position);
        if(folder == null){
            return;
        }
        holder.folderName.setText(folder.getFolderName());
        holder.folderDay.setText(folder.getFolderDay());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment current = FragmentManager.findFragment(view);
                FragmentTransaction transaction = current.getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.allFolderScreen, new AllNoteScreen_FolderScreen());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listFolder != null){
            return listFolder.size();
        }
        return 0;
    }


    public class FolderRecyclerView extends RecyclerView.ViewHolder{
        TextView folderName, folderDay;

        public FolderRecyclerView(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.textFolderName);
            folderDay = itemView.findViewById(R.id.textFolderDay);
        }


    }
}
