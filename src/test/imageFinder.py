import urllib.request
import json

urlPrefix = "http://www.indiana.edu/~knotinfo/diagrams/"
jonesPath = "./jones.json"

jonesFile = open(jonesPath, 'r')
jonesText = jonesFile.read()
jonesFile.close()

jonesDict = json.loads(jonesText)
numComplete = 0

for knotID in jonesDict:
    url = urlPrefix + knotID[2:] + ".png"
    urllib.request.urlretrieve(url, "../../images/eval/" + knotID + ".png")
    numComplete += 1
    progress = int(10000.0 * float(numComplete) / len(jonesDict)) / 100.0
    print ("Progress: ", progress)
