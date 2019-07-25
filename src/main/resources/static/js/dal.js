'use strict';


function sendMailRequest(name, subject, text) {
    var data = {};
    data.name = name;
    data.subject = subject;
    data.text = text;
    return $.ajax({
      type: 'POST',
      contentType: "application/json",
      url: "/email/send",
      data: JSON.stringify(data)
    })
}
