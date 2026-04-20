def calc(a):
    if (a < 0):
        return "Input must be a positive integer"
    while(a != 1):
        if(a %2 == 0):
            a = a/2
        elif(a %2 == 1):
            a = 3*a + 1
        print(a)    
            
    return a


print("Result: {0}".format(calc(9)))