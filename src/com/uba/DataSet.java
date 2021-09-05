package com.uba;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataSet {


        public List<String> datum;
        public List<String> data;

        public String printLengths(){
                return "Datum length:" + datum.size() + " ; Data length:" + data.size();
        }

        public boolean isLengthsValid(){
                return datum.size()==data.size();
        }

        /**
         *
         * @param starting_month data will start with this month, including this month
         * @param ending_month data will end with this month including this month
         * @param starting_hour data will start with this hour, including this month
         * @param ending_hour data will end with this hour including this month
         *                    All params are expected as string numerals
         *                    Must be a valid DataSet
         */
        public void trimDataByMonthAndHour(String starting_month, String ending_month, String starting_hour, String ending_hour ){

                List<String> tempDatum = new ArrayList<>();
                List<String> tempdata = new ArrayList<>();

                for(int i = 0; i< data.size();i++){
                        if(Integer.parseInt(datum.get(i).substring(3,5))>= Integer.parseInt(starting_month) && Integer.parseInt(datum.get(i).substring(3,5)) <= Integer.parseInt(ending_month)){
                                if(Integer.parseInt(datum.get(i).substring(11,13))>= Integer.parseInt(starting_hour) && Integer.parseInt(datum.get(i).substring(11,13))<= Integer.parseInt(ending_hour)){
                                        tempDatum.add(this.datum.get(i));
                                        tempdata.add(this.data.get(i));
                                }
                        }
                }
                datum = tempDatum;
                data = tempdata;
        }

        /**
         * Only takes dates which are corresponding with each other. excess non corresponding dates will be cut off.
         * Max date is the 1st of december 2020
         * @param a
         * @param b
         * @return
         */
        public static List<DataSet> removeDateOffsets (DataSet a, DataSet b){

                DataSet tempA = new DataSet();
                DataSet tempB = new DataSet();
                List<DataSet> result = new ArrayList<>();
                LocalDate maxDate = LocalDate.parse("2020-12-01");
                LocalDate startingDayA = LocalDate.parse(a.datum.get(0).substring(6,10)+"-"+a.datum.get(0).substring(3,5)+"-"+a.datum.get(0).substring(0,2));
                LocalDate startingDayB = LocalDate.parse(b.datum.get(0).substring(6,10)+"-"+b.datum.get(0).substring(3,5)+"-"+b.datum.get(0).substring(0,2));

                if(startingDayA.isBefore(startingDayB)){
                        for(int i = 0; i< a.datum.size();i++){
                                if(!LocalDate.parse(a.datum.get(i).substring(6,10)+"-"+a.datum.get(i).substring(3,5)+"-"+a.datum.get(i).substring(0,2)).isBefore(startingDayB)
                                && LocalDate.parse(a.datum.get(i).substring(6,10)+"-"+a.datum.get(i).substring(3,5)+"-"+a.datum.get(i).substring(0,2)).isBefore(maxDate)){
                                        tempA.datum.add(a.datum.get(i));
                                        tempA.data.add(a.data.get(i));
                                }
                        }
                        for(int i = 0; i < b.datum.size();i++){
                                if(LocalDate.parse(b.datum.get(i).substring(6,10)+"-"+b.datum.get(i).substring(3,5)+"-"+b.datum.get(i).substring(0,2)).isBefore(maxDate)){
                                        tempB.datum.add(b.datum.get(i));
                                        tempB.data.add(b.data.get(i));
                                }
                        }
                }else if (!startingDayA.isBefore(startingDayB)){
                        for(int i = 0; i< b.datum.size();i++){
                                if(!LocalDate.parse(b.datum.get(i).substring(6,10)+"-"+b.datum.get(i).substring(3,5)+"-"+b.datum.get(i).substring(0,2)).isBefore(startingDayB)
                                        && LocalDate.parse(b.datum.get(i).substring(6,10)+"-"+b.datum.get(i).substring(3,5)+"-"+b.datum.get(i).substring(0,2)).isBefore(maxDate)){
                                        tempB.datum.add(b.datum.get(i));
                                        tempB.data.add(b.data.get(i));
                                }
                        }
                        for(int i = 0; i < a.datum.size();i++){
                                if(LocalDate.parse(a.datum.get(i).substring(6,10)+"-"+a.datum.get(i).substring(3,5)+"-"+a.datum.get(i).substring(0,2)).isBefore(maxDate)){
                                        tempA.datum.add(a.datum.get(i));
                                        tempA.data.add(a.data.get(i));
                                }
                        }
                }
                result.add(tempA);
                result.add(tempB);
                return result;
        }

        /**
         * If any of both sets have an invalid measurement (NA, *, ?) both corresponding data sets will be removed, even if the other has a value
         * example : Set "a" has 23-9-2020 17:00 a measurement "*" and Set "b" has for the same time a valid measurement.
         * As a result, both entries will be deleted. Only if both entries have a valid value for the same time, the entries will remain.
         *
         * Must be a valid DataSet
         * @param a first Dataset
         * @param b second Dataset
         * @return returns a List with Dataset "a" as Argument atIndex 0 and Dataset "b" as Argument atIndex "1"
         */
        public static List<DataSet> makeComparableSetsByRemovingInvalidInputs(DataSet a, DataSet b){

                DataSet tempA = new DataSet();
                DataSet tempB = new DataSet();
                List<DataSet> result = new ArrayList<>();
                for(int i = 0; i< a.datum.size();i++){
                        if(!a.data.get(i).contains("NA") && !a.data.get(i).contains("?") && !a.data.get(i).contains("*") &&
                                !b.data.get(i).contains("NA") && !b.data.get(i).contains("?") && !b.data.get(i).contains("*")){
                                tempA.datum.add(a.datum.get(i));
                                tempA.data.add(a.data.get(i));
                                tempB.datum.add(b.datum.get(i));
                                tempB.data.add(b.data.get(i));
                        }
                }
                result.add(tempA);
                result.add(tempB);
                return result;
        }

        /**
         * REQUIRES BOTH DATASETS TO BE COMPARABLE
         * ensure this by calling "makeComparableSetsByRemovingInvalidInputs" first
         * Until now the datasets were based on hourly data and separated in 2 lists.
         * This Method packs the data BY DAY and also cramps the before separated datum, data A and data B into one ComparableDayDataSet.
         * Which makes it possible to later calculate Paerson correlation more easily as the data will already be presorted for this very purpose.
         * @param a
         * @param b
         * @return
         */

        public static List<ComparableDayDataSet> makeComparableDayDataSets (DataSet a, DataSet b){

                List<ComparableDayDataSet> result = new ArrayList<>();
                ComparableDayDataSet tempSet = new ComparableDayDataSet();
                String currentDay = a.datum.get(0).substring(0,10);
                List<String> tempA = new ArrayList<>();
                List<String> tempB = new ArrayList<>();

                for(int i = 0; i<a.datum.size();i++){
                        if(i==0){
                               tempA.add(a.data.get(i));
                               tempB.add(b.data.get(i));
                        }else{
                                if(a.datum.get(i).substring(0,2).equals(currentDay.substring(0,2))){
                                        tempA.add(a.data.get(i));
                                        tempB.add(b.data.get(i));
                                }else{
                                        tempSet.Datum = currentDay;
                                        tempSet.a = tempA;
                                        tempSet.b = tempB;
                                        result.add(tempSet);
                                        tempSet = new ComparableDayDataSet();
                                        tempA = new ArrayList<>();
                                        tempB = new ArrayList<>();
                                        currentDay = a.datum.get(i).substring(0,10);
                                        tempA.add(a.data.get(i));
                                        tempB.add(b.data.get(i));
                                }

                        }
                        if(i==a.datum.size()-1){
                                tempSet.Datum = currentDay;
                                tempSet.a = tempA;
                                tempSet.b = tempB;
                                result.add(tempSet);
                                tempSet = new ComparableDayDataSet();
                        }
                }
                List<ComparableDayDataSet> temp_result = new ArrayList<>();
                for(var temp_cdds: result){
                        if(temp_cdds.a.size()>8 && temp_cdds.b.size()>8){
                                temp_result.add(temp_cdds);
                        }
                }
                result = temp_result;
                return result;
        }

        /**
         * REQUIRES BOTH DATASETS TO BE COMPARABLE
         * ensure this by calling "makeComparableSetsByRemovingInvalidInputs" first
         * Until now the datasets were based on hourly data and separated in 2 lists.
         * This Method packs the data BY MONTH and also cramps the before separated datum, data A and data B into one ComparableDataSet.
         * Which makes it possible to later calculate Paerson correlation more easily as the data will already be presorted for this very purpose.
         * @param a
         * @param b
         * @return
         */

        public static List<ComparableMonthDataSet> makeComparableMonthDataSets (DataSet a, DataSet b){

                List<ComparableMonthDataSet> result = new ArrayList<>();
                ComparableMonthDataSet tempSet = new ComparableMonthDataSet();
                String currentDay = a.datum.get(0).substring(0,10);
                List<String> tempA = new ArrayList<>();
                List<String> tempB = new ArrayList<>();

                for(int i = 0; i<a.datum.size();i++){
                        if(i==0){
                                tempA.add(a.data.get(i));
                                tempB.add(b.data.get(i));
                        }else{
                                if(a.datum.get(i).substring(3,5).equals(currentDay.substring(3,5))){
                                        tempA.add(a.data.get(i));
                                        tempB.add(b.data.get(i));
                                }else{
                                        tempSet.Datum = currentDay;
                                        tempSet.a = tempA;
                                        tempSet.b = tempB;
                                        result.add(tempSet);
                                        tempSet = new ComparableMonthDataSet();
                                        tempA = new ArrayList<>();
                                        tempB = new ArrayList<>();
                                        currentDay = a.datum.get(i).substring(0,10);
                                        tempA.add(a.data.get(i));
                                        tempB.add(b.data.get(i));
                                }

                        }
                        if(i==a.datum.size()-1){
                                tempSet.Datum = currentDay;
                                tempSet.a = tempA;
                                tempSet.b = tempB;
                                result.add(tempSet);
                                tempSet = new ComparableMonthDataSet();
                        }
                }
                List<ComparableMonthDataSet> temp_result = new ArrayList<>();
                for(var temp_cdds: result){
                        if(temp_cdds.a.size()>8 && temp_cdds.b.size()>8){
                                temp_result.add(temp_cdds);
                        }
                }
                result = temp_result;
                return result;
        }

        /**
         * REQUIRES BOTH DATASETS TO BE COMPARABLE
         * ensure this by calling "makeComparableSetsByRemovingInvalidInputs" first
         * Until now the datasets were based on hourly data and separated in 2 lists.
         * This Method packs the data BY YEAR and also cramps the before separated datum, data A and data B into one ComparableDataSet.
         * Which makes it possible to later calculate Paerson correlation more easily as the data will already be presorted for this very purpose.
         * @param a
         * @param b
         * @return
         */

        public static List<ComparableYearDataSet> makeComparableYearDataSets (DataSet a, DataSet b){

                List<ComparableYearDataSet> result = new ArrayList<>();
                ComparableYearDataSet tempSet = new ComparableYearDataSet();
                String currentDay = a.datum.get(0).substring(0,10);
                List<String> tempA = new ArrayList<>();
                List<String> tempB = new ArrayList<>();

                for(int i = 0; i<a.datum.size();i++){
                        if(i==0){
                                tempA.add(a.data.get(i));
                                tempB.add(b.data.get(i));
                        }else{
                                if(a.datum.get(i).substring(8,10).equals(currentDay.substring(8,10))){
                                        tempA.add(a.data.get(i));
                                        tempB.add(b.data.get(i));
                                }else{
                                        tempSet.Datum = currentDay;
                                        tempSet.a = tempA;
                                        tempSet.b = tempB;
                                        result.add(tempSet);
                                        tempSet = new ComparableYearDataSet();
                                        tempA = new ArrayList<>();
                                        tempB = new ArrayList<>();
                                        currentDay = a.datum.get(i).substring(0,10);
                                        tempA.add(a.data.get(i));
                                        tempB.add(b.data.get(i));
                                }

                        }
                        if(i==a.datum.size()-1){
                                tempSet.Datum = currentDay;
                                tempSet.a = tempA;
                                tempSet.b = tempB;
                                result.add(tempSet);
                                tempSet = new ComparableYearDataSet();
                        }
                }
                List<ComparableYearDataSet> temp_result = new ArrayList<>();
                for(var temp_cdds: result){
                        if(temp_cdds.a.size()>8 && temp_cdds.b.size()>8){
                                temp_result.add(temp_cdds);
                        }
                }
                result = temp_result;
                return result;
        }



        public DataSet(List<String> datum, List<String> data) {
                this.datum = datum;
                this.data = data;
        }

        public DataSet() {
                this.datum = new ArrayList<>();
                this.data = new ArrayList<>();
        }

        public List<String> getDatum() {
                return datum;
        }

        public void setDatum(List<String> datum) {
                this.datum = datum;
        }

        public List<String> getData() {
                return data;
        }

        public void setData(List<String> data) {
                this.data = data;
        }
}
