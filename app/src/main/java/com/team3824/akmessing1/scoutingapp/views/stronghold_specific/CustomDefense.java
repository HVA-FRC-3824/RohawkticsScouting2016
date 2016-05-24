package com.team3824.akmessing1.scoutingapp.views.stronghold_specific;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.team3824.akmessing1.scoutingapp.utilities.Constants;
import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutMap;
import com.team3824.akmessing1.scoutingapp.utilities.ScoutValue;
import com.team3824.akmessing1.scoutingapp.views.CustomScoutView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;

/**
 * Custom input used to count the number of times that a team crosses a defense and how quickly
 *
 * @author Andrew Messing
 * @version
 */
public class CustomDefense extends CustomScoutView implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{
    private final String TAG = "CustomDefenseInput";

    private RadioGroup crossNum;
    private RadioGroup timeRadios;
    private Button submit;
    private Button delete;
    private JSONArray jsonArray;
    private TextView canCrossText;

    private String key;

    public CustomDefense(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_defense, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        key = typedArray.getString(R.styleable.CustomScoutView_key);
        ((TextView)findViewById(R.id.label)).setText(typedArray.getString(R.styleable.CustomScoutView_label));

        jsonArray = new JSONArray();

        canCrossText = (TextView)findViewById(R.id.can_cross_text);

        timeRadios = (RadioGroup)findViewById(R.id.radiobuttons);

        for(int i = 0; i < Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES.length;i++)
        {
            RadioButton radioButton = new RadioButton(context, attrs);
            radioButton.setText(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES[i]);
            radioButton.setId(i);
            timeRadios.addView(radioButton);
        }

        crossNum = (RadioGroup)findViewById(R.id.cross_num);

        RadioButton radioButton = new RadioButton(context, attrs);
        radioButton.setId(0);
        crossNum.addView(radioButton);
        crossNum.check(0);

        crossNum.setOnCheckedChangeListener(this);

        submit = (Button)findViewById(R.id.submit_button);
        submit.setOnClickListener(this);

        delete = (Button)findViewById(R.id.delete_button);
        delete.setOnClickListener(this);

    }

    @Override
    public String writeToMap(ScoutMap map)
    {
        String saveValue = jsonArray.toString();
        map.put(key, saveValue);
        return "";
    }

    // Custom restore
    @Override
    public void restoreFromMap(ScoutMap map) {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            String restoreValue = sv.getString();
            try {
                jsonArray = new JSONArray(restoreValue);
                if(jsonArray.length() > 6)
                {
                    timeRadios.setVisibility(GONE);
                    crossNum.setVisibility(GONE);
                    submit.setVisibility(GONE);
                    delete.setVisibility(GONE);
                    canCrossText.setVisibility(VISIBLE);
                }
                else if(jsonArray.length() > 0)
                {
                    timeRadios.check(Arrays.asList(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES).indexOf(jsonArray.getString(0)));
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        RadioButton radioButton = new RadioButton(getContext());
                        radioButton.setId(i+1);
                        crossNum.addView(radioButton);
                    }
                    crossNum.check(0);
                    delete.setVisibility(VISIBLE);
                    submit.setText("Update");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int whichCross = crossNum.getCheckedRadioButtonId();
        switch (v.getId())
        {
            case R.id.submit_button:
                if (jsonArray.length() == whichCross) {
                    if(timeRadios.getCheckedRadioButtonId() > -1) {
                        jsonArray.put(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES[timeRadios.getCheckedRadioButtonId()]);
                        RadioButton radioButton1 = new RadioButton(getContext());
                        radioButton1.setId(whichCross + 1);
                        crossNum.addView(radioButton1);
                        crossNum.check(whichCross + 1);
                        timeRadios.clearCheck();

                        if(whichCross >= 5)
                        {
                            timeRadios.setVisibility(GONE);
                            crossNum.setVisibility(GONE);
                            submit.setVisibility(GONE);
                            delete.setVisibility(GONE);
                            canCrossText.setVisibility(VISIBLE);
                        }
                    }
                } else {
                    if(timeRadios.getCheckedRadioButtonId() > -1) {
                        try {
                            jsonArray.put(whichCross, Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES[timeRadios.getCheckedRadioButtonId()]);
                            crossNum.check(whichCross + 1);
                            String string1 = jsonArray.getString(whichCross + 1);
                            timeRadios.check(Arrays.asList(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES).indexOf(string1));
                        } catch (JSONException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
                break;
            case R.id.delete_button:
                int lastRadio = jsonArray.length();
                jsonArray.remove(whichCross);
                crossNum.removeViewAt(lastRadio);
                crossNum.check(whichCross);
                if(whichCross < jsonArray.length()) {
                    try {
                        String string1 = jsonArray.getString(whichCross);
                        timeRadios.check(Arrays.asList(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES).indexOf(string1));
                    } catch (JSONException e) {
                        Log.e(TAG, "Error: ", e);
                    }
                }
                else
                {
                    timeRadios.clearCheck();
                    submit.setText("Submit");
                    delete.setVisibility(GONE);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == jsonArray.length()) {
            timeRadios.clearCheck();
            delete.setVisibility(GONE);
            submit.setText("Submit");
        } else {
            try {
                String time = jsonArray.getString(checkedId);
                timeRadios.check(Arrays.asList(Constants.Teleop_Inputs.TELEOP_DEFENSE_TIMES).indexOf(time));
                delete.setVisibility(VISIBLE);
                submit.setText("Update");
            } catch (JSONException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
