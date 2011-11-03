package org.icefaces.application.showcase.view.bean.examples.component.outputChart;
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

import com.icesoft.faces.component.outputchart.OutputChart;

import java.util.ArrayList;

/**
 * <p>The AbstractChartData class stores common attributes for chart data
 * set and has abstract method definitions to allow for different data set
 * implementats, mainly axial data vs. pie data. </p>
 * <p>If a new type of data is to be displayed in a chart consider implementing
 * this class to make life a little easier.  Remember data, color and label
 * arrays must have matching lengths or the OutputChart component will fail
 * to render.</p>
 *
 * @see org.icefaces.application.showcase.view.bean.examples.component.outputChart.ChartModelAxial
 * @see org.icefaces.application.showcase.view.bean.examples.component.outputChart.ChartModelCustom
 * @see org.icefaces.application.showcase.view.bean.examples.component.outputChart.ChartModelCustom
 * @since 1.7
 */
public abstract class AbstractChartData {

    // axial chart data can be displayed horizontally or vertically
    public static final boolean DATA_ORIENTATION_HORIZONTAL = true;
    public static final boolean DATA_ORIENTATION_VERTICAL = false;

    // chart types.
    public static final int AXIAL_CHART_TYPE = 0;
    public static final int RADIAL_CHART_TYPE = 1;
    public static final int CUSTOM_CHART_TYPE = 2;

    // render on submit flag
    protected boolean renderOnSubmit;

    // chart type support area mapping
    protected boolean areaMapEnabled;
    // chart type supports orientation 
    protected boolean axisOrientationEnabled;
    // chart legend placement
    protected boolean enabledLengendPosition;

    protected String areaMapClickValue;

    // bar chart axis orientation,
    protected boolean barChartOrientationHorizontal;

    // enable/disable legend controls.
    protected boolean enableLegend;

    // enable/disable dynamic add/remove/reset buttons.
    protected boolean enableDynamicValues;

    // legend lables
    protected ArrayList legendLabels;

    // title displayed at top of chart
    protected String chartTitle;

    // x Axis title
    protected String xAxisTitle;
    // y Axis title
    protected String yAxisTitle;

    // labels assosiated with x axis
    protected ArrayList xAxisLabels;

    protected ArrayList colors;

    // placement of legend
    protected String legendPlacement;

    // shapes of point plotted on chart
    protected String plotPointShape;

    /**
     * Add new data to implementing classes data model.
     */
    public abstract void addData();

    /**
     * Remvoe new data to implementing classes data model.
     */
    public abstract void removeData();

    /**
     * Reset/restore data model to defaults.
     */
    public abstract void resetData();

    /**
     * Gets data assosiated with this type of chart.
     *
     * @return chart data.
     */
    public abstract ArrayList getChartData();

    /**
     * Gets chart type.
     *
     * @return chart type.
     */
    public abstract int getChartType();

    /**
     * Method to tell the page to render or not based on the initialized flag
     *
     * @param component chart component which will be rendered.
     * @return boolean true if OutputChart should be re-rendered; otherwise,
     *         false.
     */
    public boolean renderOnSubmit(OutputChart component){
        boolean renderOnSubmit = this.renderOnSubmit;

        // reset render on submit for next request/response
        if (renderOnSubmit){
            this.renderOnSubmit = false;
        }
        
        return renderOnSubmit;
    }

    /**
     * Indicates that current chart instance supports image map interaction
     *
     * @return true if the chart types upports images maps, otherwise false.
     */
    public boolean getAreaMapEnabled() {
        return areaMapEnabled;
    }

    /**
     * Indicates that the map types upports axis orientation changing.
     *
     * @return true if the chart types upports axis orientation, otherwise false.
     */
    public boolean getAxisOrientationEnabled() {
        return axisOrientationEnabled;
    }

    public String getAreaMapClickValue() {
        return areaMapClickValue;
    }

    public void setAreaMapClickValue(String areaMapClickValue) {
        this.areaMapClickValue = areaMapClickValue;
    }

    public boolean isEnableLegend() {
        return enableLegend;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public String getXAxisTitle() {
        return xAxisTitle;
    }

    public void setXAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
    }

    public String getYAxisTitle() {
        return yAxisTitle;
    }

    public void setYAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
    }

    public ArrayList getXAxisLabels() {
        return xAxisLabels;
    }

    public void setXAxisLabels(ArrayList xAxisLabels) {
        this.xAxisLabels = xAxisLabels;
    }

    public String getLegendPlacement() {
        return legendPlacement;
    }

    public void setLegendPlacement(String legendPlacement) {
        this.legendPlacement = legendPlacement;
        renderOnSubmit = true;
    }

    public String getPlotPointShape() {
        return plotPointShape;
    }

    public void setPlotPointShape(String plotPointShape) {
        this.plotPointShape = plotPointShape;
    }

    public ArrayList getColors() {
        return colors;
    }

    public void setColors(ArrayList colors) {
        this.colors = colors;
    }

    public boolean isBarChartOrientationHorizontal() {
        return barChartOrientationHorizontal;
    }

    public void setBarChartOrientationHorizontal(boolean barChartOrientationHorizontal) {
        this.barChartOrientationHorizontal = barChartOrientationHorizontal;
        renderOnSubmit = true;
    }

    public ArrayList getLegendLabels() {
        return legendLabels;
    }

    public boolean isAreaMapEnabled() {
        return areaMapEnabled;
    }

    public boolean isAxisOrientationEnabled() {
        return axisOrientationEnabled;
    }

    public boolean isEnableDynamicValues() {
        return enableDynamicValues;
    }

    public boolean isEnabledLengendPosition() {
        return enabledLengendPosition;
    }

    public void setEnabledLengendPosition(boolean enabledLengendPosition) {
        this.enabledLengendPosition = enabledLengendPosition;
    }
}
