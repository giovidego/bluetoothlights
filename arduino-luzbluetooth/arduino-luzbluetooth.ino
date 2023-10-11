#include <SoftwareSerial.h>


const int bluetoothTx = 1;  // Pin TX del módulo Bluetooth conectado al pin 1 del Arduino
const int bluetoothRx = 0;  // Pin RX del módulo Bluetooth conectado al pin 0 del Arduino

SoftwareSerial bluetoothSerial(bluetoothTx, bluetoothRx);


const int ledVerdePin = 13;  // LED verde conectado al pin 13
const int ledRojoPin = 12;   // LED rojo conectado al pin 12

void setup() {
  pinMode(ledVerdePin, OUTPUT);
  pinMode(ledRojoPin, OUTPUT);
  
  digitalWrite(ledVerdePin, LOW);
  digitalWrite(ledRojoPin, LOW);
  
  // Iniciamos la comunicación con Bluetooth
  bluetoothSerial.begin(9600);
}

void loop() {
  if (bluetoothSerial.available() > 0) {
    char data = bluetoothSerial.read();
    
    // Si data = "1", encendemos el LED verde y apagamos el LED rojo
    if (data == '1') {
      digitalWrite(ledVerdePin, HIGH);
      digitalWrite(ledRojoPin, LOW);
    }
    
    // Si data = "2", apagamos el LED verde y encendemos el LED rojo
    else if (data == '2') {
      digitalWrite(ledVerdePin, LOW);
      digitalWrite(ledRojoPin, HIGH);
    }
  }
}