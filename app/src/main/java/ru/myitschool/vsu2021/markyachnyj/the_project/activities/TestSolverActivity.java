package ru.myitschool.vsu2021.markyachnyj.the_project.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import ru.myitschool.vsu2021.markyachnyj.the_project.R;
import ru.myitschool.vsu2021.markyachnyj.the_project.fragments.task_fragments.FormulaConstructorTaskFragment;
import ru.myitschool.vsu2021.markyachnyj.the_project.fragments.task_fragments.SimpleAnswerTaskFragment;
import ru.myitschool.vsu2021.markyachnyj.the_project.fragments.task_fragments.TaskFragment;
import ru.myitschool.vsu2021.markyachnyj.the_project.fragments.task_fragments.UnitChoiceTaskFragment;
import ru.myitschool.vsu2021.markyachnyj.the_project.logic.Test;
import ru.myitschool.vsu2021.markyachnyj.the_project.logic.tasks.FormulaConstructorTask;
import ru.myitschool.vsu2021.markyachnyj.the_project.logic.tasks.SimpleAnswerTask;
import ru.myitschool.vsu2021.markyachnyj.the_project.logic.tasks.Task;
import ru.myitschool.vsu2021.markyachnyj.the_project.logic.tasks.UnitChoiceTask;

public class TestSolverActivity extends AppCompatActivity {

    private Test test;
    private HashMap<Button, TaskFragment> fragment_map;
    private ArrayList<Button> task_buttons;
    private int current_task_id=0;
    private boolean is_finished=false;
    private String TABLE_VALUES;

    private LinearLayout Task_Buttons_LL;
    private TextView Topic_Name_TV;
    private ImageButton Next_Task_Btn;
    private ImageButton Previous_Task_Btn;
    private ImageButton Finish_Test_Btn;
    private ImageButton Exit_Test_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_solver);
        Task_Buttons_LL = (LinearLayout) findViewById(R.id.activity_test_solver_task_buttons_ll);
        Topic_Name_TV = (TextView) findViewById(R.id.activity_test_solver_topic_name_tv);
        Next_Task_Btn = (ImageButton) findViewById(R.id.activity_test_solver_next_task_btn);
        Previous_Task_Btn = (ImageButton) findViewById(R.id.activity_test_solver_previous_task_btn);
        Finish_Test_Btn = (ImageButton) findViewById(R.id.activity_test_solver_finish_test_btn);
        Exit_Test_Btn = (ImageButton) findViewById(R.id.activity_test_solver_exit_test_btn);
        task_buttons = new ArrayList<>();
        fragment_map = new HashMap<>();
        Next_Task_Btn.setOnClickListener(Next_Task_Btn_Listener);
        Previous_Task_Btn.setOnClickListener(Previous_Task_Btn_Listener);
        Finish_Test_Btn.setOnClickListener(Finish_Test_Btn_Listener);
        Exit_Test_Btn.setOnClickListener(Exit_Test_Btn_Listener);
        /*TEST*/
        test = (Test) getIntent().getSerializableExtra("test");
        TABLE_VALUES = getIntent().getStringExtra("table_values");
        /*TEST*/
        PlaceExerciseButtons();
        MakeExerciseFragments();
        task_buttons.get(0).callOnClick();
        Topic_Name_TV.setText(test.getTopic().getName());
    }

    @Override
    public void onBackPressed() {
        Exit_Test_Btn.callOnClick();
    }

    private void PlaceExerciseButtons(){
        for(int i=0;i<test.getTasks().size();i++){
            Button btn = new Button(this);
            ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(getResources().getColor(R.color.red_pastel)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0,5,0,5);
            btn.setLayoutParams(params);
            btn.setText(String.valueOf(i+1));
            btn.setTextColor(getResources().getColor(R.color.white));
            btn.setTextSize(getResources().getDimension(R.dimen.activity_test_solver_task_buttons_text_size_small));
            btn.setOnClickListener(Task_Button_Listener);
            Task_Buttons_LL.addView(btn);
            task_buttons.add(btn);
        }
    }

    private void MakeExerciseFragments(){
        for(int i=0;i<test.getTasks().size();i++){
            Task task = test.getTasks().get(i);
            if(task.getClass().equals(SimpleAnswerTask.class)){
                fragment_map.put(task_buttons.get(i),new SimpleAnswerTaskFragment((SimpleAnswerTask)task,TABLE_VALUES));
            } else if(task.getClass().equals(FormulaConstructorTask.class)){
                fragment_map.put(task_buttons.get(i),new FormulaConstructorTaskFragment((FormulaConstructorTask)task));
            } else if(task.getClass().equals(UnitChoiceTask.class)){
                fragment_map.put(task_buttons.get(i),new UnitChoiceTaskFragment((UnitChoiceTask) task));
            }
        }
    }

    public void GiveAnswer(Task task, boolean value){
        test.giveAnswer(task, value);
        InvalidateTaskButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(is_finished){
            finish();
        }
    }

    private Comparator<Button> task_button_cmp = new Comparator<Button>() {
        @Override
        public int compare(Button o1, Button o2) {
            return o1.getText().toString().compareTo(o2.getText().toString());
        }
    };

    private View.OnClickListener Task_Button_Listener  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int new_current_task_id = Collections.binarySearch(task_buttons, (Button) v,task_button_cmp)+1;
            SetActivatedNextPreviousTaskBtn(Previous_Task_Btn, new_current_task_id != 1);
            SetActivatedNextPreviousTaskBtn(Next_Task_Btn, new_current_task_id != task_buttons.size());
            if(new_current_task_id!=current_task_id){
                ((Button) v).setTextSize(getResources().getDimension(R.dimen.activity_test_solver_task_buttons_text_size_large));
                if(current_task_id>0){
                    task_buttons.get(current_task_id-1).setTextSize(getResources().getDimension(R.dimen.activity_test_solver_task_buttons_text_size_small));
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                if(new_current_task_id>current_task_id){
                    ft.setCustomAnimations(R.anim.fragment_task_enter_from_right, R.anim.fragment_task_exit_to_left);
                } else {
                    ft.setCustomAnimations(R.anim.fragment_task_enter_from_left, R.anim.fragment_task_exit_to_right);
                }
                ft.replace(R.id.activity_test_solver_task_fragment_holder_fl, fragment_map.get((Button)v));
                ft.commit();
                current_task_id = new_current_task_id;
            }
        }
    };

    private void SetActivatedNextPreviousTaskBtn(ImageButton btn, boolean activated){
        btn.setClickable(activated);
        if(activated){
            ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(getResources().getColor(R.color.dark_blue)));
        } else {
            ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(getResources().getColor(R.color.light_gray)));
        }
    }

    private View.OnClickListener Next_Task_Btn_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            task_buttons.get(current_task_id).callOnClick();
        }
    };

    private View.OnClickListener Previous_Task_Btn_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            task_buttons.get(current_task_id-2).callOnClick();
        }
    };

    private View.OnClickListener Finish_Test_Btn_Listener = v -> ShowFinishTestAlertDialog();

    private View.OnClickListener Exit_Test_Btn_Listener = v -> ShowExitTestAlertDialog();

    private void InvalidateTaskButtons(){
        for(Map.Entry<Button,TaskFragment> entry:fragment_map.entrySet()){
            Task task = entry.getValue().getTask();
            if(test.isAnswerGiven(task)){
                ViewCompat.setBackgroundTintList(entry.getKey(), ColorStateList.valueOf(getResources().getColor(R.color.light_green_pastel)));
            } else {
                ViewCompat.setBackgroundTintList(entry.getKey(), ColorStateList.valueOf(getResources().getColor(R.color.red_pastel)));
            }
        }
    }

    private void ShowFinishTestAlertDialog(){
        boolean are_answers_chosen = true;
        for(Button button:task_buttons){
            Task task = fragment_map.get(button).getTask();
            if(!test.isAnswerGiven(task)){
                are_answers_chosen = false;
                break;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = (LayoutInflater.from(this)).inflate(R.layout.alert_dialog_finish_test,findViewById(R.id.alert_dialog_finish_test_dialog_holder));
        TextView Message_TV = (TextView) view.findViewById(R.id.alert_dialog_finish_test_message_tv);
        if(are_answers_chosen){
            Message_TV.setText("Завершить тест?");
        } else {
            Message_TV.setText("Завершить тест?\nОтветы даны не на все задания");
        }
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        ((Button)view.findViewById(R.id.alert_dialog_finish_test_negative_btn)).setOnClickListener(v -> dialog.dismiss());
        ((Button)view.findViewById(R.id.alert_dialog_finish_test_positive_btn)).setOnClickListener(v -> {
            is_finished=true;
            Intent i = new Intent(getApplicationContext(),TestResultActivity.class);
            i.putExtra("test",test);
            startActivity(i);
        });
        dialog.show();
    }

    private void ShowExitTestAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = (LayoutInflater.from(this)).inflate(R.layout.alert_dialog_exit_test,(LinearLayout)findViewById(R.id.alert_dialog_exit_test_dialog_holder));
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        ((Button)view.findViewById(R.id.alert_dialog_exit_test_negative_btn)).setOnClickListener(v -> TestSolverActivity.this.finish());
        ((Button)view.findViewById(R.id.alert_dialog_exit_test_positive_btn)).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}