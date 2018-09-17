import firebase_admin
from firebase_admin import credentials
from firebase_admin import auth
from firebase_admin import firestore
import os
import threadSockListen
import time
import json
import random
import string

def delete_collection(coll_ref):
    docs = coll_ref.limit(10).get()
    deleted = 0

    for doc in docs:
        doc.reference.delete()
        deleted = deleted + 1

def clearDatabase(firebase,companyId):
    delete_collection(firebase.collection("Companies",companyId,"Robots"))
    print(companyId, "has been deleted!")

def registerUser(firebase,companyId,username,password):
    userRecord = auth.create_user(email = username,password = password)
    firebase.document("Users",userRecord.uid).create({"companyid" : companyId})
    return userRecord.uid

def deleteUser(firebase,uid):
    auth.delete_user(uid)
    firebase.document("Users",uid).delete()


def randomword(length):
   letters = string.ascii_lowercase
   return ''.join(random.choice(letters) for i in range(length))

def generateCompanyID():
    return randomword(6)

def generateUsernameAndPassword():
    username = randomword(8) + "@test.se"
    password = randomword(8)
    return (username,password)

def registerCompany(companyId,db):
    db.collection("Companies").document(companyId)

def main() :
    cred = credentials.Certificate("dva313-g5-2017-firebase-adminsdk-nxp9i-acf0ac64fb.json")
    default_app = firebase_admin.initialize_app(cred)
    db = firestore.client()
    companyID = generateCompanyID()
    registerCompany(companyID,db)
    (username,password) = generateUsernameAndPassword()
    print("New company created, ID:",companyID)
    print("Username:",username)
    print("Password:", password)
    uid = registerUser(db,companyID,username,password)
    threadSock = threadSockListen.ThreadSockListen(db,companyID)
    threadSock.daemon = True
    threadSock.start()
    try :
        while(True):
            time.sleep(1)
    except KeyboardInterrupt:
        print("Server is stopping...")
        clearDatabase(db,companyID)
        deleteUser(db,uid)
        print(username,"has been deleted!")
        
if (__name__ == "__main__"):
    main()