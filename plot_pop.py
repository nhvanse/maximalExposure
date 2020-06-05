import matplotlib.pyplot as plt
import numpy as np
import sys

plt.axis('equal')


def plotChart(filename):
    output = open(filename).read()
    lines = output.split('\n')
    w, h = map(float, lines[0].split())
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
    gene_length = int(lines[index])

    index += 1
    num_individual = int(lines[index])

    for i in range(index + 1, index + num_individual + 1):
        list_locs = []
        convert = lambda string: (float(string.split(',')[0]), float(string.split(',')[1]))
        xy_strings = lines[i].split()
        for string in xy_strings:
            x, y = convert(string)
            list_locs.append([x, y])
        a = np.array(list_locs)
        plt.plot(a[:, 0], a[:, 1])

    ax = plt.gca()
    for sensor in list_sensors:
        circle = plt.Circle((sensor[0], sensor[1]), sensor[2], color='r')
        ax.add_artist(circle)


plotChart(sys.argv[1])
plt.show(block=False)
plt.pause(5)
plt.close()
