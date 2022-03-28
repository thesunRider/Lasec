void setup() {
  // put your setup code here, to run once:
  pinMode(0,OUTPUT);
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  Serial.print("yo");
  delay(1000);
}
