const firebase = require('@firebase/testing');
const fs = require('fs');

const uids = ["BKVURYUugYYvhYByuH","DOn8TJfvFkWfxESVvDIZb6tMw3v1","xc6OUE6qsedGBEDbQSgSUH2QeYr2","Q3JOfXbdkMa4nHxF1RAg1bnZPrh1"];

function print(a,b){
    console.log(a.val());
}

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
let allData = {};
let infoData = {};
let listInfo = [];
let statusData = {};
let listStatus = [];
let updateData = {};
let listUpdate = [];

describe("control test", () => {
    before("clear data", async () => {
        const db = await getAdminDatabase().ref();
        await db.child('control').once("value", (a,b) => allData = a.val());
        await db.child('control/info').once("value", (a,b) => {
            infoData = a.val();
            a.forEach(snap => {
                listInfo.push(snap.val());
            });
        });
        await db.child('control/status').once("value", (a,b) => {
            statusData = a.val();
            a.forEach(snap => {
                listStatus.push(snap.val());
            });
        });
        await db.child('control/update').once("value", (a,b) => {
            updateData = a.val();
            a.forEach(snap => {
                listUpdate.push(snap.val());
            });
        });
        let board_data;
        await db.child('metadata/board_data').once("value", (a,b) => board_data = a.val());
        await db.child('control').set(null);
    });

    // read board data
    it("read data::succeed", async() => {
        const db = await getDatabase(uids[1]).ref("/metadata/board_data");
        await firebase.assertSucceeds(db.once("value",(a,b) => print(a,b)));
    });
    it("write data::must fail", async() => {
        const db = await getDatabase(uids[1]).ref("/metadata/board_data");
        await firebase.assertFails(db.set({"heart_beat":1}));
    });

    // no auth:: must fail
    it("read data", async() => {
        const db = await firebase.initializeTestApp({
            projectId: "check-78f83",
            databaseName: "check-78f83-default-rtdb",
        }).database().ref('control');
        await firebase.assertFails(db.child('info').once("value", (a,b) => print(a,b)));
    });
    it("write data", async() => {
        const db = await firebase.initializeTestApp({
            projectId: "check-78f83",
            databaseName: "check-78f83-default-rtdb",
        }).database().ref('control');
        await firebase.assertFails(db.child('info/1').set(null));
    });

    it("read whole tree", async() => {
        const db = await getDatabase(uids[3]).ref('/control');
        await firebase.assertFails(db.once("value", (a,b) => print(a,b)));
    });

    // test info data
    
    it("read all data::success", async() => {
        const db = await getDatabase(uids[1]).ref('/control/info');
        await firebase.assertSucceeds(db.once("value", (a,b) => print(a,b)));
    });
    it("write new data::will fail[non admin]", async() => {
        const db = await getDatabase(uids[1]).ref('/control/info');
        await firebase.assertFails(db.child('1').set(listInfo[0]));
    });
    it("write new data::will fail[invalid data][admins]", async() => {
        const db = await getDatabase(uids[2]).ref('/control/info');
        await firebase.assertFails(db.child('1').set({"name": 1}));
    });
    it("write new data::will fail[invalid data][admins]", async() => {
        const db = await getDatabase(uids[2]).ref('/control/info');
        await firebase.assertFails(db.child('1').set({"name": "tt","flag":true}));
    });
    it("write new data::will succeed[admins]", async() => {
        const db = await getDatabase(uids[2]).ref('/control/info');
        await firebase.assertSucceeds(db.child('1').set(listInfo[0]));
    });
    it("read existing data::fail[blocked]", async() => {
        const db = await getDatabase(uids[0]).ref('/control/info');
        await firebase.assertFails(db.child('1').once("value", (a,b)=>print(a,b)));
    });
    it("read existing data::succeed", async() => {
        const db = await getDatabase(uids[1]).ref('/control/info');
        await firebase.assertSucceeds(db.child('1').once("value", (a,b)=>print(a,b)));
    });
    it("update existing data::succeed", async() => {
        const db = await getDatabase(uids[1]).ref('/control/info/1');
        await firebase.assertSucceeds(db.set(listInfo[1]));
    });
    it("write new data::will fail[blocked]", async() => {
        const db = await getDatabase(uids[0]).ref('/control/info');
        await firebase.assertFails(db.child('2').set(listInfo[1]));
    });
    it("read child data", async () => {
        const db = await getDatabase(uids[1]).ref('/control/info');
        await firebase.assertSucceeds(db.once("child_added", (a,b) => print(a,b)));
    });

    // test update data
    it("read all update data:succeed", async () => {
        const db = await getDatabase(uids[1]).ref('/control/update');
        await firebase.assertSucceeds(db.once("value"));
    });
    it("write data:fail[not same user]", async () => {
        const db = await getDatabase(uids[2]).ref('/control/update/1');
        await firebase.assertFails(db.set(listUpdate[0]));
    });
    it("write data:fail[less children]", async () => {
        const db = await getDatabase(uids[2]).ref('/control/update/1');
        await firebase.assertFails(db.set({"changer_id":uids[2]}));
    });
    it("write data:succeed", async () => {
        const db = await getDatabase(uids[3]).ref('/control/update/1');
        await firebase.assertSucceeds(db.set(listUpdate[0]));
    });
    it("write data:fail[less children]", async () => {
        const db = await getDatabase(uids[3]).ref('/control/update/1');
        await firebase.assertFails(db.set({"changer_id":uids[3]}));
    });
    it("read all update data:succeed", async () => {
        const db = await getDatabase(uids[1]).ref('/control/update/1');
        await firebase.assertSucceeds(db.once("value", (a,b) => print(a,b)));
    });

    // status data
    it("read all status data:succeed", async () => {
        const db = await getDatabase(uids[1]).ref('/control/status');
        await firebase.assertSucceeds(db.once("value"));
    });
    it("write data:fail[non admin]", async () => {
        const db = await getDatabase(uids[1]).ref('/control/status/1');
        await firebase.assertFails(db.set(true));
    });
    it("write data:fail[admin,invalid data]", async () => {
        const db = await getDatabase(uids[2]).ref('/control/status/1');
        await firebase.assertFails(db.set(1));
    });
    it("write data:success[admin]", async () => {
        const db = await getDatabase(uids[2]).ref('/control/status/1');
        await firebase.assertSucceeds(db.set(true));
    });
    it("read data:success", async () => {
        const db = await getDatabase(uids[1]).ref('/control/status/1');
        await firebase.assertSucceeds(db.once("value", (a,b) => print(a,b)));
    });
    it("read status child data", async () => {
        const db = await getDatabase(uids[1]).ref('/control/status');
        await firebase.assertSucceeds(db.once("child_added", (a,b) => print(a,b)));
    });


    after("clear data", async () => {
        const db = await getAdminDatabase().ref('/control');
        await db.child('update').set(updateData);
        await db.child('info').set(infoData);
        await db.child('status').set(statusData);
    });

})