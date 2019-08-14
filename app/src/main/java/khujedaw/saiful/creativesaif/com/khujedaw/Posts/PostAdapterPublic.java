package khujedaw.saiful.creativesaif.com.khujedaw.Posts;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import khujedaw.saiful.creativesaif.com.khujedaw.R;

public class PostAdapterPublic extends RecyclerView.Adapter<PostAdapterPublic.EachPostView>{


    private Context context;
    private List<Post> postList;

    public PostAdapterPublic(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public EachPostView onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.each_post_card,null);
        return new EachPostView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EachPostView eachPostView, int i) {

        Post post = postList.get(i);

        //String place_photo_path = getString(R.string.base_url);
        Glide.with(context)
                .load(context.getString(R.string.base_url)+context.getString(R.string.post_photo_path)+post.getPlace_photo())
                .placeholder(R.drawable.ic_menu_gallery)
                .into(eachPostView.imageViewPlace_Image);

        eachPostView.textViewTime.setText(post.getTime());
        eachPostView.textViewPostId.setText("#Post ID: 000"+post.getPost_id());
        eachPostView.textViewPlace_name.setText(post.getPlace_name());
        eachPostView.textViewCategory.setText(post.getCategory());
        eachPostView.textViewFee.setText("BDT: "+post.getFee()+"/Monthly");
        eachPostView.textViewAddress.setText(post.getPlace_address());

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class EachPostView extends RecyclerView.ViewHolder{
        List<Post> postListViewHOlder;


        private TextView textViewTime, textViewPostId, textViewPlace_name, textViewCategory,
        textViewFee, textViewAddress;
        ImageView imageViewPlace_Image;

        private EachPostView(@NonNull View itemView) {
            super(itemView);
            postListViewHOlder = postList;

            textViewTime = itemView.findViewById(R.id.tvTime);
            textViewPostId = itemView.findViewById(R.id.tvPostId);
            textViewPlace_name = itemView.findViewById(R.id.tvPlaceName);
            textViewCategory = itemView.findViewById(R.id.tvCategory);
            textViewFee = itemView.findViewById(R.id.tvFee);
            textViewAddress = itemView.findViewById(R.id.tvAddre);

            imageViewPlace_Image = itemView.findViewById(R.id.imageViewImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    Post post = postListViewHOlder.get(position);
                    Intent i = new Intent(context, PostDetailsPublic.class);
                    i.putExtra("postDetails",post);
                    context.startActivity(i);
                }
            });
        }
    }

}
