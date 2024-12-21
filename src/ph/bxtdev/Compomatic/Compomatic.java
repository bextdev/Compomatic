package ph.bxtdev.Compomatic;

import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import com.google.appinventor.components.annotations.SimpleEvent;
import gnu.lists.LList;
import gnu.mapping.Environment;
import gnu.mapping.Location;
import gnu.mapping.LocationEnumeration;
import gnu.mapping.SimpleSymbol;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.YailList;
import com.google.appinventor.components.runtime.EventDispatcher;

public class Compomatic extends AndroidNonvisibleComponent {
    private Map<String, Component> componentsMap;

    public Compomatic(ComponentContainer container) {
        super(container.$form());
        componentsMap = new HashMap<>(); // Initialize the map
    }

    @SimpleFunction
    public void GetListOfComponentsType() {
        try {
            // Using the appropriate form handling logic
            if (form.getClass().getSimpleName().equals("ReplForm")) {
                componentsMap = mapComponentsTypeRepl();
            } else {
                componentsMap = mapComponentsType();
            }

            // Convert the map into a YailList
            YailList yailList = convertMapToYailList(componentsMap);

            // Convert the YailList to a String
            String componentsAsString = yailList.toString();

            // Trigger the event with the list as a string
            OnComponentsConvertedToString(componentsAsString);

        } catch (Exception e) {
            throw new YailRuntimeError(e.getMessage(), "Error");
        }
    }

    @SimpleFunction
    public void GetListOfComponentsName() {
        try {
            // Using the appropriate form handling logic
            if (form.getClass().getSimpleName().equals("ReplForm")) {
                componentsMap = mapComponentsNameRepl();
            } else {
                componentsMap = mapComponentsName();
            }

            // Convert the map into a YailList
            YailList yailList = convertMapToYailList(componentsMap);

            // Convert the YailList to a String
            String componentsAsString = yailList.toString();

            // Trigger the event with the list as a string
            OnComponentsConvertedToString(componentsAsString);

        } catch (Exception e) {
            throw new YailRuntimeError(e.getMessage(), "Error");
        }
    }

    private Map<String, Component> mapComponentsName() throws NoSuchFieldException, IllegalAccessException {
        componentsMap = new HashMap<>();
        Field componentsField = form.getClass().getField("components$Mnto$Mncreate");
        LList listComponents = (LList) componentsField.get(form);
        for (Object component : listComponents) {
            LList lList = (LList) component;
            SimpleSymbol symbol = (SimpleSymbol) lList.get(2);
            String componentName = symbol.getName();
            Object value = form.getClass().getField(componentName).get(form);
            if (value instanceof Component) {
                componentsMap.put(componentName, (Component) value); // Add key-value pair
            }
        }
        return componentsMap;
    }

    private Map<String, Component> mapComponentsNameRepl() throws NoSuchFieldException, IllegalAccessException {
        componentsMap = new HashMap<>();
        Field field = form.getClass().getField("form$Mnenvironment");
        Environment environment = (Environment) field.get(form);
        LocationEnumeration locationEnumeration = environment.enumerateAllLocations();
        while (locationEnumeration.hasMoreElements()) {
            Location location = locationEnumeration.next();
            String componentName = location.getKeySymbol().getName();
            Object value = location.getValue();
            if (value instanceof Component) {
                componentsMap.put(componentName, (Component) value); // Add key-value pair
            }
        }
        return componentsMap;
    }

    private Map<String, Component> mapComponentsTypeRepl() throws NoSuchFieldException, IllegalAccessException {
        componentsMap = new HashMap<>();
        Field componentsField = form.getClass().getField("components$Mnto$Mncreate");
        LList listComponents = (LList) componentsField.get(form);
        for (Object component : listComponents) {
            LList lList = (LList) component;
            SimpleSymbol symbol = (SimpleSymbol) lList.get(2);
            String componentName = symbol.getName();
            Object value = form.getClass().getField(componentName).get(form);
            if (value instanceof Component) {
                componentsMap.put(componentName, (Component) value); // Add key-value pair
            }
        }
        return componentsMap;
    }

    private Map<String, Component> mapComponentsType() throws NoSuchFieldException, IllegalAccessException {
        componentsMap = new HashMap<>();
        Field field = form.getClass().getField("form$Mnenvironment");
        Environment environment = (Environment) field.get(form);
        LocationEnumeration locationEnumeration = environment.enumerateAllLocations();
        while (locationEnumeration.hasMoreElements()) {
            Location location = locationEnumeration.next();
            String componentName = location.getKeySymbol().getName();
            Object value = location.getValue();
            if (value instanceof Component) {
                componentsMap.put(componentName, (Component) value); // Add key-value pair
            }
        }
        return componentsMap;
    }

    private YailList convertMapToYailList(Map<String, Component> componentsMap) {
        Object[] items = new Object[componentsMap.size()];
        int index = 0;
        for (Map.Entry<String, Component> entry : componentsMap.entrySet()) {
            // Create a sub-list with the component name and type
            items[index++] = YailList.makeList(new Object[]{
                entry.getKey(), // Component name
                entry.getValue().getClass().getSimpleName() // Component type
            });
        }
        return YailList.makeList(items);
    }

    @SimpleEvent
    public void OnComponentsConvertedToString(String componentsAsString) {
        EventDispatcher.dispatchEvent(this, "OnComponentsConvertedToString", componentsAsString);
    }
}
