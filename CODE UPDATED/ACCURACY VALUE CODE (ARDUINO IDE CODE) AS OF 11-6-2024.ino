  #include <Wire.h> // for I2C communication
  #include <LiquidCrystal_I2C.h> // for LCD display
  #include <math.h> // for sqrt function

  #define buzzer 12 
  #define led 13 

  #define x A0 
  #define y A1 
  #define z A2 

  /*variables*/
  int xsample=0;
  int ysample=0;
  int zsample=0;
  long start;
  int buz=0;

  /*Macros*/
  #define samples 10
  #define maxVal 100 // max change limit
  #define minVal -100 // min change limit
  #define buzTime 5000 // buzzer on time

  LiquidCrystal_I2C lcd(0x27, 20, 4); // I2C address, columns, rows

  void setup()
  {
    lcd.init();
    lcd.backlight();
    lcd.begin(16,2); 
    Serial.begin(9600); 
    delay(1000);
    lcd.print("Earthquake ");
    lcd.setCursor(0,1);
    lcd.print("Detector ");
    lcd.print("System ");
    delay(2000);
    lcd.clear();
    lcd.print("Calibrating.....");
    lcd.setCursor(0,1);
    lcd.print("Please wait...");
    pinMode(buzzer, OUTPUT);
    pinMode(led, OUTPUT);
    buz=0;
    digitalWrite(buzzer, buz);
    digitalWrite(led, buz);
    for(int i=0;i<samples;i++) 
    {
      xsample+=analogRead(x);
      ysample+=analogRead(y);
      zsample+=analogRead(z);
    }
    xsample/=samples; 
    ysample/=samples; 
    zsample/=samples; 
    delay(3000);
    lcd.clear();
    lcd.print("Calibrated");
    delay(1000);
    lcd.clear();
    lcd.print("Device Ready");
    delay(1000);
    lcd.clear();
    lcd.print(" X   Y   Z ");
  }

  int calculateMagnitude(int x, int y, int z)
  {
    int magnitude = sqrt(sq(x) + sq(y) + sq(z));
    if (magnitude < 50) return 1;
    else if (magnitude < 100) return 2;
    else if (magnitude < 150) return 3;
    else if (magnitude < 200) return 4;
    else if (magnitude < 250) return 5;
    else if (magnitude < 300) return 6;
    else if (magnitude < 350) return 7;
    else if (magnitude < 400) return 8;
    else return 9;
  }

  void loop()
  {
    int value1=analogRead(x); 
    int value2=analogRead(y); 
    int value3=analogRead(z); 

    int xValue=xsample-value1; 
    int yValue=ysample-value2; 
    int zValue=zsample-value3; 

    // Calculate magnitude
    int magnitude = calculateMagnitude(xValue, yValue, zValue);

    // Display magnitude level
    lcd.setCursor(0,0);
    if (magnitude == 1) 
    {
      lcd.print("No Earthquake");
    }
    else if (magnitude == 2) 
    {
      lcd.print("No Earthquake");
    }
    else if (magnitude == 3) 
    {
      lcd.print("Earthquake Alert");
    }
    else if (magnitude == 4) 
    {
      lcd.print("Magnitude 4");
    }
    else if (magnitude == 5) 
    {
      lcd.print("Magnitude 5");
    }
    else if (magnitude == 6) 
    {
      lcd.print("Magnitude 6");
    }
    else if (magnitude == 7) 
    {
      lcd.print("Magnitude 7");
    }
    else if (magnitude == 8) 
    {
      lcd.print("Magnitude 8");
    }
    else if (magnitude == 9) 
    {
      lcd.print("Magnitude 9");
    }

    // Display x, y, z values
    lcd.setCursor(0,1);
    lcd.print("X=");
    lcd.print(xValue);
    lcd.setCursor(6,1);
    lcd.print("Y=");
    lcd.print(yValue);
    lcd.setCursor(12,1);
    lcd.print("Z=");
    lcd.print(zValue);

    delay(125);

    if(xValue < minVal || xValue > maxVal || yValue < minVal || yValue > maxVal || zValue < minVal || zValue > maxVal)
    {
      if(buz == 0)
        start=millis(); // timer start
      buz=1; // buzzer / led flag activated
      lcd.setCursor(0,0); // Move cursor to top left
      lcd.print("Earthquake Alert"); // Print "Earthquake Alert" when buzzer and LED are activated
    }
    else if(buz == 1) 
    {
      if(millis()>= start+buzTime)
        buz=0;
    }
    else
    {
      lcd.clear();
      lcd.print("  ");
    }

    digitalWrite(buzzer, buz); // buzzer on and off command
    digitalWrite(led, buz); // led on and off command

    // Send data to serial plotter
    Serial.print(xValue);
    Serial.print(",");
    Serial.print(yValue);
    Serial.print(",");
    Serial.println(zValue);
  }