#include <SPI.h>
#include <RFID.h>
#include <Servo.h>

Servo myservo; // create servo object to control a servo
// twelve servo objects can be created on most boards

int pos = 0; // variable to store the servo position
RFID monModuleRFID(10,9);

int UID[5];
int MASTERKEY[5]={86,162,124,50,186};
void setup()
{
Serial.begin(9600);
SPI.begin();
monModuleRFID.init();
myservo.attach(7);
myservo.write(0);

}

void loop()
{
  
if (monModuleRFID.isCard()) {
if (monModuleRFID.readCardSerial()) {
Serial.print("L'UID est: ");
for(int i=0;i<=4;i++)
{
UID[i]=monModuleRFID.serNum[i];
Serial.print(UID[i],DEC);
Serial.print(".");
}
Serial.println("");
}
if (UID[0] == MASTERKEY[0]
&& UID[1] == MASTERKEY[1]
&& UID[2] == MASTERKEY[2]
&& UID[3] == MASTERKEY[3]
&& UID[4] == MASTERKEY[4])
{
Serial.println("acces autorisé");
for(pos = 0; pos <= 90; pos += 1)
{
myservo.write(pos);
delay(200);
}
delay(5000);
for(pos = 90; pos >= 90; pos -= 1)
{
myservo.write(pos);
delay(200);
}
}
else
{

Serial.println("acce­s interdit");
}
monModuleRFID.halt();
}
delay(1);
}
