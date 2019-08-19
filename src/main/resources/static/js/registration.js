'use strict';


$(function () {
  $('#resend').click(function () {
    $('#loader').show();
    $(this).prop('disabled', true);
    const response = resendEmailConfirmationRequest($(this).data('email'));
    response.then(() => {
      $('#loader').hide();
      $('#resend').prop('disabled', false);
      $('#successModal').modal({show: true});
    }).catch(error => {
      $('#loader').hide();
      $('#resend').prop('disabled', false);
      $('#failCode').html(error.status);
      $('#failModal').modal({show: true});
    });
  })
});
