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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.ChartDataException;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.properties.*;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;
import org.krysalis.jcharts.properties.util.ChartFont;
import java.awt.*;
import java.util.ArrayList;

/**
 * <p>The ChartModelCustom class purpose is to show how the outputChart component
 * can be used to display custom data.  This demo does not support dynamic
 * data but in theory it is possible.  The init method is responsible for
 * building the custom combined chart and it is bound to the component in
 * the  #renderOnSubmit method. </p>
 *
 * @see org.icefaces.application.showcase.view.bean.examples.component.outputChart.AbstractChartData
 * @since 1.7
 */
public class ChartModelCustom extends AbstractChartData {

    public static final Log log = LogFactory.getLog(ChartModelCustom.class);

    private static AxisChart axisChart;
    
    private ChartFont axisFont = new ChartFont(new Font( "Sans-Serif", Font.PLAIN, 12 ), Color.BLACK);
    private ChartFont titleFont = new ChartFont(new Font( "Sans-Serif", Font.BOLD, 12 ), Color.BLACK);
    private ChartFont legendFont = new ChartFont(new Font( "Sans-Serif", Font.ITALIC, 10 ), Color.BLACK);

    public ChartModelCustom(boolean areaMapEnabled,
                            boolean axisOrientationEnabled,
                            boolean enableDynamicValues,
                            boolean enabledLengendPosition) {
        this.areaMapEnabled = areaMapEnabled;
        this.axisOrientationEnabled = axisOrientationEnabled;
        this.enableDynamicValues = enableDynamicValues;
        this.enabledLengendPosition = enabledLengendPosition;
        init();

    }

    public int getChartType(){
        return CUSTOM_CHART_TYPE;
    }

    /**
     * Initialze the default chart data. It is very important that the first
     * dimension of the arrays are equal in length.
     */
    private void init() {

        String[] xAxisLabels;
        String xAxisTitle;
        String yAxisTitle;
        String[] legendLabels;

        try {
            xAxisLabels = new String[]{"1998", "1999", "2000", "2001", "2002", "2003", "2004"};
            xAxisTitle = "Years";
            yAxisTitle = "Problems";
            String title = "Company Software";

            DataSeries dataSeries =
                    new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);

            double[][] data = TestDataGenerator.getRandomNumbers(3, 7, 0, 5000);
            legendLabels = new String[]{"Bugs", "Enhancements", "Fixes"};
            Paint[] paints = new Color[]{
                    new Color(0xCAE1EF),
                    new Color(0xF78208),
                    new Color(0x0D4274)};

            BarChartProperties barChartProperties = new BarChartProperties();
            AxisChartDataSet axisChartDataSet =
                    new AxisChartDataSet(data,
                            legendLabels,
                            paints,
                            ChartType.BAR,
                            barChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);


            data = TestDataGenerator.getRandomNumbers(2, 7, 1000, 5000);
            paints = new Paint[]{new Color(0x0D4274),
                    new Color(0xF78208)};

            Stroke[] strokes = {LineChartProperties.DEFAULT_LINE_STROKE,
                    LineChartProperties.DEFAULT_LINE_STROKE};
            Shape[] shapes = {PointChartProperties.SHAPE_CIRCLE,
                    PointChartProperties.SHAPE_TRIANGLE};
            LineChartProperties lineChartProperties =
                    new LineChartProperties(strokes, shapes);

            axisChartDataSet = new AxisChartDataSet(data, null, paints,
                    ChartType.LINE,
                    lineChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);

            ChartProperties chartProperties = new ChartProperties();
            AxisProperties axisProperties = new AxisProperties();
            LegendProperties legendProperties = new LegendProperties();

            // Set the font to a custom sans serif
            chartProperties.setTitleFont(titleFont);
            axisProperties.getYAxisProperties().setAxisTitleChartFont(axisFont);
            axisProperties.getXAxisProperties().setAxisTitleChartFont(axisFont);
            legendProperties.setChartFont(legendFont);

            axisChart = new AxisChart(dataSeries, chartProperties,
                    axisProperties,
                    legendProperties, 450, 300);
        } catch (ChartDataException e) {
            log.error("Error building custom outputChart data:", e);
        }

    }

    /**
     * Method to tell the page to render or not based on the initialized flag
     *
     * @param component chart component which will be rendered.
     * @return boolean true if OutputChart should be re-rendered; otherwise,
     *         false.
     */
    public boolean renderOnSubmit(OutputChart component) {
        // asign our custum bulid chart. 
        if (axisChart != null) {
            component.setChart(axisChart);
        }
        return renderOnSubmit;
    }

    /**
     * Add new data to implementing classes data model.
     */
    public void addData() {
        log.info("Add data has not been implemented for custom charts");
    }

    /**
     * Remvoe new data to implementing classes data model.
     */
    public void removeData() {
        log.info("Remove data has not been implemented for custom charts");
    }

    /**
     * Remvoe new data to implementing classes data model.
     */
    public void resetData() {
        log.info("Reset data has not been implemented for custom charts");
    }

    /**
     * Gets data assosiated with this type of chart. For Axial type outputChart
     * the dat should be return as an array.
     *
     * @return return the chart data associated with this data model.
     */
    public ArrayList getChartData() {
        return null;
    }


}