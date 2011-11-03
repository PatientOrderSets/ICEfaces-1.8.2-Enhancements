/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */
package org.icefaces.application.showcase.view.bean.examples.component.outputChart;

import com.icesoft.faces.component.outputchart.OutputChart;
import org.icefaces.application.showcase.util.RandomNumberGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * <p>The ChartModelAxial class purpose is to show how the outputChart component
 * can be used to display axial data.  It is very important that data and
 * label arrays are of the same length.</p>
 *
 * @see org.icefaces.application.showcase.view.bean.examples.component.outputChart.AbstractChartData
 * @since 1.7
 */
public class ChartModelAxial extends AbstractChartData {

    private ArrayList chartData;

    public ChartModelAxial(String chartTitle,
                           boolean areaMapEnabled,
                           boolean axisOrientationEnabled,
                           boolean enableDynamicValues,
                           boolean enabledLengendPosition) {
        this.chartTitle = chartTitle;
        this.areaMapEnabled = areaMapEnabled;
        this.axisOrientationEnabled = axisOrientationEnabled;
        this.enableDynamicValues = enableDynamicValues;
        this.enabledLengendPosition = enabledLengendPosition;
        init();
    }
    
    public int getChartType(){
        return AXIAL_CHART_TYPE;
    }

    /**
     * Initialze the default chart data. It is very important that the first
     * dimension of the arrays are equal in length.
     */
    private void init() {
        // build default data
        double[][] doubleArray = {new double[]{350, 50, 400},
                new double[]{45, 145, 50},
                new double[]{-36, 6, 98},
                new double[]{66, 166, 74},
                new double[]{145, 105, 55},
                new double[]{80, 110, 4},
                new double[]{10, 90, 70}};
        chartData = new ArrayList(
                Arrays.asList(doubleArray));
        // build x axis labels
        xAxisLabels = new ArrayList(Arrays.asList(
                new String[]{"2005", "2005", "2006", "2007", "2008", "2009", "2010"}));

        // build colors
        colors = new ArrayList(Arrays.asList(
                new Color[]{new Color(26, 86, 138),
                            new Color(76, 126, 167),
                            new Color(148, 179, 203)}));

        // build legend lables
        legendLabels = new ArrayList(Arrays.asList(
                new String[]{"Bugs", "Enhancements", "Fixed"}));

        // x axis lables
        xAxisLabels = new ArrayList(Arrays.asList(
                new String[]{"2005", "2005", "2006", "2007", "2008", "2009", "2010"}));

        xAxisTitle = "Years";

        yAxisTitle = "Occurences";

        legendPlacement = "bottom";
        renderOnSubmit = true;
    }

    /**
     * Add new data to implementing classes data model.
     */
    public void addData() {
        RandomNumberGenerator randomNumberGenerator =
                RandomNumberGenerator.getInstance();

        // add some new data randomly. 
        chartData.add(
                new double[]{randomNumberGenerator.getRandomDouble(100.0, 450.0),
                        randomNumberGenerator.getRandomDouble(50.0, 100.0),
                        randomNumberGenerator.getRandomDouble(250.0, 650.0)});

        // get previous year and increment
        int year = Integer.parseInt(
                (String)xAxisLabels.get(xAxisLabels.size() - 1)) + 1;
        xAxisLabels.add(String.valueOf(year));
        renderOnSubmit = true;
    }

    /**
     * Remove new data to implementing classes data model.
     */
    public void removeData() {
        // remove last added data, we have to leave at least one set of data.
        if (chartData.size() > 1 && xAxisLabels.size() > 1) {
            chartData.remove(chartData.size() - 1);
            xAxisLabels.remove(xAxisLabels.size() - 1);
            renderOnSubmit = true;
        }

    }

    /**
     * Remvoe new data to implementing classes data model.
     */
    public void resetData() {
        init();
    }

    /**
     * Gets data assosiated with this type of chart. For Axial type outputChart
     * the dat should be return as an array.
     *
     * @return return the chart data associated with this data model.
     */
    public ArrayList getChartData() {
        return chartData;
    }

}
