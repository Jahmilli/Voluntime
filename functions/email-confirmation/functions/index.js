'use strict';

const functions = require('firebase-functions');
const nodemailer = require('nodemailer');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

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

// Sends an email confirmation when a user changes his mailing list subscription.
exports.sendEmailConfirmation = functions.database.ref('/Volunteers/{uid}/Events/{eventID}').onWrite(async (change, context) => {
    const snapshot = change.after;
    const val = snapshot.val();
    let email = undefined;
    let eventData = undefined;
    let charityData = undefined;

    if (val === PENDING) {
        return;
    }

    email = admin.database()
    .ref(`/Volunteers/${context.params.uid}/Profile/email`)
    .once('value', res => {
        email = res.val();
        console.log('email is: ', email);
    });

    eventData = admin.database()
    .ref(`/Events/${context.params.eventID}`)
    .once('value', res => {
        eventData = res.val();
    });

    await Promise.all([email, eventData])
        .then(res => res)
        .catch(err => err);

    await admin.database()
    .ref(`/Charities/${eventData.organisers}/Profile`)
    .once('value', res => {
        charityData = res.val();
    });

    const mailOptions = {
        from: '"Voluntime." <noreply@firebase.com>',
        to: email,
    };

    // Building Email message.
    if (val === REGISTERED) {
        mailOptions.subject = 'You have been selected to help out with our event!';
        mailOptions.text = `Thanks for registering for an event, the details are as follows:
        Title: ${eventData.title}
        Date: ${eventData.date}
        Time: TBA\n
        Description: ${eventData.description}
        Category: ${eventData.category}
        Location: ${eventData.location}\n\n\n
        Looking forward to seeing you,
        ${charityData.name}
        ${charityData.phoneNumber}
        ${charityData.address}`;

    } else if (val === PREVIOUS) {
        mailOptions.subject = "Thank you for your support";
        mailOptions.text = `All of ${eventData.organisers}
        Would just like to thank you for your help and hope to see you at our future events.\n\n\n
        ${charityData.name}
        ${charityData.phoneNumber}
        ${charityData.address}`;
        // Potentially list future events from the charity here.
    }

    try {
        await mailTransport.sendMail(mailOptions);
        console.log(`An email was successfully sent to ${email}`);
    } catch(error) {
        console.error('There was an error while sending the email:', error);
    }
});
