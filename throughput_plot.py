import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV file into a DataFrame
csv_file = "performance_log_20241012_120636.csv" 
df = pd.read_csv(csv_file)

# Convert startTime to datetime format and extract seconds
df['startTime'] = pd.to_datetime(df['startTime'], unit='ms')
df.set_index('startTime', inplace=True)

# Resample the data by 1-second intervals and count requests per second
throughput = df.resample('1S').size()

# Plot the throughput over time
plt.figure(figsize=(10, 6))
plt.plot(throughput.index, throughput.values, marker='o', linestyle='-', label='Throughput (requests/second)')
plt.xlabel('Time')
plt.ylabel('Throughput (requests/second)')
plt.title('Throughput Over Time')
plt.legend()
plt.grid(True)
plt.show()
