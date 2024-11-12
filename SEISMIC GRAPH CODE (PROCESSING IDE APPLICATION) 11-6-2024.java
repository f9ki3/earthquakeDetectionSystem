import processing.serial.*;

Serial myPort; // Create object from Serial class
String val; // Data received from the serial port

// Graph variables
int graphWidth = 600;
int graphHeight = 400;
int graphMargin = 20;

// Axis variables
int xAxisLength;
int yAxisLength;

// Data variables
int xValue = 0;
int yValue = 0;
int zValue = 0;

// Plot variables
int plotWidth = 500;
int plotHeight = 350;
int plotMargin = 10;

// Data arrays
int[] xData = new int[plotWidth];
int[] yData = new int[plotWidth];
int[] zData = new int[plotWidth];

boolean isMaximized = false;

// Login and registration variables
String username = "";
String password = "";
String confirmPassword = "";
String registeredUsername = "";
String registeredPassword = "";

// Login and registration functions
void setup() {
  size(800, 700); // Set the window size
  background(25); // Set the background color to dark grey

  // Initialize the serial port
  myPort = new Serial(this, "COM3", 9600); // Replace COM7 with your Arduino's serial port

  // Initialize the data arrays with default values
  for (int i = 0; i < plotWidth; i++) {
    xData[i] = 0;
    yData[i] = 0;
    zData[i] = 0;
  }
}

void draw() {
  background(25); // Clear the background with dark grey

  // Draw the title
  fill(255); // White text
  textSize(24);
  text("Earthquake Detector Seismic Graph", width / 2 - 5, 70);

  // Draw the graph
  drawGraph();

  // Plot the data
  plotData();

  // Read data from the serial port
  if (myPort.available() > 0) {
    val = myPort.readStringUntil('\n');
    if (val != null) {
      // Parse the data
      String[] values = split(val, ',');
      xValue = int(values[0]);
      yValue = int(values[1]);
      zValue = calculateZ(xValue, yValue); // Calculate z value based on x and y

      // Shift the data arrays
      for (int i = plotWidth - 1; i > 0; i--) {
        xData[i] = xData[i - 1];
        yData[i] = yData[i - 1];
        zData[i] = zData[i - 1];
      }

      // Add the new data point
      xData[0] = xValue;
      yData[0] = yValue;
      zData[0] = zValue;
    }
  }

  // Display x, y, z values at the bottom
  fill(255); // White text
  textSize(16);
  int x = width / 2 - 100;
  int y = height - 20;
  text("X Value: " + xValue, x, y);
  x += 100;
  text("Y Value: " + yValue, x, y);
  x += 100;
  text("Z Value: " + zValue, x, y);

  // Display max, min, and average values at the bottom
  x = width / 2 - 100;
  y -= 20;
  text("Max X: " + getMax(xData), x, y);
  x += 100;
  text("Max Y: " + getMax(yData), x, y);
 x += 100;
  text("Max Z: " + getMax(zData), x, y);

  x = width / 2 - 100;
  y -= 20;
  text("Min X: " + getMin(xData), x, y);
  x += 100;
  text("Min Y: " + getMin(yData), x, y);
  x += 100;
  text("Min Z: " + getMin(zData), x, y);

  x = width / 2 - 100;
  y -= 20;
  text("Avg X: " + getAverage(xData), x, y);
  x += 100;
  text("Avg Y: " + getAverage(yData), x, y);
  x += 100;
  text("Avg Z: " + getAverage(zData), x, y);

  // Display magnitude at the bottom
  x = width / 2 - 10;
  y -= 50;
  float magnitude = sqrt(sq(xValue) + sq(yValue) + sq(zValue));
  fill(255); // White text
  textSize(24);
  text("Shake Movement Value: " + magnitude, x, y);
}

void drawGraph() {

  // Calculate graph position
  int graphX = width / 2 - graphWidth / 2;
  int graphY = height / 2 - graphHeight / 2 - 50; // Adjusted graphY to center the graph

  // Draw the graph background
  fill(35); // Dark grey background
  stroke(255); // White border
  strokeWeight(2);
  rect(graphX, graphY, graphWidth, graphHeight);

  // Draw the graph grid
  stroke(255); // White grid
  strokeWeight(1);
  xAxisLength = graphWidth - 2 * graphMargin;
  yAxisLength = graphHeight - 2 * graphMargin;
  for (int i = 0; i <= xAxisLength; i += 50) {
    line(graphX + i, graphY, graphX + i, graphY + graphHeight);
  }
  for (int i = 0; i <= yAxisLength; i += 50) {
    line(graphX, graphY + i, graphX + graphWidth, graphY + i);
  }

  // Draw x, y, z axis labels
  fill(255); // White text
  textSize(16);
  textAlign(CENTER, CENTER); // Set text alignment to center
  text("X", graphX + graphWidth / 2, graphY + graphHeight + 30); 
  text("Y", graphX - 40, graphY + graphHeight / 2); 
  text("Z", graphX + graphWidth + 30, graphY + graphHeight / 2); 

  // Draw y-axis measurement values
  textAlign(RIGHT, CENTER); // Set text alignment to right
  for (int i = 0; i <= yAxisLength; i += 50) {
    text(-100 + (i / 50) * 100, graphX - 5, graphY + graphHeight - i); // Adjusted x-coordinate to -5
  }

  // Draw x-axis measurement values
  textAlign(CENTER, CENTER); // Set text alignment to center
  for (int i = 0; i <= xAxisLength; i += 50) {
    text(i / 60, graphX + i, graphY + graphHeight + 10); // Adjusted y-coordinate to graphY + graphHeight + 20
  }
}

void plotData() {
  // Calculate plot position
  int plotX = width / 2 - plotWidth / 2;
  int plotY = height / 2 - plotHeight / 2 - 50; // Adjusted plotY to center the plot

  // Plot the data
  noFill();
  stroke(255, 0, 0); // Red for x-axis
  strokeWeight(2);
  beginShape();
  for (int i = 0; i < plotWidth; i++) {
    vertex(plotX + i, plotY + plotHeight - map(xData[i], -100, 100, 0, plotHeight));
  }
  endShape();

  stroke(0, 255, 0); // Green for y-axis
  strokeWeight(2);
  beginShape();
  for (int i = 0; i < plotWidth; i++) {
    vertex(plotX + i, plotY + plotHeight - map(yData[i], -100, 100, 0, plotHeight));
  }
  endShape();

  stroke(0, 0, 255); // Blue for z-axis
  strokeWeight(2);
  beginShape();
  for (int i = 0; i < plotWidth; i++) {
    vertex(plotX + i, plotY + plotHeight - map(zData[i], -100, 100, 0, plotHeight));
  }
  endShape();
}

int getMax(int[] data) {
  int max = data[0];
  for (int i = 1; i < data.length; i++) {
    if (data[i] > max) {
      max = data[i];
    }
  }
  return max;
}

int getMin(int[] data) {
  int min = data[0];
  for (int i = 1; i < data.length; i++) {
    if (data[i] < min) {
      min = data[i];
    }
  }
  return min;
}

float getAverage(int[] data) {
  int sum = 0;
  for (int i = 0; i < data.length; i ++) {
    sum += data[i];
  }
  return (float) sum / data.length;
}

// New function to calculate z value based on x and y
int calculateZ(int x, int y) {
  return x + y; // Example calculation
}