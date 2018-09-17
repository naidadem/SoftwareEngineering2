const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.onRobotDataReceived = functions.firestore.document('/Companies/{companyId}/Robots/{robotId}').onWrite(event => {
	
	const newData = event.data.data();
	
	if(newData.error == false){
		return;
	}
	
	var data = {
		errorid: newData.eventcode,
		errormessage: newData.log,
		iserror: newData.error,
		robotid: newData.robotid,
		solved: false,
		timestamp: newData.currenttime
	}
	console.log(data);
	
	console.log('/Companies/'+event.params.companyId+'/Errors');
	
	return admin.firestore().collection('/Companies/'+event.params.companyId+'/Errors').add(data);
});
