#ifndef __FIREBASE__
#define __FIREBASE__
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include "FirebaseResponse.h"
#include <ESP8266HTTPClient.h>
#include <WiFiClientSecure.h>
#include <ArduinoJson.h>

class Firebase
{
private:
    char *idToken = "";
    unsigned long fetchTime;

    HTTPClient httpClient;
    WiFiClientSecure secure;
    std::unique_ptr<FirebaseResponse> __response;

    String makeUrl(String path);

    void fetchIdToken();

public:
    Firebase();
    void begin(const char *host);
    FirebaseResponse PUT(String path, String payLoad);
    FirebaseResponse GET(String path);
    ~Firebase();

    static const char *BOARD_ID;
    static const String EMAIL;
    static const String PASSWORD;
};

#endif