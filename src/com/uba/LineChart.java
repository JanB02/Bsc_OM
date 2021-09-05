package com.uba;
import java.awt.Color;
import java.awt.BasicStroke;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

import static com.uba.Main.readingDataFromManyFilesIntoADataSet;
import static com.uba.Main.readingDataFromSingleFileIntoADataSet;

public class LineChart extends ApplicationFrame {


    public String ZweiteMessgroeßeFileFilter1 ="";
    public String ZweiteMessgroeßeFileFilter2 ="";
    public String Anstieg = "";

    public LineChart(String applicationTitle, String chartTitle, boolean createAllJpegs, boolean added_Correlation, boolean addedAB ,boolean addedAB_Regression) {
        super(applicationTitle);
        if(!createAllJpegs){
            JFreeChart xylineChart = ChartFactory.createXYLineChart(
                    chartTitle ,
                    "Jahre" ,
                    "Korrelationskoeffizient" ,
                    createDataset(null, null, null,added_Correlation,addedAB,addedAB_Regression) ,
                    PlotOrientation.VERTICAL ,
                    true , true , false);

            ChartPanel chartPanel = new ChartPanel( xylineChart );
            chartPanel.setPreferredSize( new java.awt.Dimension( 1900 , 1000 ) );
            final XYPlot plot = xylineChart.getXYPlot( );

            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer( );
            renderer.setSeriesPaint( 0 , Color.BLUE );
            renderer.setSeriesPaint( 1 , Color.RED );
            renderer.setSeriesPaint( 2 , Color.GREEN );
            renderer.setSeriesPaint( 3 , Color.MAGENTA );
            renderer.setSeriesPaint( 4 , Color.BLACK);
            renderer.setSeriesPaint( 5 , Color.ORANGE );

            renderer.setSeriesStroke( 0 , new BasicStroke( 2.0f ) );
            renderer.setSeriesStroke( 1 , new BasicStroke( 1.0f ) );
            renderer.setSeriesStroke( 2 , new BasicStroke( 1.0f ) );
            renderer.setSeriesStroke( 3 , new BasicStroke( 1.0f ) );
            renderer.setSeriesStroke( 4 , new BasicStroke( 1.0f ) );
            renderer.setSeriesStroke( 5 , new BasicStroke( 1.0f ) );

            plot.setRenderer( renderer );
            setContentPane( chartPanel );
        }else if(createAllJpegs){
            String[] Messstellen = new String[]{"ILL1","0103","0202","0301","0401","0502","1102","1301","2101","2401","2701","PIL1"};
            String[] ZweiteMessgroeßen = new String[]{"LUTE","LUFE","GSTR","WIRI","WIGE"};

            int counter = 1;
            for(var ms: Messstellen){
                for(var zweiteM: ZweiteMessgroeßen){
                    System.out.println("Creating "+counter++ +" out of "+(Messstellen.length*ZweiteMessgroeßen.length) + " Graphs");
                    JFreeChart temp_xylineChart = ChartFactory.createXYLineChart(
                            chartTitle ,
                            "Jahre" ,
                            "Korrelationskoeffizient" ,
                            createDataset("O3", zweiteM, ms,added_Correlation,addedAB,addedAB_Regression) ,
                            PlotOrientation.VERTICAL ,
                            true , true , false);

                    String basic_path_InputFiles = "src\\resources\\Output_Files\\";
                    File XYChart = new File( basic_path_InputFiles+ms+"_O3_"+zweiteM+"_Cor"+(added_Correlation==true?"1":"0")+"_AB"+(addedAB==true?"1":"0")+"_ABReg"+(addedAB_Regression==true?"1":"0")+".jpeg" );
                    try {
                        ChartUtils.saveChartAsJPEG( XYChart, temp_xylineChart, 1900, 1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            System.out.println("Creating of "+(Messstellen.length*ZweiteMessgroeßen.length) + " Graphs finished");
        }

    }

    public XYDataset createDataset(String ersteMessgroeße, String zweiteMessgroeße, String Messstelle, boolean added_Correlation, boolean addedAB, boolean addedAB_Regression ) {

        /*
        * ILL1, 0103, 0202, 0301, 0401, 0502, 1102, 1301, 2101, 2401, 2701, PIL1
        *
        * Bekannte: ILL1, 0202,0301,0401,0502,2401,2701,PIL1
        * Unbekannte: 0103,1102,1301,2101
        * ILL1 - Illmitz am Neusiedler See
        * 0202 - Forsthof am Schöpfl
        * 0301 - Hainburg
        * 0401 - Gänserndorf
        * 0502 - Heidenreichstein
        * 2401 - Wiener Neustadt
        * 2701 - Schwechat
        * PIL1 - Pillersdorf bei Retz
        * 0103 - Kollmitzberg
        * 1102 - Annaberg
        * 1301 - Mistelbach
        * 2101 - Wiesmath
        * Gibt keine O3,NO,NO2 Daten für : AKA
        *
        * */
        if(ersteMessgroeße == null && zweiteMessgroeße == null && Messstelle == null){
            ersteMessgroeße = "O3";
            zweiteMessgroeße = "LUTE";
            Messstelle = "ILL1";
        }

        if(zweiteMessgroeße == "LUTE" || zweiteMessgroeße == "LUFE" || zweiteMessgroeße == "GSTR" || zweiteMessgroeße == "GSTR#2"){
            ZweiteMessgroeßeFileFilter1 = "RF";
            ZweiteMessgroeßeFileFilter2 = "GS";
        }else if(zweiteMessgroeße == "WIRI" || zweiteMessgroeße == "WIGE"){
            ZweiteMessgroeßeFileFilter1 = "Wind";
            ZweiteMessgroeßeFileFilter2 = "MW1";
        }



        DataSet set1 = readingDataFromSingleFileIntoADataSet(".txt",ersteMessgroeße,Messstelle);
        set1.trimDataByMonthAndHour("05","08","07","19");

        DataSet set2 = readingDataFromManyFilesIntoADataSet(".dat",ZweiteMessgroeßeFileFilter1,ZweiteMessgroeßeFileFilter2,zweiteMessgroeße,Messstelle);
        set2.trimDataByMonthAndHour("05","08","07","19");

        List<DataSet> temp = DataSet.removeDateOffsets(set1,set2);
        temp = DataSet.makeComparableSetsByRemovingInvalidInputs(temp.get(0),temp.get(1));

        //change between DAY MONTH and YEAR in this line
        List<ComparableMonthDataSet> t = DataSet.makeComparableMonthDataSets(temp.get(0),temp.get(1));

        PearsonsCorrelation pc = new PearsonsCorrelation();
        List<Double> yList = new ArrayList<>();
        List<Double> yListA = new ArrayList<>();
        List<Double> yListB = new ArrayList<>();
        List<Double> xList = new ArrayList<>();

        for(ComparableDataSet comparableDataSet: t ){

            if(comparableDataSet instanceof ComparableYearDataSet){
                xList.add(Double.parseDouble(comparableDataSet.Datum.substring(6,10)));
            }else if(comparableDataSet instanceof  ComparableMonthDataSet){
                double te = 0;
                if(Integer.parseInt(comparableDataSet.Datum.substring(3,5))!=1){
                    te+=Double.parseDouble(comparableDataSet.Datum.substring(3,5))*0.083333333333333;
                }
                xList.add(Double.parseDouble(comparableDataSet.Datum.substring(6,10))+te);
            }else{
                xList.add(Double.parseDouble(comparableDataSet.Datum.substring(6,10)));
            }
            yList.add(pc.correlation(comparableDataSet.getAasDoubleArray(),comparableDataSet.getBasDoubleArray()));
            yListA.add(comparableDataSet.getAverageOfA());
            yListB.add(comparableDataSet.getAverageOfB());
        }
        String pattern = "#.####";
        DecimalFormat decimalFormat = new DecimalFormat(pattern);


        double sum = 0;
        for(var y:yList){
            sum+=y;
        }
        double sumA = 0;
        for(var y:yListA){
            sumA+=y;
        }
        double sumB = 0;
        for(var y:yListB){
            sumB+=y;
        }

        //T-Test
        TTest test = new TTest();
        Boolean p = test.pairedTTest(Util.getAsArray(yListA),Util.getAsArray(yListB),0.05);


        String durchschnittlicheKorrelation = decimalFormat.format(sum/ yList.size());
        List<Double> tmpA = new ArrayList<>();
        List<Double> tmpB = new ArrayList<>();
        for(int i = 0;i <yListA.size();i++){
            tmpA.add(yListA.get(i)/100);
            tmpB.add(yListB.get(i)/100);
        }
        yListA= tmpA;
        yListB = tmpB;

        final XYSeries data1 = new XYSeries("Korrelation zwischen " + ersteMessgroeße + " und " + zweiteMessgroeße + " für die Messstelle : " +Messstelle );
        for(int i = 0 ; i< yList.size();i++){
            data1.add(xList.get(i), yList.get(i) );
        }

        final XYSeries data3 = new XYSeries(ersteMessgroeße + " für die Messstelle : " +Messstelle );
        for(int i = 0 ; i< xList.size();i++){
            data3.add((double)xList.get(i), yListA.get(i));
        }
        final XYSeries data4 = new XYSeries(zweiteMessgroeße + " für die Messstelle : " +Messstelle );
        for(int i = 0 ; i< xList.size();i++){
            data4.add((double)xList.get(i), yListB.get(i));
        }

        final XYSeriesCollection dataset = new XYSeriesCollection( );



        // Regressionslinie Berechnung
        // Nimmt als Parameter Xlist und YList
        double[] abResult = Util.getAndBforRegressionLine(xList,yList);
        double a = abResult[0];
        double b = abResult[1];

        double y1 =(double)a+(b*xList.get(0));
        double y2 =(double)a+(b*xList.get(xList.size()-1));
        Anstieg = decimalFormat.format((y2-y1)*100);
        final XYSeries data2 = new XYSeries("Regressionslinie mit Koeffizientenänderung von: "+Anstieg + " " + "     Durchschnittliche Korrelation beträgt: " + durchschnittlicheKorrelation );

        data2.add((double)xList.get(0),y1);
        data2.add((double)xList.get(xList.size()-1),y2);
        // Regressionslinie für Korrelation Berechnung Ende


        //Regressionslinie Berechnung für A
        String durchschnittlicheKorrelationA = decimalFormat.format(sumA/ yListA.size());
        double[] abResult_A = Util.getAndBforRegressionLine(xList,yListA);
        double a_A = abResult_A[0];
        double b_A = abResult_A[1];
        double y1_A =(double)a_A+(b_A*xList.get(0));
        double y2_A =(double)a_A+(b_A*xList.get(xList.size()-1));
        var Ans_A = decimalFormat.format((y2_A-y1_A)*100);
        final XYSeries data5 = new XYSeries("Änderung " +ersteMessgroeße + ": "+Ans_A + " " + "     Durchs: " + durchschnittlicheKorrelationA);
        data5.add((double)xList.get(0),y1_A);
        data5.add((double)xList.get(xList.size()-1),y2_A);

        //Regressionslinie Berechnung für B
        String durchschnittlicheKorrelationB = decimalFormat.format(sumB/ yListB.size());
        double[] abResult_B = Util.getAndBforRegressionLine(xList,yListB);
        double a_B = abResult_B[0];
        double b_B = abResult_B[1];
        double y1_B =(double)a_B+(b_B*xList.get(0));
        double y2_B =(double)a_B+(b_B*xList.get(xList.size()-1));
        var Ans_B = decimalFormat.format((y2_B-y1_B)*100);
        final XYSeries data6 = new XYSeries("Änderung " +zweiteMessgroeße + ": "+Ans_B + " " + "     Durchs: " + durchschnittlicheKorrelationB+ "            Statistisch signifikant für Alpha 0,05 :" + (p==true?"Ja":"Nein"));
        data6.add((double)xList.get(0),y1_B);
        data6.add((double)xList.get(xList.size()-1),y2_B);



        if(added_Correlation){
            dataset.addSeries(data1);
            dataset.addSeries(data2);
        }
        if(addedAB){
            dataset.addSeries(data3);
            dataset.addSeries(data4);
        }
        if(addedAB_Regression){
            dataset.addSeries(data5);
            dataset.addSeries(data6);
        }
        return dataset;
    }

    public static void main( String[ ] args ) {
        LineChart chart = new LineChart("Data",
                "Korrelationsdaten",true,true, true,true);
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }
}