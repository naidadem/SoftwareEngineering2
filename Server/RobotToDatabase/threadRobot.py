import threading
import json

class ThreadRobot(threading.Thread):
    def __init__(self,socketServ,firebaseConnection,companyId):
        super().__init__()
        self._socketServ = socketServ
        self.firebaseConnection = firebaseConnection
        self.companyId = companyId

    def initDatabaseRobot(self, robot):
        self.firebaseConnection.collection("Companies",self.companyId,"Robots").document(robot["robotid"]).create(robot)
        print(robot["robotid"],"is connected! Beginning sending data to the database!")
    
    def sendDataRobot(self, robot):
        self.firebaseConnection.collection("Companies",self.companyId,"Robots").document(robot["robotid"]).update(robot)

    def run(self):
        initialized = False
        try :
            while(True):
                bytesReceived = self._socketServ.recv(4096)
                toDecode = bytesReceived.decode("utf-8")
                newObject = json.JSONDecoder().decode(toDecode)
                if (initialized):
                    self.sendDataRobot(newObject)
                else:
                    self.initDatabaseRobot(newObject)
                    initialized = True
        except ConnectionResetError :
            pass