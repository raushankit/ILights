#include "FirebaseResponse.h"

FirebaseResponse::FirebaseResponse()
{
    _response_code = -1;
}

void FirebaseResponse::setResponce(String response) { response.trim(), _response = response; }

void FirebaseResponse::setResPonseCode(int responseCode) { _response_code = responseCode; }

String FirebaseResponse::getResponce() { return _response; }

int FirebaseResponse::getResPonseCode() { return _response_code; }