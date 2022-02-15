const assert = require('assert');
const firebase = require('@firebase/testing');

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

describe("firebase users node test", () => {
    it("basics", () => {
        assert(4*5,20);
    });
});
