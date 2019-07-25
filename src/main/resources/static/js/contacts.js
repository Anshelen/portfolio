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
        required: "Укажите от кого письмо",
        minlength: "Введите не менее 2-х символов в поле 'Имя'",
        maxlength: "Слишком длинное имя"
      },
      subject: {
        required: "Тема письма обязательна к заполнению",
        minlength: "Введите не менее 2-х символов в заголовок",
        maxlength: "Слишком длинный заголовок"
      },
      text: {
        required: "Заполните тело письма",
        maxlength: "Слишком длинное письмо"
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
