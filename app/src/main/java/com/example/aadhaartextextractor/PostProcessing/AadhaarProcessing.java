package com.example.aadhaartextextractor.PostProcessing;

import android.content.Context;
import android.graphics.Rect;
import android.widget.Toast;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class AadhaarProcessing {

    public HashMap<String, String> processExtractTextForFrontPic(FirebaseVisionText text, Context context) {
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();

        if (blocks.size() == 0) {
            Toast.makeText(context, "No Text Found:", Toast.LENGTH_SHORT).show();
            return null;
        }
        TreeMap<String, String> map = new TreeMap<>();

        for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
            for (FirebaseVisionText.Line line : block.getLines()) {
                Rect rect = line.getBoundingBox();
                String y = String.valueOf(rect.exactCenterY());
                String lineText = line.getText().toLowerCase();
                map.put(y, lineText);
            }
        }
        List<String> orderedData = new ArrayList<>(map.values());
        HashMap<String, String> dataMap = new HashMap<>();

        int i = 0;
        String regx = "\\d\\d\\d\\d([,\\s])?\\d\\d\\d\\d.*";
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).matches(regx)) {
                dataMap.put("name", orderedData.get(i));
                break;
            }
        }

        //setting gender first
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("female")) {
                dataMap.put("gender", "Female");
                break;
            } else if (orderedData.get(i).contains("male")) {
                dataMap.put("gender", "Male");
                break;

            }
        }
        if (!dataMap.containsKey("aadhaar")) {
            if (i + 1 < orderedData.size()) {
                dataMap.put("aadhaar", orderedData.get(i + 1));
            }
        }

        //searching for father
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("father")) {
                dataMap.put("fatherName", orderedData.get(i).replace("father", "").replace(":", ""));
                break;
            }
        }
        if (dataMap.containsKey("fatherName")) {
            if (i - 2 > -1) {
                dataMap.put("fatherName", orderedData.get(i - 2));
            }
        }

        //search for year of birth

        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("birth")) {
                dataMap.put("dob", orderedData.get(i).substring(orderedData.get(i).length() - 4));
                break;
            }
        }
        if (i - 1 > -1 && !orderedData.get(i - 1).contains("father"))
            dataMap.put("name", orderedData.get(i - 1));
        return dataMap;
        }

       // return dataMap;


    public HashMap<String, String> processExtractedTextForBackPic (FirebaseVisionText text, Context context){
        List<FirebaseVisionText.TextBlock> blocks = text.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(context, "No Text:(", Toast.LENGTH_SHORT).show();
            return null;
        }

        TreeMap<String, String> map = new TreeMap<>();

        for (FirebaseVisionText.TextBlock textBlock : text.getTextBlocks()) {
            for (FirebaseVisionText.Line line : textBlock.getLines()) {


                Rect rect = line.getBoundingBox();
                String y = String.valueOf(rect.exactCenterY());
                String lineText = line.getText().toLowerCase();
                map.put(y, lineText);

            }
        }

        List<String> orderedData = new ArrayList<>(map.values());
        HashMap<String, String> dataMap = new HashMap<>();
        int i = 0;
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).contains("address")) {
                dataMap.put("addressLine1", orderedData.get(i).replace("address", "").replace(":", ""));
                break;
            }
        }
        if (i + 1 < orderedData.size())
            dataMap.put("addressLine2", orderedData.get(i + 1));

        //pincode
        String regex = "\\d\\d\\d\\d\\d\\d";
        for (i = 0; i < orderedData.size(); i++) {
            if (orderedData.get(i).matches(regex) || orderedData.contains(".*" + regex)) {
                dataMap.put("pin", orderedData.get(i));
                break;
            }

        }
        return dataMap;

    }
}


