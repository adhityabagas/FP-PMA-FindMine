package com.planhub.findmine.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.planhub.findmine.DetailActivity;
import com.planhub.findmine.Model.Post;
import com.planhub.findmine.Model.User;
import com.planhub.findmine.R;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public List<Post> postList;
    public List<User> userList;

    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    public PostAdapter(List<Post> postList, List<User> userList) {

        this.postList = postList;
        this.userList = userList;

    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);

        final String postId = postList.get(position).PostId;
        String currentUserId = mAuth.getCurrentUser().getUid();

        String titleData = postList.get(position).getTitle();
        holder.setTitleText(titleData);

        String img_url = postList.get(position).getImg_url();
        holder.setImgPost(img_url);

        /*String descData = postList.get(position).getDesc();
        holder.setDescText(descData);*/

        /*String img_profile = postList.get(position).getImg_profile();
        holder.setImgProfile(img_profile);*/

        // menampilkan user sesuai user id di database Users
        final String id_user = postList.get(position).getId_user();

        if (id_user.equals(currentUserId)) {
            holder.btnDeletePost.setEnabled(true);
            holder.btnDeletePost.setVisibility(View.VISIBLE);
        }

        /*String userNamePost = userList.get(position).getNameUser();
        String userImagePost = userList.get(position).getImgDetailProfile();
        holder.setUserData(userNamePost, userImagePost);*/
        firebaseFirestore.collection("Users").document(id_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("img_profile");

                    holder.setUserData(userName, userImage);

                } else {

                    //Firebase Exception

                }

            }
        });

        long millisecond = postList.get(position).getTimestamp().getTime();
        String dateString = DateFormat.format("MMM d, hh:mm a", new Date(millisecond)).toString();
        holder.setTime(dateString);

        holder.btnDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Posts").document(postId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        postList.remove(position);
                        userList.remove(position);

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView mTitle, mDesc, mDate;
        private Button btnDeletePost;
        private ImageView mImgProfile;
        private PorterShapeImageView mImgPost;

        private TextView userName;
        private CircleImageView userImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            btnDeletePost = itemView.findViewById(R.id.btnDelete);

            // menampilkan detail barang
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Intent intent = new Intent(context, DetailActivity.class);
                    final int position = getAdapterPosition();

                    intent.putExtra("title", postList.get(position).getTitle());
                    intent.putExtra("img_url", postList.get(position).getImg_url());
                    intent.putExtra("desc", postList.get(position).getDesc());
                    /*intent.putExtra("name", postList.get(position).getName());
                    intent.putExtra("img_profile", postList.get(position).getImg_profile());*/

                    long millisecond = postList.get(position).getTimestamp().getTime();
                    intent.putExtra("timestamp", millisecond);

                    context.startActivity(intent);

                    /*String id_user = postList.get(position).getId_user();
                    firebaseFirestore.collection("Users").document(id_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {

                                String userNamePost = task.getResult().getString("name");
                                String userImagePost = task.getResult().getString("img_profile");

                                setUserData(userNamePost, userImagePost);

                                intent.putExtra("name", postList.get(position).getName());
                                intent.putExtra("img_profile", postList.get(position).getImg_profile());

                                context.startActivity(intent);

                            } else  {

                            }

                        }
                    });*/

                }
            });

        }

        public void setTitleText(String titleText) {

            mTitle = mView.findViewById(R.id.tvTitlePost);
            mTitle.setText(titleText);

        }

        public void setImgPost(String imgDownloadURI) {

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.post_placeholder);

            mImgPost = mView.findViewById(R.id.imgPost);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imgDownloadURI).into(mImgPost);

        }

        public void setTime(String date) {

            mDate = mView.findViewById(R.id.tvDatePost);
            mDate.setText(date);

        }

        public void setUserData(String name, String img_profile) {

            userName = mView.findViewById(R.id.tvNameUserPost);
            userImg = mView.findViewById(R.id.imgUserPost);

            userName.setText(name);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(img_profile).into(userImg);

        }
    }
}

