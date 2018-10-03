

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const getCompany = (idUser) =>
	firebase.database()
		.ref('/')
		.once('value')
		.then(snapshot => {
			const listCompagny = snapshot.val()
			listCompagny.Users.forEach(users => {
				const user = users.val()
				if (user == idUser) {
				}
			})
		})

exports.addMessageTest = functions.https.onRequest((req, res) => {
	const original = req.query.text;
	return admin.database().ref('/idSession/test').push({ original: original }).then((snapshot) => {
		return res.redirect(303, snapshot.ref.toString());
	});
});

exports.addGrid = functions.https.onRequest((req, res) => {
	const name = req.query.name
	const tags = req.query.tags
	const valueTags = {
		color: req.query.color,
		leftOffset: req.query.leftOffset,
		rigthOffset: req.query.rigthOffset
	}

	return admin.database().ref('{Entreprise}/Users/{userId}/tagsSets/').push({ name: name }, { tags: tags }).then((snapshot) => {
		const keyIdTagsSets = snapshot.key()
		return res.redirect(303, snapshot.ref.toString());

	});
	return admin.database().ref('{Entreprise}/Users/{userId}/tagsSets/{keyIdTagsSets}').push({ valueTags }).then((snapshot) => {
		return res.redirect(303, snapshot.ref.toString());


	});


	const getUserRole = uid =>
		firebase.database().ref(`/${uid}/U`).once('value')
			.then(snapshot => {
				const profileData = snapshot.val()
				if (!profileData || !profileData.role || !isValidRole(profileData.role)) {
					return 'viewer'
				}
				return profileData.role
			})




	// Get the company for a given user
	exports.getCompany = functions.https.onRequest((req, res) => {
		const userId = req.query.text
		return admin.database().ref('/').once('value').then((snapshot) => {
			snapshot.forEach(company => {
				const companyValue = company.val();
				companyValue.Users.forEach(user => {
					if (user.key() === userId) { return res.json(companyValue) }
				})
				Promise.reject(err)
			})
		})
	});