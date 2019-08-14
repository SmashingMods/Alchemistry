import csv
with open('elements.csv') as file:
    reader = csv.reader(file)
    for row in reader:
        if reader.line_num > 2:
            if int(row[0]) not in [1, 2, 6, 7, 8, 9, 10, 15, 16, 17, 18, 26, 35, 36, 53, 54, 79, 80, 86]:
                print("item.alchemistry:ingot_" + row[1] + ".name="+row[1].capitalize()+" Ingot")