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

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

/**
 * The ChartController class is responsible for handling all user interations
 * assosiated with the chart demo.
 *
 * @since 1.7
 */
public class ChartController implements Serializable {

    private static final String AREA_CHART_NAME = "Area";
    private static final String AREA_STACKED_CHART_NAME = "Area Stacked";
    private static final String BAR_CHART_NAME = "Bar";
    private static final String BAR_CLUSTERED_CHART_NAME = "Bar Clustered";
    private static final String BAR_STACKED_CHART_NAME = "Bar Stacked";
    private static final String LINE_CHART_NAME = "Line";
    private static final String POINT_CHART_NAME = "Point";
    private static final String PIE2D_CHART_NAME = "Pie 2D";
    private static final String PIE3D_CHART_NAME = "Pie 3D";
    private static final String CUSTOM_CHART_NAME = "Custom";


    // List of outputChart that the user can view in jspx page
    private static final SelectItem[] chartList = new SelectItem[]{
            new SelectItem(OutputChart.AREA_CHART_TYPE, AREA_CHART_NAME),
            new SelectItem(OutputChart.AREA_STACKED_CHART_TYPE, AREA_STACKED_CHART_NAME),
            new SelectItem(OutputChart.BAR_CHART_TYPE, BAR_CHART_NAME),
            new SelectItem(OutputChart.BAR_CLUSTERED_CHART_TYPE, BAR_CLUSTERED_CHART_NAME),
            new SelectItem(OutputChart.BAR_STACKED_CHART_TYPE, BAR_STACKED_CHART_NAME),
            new SelectItem(OutputChart.LINE_CHART_TYPE, LINE_CHART_NAME),
//            new SelectItem(OutputChart.STOCK_CHART_TYPE, "Stock"),
            new SelectItem(OutputChart.POINT_CHART_TYPE, POINT_CHART_NAME),
            new SelectItem(OutputChart.PIE2D_CHART_TYPE, PIE2D_CHART_NAME),
            new SelectItem(OutputChart.PIE3D_CHART_TYPE, PIE3D_CHART_NAME),
            new SelectItem(OutputChart.CUSTOM_CHART_TYPE, CUSTOM_CHART_NAME)
    };

    // map of available chart types, keys should be defined in the chartList
    private Map chartDataModels;

    // currently selected data model pointer
    private AbstractChartData currentChartModel;

    private String currentChartType;

    public ChartController() {
        init();
    }

    /**
     * Initialize the default models for each chart type.
     */
    private void init() {

        chartDataModels = new HashMap();

        // add default axial type outputChart
        chartDataModels.put(OutputChart.AREA_CHART_TYPE,
                new ChartModelAxial(AREA_CHART_NAME, false, false, true, true));
        chartDataModels.put(OutputChart.AREA_STACKED_CHART_TYPE,
                new ChartModelAxial(AREA_STACKED_CHART_NAME, false, false, true, true));
        chartDataModels.put(OutputChart.BAR_CHART_TYPE,
                new ChartModelAxial(BAR_CHART_NAME, true, true, true, true));
        chartDataModels.put(OutputChart.BAR_CLUSTERED_CHART_TYPE,
                new ChartModelAxial(BAR_CLUSTERED_CHART_NAME, true, true, true, true));
        chartDataModels.put(OutputChart.BAR_STACKED_CHART_TYPE,
                new ChartModelAxial(BAR_STACKED_CHART_NAME, true, false, true, true));
        chartDataModels.put(OutputChart.LINE_CHART_TYPE,
                new ChartModelAxial(LINE_CHART_NAME, true, false, true, true));
        chartDataModels.put(OutputChart.POINT_CHART_TYPE,
                new ChartModelAxial(POINT_CHART_NAME, true, false, true, true));
//        chartDataModels.put(OutputChart.STOCK_CHART_TYPE,
//                new ChartModelAxial(true, false, true, true));

        // add default pie type outputChart
        chartDataModels.put(OutputChart.PIE2D_CHART_TYPE,
                new ChartModelRadial(PIE2D_CHART_NAME, false, false, false, false));
        chartDataModels.put(OutputChart.PIE3D_CHART_TYPE,
                new ChartModelRadial(PIE3D_CHART_NAME, false, false, false, false));

        // add the only custom chart model.
        chartDataModels.put(OutputChart.CUSTOM_CHART_TYPE,
                new ChartModelCustom(true, false, false, false));

        // set the default dataModel
        currentChartType = OutputChart.CUSTOM_CHART_TYPE;
        currentChartModel = (AbstractChartData)chartDataModels.get(currentChartType);
    }

    /**
     * Chart type change listener.  Chart data model is updated when
     * a new chart type has been selected.
     *
     * @param event jsf values change event
     */
    public void chartTypeChange(ValueChangeEvent event) {
        String newChartType = (String) event.getNewValue();
        if (newChartType != null) {
            currentChartType = newChartType;
            currentChartModel = (AbstractChartData)chartDataModels.get(newChartType);
            if (currentChartModel!= null)
                currentChartModel.renderOnSubmit = true;
        }
    }

    /**
     * Removes a unit of data to the current chart data model.
     *
     * @param event jsf action event.
     */
    public void addChartData(ActionEvent event) {
        if (currentChartModel != null) {
            currentChartModel.addData();
        }
    }

    /**
     * Adds a unit of data to the current chart data model.
     *
     * @param event jsf action event.
     */
    public void removeChartData(ActionEvent event) {
        if (currentChartModel != null) {
            currentChartModel.removeData();
        }
    }

    /**
     * Resets the current chart data model.
     *
     * @param event jsf action event.
     */
    public void resetChartData(ActionEvent event) {
        if (currentChartModel != null) {
            currentChartModel.resetData();
        }
    }

    /**
     * Called if a chart type supports clickable areas.
     *
     * @param event jsf action event
     */
    public void chartAreaClicked(ActionEvent event) {

        if (event.getSource() instanceof OutputChart) {
            OutputChart chart = (OutputChart) event.getSource();
            if (chart.getClickedImageMapArea().getXAxisLabel() != null) {
                currentChartModel.setAreaMapClickValue(
                        chart.getClickedImageMapArea()
                                .getXAxisLabel() +
                                "  :  " + chart.getClickedImageMapArea()
                                .getValue());
            }

        }
        if (currentChartModel!= null)
            currentChartModel.renderOnSubmit = false;
    }

    public AbstractChartData getCurrentChartModel() {
        return currentChartModel;
    }

    public String getCurrentChartType() {
        return currentChartType;
    }

    public void setCurrentChartType(String currentChartType) {
        this.currentChartType = currentChartType;
    }

    public SelectItem[] getChartList() {
        return chartList;
    }

    public Map getChartDataModels() {
        return chartDataModels;
    }
}
