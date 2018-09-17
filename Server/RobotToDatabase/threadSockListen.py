import threading
import threadRobot
import socket

class ThreadSockListen(threading.Thread):
    def __init__(self,firebaseConnection,companyId):
        super().__init__()
        self.firebaseConnection = firebaseConnection
        self.companyId = companyId
    def run(self):
        sockListen = socket.socket()
        sockListen.bind(('127.0.0.1',12312))
        sockListen.listen()
        print("Server up and running at port 12312")
        print("Use CTRL+C to close the server")
        while (True) :
            (sockServ,addressRobot) = sockListen.accept()
            threadBot = threadRobot.ThreadRobot(sockServ,self.firebaseConnection,self.companyId)
            threadBot.daemon = True
            threadBot.start()