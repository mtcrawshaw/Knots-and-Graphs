from bs4 import BeautifulSoup

def isNumber(i):
    ascii = ord(i)
    return ascii >= 48 and ascii <= 57

def formatPolynomial(poly):
    # Add spaces before and after + and - signs, if they don't already exist
    # Get rid of *
    # Place parentheses around exponents, if they don't already exist
    i = 0
    poly = str(poly)

    while i < len(poly):
        if poly[i] == '*':
            poly = poly[:i] + poly[i + 1:]
            i -= 1
        elif poly[i] == '+':
            if i > 0 and poly[i - 1] != ' ':
                poly = poly[:i] + ' ' + poly[i:]
                i += 1
            if i < len(poly) - 1 and poly[i + 1] != ' ':
                poly = poly[:i + 1] + ' ' + poly[i + 1:]
                i += 1
        elif poly[i] == '-':
            if i > 0 and poly[i - 1] != ' ':
                poly = poly[:i] + ' ' + poly[i:]
                i += 1
            if i < len(poly) - 1 and poly[i + 1] != ' ' and i != 0:
                poly = poly[:i + 1] + ' ' + poly[i + 1:]
                i += 1
        elif poly[i] == '^':
            if i < len(poly) - 1 and poly[i + 1] != '(':
                poly = poly[:i+1] + '(' + poly[i + 1:]
                start = i
                i += 2
                while i < len(poly) and (isNumber(poly[i]) or (i == start + 2 and poly[i] == '-')):
                    i += 1
                poly = poly[:i] + ')' + poly[i:]
            else:
                i += 1
                while i < len(poly) and poly[i] != ')':
                    i += 1

        i += 1

    return poly

readFile = open("indianaKnots.html", "r")
pageText = readFile.read()
readFile.close()

bs = BeautifulSoup(pageText, 'html.parser')
jsonString = "{\n\t"

for row in bs.find_all('tr'):
    try:
        knotID = "I_" + (row.contents[0].find('a')).contents[0]
        jones = (row.contents[1]).contents[0]
        formattedJones = formatPolynomial(jones)
        jsonString = jsonString + "\"" + knotID + "\":\"" + formattedJones + "\",\n\t"
    except:
        pass

n = len(jsonString)
jsonString = jsonString[:n - 3] + "\n}"

writeFile = open("jones.json", "w")
writeFile.write(jsonString)
writeFile.close()
