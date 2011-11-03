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

package com.icesoft.faces.component.outputchart;

import com.icesoft.faces.context.ResourceRegistry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.krysalis.jcharts.Chart;
import org.krysalis.jcharts.encoders.JPEGEncoder;
import org.krysalis.jcharts.imageMap.ImageMap;
import org.krysalis.jcharts.imageMap.ImageMapArea;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.properties.PointChartProperties;
import org.krysalis.jcharts.test.TestDataGenerator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractChart {
    private final Log log = LogFactory.getLog(AbstractChart.class);
    protected Chart chart = null;
    private Chart userDefinedChart = null;
    private static ColorMap colorMap = new ColorMap();
    private static ShapeMap shapeMap = new ShapeMap();
    private static LegendPlacementMap legendPlacementMap = new LegendPlacementMap();
    private ImageMapArea clickedImageMapArea;

    public AbstractChart(UIComponent uiComponent) throws Throwable {
    }

    public void encode(FacesContext context, OutputChart outputChart) throws Throwable {
        //if type is dynamic here we should update it
        String type = outputChart.getType();
        Chart currentChart = getChart();
        if (chart == currentChart) {
            buildChart(outputChart);
        }
        if (getChart() != null) {
            if (outputChart.isClientSideImageMap()) {
                generateMap(getChart());
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            JPEGEncoder.encode(getChart(), 1.0f, bos);
            byte[] data = bos.toByteArray();
            bos.flush();
            bos.close();
            bos = null;
            outputChart.setChartResource(new ChartResource(data));
            outputChart.setChartURI(((ResourceRegistry) context).registerResource(outputChart.getChartResource()));
        } else {
            log.equals("The jchart is not defined for the " +
                    outputChart.getClientId(FacesContext.getCurrentInstance()) +
                    ", please check if the proper [type] has been defined");
        }
    }

    private Map generatedImageMapArea = new HashMap();

    private void generateMap(Chart chart) throws Throwable {
        chart.renderWithImageMap();
        generatedImageMapArea.clear();
        ImageMap imageMap = chart.getImageMap();
        Iterator iterator = imageMap.getIterator();
        while (iterator.hasNext()) {
            ImageMapArea mapArea = (ImageMapArea) iterator.next();
            generatedImageMapArea.put(mapArea.hashCode() + "", mapArea);
        }
    }

    protected abstract void buildChart(OutputChart outputChart) throws Throwable;

    static AbstractChart createChart(UIComponent uiComponent) throws Throwable {
        String type = ((OutputChart) uiComponent).getType();
        if (OutputChart.PIE2D_CHART_TYPE.equalsIgnoreCase(type) ||
                OutputChart.PIE3D_CHART_TYPE.equalsIgnoreCase(type)) {
            return new PieChart(uiComponent);
        } else {
            return new AxisChart(uiComponent);
        }
    }

    public Chart getChart() {
        if (userDefinedChart != null) {
            return userDefinedChart;
        }
        return chart;
    }

    public void setChart(Chart userDefinedChart) {
        this.userDefinedChart = userDefinedChart;
    }

    public String[] getAsStringArray(Object obj) {
        if (obj instanceof String[]) {
            return (String[]) obj;
        } else if (obj instanceof String) {
            return ((String) obj).split(",");
        } else if (obj instanceof List) {
            return (String[]) ((List) obj).toArray(new String[0]);
        } else {
            return null;
        }
    }


    public double[][] getAs2dDoubleArray(Object obj) {
        double[][] dbl2DArray = null;
        if (obj instanceof double[][]) {
            dbl2DArray = (double[][]) obj;
        } else if (obj instanceof String) {
            String[] temp = ((String) obj).split(":");
            dbl2DArray = new double[temp.length][];
            for (int i = 0; i < temp.length; i++) {
                dbl2DArray[i] = getAsDoubleArray(temp[i]);
            }
        } else if (obj instanceof List) {
            List list = (List) obj;
            double[] outer = (double[]) list.get(0);
            dbl2DArray = new double[outer.length][list.size()];
            for (int j = 0; j < list.size(); j++) {
                for (int i = 0; i < outer.length; i++) {
                    outer = (double[]) list.get(j);
                    dbl2DArray[i][j] = outer[i];
                }
            }
        }
        return dbl2DArray;
    }


    public double[] getAsDoubleArray(Object obj) {
        double[] dblArray = null;
        if (obj instanceof String) {
            String[] temp = ((String) obj).split(",");
            dblArray = new double[temp.length];
            for (int i = 0; i < temp.length; i++) {
                dblArray[i] = Double.parseDouble(temp[i]);
            }
        } else if (obj instanceof List) {
            List objList = (List) obj;
            dblArray = new double[objList.size()];
            for (int i = 0; i < objList.size(); i++) {
                dblArray[i] = ((Double) objList.get(i)).doubleValue();
            }
        } else if (obj instanceof double[]) {
            dblArray = (double[]) obj;
        }
        return dblArray;
    }

    Paint[] paintArray = null;

    public Paint[] getAsPaintArray(Object obj) {
        if (obj instanceof String) {
            if (paintArray != null) {
                return paintArray;
            }
            String[] temp = ((String) obj).split(",");
            paintArray = new Paint[temp.length];
            for (int i = 0; i < temp.length; i++) {
                paintArray[i] = colorMap.getColor(temp[i].trim());
            }
        } else if (obj instanceof List) {
            List objList = (List) obj;
            paintArray = new Paint[objList.size()];
            for (int i = 0; i < objList.size(); i++) {
                paintArray[i] = (Paint) objList.get(i);
            }
        } else if (obj instanceof String[]) {
            String[] colors = (String[]) obj;
            paintArray = new Paint[colors.length];
            for (int i = 0; i < colors.length; i++) {
                paintArray[i] = colorMap.getColor(colors[i]);
            }
        }
        return paintArray;
    }


    public Shape[] getAsShapeArray(Object obj) {
        Shape[] shapeArray = null;
        if (obj instanceof String) {
            String[] temp = ((String) obj).split(",");
            shapeArray = new Shape[temp.length];
            for (int i = 0; i < temp.length; i++) {
                shapeArray[i] = shapeMap.getShape(temp[i].trim());
            }
        } else if (obj instanceof List) {
            List objList = (List) obj;
            shapeArray = new Shape[objList.size()];
            for (int i = 0; i < objList.size(); i++) {
                shapeArray[i] = (Shape) objList.get(i);
            }
        }
        return shapeArray;
    }

    Shape[] getGeneratedShapes(int count) {
        Shape[] tempShapeArray = new Shape[count];
        Iterator it = shapeMap.values().iterator();
        for (int i = 0; i < count; i++) {
            if (it.hasNext()) {
                tempShapeArray[i] = (Shape) it.next();
            } else {
                it = shapeMap.values().iterator();
                tempShapeArray[i] = (Shape) it.next();
            }
        }
        return tempShapeArray;
    }

    String[] getGeneratedLabels(String label, int count) {
        String[] tempStringArray = new String[count];
        for (int i = 0; i < count; i++) {
            tempStringArray[i] = label + " " + i;
        }
        return tempStringArray;
    }

    public Paint[] getPaints(Object obj, int count) {
        if (obj == null && paintArray == null) {
            return paintArray = TestDataGenerator.getRandomPaints(count);
        } else if (obj == null && paintArray != null) {
            return paintArray;
        } else {
            if(paintArray != null && paintArray.length != count) {
                //data has dynamically changed, so let it to be reevaluated
                paintArray = null;
            }
            return getAsPaintArray(obj);
        }
    }

    public Map getGeneratedImageMapArea() {
        return generatedImageMapArea;
    }

    public ImageMapArea getClickedImageMapArea() {
        return clickedImageMapArea;
    }

    public void setClickedImageMapArea(ImageMapArea clickedImageMapArea) {
        this.clickedImageMapArea = clickedImageMapArea;
    }

    public static Color getColor(String color) {
        return colorMap.getColor(color);
    }

    public LegendProperties getLegendProperties(OutputChart outputChart) {
        String legendPlacement = (String) outputChart.getLegendPlacement();
        if (legendPlacement.equals("none")) {
            return null;
        }
        LegendProperties legendProperties = new LegendProperties();
        legendProperties.setPlacement(legendPlacementMap.getLegendPlacement(legendPlacement));
        Object legendColumns = outputChart.getLegendColumns();
        if (legendColumns instanceof Integer) {
            legendProperties.setNumColumns(((Integer) outputChart.getLegendColumns()).intValue());
        } else if (legendColumns instanceof String) {
            legendProperties.setNumColumns(Integer.parseInt(outputChart.getLegendColumns().toString()));
        }
        return legendProperties;
    }
}

class ColorMap extends HashMap {
    private static final long serialVersionUID = 1L;

    public ColorMap() {
        this.put("black", Color.BLACK);
        this.put("blue", Color.BLUE);
        this.put("cyan", Color.CYAN);
        this.put("darkGray", Color.DARK_GRAY);
        this.put("gray", Color.GRAY);
        this.put("green", Color.GREEN);
        this.put("lightGray", Color.LIGHT_GRAY);
        this.put("magenta", Color.MAGENTA);
        this.put("orange", Color.ORANGE);
        this.put("pink", Color.PINK);
        this.put("red", Color.RED);
        this.put("white", Color.WHITE);
        this.put("yellow", Color.YELLOW);
    }

    public Color getColor(String key) {
        return (Color) super.get(key);
    }
}

class ShapeMap extends HashMap {

    private static final long serialVersionUID = 1L;

    public ShapeMap() {
        this.put("circle", PointChartProperties.SHAPE_CIRCLE);
        this.put("diamond", PointChartProperties.SHAPE_DIAMOND);
        this.put("square", PointChartProperties.SHAPE_SQUARE);
        this.put("triangle", PointChartProperties.SHAPE_TRIANGLE);
    }

    public Shape getShape(String key) {
        return (Shape) super.get(key);
    }
}

class LegendPlacementMap extends HashMap {
    public LegendPlacementMap() {
        this.put("top", new Integer(LegendProperties.TOP));
        this.put("bottom", new Integer(LegendProperties.BOTTOM));
        this.put("left", new Integer(LegendProperties.LEFT));
        this.put("right", new Integer(LegendProperties.RIGHT));
    }

    public int getLegendPlacement(String key) {
        if (!super.containsKey(key)) {
            return 0;
        }
        return Integer.parseInt(super.get(key).toString());
    }
}