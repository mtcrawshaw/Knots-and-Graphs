from bs4 import BeautifulSoup
import random

readFile = open("indianaKnots.html", "r")
pageText = readFile.read()
readFile.close()

bs = BeautifulSoup(pageText, 'html.parser')
count = 0

for row in bs.find_all('tr'):
    if random.random() < 1.0 / 1000:
        print (row.contents)
    count += 1

print (count)
