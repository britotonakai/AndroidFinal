package com.example.homescreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderRecyclerView> {
    Context context;
    List<Folder> listFolder = new ArrayList<>();

    AllFolderScreen allFolderScreen = new AllFolderScreen();


    public FolderAdapter(Context context){
        this.context = context;
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
        holder.folderView.setCardBackgroundColor(holder.itemView.getResources().getColor(getRandomColor()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment current = FragmentManager.findFragment(view);
                FragmentTransaction transaction = current.getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.allFolderScreen, new AllFolderScreen_AllNote());
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

    private int getRandomColor() {
        List<Integer> colorHex = new ArrayList<>();
        colorHex.add(R.color.light_green);
        colorHex.add(R.color.neon_green);
        colorHex.add(R.color.light_pink);

        Random random = new Random();
        int color = random.nextInt(colorHex.size());
        return colorHex.get(color);
    }

    public class FolderRecyclerView extends RecyclerView.ViewHolder{
        TextView folderName, folderDay;
        CardView folderView;

        public FolderRecyclerView(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.textFolderName);
            folderDay = itemView.findViewById(R.id.textFolderDay);
            folderView = itemView.findViewById(R.id.FolderView);
        }


    }
}
