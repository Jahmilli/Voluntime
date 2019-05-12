# Email Cloud Function
This function is triggered by changes in User's events and creates and sends an email to the user.

```
/voluntime-27851
    /Volunteers
        /$uid
            /Events
                /$eventID: String # pending/pegistered/previous/cancelled
```
## Deploying the function
Requirements:
As this function is using google maps, we need to set the API key that is used in the code, to do this run:
`firebase functions:config:set googlemaps.key="INSERT API KEY HERE"`
To check if the key is available in configuration, run:
`firebase functions:config:get`

To deploy the function you can either run:
`firebase deploy` OR `firebase deploy --only functions`
