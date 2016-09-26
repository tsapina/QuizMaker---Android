package hr.tosapinaetfos.quizmaker.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hr.tosapinaetfos.quizmaker.R;
import hr.tosapinaetfos.quizmaker.data.DBHelperQuizCreator;

/**
 * Created by tosap on 14.05.2016..
 */
public class DynamicLayoutHelper {

    Context ctx;
    TableLayout ansLayout;
    String purpose = "";
    String strAnswerName = " ";
    DBHelperQuizCreator dbHelperQuizCreator;
    //https://en.wikipedia.org/wiki/Builder_pattern
    public DynamicLayoutHelper setStrAnswerName(String strAnswerName) {
        this.strAnswerName = strAnswerName;
        return this;
    }

    public DynamicLayoutHelper setCtx(Context ctx) {
        this.ctx = ctx;
        return this;
    }

    public DynamicLayoutHelper setAnsLayout(TableLayout ansLayout) {
        this.ansLayout = ansLayout;
        return this;
    }

    public DynamicLayoutHelper setPurpose(String purpose) {
        this.purpose = purpose;
        return this;
    }

    public DynamicLayoutHelper addAnswer(){
        //Set params for new row
        TableLayout.LayoutParams trParams = new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        trParams.setMargins(0, 0, 0, 7);

        //Creating new row
        TableRow tr = new TableRow(this.ctx);

        //Set params for new edittext
        TableRow.LayoutParams etParams = new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 6);

        //Set params for new deleteimage
        TableRow.LayoutParams imgParams = new TableRow.LayoutParams(0, 50, 1);
        imgParams.gravity = Gravity.CENTER_VERTICAL;

        //Creating new img
        ImageView deleteAns = new ImageView(this.ctx);


        deleteAns.setImageResource(R.drawable.delete);
        deleteAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRow(v);
            }

        });

        //Creating new ET
        EditText cAns = new EditText(this.ctx);
        cAns.setBackgroundColor(Color.parseColor("#a5f687"));
        if(this.purpose.equals("listing")) {
            cAns.setText(this.strAnswerName);
        }else {
            cAns.setHint(ctx.getResources().getString(R.string.standard_question_enter_answer_edit_text));
        }

        cAns.setPadding(5, 5, 5, 5);

        //Add new ET cAnd to new row view
        tr.addView(cAns, etParams);

        //Add new IW deleteAns to new row view
        tr.addView(deleteAns, imgParams);
        //Add new row to answersTableLayout
        ansLayout.addView(tr, trParams);
        return this;
    }

    public Boolean saveQuestion(Context c, EditText etEnterQuestion, JSONArray qData,TableLayout answers){

        String ansName;
        String strAnswerId;
        boolean success = false;
        //Get text from etEnterQuestion
        String strQuestionName = etEnterQuestion.getText().toString();
        String strQuestionID = etEnterQuestion.getTag().toString();

        DBHelperQuizCreator dbHelperQuizCreator = new DBHelperQuizCreator(c);
        //Update question
        dbHelperQuizCreator.updateQuestion(strQuestionID, strQuestionName);

        //delete all answers , so can be new inserted, update is complicated in this case
        for(int i=1; i<qData.length(); i++){
            try {
                JSONObject jsonobject= (JSONObject) qData.get(i);
                strAnswerId = jsonobject.optString("s_answer_id").toString();
                dbHelperQuizCreator.deleteSAnswerById(strAnswerId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        //Loop through answers layout
        for (int i = 0; i < answers.getChildCount(); i++) {

            // get answers child
            Object child = answers.getChildAt(i);

            //check if is child a tablerow
            if (child instanceof TableRow) {

                //create tablerow object
                TableRow row = (TableRow) child;

                //get child at 0 position, always EditText
                Object rchild = row.getChildAt(0);

                //Check is child Edit text
                if (rchild instanceof EditText) {

                    //Get name and tag for each fields
                    EditText et = (EditText) rchild;
                    ansName = et.getText().toString();

                    //Insert name , type, question id, method returns true or false
                    if(dbHelperQuizCreator.insertNewStandardAnswer(ansName, strQuestionID)){
                       success = true;
                    }else{
                        return false;
                    }
                }
            }
        }
        return  success;
    }

    public void deleteRow(View v) {
        // get row that is parent of clicked img
        View row = (View) v.getParent();
        // get rows container
        ViewGroup container = ((ViewGroup) row.getParent());
        // delete the row and invalidate view so it gets redrawn
        container.removeView(row);
        container.invalidate();
    }
}
