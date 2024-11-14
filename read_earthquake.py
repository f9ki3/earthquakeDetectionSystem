import serial
import csv
import time
from datetime import datetime
import math

# Replace with your Arduino's port
arduino_port = 'COM3'  # For Windows, or '/dev/ttyUSB0' for Linux
baud_rate = 9600
filename = "earthquake_data.csv"

# Initialize serial connection
ser = serial.Serial(arduino_port, baud_rate)

# Open CSV file for writing
with open(filename, mode='w', newline='') as file:
    writer = csv.writer(file)
    # Write header row
    writer.writerow(["Timestamp", "Magnitude", "X", "Y", "Z"])
    
    print("Saving data to CSV...")

    while True:
        try:
            # Read data from Arduino
            data = ser.readline().decode('utf-8').strip()
            if data:
                # Split the data into x, y, z values
                values = data.split(',')
                if len(values) == 3:
                    try:
                        x, y, z = map(float, values)  # Convert to floats
                        # Calculate magnitude
                        magnitude = math.sqrt(x**2 + y**2 + z**2)
                        
                        # Check if magnitude is between 1 and 9
                        if 1 <= magnitude <= 9:
                            # Get the current timestamp
                            timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
                            # Write to CSV
                            writer.writerow([timestamp, round(magnitude, 2), x, y, z])
                            print(f"Data saved: Timestamp={timestamp}, Magnitude={round(magnitude, 2)}, X={x}, Y={y}, Z={z}")
                    except ValueError:
                        print("Error: Invalid data received")
        except KeyboardInterrupt:
            print("Stopping...")
            break
        except Exception as e:
            print(f"Error: {e}")
            break
