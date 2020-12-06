package com.example.infinispace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ScorerAdapter extends ArrayAdapter<Scorer> {
    private Context mContext;
    private List<Scorer> highscores=new ArrayList<>();

    public ScorerAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<Scorer> list){
        super(context,0,list);
        mContext =context;
        highscores=list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View listItem = convertView;
        if(listItem==null)
            listItem= LayoutInflater.from(mContext).inflate(R.layout.custon_row,parent,false);


        Scorer currentScorer = highscores.get(position);

        TextView scoreRank = listItem.findViewById(R.id.positionscoreTV);
        scoreRank.setText(Integer.toString(currentScorer.getRank()));

        TextView scoreScore = listItem.findViewById(R.id.highscoreTV);
        scoreScore.setText(currentScorer.getScore());



        return listItem;
        }
    }

