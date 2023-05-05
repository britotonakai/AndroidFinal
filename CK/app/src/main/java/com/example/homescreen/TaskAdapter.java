package com.example.homescreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

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
}
