const firebase = require('@firebase/testing');
const fs = require('fs');

const uids = ["BKVURYUugYYvhYByuH","DOn8TJfvFkWfxESVvDIZb6tMw3v1","xc6OUE6qsedGBEDbQSgSUH2QeYr2","Q3JOfXbdkMa4nHxF1RAg1bnZPrh1"];


function getDatabase(uid){
    return firebase.initializeTestApp({
        projectId: "check-78f83",
        databaseName: "check-78f83-default-rtdb",
        auth: {uid: uid},
    }).database();
}

function getAdminDatabase(){
    return firebase.initializeAdminApp({
        projectId: "check-78f83",
        databaseName: "check-78f83-default-rtdb",
    }).database();
}

describe("update tests", async() => {
    const db = await getDatabase(uids[2]).ref('control');
    it("update switch state", async () => {
        let state;
        let updateData;
        await db.child('update/2').once("value", (a,b) => updateData = a.val());
        await db.child('status/2').once("value", (a,b) => state = a.val());
        updateData["changed_at"] = Date.now();
        updateData["changed_by"] = "anita kumari sinha";
        updateData["changer_id"] = uids[2];
        console.log('state: ', !state, ' updateData: ', updateData);
        
        let data = {};
        data["/update/2"] = updateData;
        data["/status/2"] = !state;

        await firebase.assertSucceeds(db.update(data, (err) => {
            if(err != null){
                console.log(err.message());
            }
        }));
    });
});