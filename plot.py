import matplotlib.pyplot as plt
import numpy as np
import sys

plt.axis('equal')



def plotChart(filename):
    output = open(filename).read()
    lines = output.split('\n')
    w, h = map(float, lines[0].split())
    plt.axis([0, w, 0, h])
    
    number_sensors = int(lines[1])
    list_sensors = []
    for i in range(2, number_sensors + 2):
        # print(lines[i])
        x, y, r = map(float, lines[i].split())
        list_sensors.append([x, y, r])

    index = number_sensors + 2
    speed = map(float, lines[index])

    index += 1
    x_start, y_start = map(float, lines[index].split())

    index += 1
    x_dest, y_dest = map(float, lines[index].split())

    index += 3
    # print(lines[index])
    exposure = float(lines[index])
    index += 1
    
    num_locs = int(lines[index])
    list_locs = []
    for i in range(index + 1, index + num_locs + 1):
        x, y = map(float, lines[i].split())
        list_locs.append([x, y])
    # print(list_locs)

    a = np.array(list_locs)
    
    
    plt.text(45,102, str(exposure), fontsize=14)
    plt.plot(a[:, 0], a[:, 1])
    plt.plot(a[0][0], a[0][1], 'bo')
    plt.plot(a[-1][0], a[-1][1], 'bo')
    ax = plt.gca()
    for sensor in list_sensors:
        circle = plt.Circle((sensor[0], sensor[1]), sensor[2], color='r')
        ax.add_artist(circle)

try:
    for i in range(1, len(sys.argv)):
    	plotChart(sys.argv[i])
    plt.show(block=False)
    plt.pause(10)
    plt.close()
except:
    print("error")


