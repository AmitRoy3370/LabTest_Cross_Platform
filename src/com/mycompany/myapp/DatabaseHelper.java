package com.mycompany.myapp;

import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;
import com.codename1.io.Util;
import com.codename1.ui.List;
import com.codename1.util.Callback;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper {

    private static final String API_URL = "https://sassy-obtainable-veterinarian.glitch.me";

    public String addPathologicalTest(PathologicalTest labTest, Callback<String> callback) {

        StringBuilder sb = new StringBuilder();

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {

                String responseString = Util.readToString(input);

                sb.append("raw response :- ").append(responseString);

                if (callback != null) {

                    callback.onSucess(sb.toString());

                }

                //JSONParser jsonPerser = new JSONParser();
                // Map<String, Object> response = jsonPerser.parseJSON(new CharArrayReader(responseString.toCharArray()));
                //sb.append("parse response :- ").append(response);
            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {

                sb.append("Error response code :- ").append(code).append("\n");
                sb.append("message :- ").append(message);

            }

        };

        request.setPost(true);
        request.setUrl(API_URL + "/pathologicalTest");
        request.setContentType("application/json");

        String jsonInputString = "{"
                + "\"title\": \"" + labTest.getTitle() + "\","
                + "\"cost\": \"" + labTest.getCost() + "\","
                + "\"isAvaiable\": \"" + labTest.isIsAvailable() + "\","
                + "\"reagent\": \"" + labTest.getReagent() + "\""
                + "}";

        request.setRequestBody(jsonInputString);

        NetworkManager.getInstance().addToQueue(request);

        return sb.toString();

    }

    public String addRadiologicalTest(RadioLogicalTest labTest, Callback<String> callback) {

        StringBuilder sb = new StringBuilder();

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {

                String responseString = Util.readToString(input);

                sb.append("raw response :- ").append(responseString);

                if (callback != null) {

                    callback.onSucess(sb.toString());

                }

                //JSONParser jsonPerser = new JSONParser();
                //Map<String, Object> response = jsonPerser.parseJSON(new CharArrayReader(responseString.toCharArray()));
                //sb.append("parse response :- ").append(response);
            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {

                sb.append("Error response code :- ").append(code).append("\n");
                sb.append("message :- ").append(message);

                callback.onError(code, null, code, sb.toString());

            }

        };

        request.setPost(true);
        request.setUrl(API_URL + "/radioLogicalTest");
        request.setContentType("application/json");

        String jsonInputString = "{"
                + "\"title\": \"" + labTest.getTitle() + "\","
                + "\"cost\": \"" + labTest.getCost() + "\","
                + "\"isAvaiable\": \"" + labTest.isIsAvailable() + "\","
                + "\"plateDimention\": \"" + labTest.getPlateDimention() + "\""
                + "}";

        request.setRequestBody(jsonInputString);

        NetworkManager.getInstance().addToQueue(request);

        return sb.toString();

    }

    public void getAllPathologicalTest(List list, Callback<String> callback) {

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {

                String responseString = Util.readToString(input);
                System.out.println("Raw Response: " + responseString);

                if (responseString == null || responseString.isEmpty()) {

                    System.err.println("Empty response received.");
                    return;

                }

                if (responseString.startsWith("[") && responseString.endsWith("]")) {

                    responseString = responseString.substring(1, responseString.length() - 1);

                }

                System.out.println("after removing [] :- " + responseString);

                Map<String, Object> map = new HashMap<>();
                StringBuilder keyBuilder = new StringBuilder();
                StringBuilder valueBuilder = new StringBuilder();
                boolean isKey = true;
                boolean insideString = false;
                boolean findIsAvailable = false;

                for (int i = 0; i < responseString.length(); i++) {

                    char currentChar = responseString.charAt(i);

                    if (Character.isDigit(currentChar)) {

                        valueBuilder.append(currentChar);
                        continue;

                    }

                    /*if (!findIsAvailable && !map.isEmpty() && map.containsKey("isAvaiable")) {

                        if (currentChar == 't') {

                            valueBuilder.append("active");

                        } else {

                            valueBuilder.append("close");

                        }

                        findIsAvailable = true;

                        continue;

                    }*/
                    if (currentChar == '"') {

                        insideString = !insideString;

                    } else if (insideString) {

                        if (isKey) {

                            keyBuilder.append(currentChar);

                        } else {

                            valueBuilder.append(currentChar);

                        }

                    } else {

                        if (currentChar == ':') {

                            //System.out.println(keyBuilder.toString());
                            if (keyBuilder.toString().equals("isAvaiable")) {

                                //System.out.println("I am isAvailable");
                                if (responseString.charAt(i + 1) == 't') {

                                    valueBuilder.append("Active");

                                } else {

                                    valueBuilder.append("close");

                                }

                            }

                            isKey = false;

                        } else if (currentChar == ',' || currentChar == '}') {

                            map.put(keyBuilder.toString().trim(), valueBuilder.toString().trim());

                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                            isKey = true;

                            if (currentChar == '}') {

                                StringBuilder sb = new StringBuilder();

                                for (Object val : map.values()) {

                                    sb.append(val).append(" ");

                                }

                                list.addItem(sb.toString().trim());

                                System.out.println(map);

                                map.clear();

                                findIsAvailable = false;

                            }

                        }

                    }

                }

                //System.out.println(list);
                if (callback != null && list.size() > 0) {

                    callback.onSucess("Successfully find Pathological test data");

                }

            }

            @Override
            protected void handleErrorResponseCode(int code, String message
            ) {
                System.err.println("Error Response Code: " + code + " Message: " + message);

                if (callback != null && list.size() > 0) {

                    callback.onSucess("Error Response Code: " + code + " Message: " + message);

                }

            }
        };

        request.setUrl(API_URL + "/pathologicalTest/all");

        request.setPost(
                false);
        NetworkManager.getInstance()
                .addToQueueAndWait(request);

    }

    public void getAllRadiologicalTest(List list, Callback<String> callback) {

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {

                String responseString = Util.readToString(input);
                System.out.println("Raw Response: " + responseString);

                if (responseString == null || responseString.isEmpty()) {

                    System.err.println("Empty response received.");
                    return;

                }

                if (responseString.startsWith("[") && responseString.endsWith("]")) {

                    responseString = responseString.substring(1, responseString.length() - 1);

                }

                System.out.println("after removing [] :- " + responseString);

                Map<String, Object> map = new HashMap<>();
                StringBuilder keyBuilder = new StringBuilder();
                StringBuilder valueBuilder = new StringBuilder();
                boolean isKey = true;
                boolean insideString = false;

                boolean findIsAvailable = false;

                for (int i = 0; i < responseString.length(); i++) {

                    char currentChar = responseString.charAt(i);

                    if (Character.isDigit(currentChar)) {

                        valueBuilder.append(currentChar);
                        continue;

                    }

                    /*if (!findIsAvailable && !map.isEmpty() && map.containsKey("isAvaiable")) {

                        System.out.println("I am at here.");

                        if (currentChar == 't') {

                            valueBuilder.append("active");

                        } else {

                            valueBuilder.append("close");

                        }

                        findIsAvailable = true;

                        continue;

                    }*/
                    if (currentChar == '"') {

                        insideString = !insideString;

                    } else if (insideString) {

                        if (isKey) {

                            keyBuilder.append(currentChar);

                        } else {

                            valueBuilder.append(currentChar);

                        }

                    } else {

                        if (currentChar == ':') {

                            if (keyBuilder.toString().equals("isAvaiable")) {

                                //System.out.println("I am isAvailable");
                                if (responseString.charAt(i + 1) == 't') {

                                    valueBuilder.append("Active");

                                } else {

                                    valueBuilder.append("close");

                                }

                            }

                            isKey = false;

                        } else if (currentChar == ',' || currentChar == '}') {

                            map.put(keyBuilder.toString().trim(), valueBuilder.toString().trim());

                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                            isKey = true;

                            if (currentChar == '}') {

                                StringBuilder sb = new StringBuilder();

                                for (Object val : map.values()) {

                                    sb.append(val).append(" ");

                                }

                                list.addItem(sb.toString().trim());

                                map.clear();

                                findIsAvailable = false;

                            }

                        }

                    }

                }

                //System.out.println(list);
                if (callback != null && list.size() > 0) {

                    callback.onSucess("Successfully find radiological test data");

                }

            }

            @Override
            protected void handleErrorResponseCode(int code, String message
            ) {
                System.err.println("Error Response Code: " + code + " Message: " + message);

                if (callback != null && list.size() > 0) {

                    callback.onSucess("Error Response Code: " + code + " Message: " + message);

                }

            }
        };

        request.setUrl(API_URL + "/radioLogicalTest/all");

        request.setPost(
                false);
        NetworkManager.getInstance()
                .addToQueueAndWait(request);

    }

    // Search PathologicalTest by title
    public void searchPathologicalTestByTitle(String title, Callback<String> callback, List list) {
        StringBuilder sb = new StringBuilder();

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {
                String responseString = Util.readToString(input);

                System.out.println("Raw Response: " + responseString);

                if (responseString == null || responseString.isEmpty()) {

                    System.err.println("Empty response received.");
                    return;

                }

                if (responseString.startsWith("[") && responseString.endsWith("]")) {

                    responseString = responseString.substring(1, responseString.length() - 1);

                }

                System.out.println("after removing [] :- " + responseString);

                Map<String, Object> map = new HashMap<>();
                StringBuilder keyBuilder = new StringBuilder();
                StringBuilder valueBuilder = new StringBuilder();
                boolean isKey = true;
                boolean insideString = false;

                boolean findIsAvailable = false;

                for (int i = 0; i < responseString.length(); i++) {

                    char currentChar = responseString.charAt(i);

                    if (Character.isDigit(currentChar)) {

                        valueBuilder.append(currentChar);
                        continue;

                    }

                    /*if (!findIsAvailable && !map.isEmpty() && map.containsKey("isAvaiable")) {

                        System.out.println("I am at here.");

                        if (currentChar == 't') {

                            valueBuilder.append("active");

                        } else {

                            valueBuilder.append("close");

                        }

                        findIsAvailable = true;

                        continue;

                    }*/
                    if (currentChar == '"') {

                        insideString = !insideString;

                    } else if (insideString) {

                        if (isKey) {

                            keyBuilder.append(currentChar);

                        } else {

                            valueBuilder.append(currentChar);

                        }

                    } else {

                        if (currentChar == ':') {

                            if (keyBuilder.toString().equals("isAvaiable")) {

                                //System.out.println("I am isAvailable");
                                if (responseString.charAt(i + 1) == 't') {

                                    valueBuilder.append("Active");

                                } else {

                                    valueBuilder.append("close");

                                }

                            }

                            isKey = false;

                        } else if (currentChar == ',' || currentChar == '}') {

                            map.put(keyBuilder.toString().trim(), valueBuilder.toString().trim());

                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                            isKey = true;

                            if (currentChar == '}') {

                                StringBuilder sb = new StringBuilder();

                                System.out.println("getted title :- " + map.get("title") + " searching title :- " + title);

                                if (map.get("title").toString().equalsIgnoreCase(title)) {
                                    for (Object val : map.values()) {

                                        sb.append(val).append(" ");

                                    }

                                    list.addItem(sb.toString().trim());

                                }
                                map.clear();

                                findIsAvailable = false;

                            }

                        }

                    }

                }

                if (list.size() > 0) {

                    if (callback != null) {

                        callback.onSucess("Can be able to find all the data of pathological test.");

                    }

                } else {

                    if (callback != null) {

                        callback.onSucess("Can not be able to find all the data of pathological test.");

                    }

                }

            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                sb.append("Error Code: ").append(code).append(" Message: ").append(message);
                if (callback != null) {
                    callback.onError(this, null, code, sb.toString());
                }
            }
        };

        request.setUrl(API_URL + "/pathologicalTest/search?title=" + title);
        request.setPost(false);  // GET request
        NetworkManager.getInstance().addToQueue(request);
    }

    // Search RadioLogicalTest by title
    public void searchRadioLogicalTestByTitle(String title, Callback<String> callback, List list) {
        StringBuilder sb = new StringBuilder();

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {
                String responseString = Util.readToString(input);
                System.out.println("Raw Response: " + responseString);

                if (responseString == null || responseString.isEmpty()) {

                    System.err.println("Empty response received.");
                    return;

                }

                if (responseString.startsWith("[") && responseString.endsWith("]")) {

                    responseString = responseString.substring(1, responseString.length() - 1);

                }

                System.out.println("after removing [] :- " + responseString);

                Map<String, Object> map = new HashMap<>();
                StringBuilder keyBuilder = new StringBuilder();
                StringBuilder valueBuilder = new StringBuilder();
                boolean isKey = true;
                boolean insideString = false;

                boolean findIsAvailable = false;

                for (int i = 0; i < responseString.length(); i++) {

                    char currentChar = responseString.charAt(i);

                    if (Character.isDigit(currentChar)) {

                        valueBuilder.append(currentChar);
                        continue;

                    }

                    if (currentChar == '"') {

                        insideString = !insideString;

                    } else if (insideString) {

                        if (isKey) {

                            keyBuilder.append(currentChar);

                        } else {

                            valueBuilder.append(currentChar);

                        }

                    } else {

                        if (currentChar == ':') {

                            if (keyBuilder.toString().equals("isAvaiable")) {

                                //System.out.println("I am isAvailable");
                                if (responseString.charAt(i + 1) == 't') {

                                    valueBuilder.append("Active");

                                } else {

                                    valueBuilder.append("close");

                                }

                            }

                            isKey = false;

                        } else if (currentChar == ',' || currentChar == '}') {

                            map.put(keyBuilder.toString().trim(), valueBuilder.toString().trim());

                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                            isKey = true;

                            if (currentChar == '}') {

                                StringBuilder sb = new StringBuilder();

                                System.out.println("getted title :- " + map.get("title") + " searching title :- " + title);

                                if (map.get("title").toString().equalsIgnoreCase(title)) {
                                    for (Object val : map.values()) {

                                        sb.append(val).append(" ");

                                    }

                                    list.addItem(sb.toString().trim());

                                }
                                map.clear();

                                findIsAvailable = false;

                            }

                        }

                    }

                }

                if (list.size() > 0) {

                    if (callback != null) {

                        callback.onSucess("Can be able to find all the data of radiological test.");

                    }

                } else {

                    if (callback != null) {

                        callback.onSucess("Can not be able to find all the data of radiological test.");

                    }

                }

            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                sb.append("Error Code: ").append(code).append(" Message: ").append(message);
                if (callback != null) {
                    callback.onError(this, null, code, sb.toString());
                }
            }
        };

        request.setUrl(API_URL + "/radioLogicalTest/search?title=" + title);
        request.setPost(false);  // GET request
        NetworkManager.getInstance().addToQueue(request);
    }

    // Search PathologicalTest by title
    public void searchPathologicalTestByCost(int cost, Callback<String> callback, List list) {
        StringBuilder sb = new StringBuilder();

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {
                String responseString = Util.readToString(input);

                System.out.println("Raw Response: " + responseString);

                if (responseString == null || responseString.isEmpty()) {

                    System.err.println("Empty response received.");
                    return;

                }

                if (responseString.startsWith("[") && responseString.endsWith("]")) {

                    responseString = responseString.substring(1, responseString.length() - 1);

                }

                System.out.println("after removing [] :- " + responseString);

                Map<String, Object> map = new HashMap<>();
                StringBuilder keyBuilder = new StringBuilder();
                StringBuilder valueBuilder = new StringBuilder();
                boolean isKey = true;
                boolean insideString = false;

                boolean findIsAvailable = false;

                for (int i = 0; i < responseString.length(); i++) {

                    char currentChar = responseString.charAt(i);

                    if (Character.isDigit(currentChar)) {

                        valueBuilder.append(currentChar);
                        continue;

                    }

                    /*if (!findIsAvailable && !map.isEmpty() && map.containsKey("isAvaiable")) {

                        System.out.println("I am at here.");

                        if (currentChar == 't') {

                            valueBuilder.append("active");

                        } else {

                            valueBuilder.append("close");

                        }

                        findIsAvailable = true;

                        continue;

                    }*/
                    if (currentChar == '"') {

                        insideString = !insideString;

                    } else if (insideString) {

                        if (isKey) {

                            keyBuilder.append(currentChar);

                        } else {

                            valueBuilder.append(currentChar);

                        }

                    } else {

                        if (currentChar == ':') {

                            if (keyBuilder.toString().equals("isAvaiable")) {

                                //System.out.println("I am isAvailable");
                                if (responseString.charAt(i + 1) == 't') {

                                    valueBuilder.append("Active");

                                } else {

                                    valueBuilder.append("close");

                                }

                            }

                            isKey = false;

                        } else if (currentChar == ',' || currentChar == '}') {

                            map.put(keyBuilder.toString().trim(), valueBuilder.toString().trim());

                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                            isKey = true;

                            if (currentChar == '}') {

                                StringBuilder sb = new StringBuilder();

                                //System.out.println("getted title :- " + map.get("title") + " searching title :- " + title);
                                //if (map.get("title").toString().equalsIgnoreCase(title)) {
                                for (Object val : map.values()) {

                                    sb.append(val).append(" ");

                                }

                                list.addItem(sb.toString().trim());

                                //}
                                map.clear();

                                findIsAvailable = false;

                            }

                        }

                    }

                }

                if (list.size() > 0) {

                    if (callback != null) {

                        callback.onSucess("Can be able to find all the data of pathological test.");

                    }

                } else {

                    if (callback != null) {

                        callback.onSucess("Can not be able to find all the data of pathological test.");

                    }

                }

            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                sb.append("Error Code: ").append(code).append(" Message: ").append(message);
                if (callback != null) {
                    callback.onError(this, null, code, sb.toString());
                }
            }
        };

        request.setUrl(API_URL + "/pathologicalTest/cost?cost=" + cost);
        request.setPost(false);  // GET request
        NetworkManager.getInstance().addToQueue(request);
    }

    // Search RadioLogicalTest by title
    public void searchRadioLogicalTestByCost(int cost, Callback<String> callback, List list) {
        StringBuilder sb = new StringBuilder();

        ConnectionRequest request = new ConnectionRequest() {
            @Override
            protected void readResponse(InputStream input) throws IOException {
                String responseString = Util.readToString(input);
                System.out.println("Raw Response: " + responseString);

                if (responseString == null || responseString.isEmpty()) {

                    System.err.println("Empty response received.");
                    return;

                }

                if (responseString.startsWith("[") && responseString.endsWith("]")) {

                    responseString = responseString.substring(1, responseString.length() - 1);

                }

                System.out.println("after removing [] :- " + responseString);

                Map<String, Object> map = new HashMap<>();
                StringBuilder keyBuilder = new StringBuilder();
                StringBuilder valueBuilder = new StringBuilder();
                boolean isKey = true;
                boolean insideString = false;

                boolean findIsAvailable = false;

                for (int i = 0; i < responseString.length(); i++) {

                    char currentChar = responseString.charAt(i);

                    if (Character.isDigit(currentChar)) {

                        valueBuilder.append(currentChar);
                        continue;

                    }

                    if (currentChar == '"') {

                        insideString = !insideString;

                    } else if (insideString) {

                        if (isKey) {

                            keyBuilder.append(currentChar);

                        } else {

                            valueBuilder.append(currentChar);

                        }

                    } else {

                        if (currentChar == ':') {

                            if (keyBuilder.toString().equals("isAvaiable")) {

                                //System.out.println("I am isAvailable");
                                if (responseString.charAt(i + 1) == 't') {

                                    valueBuilder.append("Active");

                                } else {

                                    valueBuilder.append("close");

                                }

                            }

                            isKey = false;

                        } else if (currentChar == ',' || currentChar == '}') {

                            map.put(keyBuilder.toString().trim(), valueBuilder.toString().trim());

                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                            isKey = true;

                            if (currentChar == '}') {

                                StringBuilder sb = new StringBuilder();

                                //System.out.println("getted title :- " + map.get("title") + " searching title :- " + title);
                                //if (map.get("title").toString().equalsIgnoreCase(title)) {
                                for (Object val : map.values()) {

                                    sb.append(val).append(" ");

                                }

                                list.addItem(sb.toString().trim());

                                //}
                                map.clear();

                                findIsAvailable = false;

                            }

                        }

                    }

                }

                if (list.size() > 0) {

                    if (callback != null) {

                        callback.onSucess("Can be able to find all the data of radiological test.");

                    }

                } else {

                    if (callback != null) {

                        callback.onSucess("Can not be able to find all the data of radiological test.");

                    }

                }

            }

            @Override
            protected void handleErrorResponseCode(int code, String message) {
                sb.append("Error Code: ").append(code).append(" Message: ").append(message);
                if (callback != null) {
                    callback.onError(this, null, code, sb.toString());
                }
            }
        };

        request.setUrl(API_URL + "/radioLogicalTest/cost?cost=" + cost);
        request.setPost(false);  // GET request
        NetworkManager.getInstance().addToQueue(request);
    }

}
