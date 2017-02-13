#include <SoftwareSerial.h>
#include <dht11.h> // charger la librairie
dht11 DHT11; // Créer le capteur
#define DHT11PIN 2 // Définir la pin utilisée


//int calibrationTime = 30;
const char PIR_LED = 7;
int inputPin = 3;               // choose the input pin (for PIR sensor)
int pirState = LOW;             // we start, assuming no motion detected
int val = 0;   

SoftwareSerial HC06(11,9);

const char DOUT_LED = 13;
const char DOUT_LED2 = 12;
const char DOUT_LED3 = 4;
String messageRecu;
float H,T;

void setup() {
  String setName = String("AT+NAME=ILISISmartHome\r\n"); //Setting name as 'ILISI Smart Home'
  Serial.begin(9600);
   HC06.begin(9600);
  //HC06.begin(38400);
  //HC06.print("AT\r\n"); //Check Status
  //HC06.print(setName); //Send Command to change the name

  HC06.begin(9600); 
  pinMode(DOUT_LED, OUTPUT);
  digitalWrite(DOUT_LED, LOW);
  pinMode(DOUT_LED2, OUTPUT);
  digitalWrite(DOUT_LED2, LOW);
  pinMode(DOUT_LED3, OUTPUT);
  digitalWrite(DOUT_LED3, LOW);


  pinMode(PIR_LED, OUTPUT);      // declare LED as output
  pinMode(inputPin, INPUT);     // declare sensor as input
  Serial.begin(9600);
/*
  Serial.print("calibrating sensor ");
  for(int i = 0; i < calibrationTime; i++){
    Serial.print(".");
    delay(1000);}
*/
  
  
}
 
void loop()
{
    runPIR();
    int chk = DHT11.read(DHT11PIN); // Lecture du capteur température
  
    while(HC06.available())
    {
      char c = HC06.read();
      messageRecu += c;
    }
    if (messageRecu.length() >0)
    {
      Serial.println(messageRecu);
      if (messageRecu == "LED1:1")     
        { digitalWrite(DOUT_LED, HIGH); }
      if (messageRecu == "LED1:0")
        { digitalWrite(DOUT_LED, LOW); }
      if (messageRecu == "LED2:0")
       { digitalWrite(DOUT_LED2, LOW); }
      if (messageRecu == "LED2:1")
       { digitalWrite(DOUT_LED2, HIGH); }
      if (messageRecu == "LED3:0")
       { digitalWrite(DOUT_LED3, LOW);}
      if (messageRecu == "LED3:1")
       { digitalWrite(DOUT_LED3, HIGH);}
      if (messageRecu == "T")
       { checkDHT(chk); }
      messageRecu="";
    }
   // delay(2000);
}

int checkDHT(int chk)
{
      float H,T;
    H = (float)DHT11.humidity;
    if(H>100)
    {
      checkDHT(chk);
      return 0;
    }
      
    char bufH[16];
    dtostrf(H, 10, 2, bufH);
  
    
    T = (double)DHT11.temperature;
    char bufT[16];
    dtostrf(T, 10, 2, bufT);

    //HC06.write("Etat du capteur: ");
    /*switch (chk) // Est-ce que le capteur fonctionne?
    {
      case DHTLIB_OK:HC06.write("OK\n"); break;
      case DHTLIB_ERROR_CHECKSUM:HC06.write("Checksum erreur\n"); break;
      case DHTLIB_ERROR_TIMEOUT:HC06.write("Time out erreur\n"); break;
      default: HC06.write("Erreur inconnue\n"); break;
    }*/
    //HC06.write("\nHumidite (%): "); // Humidité
    
    HC06.write(bufH);
    HC06.write("|");
    //HC06.write("\nTemperature (C): "); // Température
    HC06.write(bufT);
    HC06.write("|");
    

    return 1;
}

void runPIR()
{
  val = digitalRead(inputPin);  // read input value
  Serial.println(val);
  if (val == HIGH) { // check if the input is HIGH
    digitalWrite(PIR_LED, HIGH);  // turn LED ON
    //delay(150);

    if (pirState == LOW) {
      // we have just turned on
      Serial.println("Motion detected!");
      // We only want to print on the output change, not state
      pirState = HIGH;
    }
  } else {
    digitalWrite(PIR_LED, LOW); // turn LED OFF
    //delay(300);
    if (pirState == HIGH){
      // we have just turned of
      Serial.println("Motion ended!");
      // We only want to print on the output change, not state
      pirState = LOW;
    }
  }
}

