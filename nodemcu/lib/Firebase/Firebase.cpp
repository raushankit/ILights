#include "Firebase.h"

Firebase::Firebase()
{
    Serial.println("initializing firebase");
}

void Firebase::begin(const char *_host)
{
    secure.setInsecure();
    httpClient.setReuse(true);
    httpClient.addHeader("Content-Type", "application/json");
    httpClient.begin(secure, _host);
}

Firebase::~Firebase()
{
    httpClient.end();
}

String Firebase::makeUrl(String path)
{
    if (path[0] != '/')
        return "";
    return path + ".json?auth=" + FirebaseKeys::DB_SECRET;
}

FirebaseResponse Firebase::PUT(String path, String payLoad)
{
    if (!payLoad.isEmpty())
    {
        __response.reset(new FirebaseResponse());
        httpClient.setURL(makeUrl(path));
        int __code = httpClient.PUT(payLoad.c_str());
        __response->setResPonseCode(__code);
        __response->setResponce(__code > 0 ? httpClient.getString() : "unknown error");
    }
    return *__response;
}

FirebaseResponse Firebase::GET(String path)
{
    __response.reset(new FirebaseResponse());
    httpClient.setURL(makeUrl(path));
    int __code = httpClient.GET();
    __response->setResPonseCode(__code);
    __response->setResponce(__code > 0 ? httpClient.getString() : "unknown error");
    return *__response;
}