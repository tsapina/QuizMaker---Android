package hr.tosapinaetfos.quizmaker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.models.QuizModel;

/**
 * Created by tosap on 04.05.2016..
 */
public class QuizAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<QuizModel> quizList;

    //Quiz adapter constructor
    public QuizAdapter(Context ctx, ArrayList<QuizModel> quizL) {
        super();
        this.ctx = ctx;
        this.quizList = quizL;
        Log.d("list", "ctx");
    }
    @Override
    public int getCount() { return this.quizList.size(); }

    @Override
    public Object getItem(int position) {
        return quizList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            //set layout for list
            Log.d("list", "cview");
            convertView = View.inflate(ctx, R.layout.list_of_quizzes_item, null);
        }

        //Fetch objects tvQuizName and tvQuizDateOfCreation
        TextView tvQuizName = (TextView) convertView.findViewById(R.id.tvQuizName);
        TextView tvQuizDateOfCreation = (TextView) convertView.findViewById(R.id.tvQuizDateOfCreation);

        //Get Object from quizList
        QuizModel current = (QuizModel) quizList.get(position);

        Log.d("test", current.getQuizName());
        //Set text to TV objects
        tvQuizName.setText(current.getQuizName());
        tvQuizDateOfCreation.setText(current.getQuizDateOfCreation());

        return convertView;
    }
}
