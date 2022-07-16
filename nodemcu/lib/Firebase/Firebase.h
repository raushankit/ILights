#ifndef __FIREBASE__
#define __FIREBASE__
#include <Arduino.h>
#include <ESP8266WiFi.h>
#include "FirebaseResponse.h"
#include "FirebaseKeys.h"
#include <ESP8266HTTPClient.h>
#include <WiFiClientSecure.h>

class Firebase
{
private:
    HTTPClient httpClient;
    WiFiClientSecure secure;
    std::unique_ptr<FirebaseResponse> __response;

    String makeUrl(String path);

public:
    Firebase();
    void begin(const char* host);
    FirebaseResponse PUT(String path, String payLoad);
    FirebaseResponse GET(String path);
    ~Firebase();
};

#endif