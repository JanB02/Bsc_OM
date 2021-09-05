package com.uba;

import java.util.ArrayList;
import java.util.List;

public class ComparableDataSet {

    public String Datum;
    public List<String> a;
    public List<String> b;
    public String paersonCorrelation;

    public double getAverageOfA(){

        double sum = 0;
        for(var x:a){
            x = x.replace(',', '.');
            sum += Double.parseDouble(x);
        }
        return sum/a.size();
    }
    public double getAverageOfB(){

        double sum = 0;
        for(var x:b){
            x = x.replace(',', '.');
            sum += Double.parseDouble(x);
        }
        return sum/b.size();
    }

    public double[] getAasDoubleArray(){
        double[] result = new double[a.size()];
        for(int i = 0; i< result.length;i++){
            result[i] = Double.parseDouble(a.get(i).replace(',', '.'));
        }
        return result;
    }
    public double[] getBasDoubleArray(){
        double[] result = new double[b.size()];
        for(int i = 0; i< result.length;i++){
            result[i] = Double.parseDouble(b.get(i).replace(',', '.'));
        }
        return result;
    }

    public ComparableDataSet() {
        Datum = "";
        a = new ArrayList<>();
        b = new ArrayList<>();
        paersonCorrelation = "";
    }

    public ComparableDataSet(String datum, List<String> a, List<String> b, String paersonCorrelation) {
        Datum = datum;
        this.a = a;
        this.b = b;
        this.paersonCorrelation = paersonCorrelation;
    }

    public String getPaersonCorrelation() {
        return paersonCorrelation;
    }

    public void setPaersonCorrelation(String paersonCorrelation) {
        this.paersonCorrelation = paersonCorrelation;
    }

    public String getDatum() {
        return Datum;
    }

    public void setDatum(String datum) {
        Datum = datum;
    }

    public List<String> getA() {
        return a;
    }

    public void setA(List<String> a) {
        this.a = a;
    }

    public List<String> getB() {
        return b;
    }

    public void setB(List<String> b) {
        this.b = b;
    }
}
