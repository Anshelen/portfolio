'use strict';


$(function(){
  $('#emailForm').validate({
    rules: {
      name: {
        required: true,
        minlength: 2,
        maxlength: 30
      },
      subject: {
        required: true,
        minlength: 2,
        maxlength: 50
      },
      text: {
        required: true,
        maxlength: 999
      }
    },
    messages: {
      name: {
        required: messages["contacts.js.validation.name.required"],
        minlength: messages["contacts.js.validation.name.minlength"],
        maxlength: messages["contacts.js.validation.name.maxlength"]
      },
      subject: {
        required: messages["contacts.js.validation.subject.required"],
        minlength: messages["contacts.js.validation.subject.minlength"],
        maxlength: messages["contacts.js.validation.subject.maxlength"]
      },
      text: {
        required: messages["contacts.js.validation.text.required"],
        maxlength: messages["contacts.js.validation.text.maxlength"]
      }
    },
    submitHandler: function () {
      $('#loader').show();
      $('#submitBtn').prop('disabled', true);
      const response = sendMailRequest(
        $('#name').val(), $('#subject').val(), $('#text').val());
      response.then(() => {
        $('#loader').hide();
        $('#submitBtn').prop('disabled', false);
        $('#successModal').modal({show: true});
        $('#name').val("");
        $('#subject').val("");
        $('#text').val("");
      }).catch(error => {
        $('#loader').hide();
        $('#submitBtn').prop('disabled', false);
        $('#failCode').html(error.status);
        $('#failModal').modal({show: true});
      });
      return false;
    }
  });
});
