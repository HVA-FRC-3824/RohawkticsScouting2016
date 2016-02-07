package com.team3824.akmessing1.scoutingapp.views.rebound_rumble_specific;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.team3824.akmessing1.scoutingapp.R;
import com.team3824.akmessing1.scoutingapp.ScoutValue;
import com.team3824.akmessing1.scoutingapp.views.CustomCheckbox;
import com.team3824.akmessing1.scoutingapp.views.CustomCounter;
import com.team3824.akmessing1.scoutingapp.views.CustomScoutView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;

import java.util.Map;

public class CustomStacks extends CustomScoutView {

    private String TAG = "CustomStacks";

    String key;

    JSONArray jsonArray;

    final CustomCounter totes, preexistingTotes;
    final CustomCheckbox can, noodle, canDropped, stackDropped;
    final RadioGroup stackNum;
    Button finishStack;

    public CustomStacks(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.custom_stacks, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomScoutView);
        key = typedArray.getString(R.styleable.CustomScoutView_key);

        jsonArray = new JSONArray();

        stackNum = (RadioGroup)findViewById(R.id.stack_num);

        final RadioButton radioButton = new RadioButton(context, attrs);
        radioButton.setId(0);
        stackNum.addView(radioButton);
        stackNum.check(0);

        totes = (CustomCounter)findViewById(R.id.totes_stacked);
        preexistingTotes = (CustomCounter)findViewById(R.id.preexisting_totes);

        can = (CustomCheckbox)findViewById(R.id.can);
        noodle = (CustomCheckbox)findViewById(R.id.noodle);
        canDropped = (CustomCheckbox)findViewById(R.id.can_dropped);
        stackDropped = (CustomCheckbox)findViewById(R.id.stack_dropped);

        stackNum.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d(TAG, String.valueOf(checkedId)+" "+jsonArray.length());

                if(checkedId == jsonArray.length())
                {
                    can.setChecked(false);
                    noodle.setChecked(false);
                    canDropped.setChecked(false);
                    stackDropped.setChecked(false);
                    totes.setCount(0);
                    preexistingTotes.setCount(0);
                }
                else
                {
                    try {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(checkedId);
                        can.setChecked(jsonObject1.getBoolean("isCanned"));
                        noodle.setChecked(jsonObject1.getBoolean("isNoodled"));
                        canDropped.setChecked(jsonObject1.getBoolean("isCanDropped"));
                        stackDropped.setChecked(jsonObject1.getBoolean("isStackDropped"));
                        totes.setCount(jsonObject1.getInt("toteCount"));
                        preexistingTotes.setCount(jsonObject1.getInt("preexistingToteCount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        finishStack = (Button) findViewById(R.id.finish_stack);
        finishStack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                boolean isCanned = can.isChecked();
                boolean isNoodled = noodle.isChecked();
                boolean isCanDropped = canDropped.isChecked();
                boolean isStackDropped = stackDropped.isChecked();
                int toteCount = totes.getCount();
                int preexistingToteCount = preexistingTotes.getCount();
                try {
                    jsonObject.put("isCanned", isCanned);
                    jsonObject.put("isNoodled", isNoodled);
                    jsonObject.put("isCanDropped", isCanDropped);
                    jsonObject.put("isStackDropped", isStackDropped);
                    jsonObject.put("toteCount", toteCount);
                    jsonObject.put("preexistingToteCount", preexistingToteCount);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                int whichStack = stackNum.getCheckedRadioButtonId();

                Log.d(TAG, String.valueOf(whichStack)+" "+jsonArray.length());

                if (jsonArray.length() == whichStack) {
                    jsonArray.put(jsonObject);
                    RadioButton radioButton1 = new RadioButton(getContext());
                    radioButton1.setId(whichStack + 1);
                    stackNum.addView(radioButton1);
                    stackNum.check(whichStack + 1);
                    can.setChecked(false);
                    noodle.setChecked(false);
                    canDropped.setChecked(false);
                    stackDropped.setChecked(false);
                    totes.setCount(0);
                    preexistingTotes.setCount(0);
                } else {
                    jsonArray.remove(whichStack);
                    try {
                        jsonArray.put(whichStack, jsonObject);
                        stackNum.check(whichStack + 1);
                        JSONObject jsonObject1 = jsonArray.getJSONObject(whichStack + 1);
                        can.setChecked(jsonObject1.getBoolean("isCanned"));
                        noodle.setChecked(jsonObject1.getBoolean("isNoodled"));
                        canDropped.setChecked(jsonObject1.getBoolean("isCanDropped"));
                        stackDropped.setChecked(jsonObject1.getBoolean("isStackDropped"));
                        totes.setCount(jsonObject1.getInt("toteCount"));
                        preexistingTotes.setCount(jsonObject1.getInt("preexistingToteCount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    // Custom save
    @Override
    public void writeToMap(Map<String, ScoutValue> map)
    {
        String saveValue = jsonArray.toString();
        map.put(key,new ScoutValue(saveValue));
    }

    // Custom restore
    @Override
    public void restoreFromMap(Map<String, ScoutValue> map) {
        ScoutValue sv = map.get(key);
        if(sv != null) {
            String restoreValue = sv.getString();
            try {
                jsonArray = new JSONArray(restoreValue);
                if(jsonArray.length() > 0)
                {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                    can.setChecked(jsonObject1.getBoolean("isCanned"));
                    noodle.setChecked(jsonObject1.getBoolean("isNoodled"));
                    canDropped.setChecked(jsonObject1.getBoolean("isCanDropped"));
                    stackDropped.setChecked(jsonObject1.getBoolean("isStackDropped"));
                    totes.setCount(jsonObject1.getInt("toteCount"));
                    preexistingTotes.setCount(jsonObject1.getInt("preexistingToteCount"));
                    for(int i = 0; i < jsonArray.length(); i++)
                    {
                        RadioButton radioButton = new RadioButton(getContext());
                        radioButton.setId(i+1);
                        stackNum.addView(radioButton);
                    }
                    stackNum.check(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
