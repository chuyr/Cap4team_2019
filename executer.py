class Executer:
    def __init__(self, tcpServer):
        self.andRaspTCP = tcpServer
 
    def startCommand(self, command):
 

        import os
        os.system('shutdown /s /t 10')



