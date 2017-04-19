import re
import subprocess

for exnum in range(1000):
    subprocess.run("java -cp \"bin;lib\\*\" controller.GameManager -debug")

    grid = []

    with open("score.txt", "r") as infile:
        for row in infile.read().splitlines():
            grid.append([int(score) if score.isdigit() else 'X' for score in row.split(',')])

    #print(grid)

    s = [[1,1,1,0,0],[1,'X',1,0,0],[1,2,3,2,1],[0,1,'X','X',1],[0,1,2,2,1]]

    #p = [[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0],[0,0,0,0,0]]
    p = [] #[[0]*15]*15

    #print(s)
    #print(p)

    def twoDiter(width, height):
        for x in range(width):
            for y in range(height):
                yield x, y

    ##for x,y in twoDiter(3, 3):
    ##    print ("x", x-1)
    ##    print ("y", y-1)

    ORTHO = {(x-1,y-1) for x,y in twoDiter(3, 3)} - set([(0,0)])

    for x, y in twoDiter(len(grid[0]), len(grid)):
        if y == 0:
            p.append([])
        for i, j in sorted(ORTHO):
            try:
                if i == -1 and j == -1:
                    p[x].append(0)
                if x + i >= 0 and y + j >= 0:
                    p[x][y] += grid[x+i][y+j] == 'X'
            except:
                pass
        p[x][y] = 'X' if grid[x][y] == 'X' else p[x][y]


    '''
    for x in range(len(s[0])):
        for y in range(len(s)):
            for i in range(-1,2):
                for j in range(-1,2):
                    if i == 0 and j == 0:
                        if s[x][y] == 'X':
                            p[x][y] = 'X'
                    else:
                        try:
                            if s[x+i][y+j] == 'X':
                                #print ("x", x)
                                #print ("y", y)
                                #print("")
                                p[x][y] += 1
                        except:
                            continue
    '''

    assert (p == grid)
    print (exnum, p==grid)
