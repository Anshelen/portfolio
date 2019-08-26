'use strict';


function sendMailRequest(name, subject, text) {
    var data = {};
    data.name = name;
    data.subject = subject;
    data.text = text;
    return $.ajax({
      type: 'POST',
      contentType: "application/json",
      url: ajax_urls['sendMail'],
      data: JSON.stringify(data)
    })
}

function resendEmailConfirmationRequest(email) {
  return $.ajax({
    type: 'GET',
    contentType: "application/json",
    data: {email: email},
    url: ajax_urls['resendRegistrationEmail']
  });
}
