package com.uba;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.ArrayList;
import java.util.List;

import static com.uba.Main.readingDataFromManyFilesIntoADataSet;
import static com.uba.Main.readingDataFromSingleFileIntoADataSet;

public class TimeSeriesChart extends ApplicationFrame {

    String ErsteMessgroeße ="";
    String FileIDderErstenMessgroeße = "";
    String ZweiteMessgroeße ="";
    String Messstelle ="";
    String Anstieg = "";

    public TimeSeriesChart(final String title ) {
        super( title );
        final XYDataset dataset = createDataset( );
        final JFreeChart chart = createChart( dataset );
        final ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 1500 , 800 ) );
        chartPanel.setMouseZoomable( true , false );
        setContentPane( chartPanel );
    }

    private XYDataset createDataset( ) {

        ErsteMessgroeße = "O3";
        //FileIDderErstenMessgroeße = "0132";
        ZweiteMessgroeße = "LUFE";
        Messstelle = "ZA";

        final TimeSeries series = new TimeSeries( "Correlation Data" );
        final TimeSeries series2 = new TimeSeries( "Random Data" );

        DataSet set1 = readingDataFromSingleFileIntoADataSet(".txt",ErsteMessgroeße,Messstelle);
        set1.trimDataByMonthAndHour("05","08","07","19");

        DataSet set2 = readingDataFromManyFilesIntoADataSet(".dat","RF","GS",ZweiteMessgroeße,Messstelle);
        set2.trimDataByMonthAndHour("05","08","07","19");

        List<DataSet> temp = DataSet.removeDateOffsets(set1,set2);
        temp = DataSet.makeComparableSetsByRemovingInvalidInputs(temp.get(0),temp.get(1));
        //change between DAY MONTH and YEAR in this line
        List<ComparableMonthDataSet> t = DataSet.makeComparableMonthDataSets(temp.get(0),temp.get(1));

        PearsonsCorrelation pc = new PearsonsCorrelation();
        List<Double> c_result = new ArrayList<>();
        List<Day> daylist = new ArrayList<>();
        for(var comparableDataSet: t ){
            daylist.add(new Day(Integer.parseInt(comparableDataSet.Datum.substring(0,2)),Integer.parseInt(comparableDataSet.Datum.substring(3,5)),Integer.parseInt(comparableDataSet.Datum.substring(6,10))));
            c_result.add(pc.correlation(comparableDataSet.getAasDoubleArray(),comparableDataSet.getBasDoubleArray()));
        }


        for (int i = 0; i < t.size(); i++) {

            try {
                series.add(daylist.get(i), c_result.get(i));
            } catch ( SeriesException e ) {
                System.err.println("Error adding to series");
            }
        }

        TimeSeriesCollection res_TSC = new TimeSeriesCollection(series);
        //res_TSC.addSeries(series2);
        return res_TSC;
    }

    private JFreeChart createChart( final XYDataset dataset ) {
        return ChartFactory.createTimeSeriesChart(
                "Korrelation zwischen " + ErsteMessgroeße + " und " + ZweiteMessgroeße + " für die Messstelle : " +Messstelle + "   Korrelationsänderung Gesamt :" + Anstieg ,
                "Tage",
                "Korrelation",
                dataset,
                false,
                false,
                false);
    }

    public static void main( final String[ ] args ) {
        final String title = "Time Series Management";
        final TimeSeriesChart demo = new TimeSeriesChart( title );
        demo.pack( );
        RefineryUtilities.positionFrameRandomly( demo );
        demo.setVisible( true );
    }
}
