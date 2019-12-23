package com.example.mohamednadeem.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mohamed Nadeem on 20/08/2016.
 */
public class ReviewAdapter extends BaseAdapter {
    String [] result;
    Context context;
    String [] result2;
  //  int  imageId;
    private static LayoutInflater inflater=null;
    public ReviewAdapter(Context mcontext, String[] MovieReviewAuthor,String[] MovieReviewContent) {
        // TODO Auto-generated constructor stub
        result=MovieReviewAuthor;
        context=mcontext;
        result2=MovieReviewContent;
       // imageId=R.drawable.movie_trailer;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        TextView tv1;
       // ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.review, null);
        holder.tv=(TextView) rowView.findViewById(R.id.author);
        holder.tv1=(TextView) rowView.findViewById(R.id.content);
        holder.tv.setText(result[position]);
        holder.tv1.setText(result2[position]);
      //  holder.tv1.setMaxLines(200);

      //  holder.tv1.getTextSize();
      //  holder.img.setImageResource(imageId);
        return rowView;
    }

}
