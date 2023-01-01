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
    int index = path.indexOf("${BOARD_ID}");
    if (index != -1)
    {
        path = path.substring(0, index) + BOARD_ID + path.substring(index + 11);
    }
    return path + ".json?auth=" + idToken;
}

FirebaseResponse Firebase::PUT(String path, String payLoad)
{
    fetchIdToken();
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
    fetchIdToken();
    __response.reset(new FirebaseResponse());
    httpClient.setURL(makeUrl(path));
    int __code = httpClient.GET();
    __response->setResPonseCode(__code);
    __response->setResponce(__code > 0 ? httpClient.getString() : "unknown error");
    return *__response;
}

void Firebase::fetchIdToken()
{
    if (millis() < fetchTime)
    {
        return;
    }
    String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCGVyA-GzbzEzt3WU5LAq2dD65shdc_Rf8";
    String body = "{\"email\": \"" + EMAIL + "\",\"password\": \"" + PASSWORD + "\",\"returnSecureToken\":\"true\"}";
    int __code = httpClient.POST(body.c_str());
    StaticJsonDocument<200> doc;
    DeserializationError error = deserializeJson(doc, httpClient.getString());
    if (__code <= 0 || error)
    {
        idToken = "";
        fetchTime = -1;
        return;
    }
    JsonObject object = doc.as<JsonObject>();
    const char *__idToken = object["idToken"];
    idToken = (char *)__idToken;
    fetchTime = millis() + object["expiresIn"].as<unsigned long>() * 900;
}