const firebase = require('@firebase/testing');
const fs = require('fs');

const uids = ["BKVURYUugYYvhYByuH","DOn8TJfvFkWfxESVvDIZb6tMw3v1","xc6OUE6qsedGBEDbQSgSUH2QeYr2","Q3JOfXbdkMa4nHxF1RAg1bnZPrh1"];

function email(num){
    return `john.doe${num}@gmail.com`;
}

function name(num){
    return `john doe ${num}`;
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

let allData;

describe("users rules", () => {
    before(async() => {
        const db = await getAdminDatabase().ref('users');
        await db.once("value", (a,b) => allData = a.val());
        await firebase.assertSucceeds(db.set(null));
    });

    it("create user0(invalid data)::must fail", async() => {
        const db = await getDatabase(uids[0]).ref('users').child(uids[0]);
        await firebase.assertFails(db.set({"name":name(0)}));
    });
    it("create user0(invalid data)::must fail", async() => {
        const db = await getDatabase(uids[0]).ref('users').child(uids[0]);
        await firebase.assertFails(db.set({"name":name(0), "email": 1}));
    });
    it("create user0::must succeed", async() => {
        const db = await getDatabase(uids[0]).ref('users').child(uids[0]);
        await firebase.assertSucceeds(db.set({"name":name('a'), "email":email(0)}));
    });
    it("update user0::must succeed", async() => {
        const db = await getDatabase(uids[0]).ref(`users/${uids[0]}`);
        await firebase.assertSucceeds(db.update({ name: name('b')}));
    });

    it("delete user0::must fail", async() => {
        const db = await getDatabase(uids[0]).ref(`users/${uids[0]}`);
        await firebase.assertSucceeds(db.set(null));
    });
    it("create user1::must succeed", async() => {
        const db = await getDatabase(uids[1]).ref('users').child(uids[1]);
        await firebase.assertSucceeds(db.set({"name":name('a'), "email":email(1)}));
    });
    it("create user2::must succeed", async() => {
        const db = await getDatabase(uids[2]).ref('users').child(uids[2]);
        await firebase.assertSucceeds(db.set({"name":name('a'), "email":email(2)}));
    });
    it("create user3::must succeed", async() => {
        const db = await getDatabase(uids[3]).ref('users').child(uids[3]);
        await firebase.assertSucceeds(db.set({"name":name('d'), "email":email(3)}));
    });

    //////////////////////////////////////////////////querying and reading
    it("read its own data::must succeed", async() => {
        const db = await getDatabase(uids[1]).ref('users').child(uids[1]);
        await firebase.assertSucceeds(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });
    it("read data by admins::must fail", async() => {
        const db = await getDatabase(uids[2]).ref('users').child(uids[1]);
        await firebase.assertFails(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });
    it("read users data::must fail", async() => {
        const db = await getDatabase(uids[3]).ref('users');
        await firebase.assertFails(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });
    it("read users data by query[email]::must succeed", async() => {
        const db = await getDatabase(uids[3]).ref('users').orderByChild('email').equalTo('john.doe1@gmail.com');
        await firebase.assertSucceeds(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });
    it("read users data by query[name]::must succedd", async() => {
        const db = await getDatabase(uids[3]).ref('users').orderByChild('name').equalTo('john doe a');
        await firebase.assertSucceeds(db.once("value", (a,b) => {
            console.log('\tdata: ', a.val(), ' b: ', b);
        }));
    });

    after("rewrite database", async () => {
        const db = await getAdminDatabase().ref('/users');
        await db.set(null);
        await db.set(allData);
    });

});