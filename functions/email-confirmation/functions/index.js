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

    console.log('val is ', val);
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
        console.log(`res is `, res);
        res.forEach(child => {
            eventData[child.key] = child.val();
            console.log(child.val());
        });
        console.log('event data is ', eventData);
    });
  

    await Promise.all([email, eventData])
    .then(res => {
        console.log('in promise all email: ', email);
        console.log('in promise all eventData: ', eventData);
        return null;
    })
    .catch(err => {
        console.log(err);
    })

    const mailOptions = {
        from: '"Voluntime." <noreply@firebase.com>',
        to: email,
    };

    // Building Email message.
    if (val === REGISTERED) {
        mailOptions.subject = 'You\'ve been selected to help out with an event!';
        mailOptions.text = `Thanks for registering for an event, the details are as follows: \n 
        Title: ${eventData.title}\n
        Date: ${eventData.date}\n
        Time: TBA\n
        Description: ${eventData.description}\n
        Category: ${eventData.category}\n
        Location: ${eventData.location}\n
        Organisation: ${eventData.organisers}`;

    } else if (val === PREVIOUS) {
        mailOptions.subject = "Thank you for your support";
        mailOptions.text = `All of ${eventData.organisers}\n
        Would just like to thank you for your help and hope to see you at our future events. `;
        // Potentially list future events from the charity here.
    }

    try {
        await mailTransport.sendMail(mailOptions);
        console.log(`An email was successfully sent to ${email}`);
    } catch(error) {
        console.error('There was an error while sending the email:', error);
    }
    return null;
});
