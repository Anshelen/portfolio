'use strict';


function sendMailRequest(name, subject, text) {
  const data = {};
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
  const data = {};
  data.email = email;
  return $.ajax({
    type: 'POST',
    contentType: "application/json",
    data: JSON.stringify(data),
    url: ajax_urls['resendRegistrationEmail']
  });
}

function getAllUsersRequest(page, size) {
  return $.ajax({
    type: 'GET',
    contentType: "application/json",
    data: {page: page, size: size},
    url: ajax_urls["getAllUsers"]
  });
}
