package ebag.hd.widget.questions;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ebag.core.bean.QuestionBean;
import ebag.hd.R;
import ebag.hd.widget.questions.base.LineEditText;
import ebag.hd.widget.questions.head.HeadAdapter;
import ebag.hd.widget.questions.util.IQuestionEvent;

/**
 * Created by unicho on 2017/12/29.
 */

public class SentenceView extends LinearLayout implements IQuestionEvent{

    private HeadAdapter headAdapter;
    private List<String> titleList;
    private String studentAnswer;
    private LineEditText lineEditText;


    public SentenceView(Context context) {
        super(context);
        init(context);
    }

    public SentenceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SentenceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        //垂直方向
        setOrientation(VERTICAL);

        //标题
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RecyclerView headRecycler = new RecyclerView(context);
        headRecycler.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager headManager = new LinearLayoutManager(context);
        headRecycler.setLayoutManager(headManager);
        headAdapter = new HeadAdapter();
        headRecycler.setAdapter(headAdapter);
        addView(headRecycler,layoutParams);


        //这个只能用  这种方式 实例化 用 new的方式，里面设置的很多方法会没有效果
        lineEditText = new LineEditText(context);
        lineEditText.setId(R.id.multi_under_line);
        lineEditText.setLineSpacing(getResources().getDimensionPixelSize(R.dimen.x10),1);
        lineEditText.setTextColor(getResources().getColor(R.color.question_normal));
        lineEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.question_content));
        lineEditText.setBackground(null);
        lineEditText.setMinLines(1);

        addView(lineEditText,layoutParams);
    }

    @Override
    public void setData(QuestionBean questionBean) {
        titleList = new ArrayList<>();
        Collections.addAll(titleList,questionBean.getQuestionHead().split("#R#"));
        titleList.add(questionBean.getQuestionContent());
        studentAnswer = questionBean.getAnswer();
    }

    @Override
    public void show(boolean active) {
        lineEditText.setEnabled(active);
        headAdapter.setDatas(titleList);
    }

    @Override
    public void questionActive(boolean active) {
        lineEditText.setEnabled(active);
    }

    @Override
    public boolean isQuestionActive() {
        return lineEditText.isEnabled();
    }

    @Override
    public void showResult() {
        show(false);
        lineEditText.setText(studentAnswer);
    }

    @Override
    public String getAnswer() {
        return lineEditText.getText().toString();
    }

    @Override
    public void reset() {

    }
}
