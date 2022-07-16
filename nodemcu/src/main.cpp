#include <Arduino.h>
#include <ESP8266WiFi.h>
#include <Firebase.h>
#include <FirebaseResponse.h>

#define WIFI_SSID "Airtel-MW40-8C3B"
#define WIFI_PASSWORD "71883955"

const int usable_pins[10] = {2, 4, 5, 10, 12, 13, 14, 15, 16, 17};
bool parse(String rsponse);
void parse_asJSON(String json);
void parseString(String response);

const char *HOST = "https://pcs-project-71dcd-default-rtdb.firebaseio.com";
const char *PULSE_PATH = "/metadata/board_data/heart_beat";
const char *STATUS_PATH = "/control/status";
const char *PULSE_DATA = "{\".sv\":\"timestamp\"}";
Firebase firebase;
unsigned long lastTime = 0;
const unsigned long BEAT_DELAY = 5000;

void setup()
{
  // put your setup code here, to run once:
  Serial.begin(9600);
  for (int i = 0; i < 10; ++i)
  {
    pinMode(usable_pins[i], OUTPUT);
    digitalWrite(usable_pins[i], HIGH);
  }
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.println("Connecting");
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
  firebase.begin(HOST);
}

void loop()
{
  unsigned long now = millis();
  if (now - lastTime >= BEAT_DELAY)
  {
    FirebaseResponse _res = firebase.PUT(PULSE_PATH, PULSE_DATA);
    Serial.println(_res.getResPonseCode());
    Serial.println(_res.getResponce());
    if(_res.getResPonseCode() == HTTP_CODE_OK) lastTime = now;
  }
  FirebaseResponse _res = firebase.GET(STATUS_PATH);
  bool flag = parse(_res.getResponce());
  Serial.println(_res.getResPonseCode());
    Serial.println(_res.getResponce());
  if (_res.getResPonseCode() == HTTP_CODE_OK && flag)
  {
    parseString(_res.getResponce());
  }
  delay(3000);
}

bool parse(String response)
{
  int8_t num_double_quotes = 0;
  int8_t num_single_quotes = 0;
  int8_t num_middle_brackets = 0;
  int8_t num_big_brackets = 0;
  if (response[0] != '{' && response[0] != '[')
  {
    return false;
  }
  num_big_brackets = response[0] == '[' ? 1 : 0;
  num_middle_brackets = response[0] == '{' ? 1 : 0;
  for (uint8_t i = 1; i < response.length(); ++i)
  {
    switch (response[i])
    {
    case '}':
      num_middle_brackets--;
      break;
    case ']':
      num_big_brackets--;
      break;
    case '{':
      num_middle_brackets++;
      break;
    case '[':
      num_big_brackets++;
      break;
    case '\"':
      num_double_quotes++;
      break;
    case '\'':
      num_single_quotes++;
      break;
    default:
      break;
    }
  }
  return num_big_brackets == 0 && num_middle_brackets == 0 && num_double_quotes % 2 == 0 && num_single_quotes % 2 == 0;
}

void parseString(String response)
{
  unsigned int i = 0;
  uint8_t arr_counter = 0;
  while (i < response.length())
  {
    char c = response[i];
    if (c == '{' || c == ',' || c == '}' || c == '\\' || c == '\'')
    {
      i++;
      continue;
    }
    String temp = "";
    while (c != ',' && c != '}')
    {
      temp += c;
      i++;
      c = response[i];
    }
    temp.toLowerCase();
    if (temp == "null")
    {
      arr_counter++;
    }
    else if (temp == "true")
    {
      digitalWrite(arr_counter, LOW);
    }
    else if (temp == "false")
    {
      digitalWrite(arr_counter, HIGH);
    }
    else
    {
      parse_asJSON(temp);
    }
  }
}

void parse_asJSON(String json)
{
  int idx = json.indexOf(":");
  int pin = atoi(json.substring(1, idx - 1).c_str());
  json.remove(0, idx + 1);
  digitalWrite(pin, (json == "false" ? HIGH : LOW));
}