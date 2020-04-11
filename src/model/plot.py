
import matplotlib.pyplot as plt
import numpy as np

output = open ("./output/output.txt").read()
lines = output.split('\n')
w, h = map(float, lines[0].split())
number_sensors = int(lines[1])
list_sensors = []
for i in range(2, number_sensors+2):
    print(lines[i])
    x, y, r = map(float, lines[i].split())
    list_sensors.append([x,y,r])

index = number_sensors+2
speed = map(float, lines[index])

index += 1
x_start, y_start = map(float, lines[index].split())

index += 1
x_dest, y_dest = map(float, lines[index].split())

index += 4
print(lines[index])
num_locs = int(lines[index])
list_locs = []
for i in range(index+1, index+num_locs+1):
    x, y= map(float, lines[i].split())
    list_locs.append([x,y])
print(list_locs)

a = np.array(list_locs)
plt.plot(a[:,0], a[:,1])
plt.plot(a[0][0], a[0][1], 'bo')
plt.plot(a[-1][0], a[-1][1] ,'bo')


ax = plt.gca()
for sensor in list_sensors:
    circle = plt.Circle((sensor[0], sensor[1]), sensor[2], color='r')
    ax.add_artist(circle)

plt.show()