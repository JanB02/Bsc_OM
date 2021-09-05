package com.uba;

import org.apache.commons.io.IOUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {



        // 1) READ FILES
        // 2) TRIM BOTH SETS BY MONTH AND HOUR

        DataSet set1 = readingDataFromSingleFileIntoADataSet(".txt","O3","ILL1");
        System.out.println("STEP1: SET1 length raw : " +set1.printLengths());
        set1.trimDataByMonthAndHour("05","08","07","19");
        System.out.println("STEP2: SET1 length after trimming month and hour : " +set1.printLengths());

        DataSet set2 = readingDataFromManyFilesIntoADataSet(".dat","RF","GS","LUTE","ILL1");
        System.out.println("STEP1: SET2 length raw : " +set2.printLengths());
        set2.trimDataByMonthAndHour("05","08","07","19");
        System.out.println("STEP2: SET2 length after trimming month and hour : " +set2.printLengths());

        // 3) REMOVE DATA OFFSETS
        System.out.println("removing date offsets......");
        List<DataSet> temp = DataSet.removeDateOffsets(set1,set2);
        System.out.println("STEP3 :SET1 length nach Angleichen der Datum offset : " +temp.get(0).printLengths());
        System.out.println("STEP3 :SET2 length nach Angleichen der Datum offset : " +temp.get(1).printLengths());

        // 4) REMOVE INVALID VALUES IN BOTH SETS TO MAKE THEM COMPARABLE
        System.out.println("removing invalid inputs......");
        temp = DataSet.makeComparableSetsByRemovingInvalidInputs(temp.get(0),temp.get(1));
        System.out.println("STEP4 :SET1 length nach entfernen der Invaliden Inputs : " +temp.get(0).printLengths());
        System.out.println("STEP4 :SET2 length nach entfernen der Invaliden Inputs : " +temp.get(1).printLengths());

        //System.out.println("SET1 :"+temp.get(0).getDatum().get(temp.get(1).getData().size()-1)+" " +temp.get(0).getData().get(temp.get(1).getData().size()-1) + "\nSET2 :" + temp.get(1).getDatum().get(temp.get(1).getData().size()-1)+" " +temp.get(1).getData().get(temp.get(1).getData().size()-1));

        // 5) Make comparable DataSets
        System.out.println("making comparable data sets......");
        List<ComparableMonthDataSet> t = DataSet.makeComparableMonthDataSets(temp.get(0),temp.get(1));
        System.out.println("STEP5 :Size of comparable data set (number of Months) : " + t.size());
        System.out.println("Example : Datum : " + t.get(t.size()-1).Datum + "  Size of A dataset :" + t.get(t.size()-1).a.size() +"  Size of A dataset :" + t.get(t.size()-1).b.size());

        int count = 0;
        for(var s:t){
            if(s.a.size()<2 || s.b.size()< 2){
                System.out.println("Length A: " + s.a.size() + " Length B: " + s.b.size());
                count++;
            }
        }

        List<String> res = showMesstellenWithMostMessgr();
        for(var r: res){
            System.out.println("Alle fünf an der Messstelle: "+r +" size: "+ res.size());
        }

    }

    /**
     * Anhand der Filter wird ein File geholt und aus dem die Daten für eine Messstation rausgelesen.
     * Geeignet wenn sich alle Daten in nur einem file befinden wie zB für O3, NO und NO2
     * Ungeeignet wenn die Daten über mehrere Dateien verstreut sind.
     * @param fileending
     * @param Messgr
     * @param Messstelle
     * @return DataSet mit jeweiligen Werten für die jeweilige zeit für die jeweilige Messstation
     */
    public static DataSet readingDataFromSingleFileIntoADataSet(String fileending, String Messgr, String Messstelle) {

        FileInputStream fis = null;
        String data = "";
        String alldata = "START + \n";
        ArrayList<String> filesAsStrings = new ArrayList<>();

            String basic_path_InputFiles = "src\\resources\\Input_Files\\";
            File folder = new File("src\\resources\\Input_Files");
            String[] listOfFiles = folder.list();
            int index =-1;
            List<String> dates = new ArrayList<>();
            List<String> MessDatenFuerEineMessstelle = new ArrayList<>();

            StringBuilder builder = new StringBuilder(alldata);

            for (int i = 0; i < listOfFiles.length; i++) {
                String file = listOfFiles[i];
                if (file.endsWith(fileending)) {
                    try {
                        fis = new FileInputStream(basic_path_InputFiles+file);
                        data = IOUtils.toString(fis, "UTF-8");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    filesAsStrings.add(data);
                }
            }



                /*if(filesAsStrings.size()>1){
                    System.out.println("Searching in "+ filesAsStrings.size()+ " files" );

                }*/
                index = -1;
                for(var file2: filesAsStrings){
                    String[] rows = file2.split("\r\n");
                    String header = rows[0];
                    List<String> header_list= Arrays.stream(header.split(";")).toList();

                    //20949
                    for(int y = 0; y< header_list.size();y++){
                        if(header_list.get(y).contains(Messgr) && header_list.get(y).contains(Messstelle)){
                            index = y;
                        }
                    }
                    if(index == -1){
                        continue;
                    }
                    for(int x = 1; x< rows.length;x++){
                        MessDatenFuerEineMessstelle.add(rows[x].split(";")[index]);
                    }


                    dates = getDatesListFromSingleFile(filesAsStrings.get(0));
                    break;
                }

            if(index==-1){
                throw new InvalidParameterException("Keine Daten für die Messstelle:" + Messstelle+ " und Messgröße: "+ Messgr);
            }


        return new DataSet(dates,MessDatenFuerEineMessstelle);
    }

    /**
     * Anhand der Filter wird ein File geholt und aus dem die Daten für eine Messstation rausgelesen.
     * Geeignet wenn sich alle Daten in mehreren files befinden wie zB für O3, NO und NO2
     * @param fileending
     * @param filenameFilter
     * @param filenameFilter_2
     * @param Messgr2
     * @param Messstelle
     * @return DataSet mit jeweiligen Werten für die jeweilige zeit für die jeweilige Messstation
     */
    public static DataSet readingDataFromManyFilesIntoADataSet(String fileending, String filenameFilter, String filenameFilter_2, String Messgr2, String Messstelle) {

        FileInputStream fis = null;
        String data = "";
        String alldata = "START + \n";
        ArrayList<String> filesAsStrings = new ArrayList<>();
        try {
            String basic_path_InputFiles = "src\\resources\\Input_Files\\";
            File folder = new File("src\\resources\\Input_Files");
            String[] listOfFiles = folder.list();


            StringBuilder builder = new StringBuilder(alldata);

            for (int i = 0; i < listOfFiles.length; i++) {
                String file = listOfFiles[i];
                if (file.endsWith(fileending) && file.contains(filenameFilter)&& file.contains(filenameFilter_2)) {
                    fis = new FileInputStream(basic_path_InputFiles+file);
                    data = IOUtils.toString(fis, "UTF-8");
                    filesAsStrings.add(data);
                }

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(filesAsStrings.size());

        List<String> MessDatenFuerEineMessstelle = new ArrayList<>();
        int index = -1;
        for(var file : filesAsStrings){
            String[] rows = file.split("\r\n");
            String header = rows[0];
            List<String> header_list= Arrays.stream(header.split(";")).toList();

            for(int i = 0; i< header_list.size();i++){
                if(i == 74){
                    var b = true;
                }
                if(header_list.get(i).contains(Messgr2) && header_list.get(i).contains(Messstelle)){
                    index = i;
                }
            }
            if(index == -1){
                throw new InvalidParameterException("Für die Messstelle \"" + Messstelle+ "\" gibt es keine dazugehörigen meteorologischen Daten für die Messgröße \"" + Messgr2 + "\"");
            }
            for(int i = 1; i< rows.length;i++){
                MessDatenFuerEineMessstelle.add(rows[i].split(";")[index]);
            }
        }

        List<String> dates = getDatesListFromManyFile(filesAsStrings);
        return new DataSet(dates,MessDatenFuerEineMessstelle);

    }

    public static List<String> showMesstellenWithMostMessgr(){
        List<String> result = new ArrayList<>();


        FileInputStream fis = null;
        String data = "";
        String alldata = "START + \n";
        ArrayList<String> filesAsStrings = new ArrayList<>();
        List<String> MessstellenMitLUFE = new ArrayList<>();
        List<String> MessstellenMitLUTE = new ArrayList<>();
        List<String> MessstellenMitGSTR = new ArrayList<>();
        List<String> MessstellenMitWIRI = new ArrayList<>();
        List<String> MessstellenMitWIGE = new ArrayList<>();
        List<String> MessstellenMitALLEN5 = new ArrayList<>();
        try {
            String basic_path_InputFiles = "src\\resources\\Input_Files\\";
            File folder = new File("src\\resources\\Input_Files");
            String[] listOfFiles = folder.list();


            StringBuilder builder = new StringBuilder(alldata);

            for (int i = 0; i < listOfFiles.length; i++) {
                String file = listOfFiles[i];
                    fis = new FileInputStream(basic_path_InputFiles+file);
                    data = IOUtils.toString(fis, "UTF-8");
                    filesAsStrings.add(data);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(var file : filesAsStrings){
            String[] rows = file.split("\r\n");
            String header = rows[0];
            List<String> header_list= Arrays.stream(header.split(";")).toList();
            int index = -1;
            for(int i = 0; i< header_list.size();i++){
                if(header_list.get(i).contains("WIRI")){
                    if(!MessstellenMitWIRI.contains(header_list.get(i).split(":")[1])){
                        MessstellenMitWIRI.add(header_list.get(i).split(":")[1]);
                    }
                }else if(header_list.get(i).contains("WIGE")){
                    if(!MessstellenMitWIGE.contains(header_list.get(i).split(":")[1])){
                        MessstellenMitWIGE.add(header_list.get(i).split(":")[1]);
                    }
                }else if(header_list.get(i).contains("LUTE")){
                    if(!MessstellenMitLUTE.contains(header_list.get(i).split(":")[1])){
                        MessstellenMitLUTE.add(header_list.get(i).split(":")[1]);
                    }
                }else if(header_list.get(i).contains("LUFE")){
                    if(!MessstellenMitLUFE.contains(header_list.get(i).split(":")[1])){
                        MessstellenMitLUFE.add(header_list.get(i).split(":")[1]);
                    }
                }else if(header_list.get(i).contains("GSTR")){
                    if(!MessstellenMitGSTR.contains(header_list.get(i).split(":")[1])){
                        MessstellenMitGSTR.add(header_list.get(i).split(":")[1]);
                    }
                }
            }
        }

        for(var x: MessstellenMitLUTE){
            if(MessstellenMitLUFE.contains(x) && MessstellenMitWIGE.contains(x) && MessstellenMitWIRI.contains(x) && MessstellenMitGSTR.contains(x)){
                MessstellenMitALLEN5.add(x);
            }
        }

        result = MessstellenMitALLEN5;
        return result;
    }

    /**
     * Aus einem File als String werden die Datum/dates rausgeholt
     * @param file
     * @return List of dates
     */
    public static List<String> getDatesListFromSingleFile(String file){

        String[] rows = file.split("\r\n");
        String header = rows[0];
        List<String> result = new ArrayList<>();
        List<String> header_list= Arrays.stream(header.split(";")).toList();
        int index = 0;
        for(int i = 1; i< rows.length;i++){
            result.add(rows[i].split(";")[index]);
        }
        return result;
    }

    /**
     * Aus mehreren Files als String werden die Datum/dates rausgeholt
     * @param files
     * @return List of dates
     */
    public static List<String> getDatesListFromManyFile(List<String> files){

        List<String> result = new ArrayList<>();
        for(var file: files){
            String[] rows = file.split("\r\n");
            String header = rows[0];

            List<String> header_list= Arrays.stream(header.split(";")).toList();
            int index = 0;
            for(int i = 1; i< rows.length;i++){
                result.add(rows[i].split(";")[index]);
            }
        }

        return result;
    }
}


