import serial
import csv
import time

#pip install pyserial
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
                values = data.split(',')
                writer.writerow(values)  # Write the values to CSV
                print(f"Data saved: {values}")
        except KeyboardInterrupt:
            print("Stopping...")
            break
        except Exception as e:
            print(f"Error: {e}")
            break
