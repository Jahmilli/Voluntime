'use strict';

const functions = require('firebase-functions');
const nodemailer = require('nodemailer');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const googleMapsClient = require('@google/maps').createClient({
    key: functions.config().googlemaps.key
});

// Configure the email transport using the default SMTP transport and a GMail account.
// For other types of transports such as Sendgrid see https://nodemailer.com/transports/
// TODO: Configure the `gmail.email` and `gmail.password` Google Cloud environment variables.
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: gmailEmail,
        pass: gmailPassword,
    },
});
const PENDING = 'pending';
const REGISTERED = 'registered';
const PREVIOUS = 'previous';
const CANCELLED = 'cancelled';
const REJECTED = 'rejected';

// Sends an email confirmation when a user changes his mailing list subscription.
exports.sendEmailConfirmation = functions.database.ref('/Volunteers/{uid}/Events/{eventID}').onWrite(async (change, context) => {
    const snapshot = change.after;
    const val = snapshot.val();
    let volunteerData = undefined;
    let location = undefined;
    let eventData = undefined;
    let charityData = undefined;

    if (val === PENDING) {
        return;
    }

    volunteerData = admin.database()
    .ref(`/Volunteers/${context.params.uid}/Profile`)
    .once('value', res => {
        volunteerData = res.val();
        console.log('Volunteer Data is: ', volunteerData);
    });

    eventData = admin.database()
    .ref(`/Events/${context.params.eventID}`)
    .once('value', res => {
        eventData = res.val();
    });

    await Promise.all([volunteerData, eventData])
        .then(res => res)
        .catch(err => err);

    eventData.location = eventData.location.split(" ");
    eventData.location = new Promise((resolve, reject) => {
        googleMapsClient.reverseGeocode({latlng: [parseFloat(eventData.location[0]),parseFloat(eventData.location[1])]}, (err, res) => {
            if (err) {
                console.error('an error occurred', err);
                return reject(err);
            } else {
                console.log(res.json.results[0].formatted_address);
                return resolve(res.json.results[0].formatted_address);
            }
        });
    }); 

    charityData = admin.database()
    .ref(`/Charities/${eventData.organisers}/Profile`)
    .once('value', res => {
        charityData = res.val();
    });

    location = await eventData.location;
    charityData = await charityData;

    const mailOptions = {
        from: '"Voluntime." <noreply@firebase.com>',
        to: volunteerData.email,
    };

    // Building Email message.
    if (val === REGISTERED) {
        mailOptions.subject = 'You have been selected to help out with our event!';
        mailOptions.text = `Hello ${volunteerData.name}` + 
        `Thanks for registering for an event, the details are as follows: ` + 
        `\nTitle:  ${eventData.title}` +
        `\nDate: ${eventData.date}` +
        `\nTime:  TBA` +
        `\nDescription: ${eventData.description}` +
        `\nCategory: ${eventData.category}` +
        `\nLocation: ${location} \n\n\n` +
        `Looking forward to seeing you,\n` +
        `${charityData.name} \n` +
        `${charityData.phoneNumber} \n` +
        `${charityData.address}`;

    } else if (val === PREVIOUS) {
        mailOptions.subject = "Thank you for your support";
        mailOptions.text = `Hello ${volunteerData.name},` +
        `\nAll of us at ${charityData.name} would just like to thank you for your help and hope to see you at our future events!\n\n\n` +
        `Looking forward to seeing you sometime soon!` +
        `\n${charityData.name}` +
        `\n${charityData.phoneNumber}` +
        `\n${charityData.address}`;
        // Potentially list future events from the charity here.
    } else if (val === CANCELLED) {
        mailOptions.subject = "Our event has been cancelled";
        mailOptions.text = `Hello ${volunteerData.name}` + 
        `\nUnfortunately, we have had to cancel our event.` +
        `\nAll of us at ${charityData.name} would just like to apologise for having to cancel this event but we hope to see you in the future!\n\n\n` +
        `Looking forward to seeing you sometime soon!` +
        `\n${charityData.name}` +
        `\n${charityData.phoneNumber}` +
        `\n${charityData.address}`;
    } else if (val === REJECTED) {
        mailOptions.subject = "You were not picked for our event";
        mailOptions.text = `Hello ${volunteerData.name}` + 
        `\nUnfortunately, you were not picked for our event.` +
        `\nWe hope you understand and we encourage you to apply for our future events.\n\n\n` +
        `Looking forward to seeing you sometime soon!` +
        `\n${charityData.name}` +
        `\n${charityData.phoneNumber}` +
        `\n${charityData.address}`;
    }

    try {
        await mailTransport.sendMail(mailOptions);
        console.log(`An email was successfully sent to ${volunteer.email}`);
    } catch(error) {
        console.error('There was an error while sending the email:', error);
    }
});