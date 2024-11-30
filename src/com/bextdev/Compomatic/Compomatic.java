package com.bextdev.Compomatic;

import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.Component;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.util.YailList;
import com.google.appinventor.components.runtime.EventDispatcher;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import gnu.lists.LList;
import gnu.mapping.Environment;
import gnu.mapping.Location;
import gnu.mapping.LocationEnumeration;
import gnu.mapping.SimpleSymbol;

public class Compomatic extends AndroidNonvisibleComponent {

  public Compomatic(ComponentContainer container) {
    super(container.$form());
  }

  @SimpleFunction
  public void GetListOfComponents() {
    try {
      Map<String, Component> componentsMap;
      // Using the appropriate form handling logic
      if (form.getClass().getSimpleName().equals("ReplForm")) {
        componentsMap = mapComponentsRepl();
      } else {
        componentsMap = mapComponents();
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

  private Map<String, Component> mapComponents() throws NoSuchFieldException, IllegalAccessException {
    Map<String, Component> componentsMap = new HashMap<>();
    Field componentsField = form.getClass().getField("components$Mnto$Mncreate");
    LList listComponents = (LList) componentsField.get(form);
    for (Object component : listComponents) {
      LList lList = (LList) component;
      SimpleSymbol symbol = (SimpleSymbol) lList.get(2);
      String componentName = symbol.getName();
      Object value = form.getClass().getField(componentName).get(form);
      if (value instanceof Component) {
        componentsMap.put(componentName, (Component) value);
      }
    }
    return componentsMap;
  }

  private Map<String, Component> mapComponentsRepl() throws NoSuchFieldException, IllegalAccessException {
    Map<String, Component> componentsMap = new HashMap<>();
    Field field = form.getClass().getField("form$Mnenvironment");
    Environment environment = (Environment) field.get(form);
    LocationEnumeration locationEnumeration = environment.enumerateAllLocations();
    while (locationEnumeration.hasMoreElements()) {
      Location location = locationEnumeration.next();
      String componentName = location.getKeySymbol().getName();
      Object value = location.getValue();
      if (value instanceof Component) {
        componentsMap.put(componentName, (Component) value);
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
    EventDispatcher.dispatchEvent(this, "OnComponentsConverted", componentsAsString);
  }
}