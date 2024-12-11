package com.mocker;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class RandomValueReader {
    public static void main(String[] args) throws IOException {
        // Read the YAML file
    	//ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        ObjectMapper mapper = new ObjectMapper();
        Values values = mapper.readValue(new File("values.yml"), Values.class);

        // Create a random number generator
        Random random = new Random();

        // Read random values from the YAML file
        String monitorType = values.getMonitorTypes().get(random.nextInt(values.getMonitorTypes().size()));
        String type = values.getTypes().get(random.nextInt(values.getTypes().size()));
        int locationCheck = Integer.parseInt(values.getLocationChecks().get(random.nextInt(values.getLocationChecks().size())));
        boolean hiddenMonitor = Boolean.parseBoolean(values.getHiddenMonitors().get(random.nextInt(values.getHiddenMonitors().size())));
        boolean freeMonitor = Boolean.parseBoolean(values.getFreeMonitors().get(random.nextInt(values.getFreeMonitors().size())));

        // Print the random values
        System.out.println("Monitor Type: " + monitorType);
        System.out.println("Type: " + type);
        System.out.println("Location Check: " + locationCheck);
        System.out.println("Hidden Monitor: " + hiddenMonitor);
        System.out.println("Free Monitor: " + freeMonitor);
    }
}

class Values {
    private List<String> monitorTypes;
    private List<String> types;
    private List<String> locationChecks;
    private List<String> hiddenMonitors;
    private List<String> freeMonitors;

    public List<String> getMonitorTypes() {
        return monitorTypes;
    }

    public void setMonitorTypes(List<String> monitorTypes) {
        this.monitorTypes = monitorTypes;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public List<String> getLocationChecks() {
        return locationChecks;
    }

    public void setLocationChecks(List<String> locationChecks) {
        this.locationChecks = locationChecks;
    }

    public List<String> getHiddenMonitors() {
        return hiddenMonitors;
    }

    public void setHiddenMonitors(List<String> hiddenMonitors) {
        this.hiddenMonitors = hiddenMonitors;
    }

    public List<String> getFreeMonitors() {
        return freeMonitors;
    }

    public void setFreeMonitors(List<String> freeMonitors) {
        this.freeMonitors = freeMonitors;
    }
}