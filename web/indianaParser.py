from bs4 import BeautifulSoup
import random
import json

readFile = open("indianaKnots.html", "r")
pageText = readFile.read()
readFile.close()

bs = BeautifulSoup(pageText, 'html.parser')
jsonString = "{\n\t"

for row in bs.find_all('tr'):
    try:
        knotID = "I_" + (row.contents[0].find('a')).contents[0]
        jones = (row.contents[1]).contents[0]
        jsonString = jsonString + "\"" + knotID + "\":\"" + formatPolynomial(poly) + "\",\n\t"
    except:
        print ("bad")

#jsonString = jsonString[:]

print jsonString

def formatPolynomial(poly):
    """ IMPLEMENT THIS """
    return poly
