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

let allData = {};


describe("role testing", () => {

    before(async () => {
        const db = await getAdminDatabase().ref('role');
        await db.once("value", (a,b) => allData = a.val());
        await db.set(null);
    });
    
    it("no auth", async() => {
        const db = await firebase.initializeTestApp({
            projectId: "check-78f83",
            databaseName: "check-78f83-default-rtdb",
        }).database().ref('role');
        await firebase.assertFails(db.once("value", (snap, any) => {
            console.log('data: ', snap.val(), 'any: ', any);
        }));
    });

    ////////////////////////////////////////
    it("create user role of level 0::will fail", async () => {
        const db = await getDatabase(uids[0]).ref('role').child(uids[0]);
        await firebase.assertFails(db.set({"level": 0}));
    });

    it("create user role of level 1::will succeed", async () => {
        const db = await getDatabase(uids[1]).ref('role').child(uids[1]);
        await firebase.assertSucceeds(db.set({"level": 1}));
    });

    it("create user role of level 2::will fail", async () => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[2]);
        await firebase.assertFails(db.set({"level": 2}));
    });

    it("create user role of level 3::will fail", async () => {
        const db = await getDatabase(uids[3]).ref('role').child(uids[3]);
        await firebase.assertFails(db.set({"level": 3}));
    });

    /////////////////////////////////////////
    it("has child other than level", async() => {
        const db = await getDatabase(uids[0]).ref('role').child(uids[0]);
        await firebase.assertFails(db.set({"levels": 1}));
    });

    it("has children other than level", async() => {
        const db = await getDatabase(uids[0]).ref('role').child(uids[0]);
        await firebase.assertFails(db.set({"levels": 1,"test": 1}));
    });

    it("has another data type", async() => {
        const db = await getDatabase(uids[3]).ref('role').child(uids[3]);
        await firebase.assertFails(db.set({"level": "1"}));
    });

    
    //////////////////////////////////////////

    it("create role for 0::will succeed", async() => {
        const db = await getDatabase(uids[0]).ref('role').child(uids[0]);
        await firebase.assertSucceeds(db.set({"level": 1}));
    });

    it("create role for 2::will succeed", async() => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[2]);
        await firebase.assertSucceeds(db.set({"level": 1}));
    });

    it("create role for 3::will succeed", async() => {
        const db = await getDatabase(uids[3]).ref('role').child(uids[3]);
        await firebase.assertSucceeds(db.set({"level": 1}));
    });

    it("low user modifying higher user case 1", async () => {
        const db = await getDatabase(uids[1]).ref('role').child(uids[2]);
        await firebase.assertFails(db.set({"level":0}));
    });

    ////////////////////////////////////
    it("promoting user 2 to admin", async () => {
        const db = await getAdminDatabase().ref('role').child(uids[2]);
        await firebase.assertSucceeds(db.set({"level":2}));
    });

    it("promoting user 3 to developer", async () => {
        const db = await getAdminDatabase().ref('role').child(uids[3]);
        await firebase.assertSucceeds(db.set({"level":3}));
    });

    ////////////////////////////////////////

    it("promote user to its level::must fail", async () => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[1]);
        await firebase.assertFails(db.set({"level":2}));
    });

    it("promote user to more than its level::must fail", async () => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[1]);
        await firebase.assertFails(db.set({"level":3}));
    });

    it("block user 0 by user 1::must fail", async () => {
        const db = await getDatabase(uids[1]).ref('role').child(uids[0]);
        await firebase.assertFails(db.set({"level":0}));
    });

    it("block user 0 by user 2 but invalid formatted data::must fail", async () => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[0]);
        await firebase.assertFails(db.set({"level":0, "flag":false}));
    });

    it("block user 0 by user 2::must succeed", async () => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[0]);
        await firebase.assertSucceeds(db.set({"level":0}));
    });

    //////////////////////////////////////////////////read data
    it("reading data of user 0 by user 1::must fail", async () => {
        const db = await getDatabase(uids[1]).ref('role').child(uids[0]);
        await firebase.assertFails(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });
    
    it("reading data of user 1 by itself::must succeed", async () => {
        const db = await getDatabase(uids[1]).ref('role').child(uids[1]);
        await firebase.assertSucceeds(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });

    it("reading data of user 1 by user 2::must succeed", async () => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[1]);
        await firebase.assertSucceeds(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });

    it("reading data of all users::must fail", async() => {
        const db = await getDatabase(uids[2]).ref('role');
        await firebase.assertFails(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }))
    });

    ///////////////////////////////////////////testing delete operation

    it("deleting user 0 role data by user 2", async() => {
        const db = await getDatabase(uids[2]).ref('role').child(uids[0]);
        await firebase.assertFails(db.set(null));
    });

    it("deleting user 0 role by himself", async() => {
        const db = await getDatabase(uids[0]).ref('role').child(uids[0]);
        await firebase.assertSucceeds(db.set(null));
    });

    it("deleting user 1 role data by user 1", async() => {
        const db = await getDatabase(uids[1]).ref('role').child(uids[1]);
        await firebase.assertSucceeds(db.set(null));
    });

    after("rewrite database", async () => {
        const db = await getAdminDatabase().ref('role');
        await db.set(null);
        await db.set(allData);
    });
});