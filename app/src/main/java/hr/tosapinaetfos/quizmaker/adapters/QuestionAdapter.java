package hr.tosapinaetfos.quizmaker.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.models.QuestionModel;
import hr.tosapinaetfos.quizmaker.models.QuizModel;

/**
 * Created by tosap on 05.05.2016..
 */
public class QuestionAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<QuestionModel> questionList;

    //Question adapter constructor
    public QuestionAdapter(Context ctx, ArrayList<QuestionModel> quizL) {
        super();
        this.ctx = ctx;
        this.questionList = quizL;
    }

    @Override
    public int getCount() {
        return this.questionList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.questionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            //set layout for list
            convertView = View.inflate(ctx, R.layout.list_of_questions_item, null);
        }

        //Fetch objects tvQuestionName and tvQuestionType
        TextView tvQuestionName = (TextView) convertView.findViewById(R.id.tvQuestionName);
        TextView tvQuestionType = (TextView) convertView.findViewById(R.id.tvQuestionType);

        //Get Object from quizList
        QuestionModel current = (QuestionModel) questionList.get(position);

        //Set text to TV objects
        tvQuestionName.setText(current.getQuestionName());
        tvQuestionType.setText(current.getQuestionType());

        return convertView;
    }
}
