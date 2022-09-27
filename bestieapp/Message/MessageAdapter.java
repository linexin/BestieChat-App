package com.linex.bestieapp.Message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.linex.bestieapp.Chat.Chat;
import com.linex.bestieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private List<MessagesList> messagesLists;
    private final Context context;



    public MessageAdapter(List<MessagesList> messageLists, Context context) {
        this.messagesLists = messageLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.message_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {

        MessagesList list2 = messagesLists.get(position);

       /* if(!list2.getProfilePic().isEmpty()){
            Picasso.get().load(list2.getProfilePic()).into(holder.profilePic);
        }*/

        if(list2.getProfilePic().isEmpty()){
            holder.profilePic.setImageResource(R.drawable.person);
        }else{
            Picasso.get()
                    .load(list2.getProfilePic())
                    .into(holder.profilePic);
        }

            holder.name.setText(list2.getName());
        holder.lastMessage.setText(list2.getLastMessage());

        if(list2.getUnseenMessage() == 0 ){
            holder.unseenMessage.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        }else{
            holder.unseenMessage.setVisibility(View.VISIBLE);
            holder.unseenMessage.setText(list2.getUnseenMessage()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.theme_bg_color_1));
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent  intent = new Intent(context, Chat.class);
                intent.putExtra("mobile", list2.getMobile());
              intent.putExtra("name", list2.getName());
              intent.putExtra("profile_pic", list2.getProfilePic());
              intent.putExtra("chat_key", list2.getChatKey());

              context.startActivity(intent);
            }
        });

    }

    public void updateData(List<MessagesList> messagesLists){
        this.messagesLists = messagesLists;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static  class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        private TextView name;
        private TextView lastMessage;
        private TextView unseenMessage;
        private LinearLayout rootLayout;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePic = itemView.findViewById(R.id.profilePic);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            unseenMessage = itemView.findViewById(R.id.unseenMessage);
            rootLayout = itemView.findViewById(R.id.rootLayout);


        }


    }
}
