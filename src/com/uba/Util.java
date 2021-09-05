package com.uba;

import java.util.ArrayList;
import java.util.List;

public class Util {

    public static double[] getAsArray (List<Double> list){
        double[] result = new double[list.size()];
        for(int i = 0; i< result.length;i++){
            result[i] = list.get(i);
        }
        return result;
    }

    public static double[] getAndBforRegressionLine(List<Double> xList, List<Double> yList){
        double result[] = new double[2];
        double tempx = 0;
        for(var x: xList){
            tempx+=x;
        }
        double xListDurch = tempx/ xList.size();
        double tempy = 0;
        for(var y:yList){
            tempy+= y;
        }
        double yListDurch = tempy/ yList.size();

        List<Double> X_MinusDurchX = new ArrayList<>();
        List<Double> Y_MinusDurchY = new ArrayList<>();
        for(int i = 0; i<xList.size();i++){
            X_MinusDurchX.add(xList.get(i)-xListDurch);
        }
        for(int i = 0; i<yList.size();i++){
            Y_MinusDurchY.add(yList.get(i)-yListDurch);
        }
        List<Double> X_MinusDurchX_Mal_Y_MinusDurchY = new ArrayList<>();
        List<Double> X_MinusDurchX_Mal_X_MinusDurchX = new ArrayList<>();
        for(int i = 0; i<xList.size();i++){
            X_MinusDurchX_Mal_Y_MinusDurchY.add(X_MinusDurchX.get(i)*Y_MinusDurchY.get(i));
        }
        for(int i = 0; i<xList.size();i++){
            X_MinusDurchX_Mal_X_MinusDurchX.add(X_MinusDurchX.get(i)*X_MinusDurchX.get(i));
        }

        double b = 0;
        double sumtop = 0;
        double sumbot = 0;

        for(var x: X_MinusDurchX_Mal_Y_MinusDurchY){
            sumtop+=x;
        }
        for(var y: X_MinusDurchX_Mal_X_MinusDurchX){
            sumbot+=y;
        }
        b=sumtop/sumbot;
        double a = yListDurch - (b * xListDurch);
        result[0] = a;
        result[1] = b;
        return result;
    }
}
