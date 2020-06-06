import xlsxwriter
import sys

def read_log(filename):
    string = open(filename).read()
    lines = string.split('\n')
    data = []

    number_sensors = 0
    for line in lines:
        if line == '':
            break
        if line[0].isdigit():
            number_sensors = int(line.split()[0])
        else:
            contents = line.split()
            index = int(contents[1][:-1])
            exposure = float(contents[4][:-2])
            num_generation = int(contents[5])
            runTime = float(contents[7])
            data.append([number_sensors, index, exposure, num_generation, runTime])
    return data

def write_xlsx(filename):
    data = read_log(filename)
    workbook = xlsxwriter.Workbook('./logs/xlsx/'+ filename.split('/')[-1].split('.')[0]+'.xlsx')
    worksheet = workbook.add_worksheet()
    row = 0
    col = 0
    worksheet.write(0, 0, 'Number of sensors')
    worksheet.write(0, 1, 'Index')
    worksheet.write(0, 2, 'Exposure')
    worksheet.write(0, 3, 'Number of generations')
    worksheet.write(0, 4, 'Time')

    for record in data:
        row += 1
        col = 0
        for value in record:
            worksheet.write(row, col, value)
            col += 1
    workbook.close()



write_xlsx(sys.argv[1])