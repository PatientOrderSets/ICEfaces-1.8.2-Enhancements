/*
 *
 */
package com.icesoft.faces.metadata;

import com.icesoft.faces.mock.test.data.MockDataList;
import com.icesoft.faces.mock.test.data.MockDataObject;
import com.icesoft.faces.mock.test.data.MockMethodBinding;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author fye
 */
public class TestPropDataProvider {
    private static java.lang.reflect.Constructor SelectItem_ctor;
    static {
        try {
            SelectItem_ctor = javax.faces.model.SelectItem.class.getConstructor(new Class[] {
				Object.class, String.class, String.class,  Boolean.TYPE, Boolean.TYPE});
        }
        catch(Exception e) { // JSF 1.1
        }
    }

    private final static String[] keyStrings = new String[]{
        "com.icesoft.faces.mock.test.data.MockDataObject",
        "java.util.List",
        "java.lang.Double",
        "java.lang.String",
        "boolean",
        "com.icesoft.faces.context.effects.Effect",
        "java.lang.Object",
        "javax.faces.el.MethodBinding",
        "int",
        "java.lang.Integer",
        "java.util.Date",
        "java.io.File",
        "java.util.Map",
        "javax.faces.convert.Converter",
        "java.lang.Boolean",
        "javax.faces.component.UIComponent",
        "com.icesoft.faces.utils.UpdatableProperty",
        "com.icesoft.faces.component.dataexporter.OutputTypeHandler",
        "com.icesoft.faces.context.Resource"
    };
    private final static Object[] valueObjects = new Object[]{
        new MockDataObject("empty"),
        new MockDataList(),
        1.009d,
        "test1",
        false,
        new com.icesoft.faces.context.effects.Move(),
        new MockDataObject("objectValue"),
        new MockMethodBinding("#{mock.methodBinding}"),
        9,
        new Integer(99),
        new java.util.Date(System.currentTimeMillis()),
        new java.io.File("com/icesoft/faces/mock/test"),
        new java.util.HashMap(),
        new javax.faces.convert.DateTimeConverter(),
        new Boolean(false),
        new com.icesoft.faces.component.ext.HtmlInputText(),
        new com.icesoft.faces.utils.UpdatableProperty("test"),
        new com.icesoft.faces.mock.test.container.MockOutputTypeHandler("test"),
        new com.icesoft.faces.mock.test.container.MockResource()
    };
    private static HashMap<String, Object> propMap = new HashMap<String, Object>(keyStrings.length);


    static {
        for (int i = 0; i < keyStrings.length; i++) {
            propMap.put(keyStrings[i], valueObjects[i]);
        }
    }

    public Object getSimpleTestObject(String key) {
        return propMap.get(key);
    }

    public int getMatchClass(String matchName) {
//            "java.util.List", //1
//            "java.lang.Object", //2
//            "java.lang.String", //3
//            "boolean", //4
//            "com.icesoft.faces.context.effects.Effect", //5
//            "java.lang.Object", //6
//            "javax.faces.el.MethodBinding",//7
//            "int",//8
//            "java.lang.Integer",//9
//            "java.util.Date", //10
//            "java.io.File", //11
//            "java.util.Map", //12
//            "javax.faces.convert.Converter", //13
//            "java.lang.Boolean", //14
//            "javax.faces.component.UIComponent", //15
//            "com.icesoft.faces.utils.UpdatableProperty" //16

        for (int i = 0; i < keyStrings.length; i++) {
            if (matchName.equals(keyStrings[i])) {
                return i;
            }
        }
        return -1;
    }

    public static Object getSimpleTestObject(Class clazz, boolean bVal)
        throws InstantiationException, IllegalAccessException {

        if (clazz.isPrimitive()) {
            return getPrimitiveTestObject(clazz, bVal);
        }
        if (clazz.isArray()) {
            final int len = 13;
            Class elementClass = clazz.getComponentType();
            Object element = getSimpleTestObject(elementClass, bVal);
            Object array = Array.newInstance(elementClass, len);
            for(int i = 0; i < len; i++) {
                Array.set(array, i, element);
            }
            return array;
        }
        if (String.class.equals(clazz)) {
            // Portlet.namespace requires String that's valid id
            return "String" + counter++;
        }
        if (Object.class.equals(clazz)) {
            return "Object" + counter++;
        }
        if (javax.faces.el.MethodBinding.class.equals(clazz)) {
            return new MockMethodBinding("#{mock.methodBinding}");
        }
        if (javax.faces.convert.Converter.class.equals(clazz)) {
            javax.faces.convert.NumberConverter converter =
                new javax.faces.convert.NumberConverter();
            converter.setMaxIntegerDigits(counter++);
        }
        if (com.icesoft.faces.utils.UpdatableProperty.class.equals(clazz)) {
            return new com.icesoft.faces.utils.UpdatableProperty("Test" + counter++);
        }
        if (com.icesoft.faces.component.inputfile.FileInfo.class.equals(clazz)) {
			com.icesoft.faces.component.inputfile.FileInfo fileInfo =
				new com.icesoft.faces.component.inputfile.FileInfo();
			fileInfo.setStatus(counter++);
			fileInfo.setSize((long)counter++);
			fileInfo.setFileName("FileName" + counter++);
			fileInfo.setContentType("ContentType" + counter++);
			fileInfo.setFile( new java.io.File("File" + counter++) );
			fileInfo.setPercent(counter++);
			fileInfo.setPreUpload(bVal);
			fileInfo.setPostUpload(bVal);
            return fileInfo;
        }
        if (java.io.File.class.equals(clazz)) {
            return new java.io.File("File" + counter++);
        }
        if (java.net.URI.class.equals(clazz)) {
            java.net.URI uri = null;
            try {
                uri = new java.net.URI("http://host/uri" + counter++);
            }
            catch(Exception e) {
                throw new InstantiationException("URI is invalid: " + e.getMessage());
            }
            return uri;
        }
        if (com.icesoft.faces.context.effects.Effect.class.equals(clazz)) {
            com.icesoft.faces.context.effects.Pulsate effect =
                new com.icesoft.faces.context.effects.Pulsate();
            effect.setDuration((float) (counter++ % 10));
            return effect;
        }
        if (com.icesoft.faces.context.effects.CurrentStyle.class.equals(clazz)) {
            com.icesoft.faces.context.effects.CurrentStyle cs =
                new com.icesoft.faces.context.effects.CurrentStyle("iceCssStyleClass" + counter++);
            return cs;
        }
        if (com.icesoft.faces.component.outputchart.ChartResource.class.equals(clazz)) {
            com.icesoft.faces.component.outputchart.ChartResource cr =
                new com.icesoft.faces.component.outputchart.ChartResource(new byte[] {0, 1, 0, 1});
            return cr;
        }
        if (com.icesoft.faces.context.Resource.class.equals(clazz)) {
            com.icesoft.faces.context.Resource cr =
                new com.icesoft.faces.context.ByteArrayResource(
                    new byte[] {0, 1, 2, 4});
            return cr;
        }
        //com.icesoft.faces.component.outputchart.ChartResource
        if (javax.faces.component.UIComponent.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("Invalid to save/restore UIComponent");
        }
        if (List.class.equals(clazz)) {
            List list = new ArrayList();
            list.add("List" + counter++);
            return list;
        }
        if (java.util.Map.class.equals(clazz)) {
            java.util.Map map = new HashMap();
            map.put("Map", new Integer(counter++));
            return map;
        }
        if (java.util.Date.class.equals(clazz)) {
			return new java.util.Date((long)counter++);
		}
		if (javax.faces.model.SelectItem.class.equals(clazz)) {
			if (SelectItem_ctor != null) {
				try {
					return SelectItem_ctor.newInstance(new Object[] {
						"Value" + counter++,
						"Label" + counter++,
						"Description" + counter++,
						Boolean.valueOf(bVal),
						Boolean.valueOf(bVal)});
				}
				catch(java.lang.reflect.InvocationTargetException e) {
					throw new InstantiationException("Could not invoke JSF 1.2 constructor for SelectItem: " + e.getMessage());
				}
			}
			else {
				return new javax.faces.model.SelectItem(
					"Value" + counter++,
					"Label" + counter++,
					"Description" + counter++,
					bVal);
			}
		}
        if (Number.class.isAssignableFrom(clazz) || Boolean.class.equals(clazz)) {
            return getPrimitiveTestObject(clazz, bVal);
        }
        throw new InstantiationException("Default constructor for: " + clazz.getName());
        // If we fall back on the default constructor, we end up not catching all data types
        // return clazz.newInstance();
    }

    private static Object getPrimitiveTestObject(Class clazz, boolean bVal) {
//System.out.println("getPrimitiveTestObject()  clazz: " + clazz);
        if (clazz.equals(Boolean.TYPE) || clazz.equals(Boolean.class)) {
            return Boolean.valueOf(bVal);
        }
        else if (clazz.equals(Byte.TYPE) || clazz.equals(Byte.class)) {
            return Byte.valueOf( (byte) (0xFF & counter++) );
        }
        else if (clazz.equals(Character.TYPE) || clazz.equals(Character.class)) {
            return Character.valueOf( (char) (0xFFFF & counter++) );
        }
        else if (clazz.equals(Double.TYPE) || clazz.equals(Double.class)) {
            return Double.valueOf( ((double)counter++) / 3.0d );
        }
        else if (clazz.equals(Float.TYPE) || clazz.equals(Float.class)) {
            return Float.valueOf( ((float)counter++) / 5.0f );
        }
        else if (clazz.equals(Integer.TYPE) || clazz.equals(Integer.class)) {
            return Integer.valueOf(counter++);
        }
        else if (clazz.equals(Long.TYPE) || clazz.equals(Long.class)) {
            return Long.valueOf((long)counter++);
        }
        else if (clazz.equals(Short.TYPE) || clazz.equals(Short.class)) {
            return Short.valueOf( (short) (0xFFFF & counter++) );
        }
        return new Integer(counter++);
    }

    private static int counter = 23; // a prime example of a non-default value :)
}
