
#include <Keypad.h>
#include <Servo.h>


Servo servo_Motor;
char* password = "123";
int position = 0;


const byte ROWS = 3; //3 Lignes 
const byte COLS = 3; //3 Colonnes 
char keys[ROWS][COLS] = {
{'1','2','3'},
{'4','5','6'},
{'7','8','9'}
};

byte rowPins[ROWS] = {7,6,5}; //Connecté sur les lignes du KeyPad (L1, L2, L3)
byte colPins[COLS] = {4,3,2}; //Connecté sur les colonnes dy KeyPad (C1, C2, C3)

Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );

void setup() {
Serial.begin(9600);
servo_Motor.attach(8);
setLocked(true);
}

void loop()
{


 door_code();
}


void door_code()
{
  char key = keypad.getKey();
  Serial.println(key);
if (key == '*' || key == '9')
{
    Serial.println("Close");
position = 0;
setLocked(true);
}
else if (key == password[position])
{
position ++;
Serial.print("Read ");
Serial.println(position);
}
else if (key !=0 )
{
  Serial.print("Read ");
Serial.println(position);
  if (position>0){
    Serial.print("Reset ");
    position=0;
  }
}
if (position == 3)
{
  Serial.print("Open ");
setLocked(false);
}
//delay(100);
}

void door_code()
{
  char key = keypad.getKey();
  if (key == '*' || key == '9')
  {
      Serial.println("Close");
      position = 0;
      setLocked(true);
  }
else if (key == password[position])
{
position ++;
Serial.print("Read ");
Serial.println(position);
}
else if (key !=0 )
{
  Serial.print("Read ");
Serial.println(position);
  if (position>0){
    Serial.print("Reset ");
    position=0;
  }
}
if (position == 3)
{
  Serial.print("Open ");
setLocked(false);
}
//delay(100);
}

void setLocked(int locked)
{
if (locked)
{
servo_Motor.write(90);
}
else
{
servo_Motor.write(6);
}
}








