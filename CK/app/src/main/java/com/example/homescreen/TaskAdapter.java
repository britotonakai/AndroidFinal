package com.example.homescreen;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> taskList;
    private ChecklistActivity activity;

    public TaskAdapter(ChecklistActivity activity){
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position){
        Task item = taskList.get(position);
        holder.taskCheckBox.setText(item.getTask());
        holder.taskCheckBox.setChecked(toBoolean(item.getStatus()));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Hiển thị hộp thoại xác nhận xóa
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Are you sure you want to delete this task?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xóa task khỏi database
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Task").child(item.getId());
                        ref.removeValue();

                        // Xóa task khỏi danh sách task và cập nhật RecyclerView
                        int position = holder.getAdapterPosition();
                        activity.adapter.removeTask(position);
                    }
                });

                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

    }

    public int getItemCount(){
        if (taskList == null) {
            return 0;
        }
        return taskList.size();
    }
    private boolean toBoolean(int n){
        return n != 0;
    }

    public void setTasks(List<Task> taskList){
        this.taskList = taskList;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox taskCheckBox;

        ViewHolder(View view){
            super(view);
            taskCheckBox = view.findViewById(R.id.taskCheckBox);
        }
    }

    public void removeTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }
}
