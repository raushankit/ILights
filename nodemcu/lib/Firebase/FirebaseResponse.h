#ifndef __FIREBASE_RESPONSE__
#define __FIREBASE_RESPONSE__
#include <Arduino.h>

class FirebaseResponse
{
private:
    String _response;
    int _response_code;

public:
    FirebaseResponse();
    int getResPonseCode();
    String getResponce();
    void setResPonseCode(int responseCode);
    void setResponce(String response);
};

#endif